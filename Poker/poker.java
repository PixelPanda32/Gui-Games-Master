//Author: James Allen
// Start Date: May 15 2026
// Card images are copyright to their original owners 





import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

public class poker {

    private class Card implements Comparable<Card>{

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
          if(Value.equals("A"))return 14;
          if(Value.equals("K"))return 13;
          if(Value.equals("Q"))return 12;
          if(Value.equals("J"))return 11;
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


    
    ArrayList<Card> deck;
    Random random = new Random();
    
    // Hands
    ArrayList<Card> dealerHand;
    ArrayList<Card> playerHand;
    boolean[] playerSelected = new boolean[5]; // Tracks cards marked for DISCARD
    //draw with drawing up to 5 times
    int drawsRemaining = 5; 
    boolean play = false;
    String resultMessage = "Select cards to DISCARD. Draws remaining: 5";


    // Window and Card heights
    int boardWidth = 800;
    int boardHeight = 500;
    int cardWidth = 110;
    int cardHeight = 154;

 

    JFrame frame = new JFrame("Poker");
    JButton drawButton = new JButton("Discard");
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
                    cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
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
                    
                    if (playerSelected[i] && drawsRemaining > 0 && !play) {
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
          JButton[] buttons = {drawButton, playButton, restartButton, saveButton, loadButton};
            for (JButton btn : buttons) {
                btn.setFocusable(false);
                btn.setFont(new Font("Monospaced", Font.PLAIN, 14));
                btn.setBackground(new Color(23, 22, 22));
                btn.setForeground(Color.WHITE);
                btn.setBorderPainted(false);
                buttonPanel.add(btn);
        }
        buttonPanel.setOpaque(true);
        
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);
        gamePanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if(drawsRemaining <= 0|| play ) return;

                int mx = e.getX();
                int my = e.getY();

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
        });
           drawButton.addActionListener(new ActionListener() {
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
                determineWinner();
                gamePanel.repaint();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
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
public void determineWinner() {

    int playerRank = getHandRank(playerHand);
    int dealerRank = getHandRank(dealerHand);

    String playerHandName = getHandName(playerRank);
    String dealerHandName = getHandName(dealerRank);

    
    if (playerRank > dealerRank) {
        resultMessage = "Your " + playerHandName + " beats Dealer's " + dealerHandName + ".";
        return;
    } 
    if (dealerRank > playerRank) {
        resultMessage = "Dealer's " + dealerHandName + " beats your " + playerHandName + ".";
        return;
    }

  
    Collections.sort(playerHand, Collections.reverseOrder());
    Collections.sort(dealerHand, Collections.reverseOrder());

    for (int i = 0; i < 5; i++) {
        int pVal = playerHand.get(i).getValue();
        int dVal = dealerHand.get(i).getValue();

        if (pVal > dVal) {
            resultMessage = "Your " + playerHandName + " wins against dealers" + dealerHandName + ".";
            return;
        } else if (dVal > pVal) {
            resultMessage = "Dealer's " + dealerHandName + " wins against Your" + playerHandName + ".";
            return;
        }
    }

  
    resultMessage = "Tie! Both players have the exact same " + playerHandName + ".";
}

private String getHandName(int rank) {
    switch (rank) {
        case 9: return "Straight Flush";
        case 8: return "Four of a Kind";
        case 7: return "Full House";
        case 6: return "Flush";
        case 5: return "Straight";
        case 4: return "Three of a Kind";
        case 3: return "Two Pair";
        case 2: return "One Pair";
        default: return "High Card";
    }
}

private int getHandRank(ArrayList<Card> hand) {
    Collections.sort(hand); // Sort ascending to easily check for straights

    // Check Flush & Straight
    boolean isFlush = true;
    boolean isStraight = true;
    for (int i = 0; i < 4; i++) {
        if (!hand.get(i).Type.equals(hand.get(i + 1).Type)) isFlush = false;
        if (hand.get(i + 1).getValue() != hand.get(i).getValue() + 1) isStraight = false;
    }

    // Count card frequencies
    HashMap<Integer, Integer> freq = new HashMap<>();
    for (Card c : hand) {
        freq.put(c.getValue(), freq.getOrDefault(c.getValue(), 0) + 1);
    }

    int pairs = 0, trips = 0, quads = 0;
    for (int count : freq.values()) {
        if (count == 4) quads++;
        else if (count == 3) trips++;
        else if (count == 2) pairs++;
    }

    // Return a clean rank from 1 to 9
    if (isStraight && isFlush) return 9; 
    if (quads == 1)            return 8; 
    if (trips == 1 && pairs == 1) return 7; 
    if (isFlush)               return 6; 
    if (isStraight)            return 5; 
    if (trips == 1)            return 4; 
    if (pairs == 2)            return 3; 
    if (pairs == 1)            return 2; 

    return 1; // High Card
}
    public void save(){
    File myFile = new File("Poker_save_Data.txt");
    try{ FileWriter myWriter = new FileWriter(myFile);
        myWriter.write(playerHand.toString() +"\n");
        myWriter.write(drawsRemaining+ "\n");
        myWriter.write(play + "\n");
        myWriter.write(deck.toString()+"\n");
    }catch(IOException e){
    System.out.println("Error: could not write");
    }
   


}
public void load() {
     try {
            java.util.List<String> lines = Files.readAllLines(Paths.get("poker_save_data.txt"));
            this.playerHand = parseHand(lines.get(0));
            this.dealerHand = parseHand(lines.get(1));
            this.drawsRemaining = Integer.parseInt(lines.get(2));
            this.play = Boolean.parseBoolean(lines.get(3));
            this.deck = parseHand(lines.get(4));

            drawButton.setEnabled(drawsRemaining > 0 && !play);
            playButton.setEnabled(!play);
            
            if (play) {
                determineWinner();
            } else {
                resultMessage = "Game Loaded. Draws remaining: " + drawsRemaining;
            }
            gamePanel.repaint();
        } catch (IOException | IndexOutOfBoundsException e) {
            resultMessage = "Error: Could not load save file.";
            gamePanel.repaint();
        }
}

// method to turn "[A-S, 5-D]" back into ArrayList<Card>
    private ArrayList<Card> parseHand(String line) {
        ArrayList<Card> hand = new ArrayList<>();
        String clean = line.replace("[", "").replace("]", "").trim();
        if (clean.isEmpty()) return hand;

        String[] parts = clean.split(", ");
        for (String s : parts) {
            String[] cardParts = s.split("-");
            hand.add(new Card(cardParts[0], cardParts[1]));
        }
        return hand;
    }

    public static void main(String[] args) {
        new poker();
    }
}
