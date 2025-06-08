package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import engine.board.Board;
import engine.board.Cell;
import engine.board.SafeZone;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.GameException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.SplitOutOfRangeException;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.*;

@SuppressWarnings("unused")
public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
	private int currentPlayerIndex;
    private final ArrayList<Card> firePit;
    private int turn;

    public Game(String playerName) throws IOException {
        turn = 0;
        currentPlayerIndex = 0;
        firePit = new ArrayList<>();

        ArrayList<Colour> colourOrder = new ArrayList<>();
        
        colourOrder.addAll(Arrays.asList(Colour.values()));
        
        Collections.shuffle(colourOrder);
        
        this.board = new Board(colourOrder, this);
        
        Deck.loadCardPool(this.board, (GameManager)this);
        
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, colourOrder.get(0)));
        
        for (int i = 1; i < 4; i++) 
            this.players.add(new CPU("CPU " + i, colourOrder.get(i), this.board));
        
        for (int i = 0; i < 4; i++) 
            this.players.get(i).setHand(Deck.drawCards());
        
    }
    
    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getFirePit() {
        return firePit;
    }

    public void selectCard(Card card) throws InvalidCardException{
        Player currPlayer = players.get(currentPlayerIndex); // selecting player to perform method on
        currPlayer.selectCard(card);    
    }
    
    public void selectMarble(Marble marble) throws InvalidMarbleException{
        Player currPlayer = players.get(currentPlayerIndex); // selecting player to perform method on
        currPlayer.selectMarble(marble);
    }

    public void deselectAll(){
        Player currPlayer = players.get(currentPlayerIndex); // selecting player to perform method on
        currPlayer.deselectAll();
    }

    public void editSplitDistance(int splitDistance) throws SplitOutOfRangeException{
         if(splitDistance < 1 || splitDistance > 6)
            throw new SplitOutOfRangeException("Split distance must be between 1 and 6");
        this.board.setSplitDistance(splitDistance);
    }
    
    public boolean canPlayTurn(){
        Player currPlayer = players.get(currentPlayerIndex); 
        if (currPlayer.getHand().size() < turn)
            return false; 
        return true;
    }

    public void playPlayerTurn() throws GameException{ // ghaleban el katbo ay habd
        Player currPlayer = players.get(currentPlayerIndex); 
        currPlayer.play();
    }

    public void endPlayerTurn(){
        Player currPlayer = players.get(currentPlayerIndex);
        discardCurrPlayerCard(currPlayer.getSelectedCard()); // discard the selected card from the player's hand
        deselectAll();
        currentPlayerIndex = (currentPlayerIndex + 1) % 4; // increment the turn by 1 and set it to 0 if it reaches 
        if (currentPlayerIndex == 0)
            turn--; // decrement the turn by 1 if all players have played their turn (el howa currentplayerindex be 0)
        
        if (turn == 1){
            turn = 4; // khaletha tebtedi men 4 3ashan teb2a ashal wana ba3mel canPlayTurn method
            for (int i = 0 ; i < 4; i++){
                Player player = players.get(i);
                player.setHand(Deck.drawCards()); // give each player a new hand of cards
            }
        }
        if (Deck.getPoolSize() < 4)
            Deck.refillPool(firePit);
    }

    public Colour checkWin(){  
        for (int i = 0 ; i < 4 ; i++){
            SafeZone safeZone = board.getSafeZones().get(i); // gets one safezone from the list of safezones in board class
             for(int j = 0 ; j < 4 ; j++){ 
                if(safeZone.getCells().get(j).getMarble() == null) // checks if the cell in the safezone is empty
                    break; 
                else if (j == 3) // if all cells are filled with marbles
                    return safeZone.getColour();
                    // return players.get(i).getColour(); di teshtaghal sah?
            }
        } 
        return null;
    }

    public void sendHome(Marble marble){
        Player player = players.get(currentPlayerIndex);
        player.regainMarble(marble); // in the player class when it calls this method, do these methods just set the marble to null to be not removed?
    }

    public void fieldMarble() throws CannotFieldException, IllegalDestroyException{
        Player player = players.get(currentPlayerIndex);
        Marble homeZoneMarble = player.getOneMarble();
        if (homeZoneMarble == null) // method in player class that returns first marble in homezone if none available returns null
            throw new CannotFieldException("You have no marbles to field");

        int baseCellIndex = currentPlayerIndex * 25; // base cell index for the current player 
        ArrayList<Cell> track = board.getTrack();

        if (track.get(baseCellIndex).getMarble() != null)
            throw new IllegalDestroyException("base cell is occupied by another marble");
        track.get(baseCellIndex).setMarble(homeZoneMarble); // places the marble in the track        
    }

    public void discardCurrPlayerCard(Card card){
        Player player = players.get(currentPlayerIndex);
        ArrayList<Card> hand = player.getHand();  
        firePit.add(hand.remove(hand.indexOf(card))); // remove the card from the player's hand and adds it to the firepit
    }

    public void discardCard(Colour colour) throws CannotDiscardException{
        Player player = null;
        for (Player plyr : players){
            if (plyr.getColour() == colour)
                player = plyr;
        }
        ArrayList<Card> hand = player.getHand(); // get the list of cards in player's hand
        if (hand.size() == 0) // get the list of cards in player's hand and see if it's empty
            throw new CannotDiscardException("Player has no cards to discard");
        
        Random random = new Random();
        int randomIndex = random.nextInt(hand.size()); // get a random index from the player's hand from 0 inlcusive to the size of the hand exclusive
        firePit.add(hand.remove(randomIndex)); // remove the card from the player's hand and adds it to the firepit
    }

    public void discardCard() throws CannotDiscardException{
        Random random = new Random();
        int randomIndex = random.nextInt(4);
        Colour colour = players.get(randomIndex).getColour(); // select a random player and gets their colour
        discardCard(colour); // calls the original method with colour as a parameter
    }

    public Colour getActivePlayerColour(){
        return players.get(currentPlayerIndex).getColour(); // gets the colour of the active player
    }

    public Colour getNextPlayerColour(){
        int nextPlayerIndex = (currentPlayerIndex + 1) % 4; // gets the next player index in the list of players
        return players.get(nextPlayerIndex).getColour(); // gets the colour of the next player
    }

    

}
