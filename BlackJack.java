//Author: James Allen
// Start Date: May 22 2025 
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
import java.util.Scanner;

public class BlackJack {

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
        
        return "./Chips/" + chipnum + ".png";
      }


    }
    ArrayList<Card> deck;
    Random random = new Random();

    // dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount; 

    // Window
    int boardWidth = 800;
    int boardHeight = 450;

    // Cards
    int cardWidth = 110;
    int cardHeight = 154;
    
    //Chips 
    int chipWidth;
    int chipHeight;
    int money = 1145;
    int bet;

    JFrame frame = new JFrame("Black Jack");

    JButton hitButton = new JButton("Hit");
    JButton standButton = new JButton("Stand");
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
                
                // draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./Cards/green_backing.png")).getImage();
                if (!standButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getCardImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                // draw dealer hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // draw player hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 220, cardWidth, cardHeight, null);
                }
                int[] tempChips = new int[0];
                if(money > 2000 ){
                    int[] tempChips2 = {1000,500,250,100,50,25,10,5};
                    tempChips = tempChips2;
                }else if(money < 1000){
                    int[] tempChips2 = {100,50,25,10,5};
                    tempChips = tempChips2;
                } else if(money < 500){
                    int[] tempChips2 = {100,50,25,10,5};
                    tempChips = tempChips2;
                }else if(money < 2000 && money > 1000){
                    int[] tempChips2 = {250,100,50,25,10,5};
                    tempChips = tempChips2;
                }else{
                    int[] tempChips2 = {1000,500,250,100,50,25,10,5};
                    tempChips = tempChips2;
                }
                
                int[] Chips = tempChips; 
                for(int chipnum : Chips){
                   
                    if(money >= chipnum){
                        int count = money / chipnum;
                        money = money % chipnum;
                        System.out.println(count + " number of "+ chipnum);
                        
                        
                        
                    }
                   

                }
                

                if (!standButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();

                    String message = "";
                    if (playerSum > 21) {
                        message = "Bust";
                        
                    } else if (dealerSum > 21) {
                        message = "Dealer Bust";
                    } else if (playerSum == dealerSum) {
                        message = "Tie";
                        money = money + bet*2;
                    } else if (playerSum > dealerSum) {
                        
                        message = "You win";
                        money = money + bet*2;
                    } else {
                        message = "You Lose";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(new Color(23, 22, 22));
                    g.drawString(message, 75, 207);

                    restartButton.setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel buttonPanel = new JPanel();

    BlackJack() {
        System.out.println(money);
        Deck();
        StartGame();
        Bet();

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
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);    
                    standButton.setEnabled(false);
                }
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
                    dealerAceCount += card.isAce() ? 1 : 0;
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
                System.out.println(money);


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
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }
    public void Bet(){
        Scanner keyboard = new Scanner(System.in);
        int bet;
        money = 1000;

        System.out.println("how much do you want to bet");
         
        
        keyboard.close();


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

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
    public void save(){
    File myFile = new File("saveData.txt");
    try{
        FileWriter myWriter = new FileWriter(myFile);
        myWriter.write(playerSum+"\n");
        myWriter.write(playerAceCount+"\n");
        myWriter.write(playerHand+"\n");
        myWriter.write(dealerSum+"\n");
        myWriter.write(dealerHand+"\n");
        myWriter.write(hiddenCard+"\n");
        myWriter.write(dealerAceCount+"\n");
        myWriter.write(deck+"\n");
        myWriter.write(money+"\n");
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
        this.playerAceCount = Integer.parseInt(lines.get(1));
        this.playerHand = parseHand(lines.get(2));
        this.dealerSum = Integer.parseInt(lines.get(3));
        this.dealerHand = parseHand(lines.get(4));
        this.hiddenCard = parseSingleCard(lines.get(5));
        this.dealerAceCount = Integer.parseInt(lines.get(6));
        this.deck = parseHand(lines.get(7));
        this.money = Integer.parseInt(lines.get(8));

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
        new BlackJack();
    }
}
