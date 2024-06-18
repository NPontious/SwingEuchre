package EuchreGame;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import javax.swing.JLabel;

public class AIPlayer extends Player {
    String bidTrump;
    boolean hasOnSuit;

    /**
     * 
     * Creates a player with automated actions
     * @param name
     * @param index
     */
    public AIPlayer(String name, int index) {
        super(name, index);
    }
    /**
     * Chooses a card to play based on what is in the trick
     */
    public void playCard() {
        Card pickedCard = null;
        if(!Euchre.trick.isEmpty()){
            for(Card card : getHand()){
                if(card.getSuit().equals(Euchre.trick.get(0).getSuit())){
                    hasOnSuit = true;
                    break;
                } else hasOnSuit = card.isLeftBar() && card.getColor().equals(Euchre.trick.get(0).getColor());
            }
        }
    
        if (Euchre.trick.isEmpty()) {
            for (Card card : getHand()) {
                if (pickedCard == null || card.getIntRank() > pickedCard.getIntRank()) {
                    pickedCard = card;
                }
            }
        } else if(hasOnSuit){
            for (Card card : getHand()) {
                if ((pickedCard == null || card.getIntRank() > pickedCard.getIntRank()) && (card.getSuit().equals(Euchre.trick.get(0).getSuit()) || card.isLeftBar() && card.getColor().equals(Euchre.trick.get(0).getColor()))){
                    pickedCard = card;
                }
            }
        } else {
            for (Card card : getHand()) {
                if (pickedCard == null || card.getValue() > pickedCard.getValue()) {
                    pickedCard = card;
                }
            }
        }
        if(Euchre.best != null){
            if(getIndex() == (Euchre.best.getIndex() + 2) % 4){
                if(hasOnSuit){
                    for (Card card : getHand()) {
                        if ((pickedCard == null || card.getIntRank() < pickedCard.getIntRank()) && (card.getSuit().equals(Euchre.trick.get(0).getSuit()) || card.isLeftBar() && card.getColor().equals(Euchre.trick.get(0).getColor()))){
                            pickedCard = card;
                        }
                    }
                } else {
                    for (Card card : getHand()) {
                        if (pickedCard == null || card.getValue() < pickedCard.getValue()) {
                            pickedCard = card;
                        }
                    }
                }
            }
        }
    
        if (pickedCard == null) {
            pickedCard = getHand().get(0);
        }
    
        Euchre.trick.add(pickedCard);
        Euchre.playeCards.add(pickedCard);
        getHand().remove(pickedCard);

        Euchre.gui.revalidate();
        Euchre.gui.repaint();
    
        Euchre.playerTurn = (Euchre.playerTurn + 1) % 4;
        System.out.println("AI Player " + getName() + " played card " + pickedCard);
    }
    /**
     * Places a bid based on what cards the player has
     */
    @Override
    public void placeBid() {
        int[] suits = new int[4];
        int maxSuit = 0;
        Random random = new Random();
        for(Card card : getHand()){
            switch(card.getSuit()){
                case "Hearts":
                    suits[0]++;
                    break;
                case "Diamonds":
                    suits[1]++;
                    break;
                case "Clubs":
                    suits[2]++;
                    break;
                case "Spades":
                    suits[3]++;
                    break;
            }
        }
        for(int i = 0; i < suits.length; i++){
            if(suits[i] >= suits[maxSuit]){
                maxSuit = i;
                switch (i) {
                    case 0:
                        bidTrump = "Hearts";
                        break;
                    case 1:
                        bidTrump = "Diamonds";
                        break;
                    case 2:
                        bidTrump = "Clubs";
                        break;
                    case 3:
                        bidTrump = "Spades";
                        break;
                }
            }
        }
        int currentBid = maxSuit + random.nextInt(3) - 1;
        if(currentBid < 3){
            currentBid = 0;
        }
        if(Euchre.bids == 3 && currentBid < 3){
            currentBid = 3;
        }
        if(currentBid > Euchre.proposedBid){
            Euchre.proposedBid = currentBid;
        } else {
            currentBid = 0;
        }
        // if(currentBid == 6){
        //     alone = random.nextBoolean();
        //     if(alone){
        //         currentBid = 12;
        //     }
        // }
        hasBid = true;
        setBid(currentBid);
        Euchre.bids++;
        System.out.println("AI Player " + getName() + " is bidding " + getBid());
        Euchre.trickPanel.add(new JLabel("AI Player " + getName() + " is bidding " + getBid()) {{
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.PLAIN, 16));
        }});
        Euchre.trickPanel.revalidate();
        Euchre.trickPanel.repaint();
        Euchre.bidPlaced = true;
    }
    /**
     * Selects trump based on what is in the players hand
     */
    @Override
    public void selectTrump(){
        if(bidTrump == null){
            bidTrump = getHand().get(0).getSuit();
        }
        Euchre.trump = bidTrump;
        bidTrump = null;
        setTrumpColor();
        System.out.println("Trump is " + Euchre.trump);
    }
}