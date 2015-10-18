package checkersGame;

import java.util.ArrayDeque;

public class Move {	
	private ArrayDeque<Step> steps;
	private Piece p;
	
	/**
	 * Constructor for a Move, which consists of one or more Steps for a given Piece.
	 * @param p The Piece to be moved
	 * @param s The first Step in the Move
	 */
	public Move(Piece p, Step s) {
		this.p = p;
		steps = new ArrayDeque<Step>(1);
		steps.add(s);
	}
	
	public Move(Piece p, ArrayDeque<Step> s) {
		this.p = p;
		steps = new ArrayDeque<Step>(s);
	}
	
	public void addStep(Step s) {
		steps.add(s);
	}
	
	public Piece getPiece() {
		return p;
	}
	
	public ArrayDeque<Step> getSteps() {
		return steps;
	}
}
