package EuchreGame;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;

public class EuchreBotNN {
    private MultiLayerNetwork model;

    public EuchreBotNN(int inputSize, int hiddenLayerSize, int outputSize) {
        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 64; // Number of examples in each minibatch
        int numEpochs = 30; // Number of training epochs

        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
              .seed(seed)
              .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
              .updater(new Adam(learningRate))
              .weightInit(WeightInit.XAVIER)
              .list()
              .layer(0, new LSTM.Builder().nIn(inputSize).nOut(hiddenLayerSize))//
              .activation(Activation.TANH)
              .build();

        // Add more layers if needed
        builder.layer(1, new DenseLayer.Builder().nIn(hiddenLayerSize).nOut(hiddenLayerSize).activation(Activation.RELU).build());
        builder.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD))//
              .activation(Activation.SOFTMAX)
              .nIn(hiddenLayerSize).nOut(outputSize).build();

        builder.pretrain(false).backprop(true).backpropType(BackpropType.TruncatedBPTTForward);

        model = new MultiLayerNetwork(builder.build());
        model.init();
    }

    public INDArray prepareInput(ArrayList<Card> hand, ArrayList<Card> trick, boolean isTeammateWinning) {
        INDArray input = Nd4j.zeros(1, 217);
        int offset = 0;
        for (Card card : hand) {
            input.putScalar(new int[]{0, offset + card.getSuitIndex() * 8 + card.getRankIndex()}, 1);
            offset += 32;
        }
        for (Card card : trick) {
            input.putScalar(new int[]{0, offset + card.getSuitIndex() * 8 + card.getRankIndex()}, 1);
            offset += 32;
        }
        input.putScalar(new int[]{0, offset}, isTeammateWinning? 1 : 0); // Teammate winning status
        return input;
    }

    public Card chooseCard(INDArray output, ArrayList<Card> hand) {
        int[] shape = output.shape();
        int maxIndex = Nd4j.argMax(output, 1).getInt(0);
        return hand.get(maxIndex);
    }

    public void trainGame(EuchreGame game) {
        while (!game.isOver()) {
            INDArray input = prepareInput(game.getHand(), game.getTrick(), game.isTeammateWinning());
            INDArray output = model.output(input);
            Card chosenCard = chooseCard(output, game.getHand());
            game.playCard(chosenCard);
            double reward = game.getReward(); // Implement this based on your game logic
            INDArray labels = Nd4j.zeros(1, hand.size());
            labels.putScalar(new int[]{0, hand.indexOf(chosenCard)}, 1); // One-hot encoded label
            INDArray rewards = Nd4j.valueArrayOf(1, 1, reward); // Reward for the action
            model.fit(input, labels, rewards);
        }
    }

    public void play(EuchreGame game) {
        INDArray input = prepareInput(game.getHand(), game.getTrick(), game.isTeammateWinning());
        INDArray output = model.output(input);
        Card chosenCard = chooseCard(output, game.getHand());
        game.playCard(chosenCard);
    }
}