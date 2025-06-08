package engine.board;

import java.util.ArrayList;

import model.Colour;

public class SafeZone {
    private final Colour colour;
    private final ArrayList<Cell> cells;

    public SafeZone(Colour colour) {
        this.colour = colour;
        this.cells = new ArrayList<>();
        for (int i = 0; i < 4; i++) 
            this.cells.add(new Cell(CellType.SAFE));
    }

    public Colour getColour() {
        return this.colour;
    }

    public ArrayList<Cell> getCells() {
        return this.cells;
    }
// uses the method isOccupied and getMarble in the Cell class
    public boolean isFull() {
    	    for (int i = 0; i < cells.size(); i++) {
    	        if (cells.get(i).getMarble() == null) {
    	            return false;
    	        }
    	    }
    	    return true;
    	}

}
