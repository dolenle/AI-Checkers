package checkersGame;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Move {	
	private ArrayDeque<Step> steps;
	private Piece p;
	private boolean promotion = false;
	private ArrayList<Piece> captures = new ArrayList<Piece>();
	private int startX;
	private int startY;
	private int team;
	
	/**
	 * Constructor for a Move, which consists of one or more Steps for a given Piece.
	 * @param p The Piece to be moved
	 * @param s The Steps in the move
	 */
	public Move(Piece p, ArrayDeque<Step> s) {
		this.p = p;
		steps = s;
		for(Step x : steps) {
			if(x.getCapture() != null) {
				captures.add(x.getCapture());
			} else {
				break;
			}
		}
		startX = p.getX();
		startY = p.getY();
		team = p.getTeam();
	}
	
	/**
	 * Constructor for a Move, which consists of one or more Steps for a given Piece.
	 * @param p The Piece to be moved
	 * @param s The first Step in the Move
	 */
	public Move(Piece p, Step s) {
		this(p, new ArrayDeque<Step>());
		steps.add(s);
		if(s.getY() == 3.5+p.getTeam()*3.5 && !p.isKing()) {
			promotion = true;
		}
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
	
	public boolean isPromotion() {
		return promotion;
	}
	
	public void setPromotion() {
		promotion = true;
	}
	
	public ArrayList<Piece> getCaptures() {
		return captures;
	}
	
	public int getStartX() {
		return startX;
	}
	
	public int getStartY() {
		return startY;
	}
	
	public int getTeam() {
		return team;
	}
}
