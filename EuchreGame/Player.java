package EuchreGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;

public class Player {
    private final String name;
    private int score;
    private final int index;
    private int bid;
    private boolean hasTrick;
    protected boolean hasBid;
    protected boolean alone;

    private ArrayList<Card> hand = new ArrayList<>();

    /**
     * Creates a player with a name and index
     * @param name
     * @param index
     */
    public Player(String name, int index) {
        this.name = name;
        this.index = index;
    }
    /**
     * @return the index of the player
     */
    public int getIndex() {
        return index;
    }

    /**
     * Removes a card to the player's hand
     * @param card
     */
    public void removeCard(Card card) {
        Euchre.playeCards.add(card);
        hand.remove(card);
    }
    /**
     * Sets a hand all at once with an ArrayList
     * @param hand
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }
    /**
     * @return the player's hand as an ArrayList of Card objects
     */
    public ArrayList<Card> getHand() {
        return hand;
    }
    /**
     * @return the player's name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the player's score
     */
    public int getScore() {
        return score;
    }
    /**
     * Sets the player's score
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }
    /**
     * Sets the player's bid
     * @param bid
     */
    public void setBid(int bid) {
        this.bid = bid;
    }
    /**
     * @return the player's bid
     */
    public int getBid() {
        return bid;
    }
    /**
     * Sets whether the player has a bid
     * @param hasBid
     */
    public void setHasBid(boolean hasBid){
        this.hasBid = hasBid;
    }
    /**
     * @return whether the player has a bid
     */
    public boolean getHasBid(){
        return hasBid;
    }
    /**
     * Set whether the player is winning the trick
     * @param hasTrick
     */
    public void setHasTrick(boolean hasTrick){
        this.hasTrick = hasTrick;
    }
    /**
     * @return whether the player is winning the tick or not
     */
    public boolean getHasTrick(){
        return hasTrick;
    }
    /**
     * Sets the basic values back to 0
     */
    public void resetPlayer(){
        score = 0;
        bid = 0;
        hand.clear();
    }
    /**
     * Sets the trumpColor based on the trump that was chosen
     */
    public void setTrumpColor(){
        if(Euchre.trump.equals("Hearts") || Euchre.trump.equals("Diamonds")){
            Euchre.trumpColor = Color.RED;
        } else if(Euchre.trump.equals("Clubs") || Euchre.trump.equals("Spades")) {
            Euchre.trumpColor = Color.BLACK;
        }
    }
    /**
     * Creates the bidPanel and sets the player's bid
     */
    public void placeBid() {
        DefaultListModel<Integer> bidListModel = new DefaultListModel<>();
        if (Euchre.proposedBid != 0 || Euchre.bids != 3) {
            bidListModel.addElement(0);
        }
        if (Euchre.proposedBid < 3) {
            bidListModel.addElement(3);
        }
        if (Euchre.proposedBid < 4) {
            bidListModel.addElement(4);
        }
        if (Euchre.proposedBid < 5) {
            bidListModel.addElement(5);
        }
        if (Euchre.proposedBid < 6) {
            bidListModel.addElement(6);
        }
        //bidListModel.addElement(12);

        JList<Integer> bidList = new JList<>(bidListModel);
        JButton bidButton = new JButton("Bid");
        bidButton.addActionListener(e -> {
            if (bidList.getSelectedValue() != null) {
                setBid(bidList.getSelectedValue());
                Euchre.gui.remove(Euchre.bidPanel);
                Euchre.bids++;
                Euchre.bidPlaced = true;
                Euchre.gui.repaint();
                setTrumpColor();
                hasBid = true;
            }
        });

        Font bidFont = new Font("Arial", Font.BOLD, 13);
        bidList.setFont(bidFont);

        Euchre.bidPanel.setBackground(Euchre.tableGreenColor);
        Euchre.bidPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        bidList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Euchre.bidPanel.add(bidButton);
        Euchre.bidPanel.add(bidList);

        // JTextArea bidOptionsText = new JTextArea("0: Pass\n12: Going alone");
        // bidOptionsText.setFont(bidFont);
        // bidOptionsText.setEditable(false);
        // bidOptionsText.setLineWrap(true);
        // bidOptionsText.setWrapStyleWord(true);
        // bidOptionsText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Euchre.bidPanel.add(bidOptionsText);

        JTextArea playerBids = new JTextArea();
        for(Player player : Euchre.players){
            if(player.getHasBid()){
                playerBids.append(player.getName() + ": " + player.getBid() + "\n");
            }
        }
        if(playerBids.getText().isEmpty()){
            playerBids.append("You're the first bidder.");
        }
        playerBids.setFont(bidFont);
        playerBids.setEditable(false);
        playerBids.setLineWrap(true);
        Euchre.bidPanel.add(playerBids);

        Euchre.gui.add(Euchre.bidPanel, BorderLayout.CENTER);
        Euchre.gui.revalidate();
        Euchre.gui.repaint();
    }
    /**
     * Creates the trump selection panel and sets trump
     */
    public void selectTrump(){
        JList<String> suitList = new JList<>(new String[]{"Hearts", "Diamonds", "Clubs", "Spades"});
        JButton trumpButton = new JButton("Select Trump");
        trumpButton.addActionListener(e -> {
            Euchre.trump = suitList.getSelectedValue();
            Euchre.gui.remove(Euchre.trumpPanel);
            System.out.println("Trump is " + Euchre.trump);
        });

        Font trumpFont = new Font("Arial", Font.BOLD, 14);
        trumpButton.setFont(trumpFont);

        Euchre.trumpPanel.setBackground(Color.LIGHT_GRAY);
        Euchre.trumpPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
        suitList.setFont(trumpFont);
        suitList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Euchre.trumpPanel.add(trumpButton);
        Euchre.trumpPanel.add(suitList);

        Euchre.gui.add(Euchre.trumpPanel, BorderLayout.CENTER);
        Euchre.trumpPanel.revalidate();
        Euchre.trumpPanel.repaint();
    }
}