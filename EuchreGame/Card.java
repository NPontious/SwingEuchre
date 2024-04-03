package EuchreGame;

import java.awt.Color;

public class Card {
    private String suit;
    private String rank;
    private Color color;
    private int index;
    private int value;
    private boolean isLeftBar = false;

    /**
     * Creates a card object with a suit and rank
     * @param suit
     * @param rank
     */
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        if(suit.equals("Hearts") || suit.equals("Diamonds")){
            color = Color.RED;
        } else {
            color = Color.BLACK;
        }
    }

    /**
     * @return the color of the card
     */
    public Color getColor() {
        return color;
    }
    /**
     * @return the value of the card based on trump, suit, and rank
     */
    public int getValue() {
        if(suit == Euchre.trump){
            switch (rank) {
                case "9": value = 6; break;
                case "10": value = 7; break;
                case "Jack": value = 12; break;
                case "Queen": value = 8; break;
                case "King": value = 9; break;
                case "Ace": value = 10; break;
                default: value = 0; break;
            }
            return value;
        } else if(Euchre.trumpColor == color) {
            if(Euchre.trick.get(0).getSuit().equals(suit)){
                switch (rank) {
                    case "9": value = 0; break;
                    case "10": value = 1; break;
                    case "Jack": value = 11; break;
                    case "Queen": value = 3; break;
                    case "King": value = 4; break;
                    case "Ace": value = 5; break;
                    default: value = 0; break;
                }
                return value;
            } else {
                switch (rank) {
                    case "Jack": value = 11; break;
                    default: value = 0; break;
                }
                return value;
            }
        } else if(Euchre.trick.get(0).getSuit().equals(suit)){
            value = getIntRank() - 9;
            return value;
        } else {
            value = 0;
        }
        return value;
    }
    /**
     * @return which player has/had the card
     */
    public int getIndex() {
        return index;
    }
    /**
     * Sets the index
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }
    /**
     * @return the suit of the card
     */
    public String getSuit() {
        return suit;
    }
    /**
     * @return the rank of the card
     */
    public String getRank() {
        return rank;
    }
    /**
     * @return the rank of the card in int form
     */
    public int getIntRank() {
        switch (rank) {
            case "9": return 9;
            case "10": return 10;
            case "Jack": return 11;
            case "Queen": return 12;
            case "King": return 13;
            case "Ace": return 14;
            default: return 0;
        }
    }
    /**
     * Sets whether the card is the left bar or not
     * @param value
     */
    public void setLeftBar(boolean value){
        this.isLeftBar = value;
    }
    /**
     * @return whether the card is the left bar or not
     */
    public boolean isLeftBar(){
        return isLeftBar;
    }
    /**
     * @return the card as a string of its rank and suit
     */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }

}