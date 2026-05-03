import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

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
    }

    ArrayList<Card> deck;

    BlackJack() {
        StartGame();
    }

    public void StartGame() {
        // Deck Building
        buildDeck();
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "J", "K", "Q"};
        String[] types = {"C", "S", "D", "H"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }

        System.out.println("Build Deck");
        System.out.println(deck);
    }

    
    public static void main(String[] args) {
        new BlackJack(); 
}
