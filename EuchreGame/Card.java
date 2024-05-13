package EuchreGame;

import java.awt.Color;

public class Card {
    private final String suit;
    private final String rank;
    private final Color color;
    private int index;
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
        int value;
        if(suit.equals(Euchre.trump)){
            value = switch (rank) {
                case "9" -> 6;
                case "10" -> 7;
                case "Jack" -> 12;
                case "Queen" -> 8;
                case "King" -> 9;
                case "Ace" -> 10;
                default -> 0;
            };
            return value;
        } else if(Euchre.trumpColor == color) {
            if(Euchre.trick.get(0).getSuit().equals(suit)){
                value = switch (rank) {
                    case "10" -> 1;
                    case "Jack" -> 11;
                    case "Queen" -> 3;
                    case "King" -> 4;
                    case "Ace" -> 5;
                    default -> 0;
                };
            } else {
                if (rank.equals("Jack")) {
                    value = 11;
                } else {
                    value = 0;
                }
            }
            return value;
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
        return switch (rank) {
            case "9" -> 9;
            case "10" -> 10;
            case "Jack" -> 11;
            case "Queen" -> 12;
            case "King" -> 13;
            case "Ace" -> 14;
            default -> 0;
        };
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