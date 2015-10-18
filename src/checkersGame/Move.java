package checkersGame;

import java.util.ArrayList;

public class Move {	
	private ArrayList<Step> steps = new ArrayList<Step>(4);
	private Piece p;
	
	/**
	 * Constructor for a Move, which consists of one or more Steps for a given Piece.
	 * @param p The Piece to be moved
	 * @param s The first Step in the Move
	 */
	public Move(Piece p, Step s) {
		this.p = p;
		steps.add(s);
	}
	
	public void addStep(Step s) {
		steps.add(s);
	}
	
	public Piece getPiece() {
		return p;
	}
	
	public ArrayList<Step> getSteps() {
		return steps;
	}
}
