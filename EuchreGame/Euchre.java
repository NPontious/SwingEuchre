package EuchreGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Euchre {
    //A disgusting amount of static variables
    public static final int NUM_PLAYERS = 4;
    public static JCheckBox showHelp = new JCheckBox("Learning Mode");
    public static JCheckBox exportBox = new JCheckBox("Export Mode");
    public static JPanel handPanel = new JPanel(new GridLayout(1,6));
    public static JPanel scorePanel = new JPanel(new GridLayout(1,7));
    public static JPanel trickPanel = new JPanel(new GridBagLayout());
    public static JPanel bidPanel = new JPanel();
    public static JPanel trumpPanel = new JPanel();
    public static JPanel helpPanel = new JPanel(new GridBagLayout());
    public static GUI gui = new GUI();
    public static ArrayList<Card> trick = new ArrayList<>();
    public static ArrayList<Card> deck = new ArrayList<>();
    public static ArrayList<Card> playeCards = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();
    public static String trump;
    public static Color trumpColor;
    public static int proposedBid = 0;
    public static int playerTurn = 0;
    public static int roundNum = 0;
    public static int bids = 0;
    public static int team1Score = 0;
    public static int team2Score = 0;
    public static Player player1;
    public static Player player2;
    public static Player player3;
    public static Player player4;
    public static Player winningPlayer;
    public static Player highestPlayer = null;
    public static Player partner = null;
    public static boolean roundOver = false;
    public static boolean newRound = true;
    public static boolean cardPlayed = false;
    public static boolean bidPlaced = false;
    public static boolean notStart = true;
    public static Card best;
    public static Color tableGreenColor = new Color(48, 115, 83);
    public static Color onSuitColor = new Color(48, 83, 106);
    public static Color offSuitColor = new Color(165, 94, 69);
    public static Color trumpCardColor = new Color(165, 123, 69);
    
    public static void main(String[] args) throws InterruptedException {
        //Start/help screen
        gui.setResizable(false);
        Thread.sleep(400);
        HelpScreenLayout();
        gui.add(helpPanel, BorderLayout.CENTER);
        helpPanel.revalidate();
        helpPanel.repaint();
        gui.revalidate();
        gui.repaint();
        while(notStart){
            Thread.sleep(10);
            helpPanel.revalidate();
            helpPanel.repaint();
        }
        gui.remove(helpPanel);
        //Game setup
        gui.add(trickPanel, BorderLayout.CENTER);
        trickPanel.setBackground(tableGreenColor);
        players.add(new Player("Player 1", 0));
        for (int i = 1; i < NUM_PLAYERS; i++) {
            players.add(new AIPlayer("Bot " + (i), i));
        }
        player1 = players.get(0);
        player2 = players.get(1);
        player3 = players.get(2);
        player4 = players.get(3);
        //Game loop
        while(team1Score < 32 && team2Score < 32){
            reset();
            while(!roundOver){
                //Starts bidding loop
                if(newRound && trump == null){
                    bid();
                    gui.revalidate();
                    gui.repaint();
                    while(bids != 4){
                        Thread.sleep(100);
                        gui.revalidate();
                        bidPanel.revalidate();
                        bidPanel.repaint();
                    }
                    highestBid();
                    System.out.println(highestPlayer.getName() + " is the highest bidder");
                    bids = 0;
                    highestPlayer.selectTrump();
                    while(trump == null){
                        Thread.sleep(100);
                        gui.revalidate();
                    }
                    updateScorePanel();
                    gui.revalidate();
                    gui.repaint();
                    setLeftBar();
                    newRound = false;
                } else {
                    //Card laying loop
                    Thread.sleep(1000);
                    play();
                    updateTrickPanel();
                    updateScorePanel();
                    gui.revalidate();
                    gui.repaint();
                    for(Player player: players){
                        if(player.getHand().isEmpty()){
                            roundOver = true;
                        } else {
                            roundOver = false;
                            break;
                        }
                    }
                }
            }
            //Finish off the round
            updateTrickPanel();
            gui.revalidate();
            gui.repaint();
            Thread.sleep(500);
            if(!trick.isEmpty()){
                scoreTrick();
            }
            Thread.sleep(500);
            scoreRound();
            updateTrickPanel();
            updateScorePanel();
            gui.revalidate();
            gui.repaint();
            for(Player player: players){
                player.getHand().clear();
            }
            newRound = true;
            trump = null;
        }
        //Win conditionals
        JLabel winningLabel;
        if (team1Score > team2Score) {
            winningLabel = new JLabel("Team 1 wins! \n Team 1 score: " + team1Score + " \n Team 2 score: " + team2Score);
            winningLabel.setFont(new Font("Arial", Font.BOLD, 18));
            winningLabel.setOpaque(true);
            winningLabel.setBackground(Color.GREEN);
        } else {
            winningLabel = new JLabel("Team 2 wins! \n Team 1 score: " + team1Score + " \n Team 2 score: " + team2Score);
            winningLabel.setFont(new Font("Arial", Font.BOLD, 18));
            winningLabel.setOpaque(true);
            winningLabel.setBackground(Color.RED);
        }
        winningLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        winningLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        trickPanel.add(winningLabel);

        gui.revalidate();
        gui.repaint();
    }


    /**
     * Plays through one round. Calls for each player to lay a card.
     * @throws InterruptedException
     */
    public static void play() throws InterruptedException{
        //Check if trick is full, otherwise continue
        if(trick.size() == players.size()){
            scoreTrick();
        } else {
            if(trick.size() == 1){
                best = trick.get(0);
            }
            for(Card card : Euchre.trick){
                if(Euchre.trick.isEmpty()){
                    for(Player player : Euchre.players){
                        player.setHasTrick(false);
                    }
                    Euchre.players.get(card.getIndex()).setHasTrick(true);
                    best = card;
                } else if(card.getValue() > best.getValue()){
                    best = card;
                    for(Player player : Euchre.players){
                        player.setHasTrick(false);
                    }
                    Euchre.players.get(best.getIndex()).setHasTrick(true);
                }
            }
            // Exporting player's hand and played cards to a text file
            if(exportBox.isSelected()){
                export();
            }
            switch(playerTurn){
                case 0:
                    if(players.contains(player1)){
                        System.out.println(player1.getName());
                        handPanel.removeAll();
                        for(Card card: player1.getHand()){
                            createButton(card);
                        }
                        handPanel.revalidate();
                        handPanel.repaint();
                        handButtons(true);
                        System.out.println(players.get(0).getHand());
                        System.out.println(playeCards.toString());
                        while(!cardPlayed){
                            Thread.sleep(1);
                        }
                        cardPlayed = false;
                    } else {
                        playerTurn++;
                    }
                    break;
                case 1:
                    if(players.contains(player2)){
                        System.out.println(player2.getName());
                        ((AIPlayer)player2).playCard();
                    } else {
                        playerTurn++;
                    }
                    break;
                case 2:
                    if(players.contains(player3)){
                        System.out.println(player3.getName());
                        ((AIPlayer)player3).playCard();
                    } else {
                        playerTurn++;
                    }
                    break;
                case 3:
                    if(players.contains(player4)){
                        System.out.println(player4.getName());
                        ((AIPlayer)player4).playCard();
                    } else {
                        playerTurn = 0;
                    }
                    break;
            }
        }
    }

    /**
     * Exports the game data to output.txt
     */
    public static void export(){
        try (PrintWriter writer = new PrintWriter(new File("output" + playerTurn + ".txt"), "UTF-8")) { // Correctly close the resource declaration with a parenthesis
            writer.println("Player's Hand:");
            for (Card card : players.get(playerTurn).getHand()) {
                writer.println(card.getRank() + " of " + card.getSuit());
            }
            writer.println("\nPlayed Cards:");
            for (Card card : playeCards) {
                writer.println(card.getRank() + " of " + card.getSuit());
            }
            writer.println("\nCards in trick:");
            for (Card card : trick) {
                writer.println(card.getRank() + " of " + card.getSuit());
            }
            writer.println("\nTeamate have trick:");
            Card winning = winningCard();
            if(winning != null){
                writer.println(winning.getIndex() % 2 == playerTurn % 2);
            } else {
                writer.println(false);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding not supported.");
            e.printStackTrace();
        }
    }

    /**
     * Calls for each player to bid.
     * @throws InterruptedException
     */
    public static void bid() throws InterruptedException{
        for(int i = 0; i < NUM_PLAYERS; i++){
            players.get((playerTurn + i) % players.size()).placeBid();
            Thread.sleep(100);
            bidPanel.revalidate();
            bidPanel.repaint();
            Thread.sleep(900);
            while(!bidPlaced){
                Thread.sleep(1);
            }
            bidPlaced = false;
            trickPanel.removeAll();
        }
        for(Player player : players){
            player.setHasBid(false);
        }
        for(int i = 0; i < NUM_PLAYERS; i++){
            if(players.get((playerTurn + i) % players.size()).getBid() == 12){
                partner = players.get((playerTurn + i + 2) % players.size());
                players.remove(partner);
                break;
            }
        }
    }

    /**
     * Sets the highestPlayer as the one with the highest bid.
     */
    public static void highestBid(){
        int highest = 0;
        for(Player player: players){
            if(player.getBid() > highest){
                highest = player.getBid();
                highestPlayer = player;
            }
        }
        if(highestPlayer == null){
            highestPlayer = players.get(0);
        }
    }

    /**
     * Goes through each players hand and sets the left bar to the correct card.
     */
    public static void setLeftBar(){
        player1.setTrumpColor();
        for(Player player : players){
            for(Card card : player.getHand()){
                if(card.getColor().equals(trumpColor) && card.getIntRank() == 11 && !Objects.equals(card.getSuit(), trump)){
                    card.setLeftBar(true);
                }
            }
        }
    }

    /**
     * Scores the trick based on each card's rank, suit, and value.
     */
    public static void scoreTrick(){
        Card highest = trick.get(0);
        for(Card card: trick){
            if(card.getValue() > highest.getValue()){
                highest = card;
            }
        }
    
        for(Player player: players){
            if(player.getIndex() == highest.getIndex()){
                winningPlayer = player;
            }
        }
        for(Player player : players){
            if(player.getHasTrick()){
                //winningPlayer = player;
                player.setHasTrick(false);
                break;
            }
        }
        best = null;
        winningPlayer.setScore(winningPlayer.getScore() +  1);
        for(Card card: trick){
            System.out.println(card.getRank() + " of " + card.getSuit() + " Value: " + card.getValue());
        }
        System.out.println(winningPlayer.getName() + " won the trick with the " + highest.getRank() + " of " + highest.getSuit());
        trick.clear();
        playerTurn = winningPlayer.getIndex();
        gui.revalidate();
        gui.repaint();
    }
    
    /**
     * Adds points to each team based on their bid and how many tricks they won.
     */
    public static void scoreRound(){
        if(players.size() == 3){
            players.add(partner.getIndex(), partner);
        }
        for(Player player: players){
            System.out.println(player.getName() + "'s score: " + player.getScore());
        }
        if(highestPlayer.getIndex() % 2 == 1){
            team1Score += players.get(0).getScore() + players.get(2).getScore();
        } else {
            team2Score += players.get(1).getScore() + players.get(3).getScore();
        }
        if(highestPlayer.getBid() <= (highestPlayer.getScore() + players.get((highestPlayer.getIndex() + 2) % players.size()).getScore())){
            if(highestPlayer.getIndex() % 2 == 0){
                team1Score += (highestPlayer.getScore() + players.get((highestPlayer.getIndex() + 2) % players.size()).getScore());
            } else {
                team2Score += (highestPlayer.getScore() + players.get((highestPlayer.getIndex() + 2) % players.size()).getScore());
            }
        } else {
            if(highestPlayer.getIndex() % 2 == 0){
                team1Score -= highestPlayer.getBid();
            } else {
                team2Score -= highestPlayer.getBid();
            }
        }
        proposedBid = 0;
        roundNum++;
    }

    public static Card winningCard(){
        Card winning = null;
        for (Card card : trick) {
            if (winning == null || card.getValue() > winning.getValue()) {
                winning = card;
            }
        }
        return winning;
    }

    /**
     * Updates the trickPanel's data and redraws it.
     */
    public static void updateTrickPanel() {
        trickPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        int row = 0;
        for (Card card : trick) {
            if(highestPlayer.getBid() != 12){
            JLabel playerLabel = new JLabel(players.get(card.getIndex()).getName());
            playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            if (card == winningCard()) {
                switch(card.getIndex() % 2){
                    case(0):
                        playerLabel.setForeground(Color.GREEN);
                        break;
                    case(1):
                        playerLabel.setForeground(Color.RED);
                        break;
                }
            } else {
                playerLabel.setForeground(Color.WHITE);
            }
            gbc.gridy = row;
            trickPanel.add(playerLabel, gbc);
        }
            // Load and display the card image
            try {
                String imagePath = "Textures/" + card.getSuit().toLowerCase() + "_" + card.getRank().toLowerCase() + ".png";
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage cardImage = ImageIO.read(imageFile);
                    JLabel cardImageLabel = new JLabel(new ImageIcon(cardImage));
                    gbc.gridy = row + 1;
                    trickPanel.add(cardImageLabel, gbc);
                } else {
                    System.err.println("Image file not found: " + imagePath);
                    // Handle the missing file, e.g., show an error message or use a default image
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception, e.g., show an error message or use a default image
            }
        }
    
        for(int i = trick.size() * 2; i < players.size() * 2; i++){
            gbc.gridy = i;
            trickPanel.add(new JLabel(), gbc);
        }
    
        trickPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        trickPanel.revalidate();
        trickPanel.repaint();
    }

    /**
     * Creates the help/start screen
     */
    public static void HelpScreenLayout() {
        GridBagConstraints gbc = new GridBagConstraints();

        helpPanel.setBackground(tableGreenColor);

        // Export mode Checkbox
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //exportBox.setSelected(true);
        exportBox.setBackground(tableGreenColor);
        exportBox.setForeground(Color.WHITE);
        helpPanel.add(exportBox, gbc);
    
        // Show Help Checkbox
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        showHelp.setSelected(true);
        showHelp.setBackground(tableGreenColor);
        showHelp.setForeground(Color.WHITE);
        helpPanel.add(showHelp, gbc);
    
        // Title
        JLabel titleLabel = new JLabel("Learning Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        helpPanel.add(titleLabel, gbc);
    
        // Convert Color objects to hexadecimal color codes
        String offSuitColorHex = String.format("#%02x%02x%02x", offSuitColor.getRed(), offSuitColor.getGreen(), offSuitColor.getBlue());
        String onSuitColorHex = String.format("#%02x%02x%02x", onSuitColor.getRed(), onSuitColor.getGreen(), onSuitColor.getBlue());
        String trumpCardColorHex = String.format("#%02x%02x%02x", trumpCardColor.getRed(), trumpCardColor.getGreen(), trumpCardColor.getBlue());

        // Help Text Area
        JEditorPane helpText = new JEditorPane();
        helpText.setContentType("text/html");
        helpText.setEditable(false);
        helpText.setText("<html><body style='color:black; font-family: Arial; font-size: 9px;'>" +
                         "<p style='margin: 0;'>Blue: <span style='background-color:" + onSuitColorHex + "; color:white;'>Must play if in hand</span></p>" +
                         "<p style='margin: 0;'>Yellow: <span style='background-color:" + trumpCardColorHex + "; color:white;'>Trump card</span></p>" +
                         "<p style='margin: 0;'>Red: <span style='background-color:" + offSuitColorHex + "; color:white;'>Off suit card</span></p>" +
                         "</body></html>");
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        helpPanel.add(helpText, gbc);
    
        // Rules
        JLabel rulesLabel = new JLabel("Rules");
        rulesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rulesLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        helpPanel.add(rulesLabel, gbc);
    
        // Rules Text Area
        JTextArea rulesText = new JTextArea("""
                1. Each player is dealt six cards.
                2. The player to the left of the dealer makes the first bid. (Bot 3 is the first dealer)
                3. Bidding continues clockwise until everyone bids.
                4. The highest bidder chooses the trump suit.
                5. Players must follow suit if possible; otherwise, they may play any card.
                6. The player who plays the highest card wins the trick.
                7. The winner of the trick leads to the next trick.
                8. The game continues until one team reaches a score of 32 points.""");
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        helpPanel.add(rulesText, gbc);
    
        // Useful terms
        JLabel howToPlayLabel = new JLabel("Useful terms");
        howToPlayLabel.setFont(new Font("Arial", Font.BOLD, 18));
        howToPlayLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        helpPanel.add(howToPlayLabel, gbc);
    
        // Terms TextArea
        JTextArea usefulTerms = new JTextArea("""
                Trump: The suit chosen by the highest bidder to be the trump suit for the round. They are the highest cards with the exception of the left bar.
                Suit: The category of a card, such as Hearts, Diamonds, Clubs, or Spades.
                Bid: The number of points a player bids to win the round.
                Trick: A round of play consisting of four cards played by each player in turn.
                Score: The number of tricks the team has won. If a bid is not fulfilled then that many points are subtracted.
                Round: A rounds ends when all 24 cards are played or when all six tricks are played.\
                Left bar: The jack of the same color as trump, but not the trump suit. Has the second highest value and now counts as the same suit as trump.\
                Right bar: The jack of the trump suit. Has the highest value\
                Team: The player opposite you at the table. Player1 and Bot2 are a team and bots 1 and 3 are a team.""");
        usefulTerms.setEditable(false);
        usefulTerms.setLineWrap(true);
        usefulTerms.setWrapStyleWord(true);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        helpPanel.add(usefulTerms, gbc);

        // Useful terms
        JLabel howWorkLabel = new JLabel("How the game works");
        howWorkLabel.setFont(new Font("Arial", Font.BOLD, 18));
        howWorkLabel.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        helpPanel.add(howWorkLabel, gbc);

        // Terms TextArea
        JTextArea howWork = new JTextArea("""
                The game starts by showing you your hand in the bottom and asking for a bid.
                Select your bid and click the bid button.
                The bots will all place their bids and if you have the highest bid, you will be asked which suit you want to be trump.
                For the first round you wil be the first to lay a card. You wil know when it is your turn because the card buttons will be enabled.
                The game will highlight the player who is winning the trick. If it's green then your team is winning the trick. If it's red the other team is winning it.
                After all the players lay their cards, the game will automatically score the trick.
                After all the cards from your hand are played the game will add the points to each team accordingly and start the next round.\
                The game loop will continue until one team reaches 32 points.""");
        howWork.setEditable(false);
        howWork.setLineWrap(true);
        howWork.setWrapStyleWord(true);
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        helpPanel.add(howWork, gbc);
    
        // Start Game Button
        JButton startGameButton = new JButton("Start Game");
        startGameButton.setBackground(Color.WHITE);
        startGameButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startGameButton.addActionListener(e -> {
            helpPanel.remove(startGameButton);
            helpPanel.remove(showHelp);
            helpPanel.revalidate();
            helpPanel.repaint();
            gui.remove(helpPanel);
            notStart = false;
            gui.revalidate();
            gui.repaint();
        });
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        helpPanel.add(startGameButton, gbc);
    
        gui.add(helpPanel, BorderLayout.CENTER);
        gui.revalidate();
        gui.repaint();
    }

    /**
     * Updates the score panel's data and redraws it.
     */
    public static void updateScorePanel() {
        scorePanel.removeAll();
        scorePanel.setBackground(Color.LIGHT_GRAY);
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
        Font scoreFont = new Font("Arial", Font.BOLD, 14);
        Font teamFont = new Font("Arial", Font.BOLD, 14);
        //Player scores
        for (Player player : players) {
            JLabel playerScoreLabel = new JLabel(player.getName() + ": " + player.getScore());
            playerScoreLabel.setFont(scoreFont);
            playerScoreLabel.setOpaque(true);
            playerScoreLabel.setBackground(Color.WHITE);
            playerScoreLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            scorePanel.add(playerScoreLabel);
        }
        //Team 1 score
        JLabel team1ScoreLabel = new JLabel("Team 1: " + team1Score);
        team1ScoreLabel.setFont(teamFont);
        team1ScoreLabel.setOpaque(true);
        team1ScoreLabel.setBackground(Color.WHITE);
        team1ScoreLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        scorePanel.add(team1ScoreLabel);
        //Team 2 score
        JLabel team2ScoreLabel = new JLabel("Team 2: " + team2Score);
        team2ScoreLabel.setFont(teamFont);
        team2ScoreLabel.setOpaque(true);
        team2ScoreLabel.setBackground(Color.WHITE);
        team2ScoreLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        scorePanel.add(team2ScoreLabel);
        //Trump label
        JLabel trumpLabel = new JLabel("Trump: " + trump);
        trumpLabel.setFont(scoreFont);
        trumpLabel.setOpaque(true);
        trumpLabel.setBackground(Color.WHITE);
        trumpLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        scorePanel.add(trumpLabel);
    
        scorePanel.revalidate();
        scorePanel.repaint();
    }

    /**
     * Deals cards from the deck to each player.
     */
    public static void dealCards() {
        int j =  0;
        for(Player player: players){
            ArrayList<Card> tempHand = new ArrayList<>();
            for(int i = j; i < j +  6; i++){
                Card card = deck.get(i);
                tempHand.add(card);
                card.setIndex(player.getIndex());
            }
            player.setHand(tempHand);
            j +=  6;
        }
    }

    /**
     * Generates a deck of card objects.
     */
    public static void createDeck(){
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"Ace","9", "10", "Jack", "Queen", "King"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
    Collections.shuffle(deck);
    }

    /**
     * Creates the card buttons for the player.
     * @param card
     */
    public static void createButton(Card card) {
        JButton button = new JButton(card.getRank() + " of " + card.getSuit());
        button.setForeground(Color.WHITE);
        Color color;
        if(showHelp.isSelected()){
            //updateHelpPanel();
            color = validCard(card);
        } else {
            color = Color.BLACK;
        }
        button.setBackground(color);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            System.out.println(card.getRank() + " of " + card.getSuit());
            trick.add(card);
            players.get(playerTurn).removeCard(card);
            handPanel.remove(button);
            cardPlayed = true;
            gui.revalidate();
            playerTurn++;
            handButtons(false);
        });
        button.setEnabled(false);
        handPanel.add(button);
    }

    /**
     * Checks if the card can/needs to be laid or if a card is trump. Only used in leaning mode.
     * @param card
     * @return
     */
    public static Color validCard(Card card){
        if(!trick.isEmpty()){
            if((card.getSuit().equals(trick.get(0).getSuit()) && !card.isLeftBar()) || (card.isLeftBar() && !card.getSuit().equals(trick.get(0).getSuit()) && card.getColor().equals(trick.get(0).getColor()))){
                return onSuitColor;
            } else if(card.getSuit().equals(trump) || card.isLeftBar()){
                return trumpCardColor;
            } else {
                return offSuitColor;
            }
        } else if(card.getSuit().equals(trump) || card.isLeftBar()){
            return trumpCardColor;
        } else {
            return onSuitColor;
        }
    }

    /**
     * Enables and disables the player's card buttons
     * @param enabled
     */
    public static void handButtons(boolean enabled){
        for(Component button: handPanel.getComponents()){
            button.setEnabled(enabled);
        }
    }

    /**
     * Resets a bunch of the important game values
     */
    public static void reset(){
        roundOver = false;
        newRound = true;
        playerTurn = (roundNum) % players.size();
        bids = 0;
        winningPlayer = null;
        highestPlayer = null;
        trick.clear();
        deck.clear();
        playeCards.clear();
        for(Player player: players){
            player.resetPlayer();
        }
        handPanel.removeAll();
        scorePanel.removeAll();
        trickPanel.removeAll();
        bidPanel.removeAll();
        trumpPanel.removeAll();
        createDeck();
        dealCards();
        for(Card card: players.get(0).getHand()){
            createButton(card);
        }
    }
}