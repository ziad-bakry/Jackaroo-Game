package model.player;

import java.util.ArrayList;

import exception.GameException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import model.Colour;
import model.card.Card;

@SuppressWarnings("unused")
public class Player {
    private final String name;
    private final Colour colour;
    private ArrayList<Card> hand;
    private final ArrayList<Marble> marbles;
    private Card selectedCard;
	private final ArrayList<Marble> selectedMarbles;

    public Player(String name, Colour colour) {
        this.name = name;
        this.colour = colour;
        this.hand = new ArrayList<>();
        this.selectedMarbles = new ArrayList<>();
        this.marbles = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            this.marbles.add(new Marble(colour));
        }
        
        //default value
        this.selectedCard = null;
    }

    public String getName() {
        return name;
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }
    
    public ArrayList<Marble> getMarbles() {
		return marbles;
	}
    
    public Card getSelectedCard() {
        return selectedCard;
    }

    public void regainMarble(Marble marble){
        marbles.add(marble);
    }

    public Marble getOneMarble() {
        if (marbles.isEmpty()) {
            return null;  // no marbles available
        }
        
        // Return the first marble in the collection
        return marbles.get(0);
    }

    public void selectCard(Card card) throws InvalidCardException { //btset el card w .contain checks if the card is in the hand or not
        if (!hand.contains(card)) {
            throw new InvalidCardException("Card not in player's hand.");
        }
        this.selectedCard = card;
    }

    public void selectMarble(Marble marble) throws InvalidMarbleException {
        if (selectedMarbles.size() >= 2) { // btcheck lw btselect aktar mn 2 marbles 3shan el exception ( no card uses more than 2 marbles )
        throw new InvalidMarbleException("You cannot select more than two marbles.");
    }
    selectedMarbles.add(marble);
    }
    
    public void deselectAll() {
        selectedCard = null;
        selectedMarbles.clear();
    }

}
