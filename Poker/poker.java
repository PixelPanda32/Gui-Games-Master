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
            if ("AJQK".contains(Value)) {
                if (Value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(Value);
        }

        public boolean isAce() {
            return Value.equals("A");
        }

        public String getCardImagePath() {
            return "./Cards/" + toString() + ".png";
        }
    }
    private class Chip{
       static int chipnum;
      static public String getChipImagePath(){
        
        return "Gui-Game-Master/Chips/" + chipnum + ".png";
      }


    }
    ArrayList<Card> deck;
    Random random = new Random();

    // dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;

    // player
    ArrayList<Card> playerHand;
    int playerSum;


    // Window
    int boardWidth = 800;
    int boardHeight = 450;

    // Cards
    int cardWidth = 110;
    int cardHeight = 154;
    
    //Chips 
    int chipWidth;
    int chipHeight;
 

    JFrame frame = new JFrame("Poker");

    JButton hitButton = new JButton("Hit");
    JButton standButton = new JButton("Stand");
    JButton restartButton = new JButton("restart");
    JButton saveButton = new JButton("Save"); 
    JButton loadButton = new JButton("load");

    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            Image backgroundImage = new ImageIcon(getClass().getResource("./Cards/BG.png")).getImage();
            
            super.paintComponent(g);
        

            try {   
                g.drawImage(backgroundImage, 0, 0, 800, 450, this);
                
                // draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./Cards/green_backing.png")).getImage();
                if (!standButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getCardImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                // draw dealer hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    System.out.println(dealerHand.size());
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // draw player hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 220, cardWidth, cardHeight, null);
              
                
               
                    //Win conditions

               
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(new Color(23, 22, 22));


                    restartButton.setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel buttonPanel = new JPanel();

    poker() {

        Deck();
        StartGame();


        frame.setSize(boardWidth, boardHeight);
        ///frame.setBackground(new Color(122, 9, 9));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.setLayout(new BorderLayout());
        //gamePanel.setBackground(new Color(122, 9, 9));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        standButton.setFocusable(false);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        saveButton.setFocusable(false);
        loadButton.setFocusable(false);

        hitButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        standButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        restartButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        saveButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        loadButton.setFont(new Font("Monospaced", Font.PLAIN, 15));
        
        hitButton.setBackground(new Color(23, 22, 22));
        hitButton.setBorderPainted(false);
        standButton.setBackground(new Color(23, 22, 22));
        standButton.setBorderPainted(false);
        restartButton.setBackground(new Color(22, 22, 22));
        restartButton.setBorderPainted(false);
        saveButton.setBackground(new Color(22, 22, 22));
        saveButton.setBorderPainted(false);
        loadButton.setBackground(new Color(22, 22, 22));
        loadButton.setBorderPainted(false);


        saveButton.setForeground(new Color(255,255,255));
        restartButton.setForeground(new Color(255,255,255));
        standButton.setForeground(new Color(255,255,255));
        hitButton.setForeground(new Color(255,255,255));
        loadButton.setForeground(new Color(255,255,255));

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(new Color(122, 9, 9,0));
        
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
                System.out.println("saved");
            }
        });
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerHand.add(card);
                gamePanel.repaint();
            }
        });

        standButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                standButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerHand.add(card);
                }
                gamePanel.repaint();
                
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StartGame();
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

        // Key Bindings
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('h'), "hitAction");
        gamePanel.getActionMap().put("hitAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hitButton.isEnabled()) {
                    hitButton.doClick();
                }
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), "standAction");
        gamePanel.getActionMap().put("standAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (standButton.isEnabled()) {
                    standButton.doClick();
                }
            }
        });

        gamePanel.repaint();
    }
    public void Deck(){
        buildDeck();
        shuffleDeck();

    }
    public void StartGame() {
        

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
       

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
      

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerHand.add(card);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        

        

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();

            playerHand.add(card);
        }
    }

    
    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "J", "K", "Q"};
        String[] types = {"C", "S", "D", "H"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
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
