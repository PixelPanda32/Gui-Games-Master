//Author: James Allen
// Start Date: May 15 2026
// Card images are copyright to their original owners 





import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.smartcardio.Card;

import java.util.Scanner;

public class poker{

    private class Card {

        String Value;
        String Type;

        Card(String Value, String Type) {
            this.Value = Value;
            this.Type = Type;
        }

        public String toString() {
            return Value + "-" + Type;
        }

        public int getValue() {
          if(Value.equal("A")) returns 14;
          if(Value.equal("A")) returns 13;
          if(Value.equal("A")) returns 12;
          if(Value.equal("A")) returns 11;
           return Integer.parseInt(Value);
        }

       

        public String getCardImagePath() {
            return "Cards/" + toString() + ".png";
        }
        @Override 
        public int compareTo(Card o){
            return Integer.compare(this.getValue(), o.getValue());
        }
    }
    /*private class Chip{
      static int chipnum;
      static public String getChipImagePath(){
        
        return "./Chips/" + chipnum + ".png";
      }*/


    }
    ArrayList<Card> deck;
    Random random = new Random();
    
    // Hands
    ArrayList<Card> dealerHand;
    ArrayList<Card> playerHand;
    boolean[] playerSelected = new boolean[5]; // Tracks cards marked for DISCARD
    //draw with drawing up to 5 times
    int drawsRemaining = 5; 
    boolean showdown = false;
    String resultMessage = "Select cards to DISCARD. Draws remaining: 5";


    // Window and Card heights
    int boardWidth = 800;
    int boardHeight = 500;
    int cardWidth = 110;
    int cardHeight = 154;

 

    JFrame frame = new JFrame("Poker");
    JButton discardButton = new JButton("Discard");
    JButton playButton = new JButton("Play");
    JButton restartButton = new JButton("Restart");
    JButton saveButton = new JButton("Save"); 
    JButton loadButton = new JButton("load");

    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            Image backgroundImage = new ImageIcon(getClass().getResource("./Cards/BG.png")).getImage();
            
            super.paintComponent(g);
        

            try {   
                g.drawImage(backgroundImage, 0, 0, 800, 450, this);

                // draw dealer hand
                 for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg;
                    /*if (!showdown) {
                        cardImg = new ImageIcon(getClass().getResource("Cards/green_backing.png")).getImage();
                    } else {
                        cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    }*/
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }
                // draw player hand
                 for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    
                    int yOffset = playerSelected[i] ? 200 : 220;
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, yOffset, cardWidth, cardHeight, null);
                    
                    if (playerSelected[i] && drawsRemaining > 0 && !showdown) {
                        g.setFont(new Font("Arial", Font.BOLD, 12));
                        g.setColor(Color.RED);
                        g.drawString("DISCARD", 45 + (cardWidth + 5) * i, 195);
                    }
                }
                 g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(Color.WHITE);
                g.drawString(resultMessage, 20, 410);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel buttonPanel = new JPanel();

    public poker() {

        buildDeck();
        shuffleDeck(); 
        startGame();


        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        gamePanel.setLayout(new BorderLayout());
        frame.add(gamePanel);
          JButton[] buttons = {drawButton, showdownButton, restartButton, saveButton, loadButton};
            for (JButton btn : buttons) {
                btn.setFocusable(false);
                btn.setFont(new Font("Monospaced", Font.PLAIN, 14));
                btn.setBackground(new Color(23, 22, 22));
                btn.setForeground(Color.WHITE);
                btn.setBorderPainted(false);
                buttonPanel.add(btn);
        }
        Panel.setOpaque(false);
        buttonPanel.setBackground(new Color(122, 9, 9,0));
        
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);
        gamePanel.addMouseListener(new MouseAdapter()){
            @Override
            public void mousePressed(MouseEvent e){
                if(drawsRemaining <= 0|| play ) return;

                int mx = e.getX();
                int my = e.getX();

                for (int i = 0; i< playerHand.size(); i++) {
                    int cardX = 20 + (cardWidth + 5) * i;
                    int cardY = playerSelected[i] ? 200 : 220;
                    if(mx >= cardX && mx <= cardX + cardWidth && my >= cardY && my <= cardY + cardHeight){
                        playerSelected[i] = !playerSelected[i];
                        gamePanel.repaint();
                        break;
                    }

                }

            }
        }
           drawButton.addctionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (drawsRemaining > 0) {
                    discardCards();
                    drawsRemaining--;

                    if(drawsRemaining == 0){
                        drawButton.setEnabled(false);
                        resultMessage = "0 draws left, Play time.";
                    }else{
                        resultMessage = "Cards redrawn. Draws remaing " + drawsRemaining;
                    }
                    gamePanel.repaint();
                }
            }
        });
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                play = true;
                drawButton.setEnabled(false);
                playButton.setEnabled(false);
                Winner();
                gamePanel.repaint();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
                hitButton.setEnabled(true);
                standButton.setEnabled(true);
                restartButton.setVisible(false);
                if(deck.size() <= 5){
                    Deck();
                    System.out.println("New deck");
                }
                gamePanel.repaint();

            }
        });
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
                System.out.println("loaded");
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
                System.out.println("saved");
            }
        });
        frame.setVisible(true);
        gamePanel.repaint();
    }
 
    public void startGame() {
        if (deck == null || deck.size() < 30) { // Keep deck full for multiple redraws
            buildDeck();
            shuffleDeck();
        }

        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();
        playerSelected = new boolean[5]; 
        drawsRemaining = 5;
        play = false;
        drawButton.setEnabled(true);
        playButton.setEnabled(true);
        resultMessage = "Select cards to DISCARD. Draws remaining: 5";

        

        for (int i = 0; i < 5; i++) {
            playerHand.add(deck.remove(deck.size() - 1));
            dealerHand.add(deck.remove(deck.size() - 1));

        }
    }

    
    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "J", "K", "Q"};
        String[] types = {"C", "S", "D", "H"};

        
        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }
    public void discardCards(){
         for (int i = 0; i < playerHand.size(); i++) {
            if (playerSelected[i]) {
                if (deck.isEmpty()) {
                    buildDeck();
                    shuffleDeck();
                }
                playerHand.set(i, deck.remove(deck.size() - 1));
            }
            playerSelected[i] = false; 
        }

    }
    public void Winner(){
        int playerValue = scoreHand(playerHand);
        int dealerValue = scoreHand(dealerHand);

        String playerHandName = getHandName(playerValue);
        String dealerHandName = getHandName(dealerValue);

        if (playerValue > dealerValue) {
            resultMessage = "Your " + playerHandName + " beats Dealer's " + dealerHandName + ".";
        } else if (dealerValue > playerValue) {
            resultMessage = "Dealer Hand " + dealerHandName + " beats your " + playerHandName + ".";
        } else {
            resultMessage = "Tie, both players have " + playerHandName + ".";
        }
    }
      private String getHandName(int score) {
        int type = score / 1000000;
        switch (type) {
            case 8: return "Straight Flush";
            case 7: return "Four of a Kind";
            case 6: return "Full House";
            case 5: return "Flush";
            case 4: return "Straight";
            case 3: return "Three of a Kind";
            case 2: return "Two Pair";
            case 1: return "One Pair";
            default: return "High Card";
        }
    }
    private int scoreHand(ArrayList<Card> hand) {
        Collections.sort(hand);
        
        boolean isFlush = true;
        for (int i = 1; i < 5; i++) {
            if (!hand.get(i).Type.equals(hand.get(0).Type)) {
                isFlush = false;
                break;
            }
        }

        boolean isStraight = true;
        for (int i = 0; i < 4; i++) {
            if (hand.get(i + 1).getValue() != hand.get(i).getValue() + 1) {
                isStraight = false;
                break;
            }
        }

        HashMap<Integer, Integer> freq = new HashMap<>();
        for (Card c : hand) {
            freq.put(c.getValue(), freq.getOrDefault(c.getValue(), 0) + 1);
        }

        ArrayList<Integer> pairs = new ArrayList<>();
        int trips = 0;
        int quads = 0;

        for (int val : freq.keySet()) {
            int count = freq.get(val);
            if (count == 2) pairs.add(val);
            else if (count == 3) trips = val;
            else if (count == 4) quads = val;
        }
        Collections.sort(pairs, Collections.reverseOrder());

        int tieBreaker = 0;
        for (int i = 4; i >= 0; i--) {
            tieBreaker = tieBreaker * 15 + hand.get(i).getValue();
        }

        if (isStraight && isFlush) return 8 * 1000000 + hand.get(4).getValue();
        if (quads > 0) return 7 * 1000000 + quads;
        if (trips > 0 && !pairs.isEmpty()) return 6 * 1000000 + trips;
        if (isFlush) return 5 * 1000000 + tieBreaker;
        if (isStraight) return 4 * 1000000 + hand.get(4).getValue();
        if (trips > 0) return 3 * 1000000 + trips;
        if (pairs.size() == 2) return 2 * 1000000 + pairs.get(0) * 15 + pairs.get(1);
        if (pairs.size() == 1) return 1 * 1000000 + pairs.get(0) * 15 + tieBreaker / 15;
        
        return 0 * 1000000 + tieBreaker;
    }

    public void save(){
    File myFile = new File("saveData.txt");
    try{
        FileWriter myWriter = new FileWriter(myFile);
        myWriter.write(playerSum+"\n");
        myWriter.write(playerHand+"\n");
        myWriter.write(dealerSum+"\n");
        myWriter.write(dealerHand+"\n");
        myWriter.write(hiddenCard+"\n");
        myWriter.write(deck+"\n");
        myWriter.flush();
        myWriter.close();
    }catch(IOException e){
    System.out.println("Error: could not write");
    }
   


}
public void load() {
    try {
        java.util.List<String> lines = Files.readAllLines(Paths.get("saveData.txt"));
        this.playerSum = Integer.parseInt(lines.get(0));
        
        this.playerHand = parseHand(lines.get(2));
        this.dealerSum = Integer.parseInt(lines.get(3));
        this.dealerHand = parseHand(lines.get(4));
        this.hiddenCard = parseSingleCard(lines.get(5));
        this.deck = parseHand(lines.get(7));

        gamePanel.repaint();
    } catch (IOException | IndexOutOfBoundsException e) {
        System.out.println("Error: could not load correctly.");
        
    }
}

// method to turn "[A-S, 5-D]" back into ArrayList<Card>
private ArrayList<Card> parseHand(String line) {
    ArrayList<Card> hand = new ArrayList<>();
    String clean = line.replace("[", "").replace("]", "").trim();
    if (clean.isEmpty()) return hand;

    String[] parts = clean.split(", ");
    for (String s : parts) {
        hand.add(parseSingleCard(s));
    }
    return hand;
}

private Card parseSingleCard(String cardStr) {
    String[] parts = cardStr.split("-");
    return new Card(parts[0], parts[1]);
}

    public static void main(String[] args) {
        new poker();
    }
}
