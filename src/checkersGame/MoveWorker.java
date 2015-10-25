package checkersGame;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class MoveWorker implements Callable<ArrayDeque<Move>> {
	private ArrayDeque<Move> moves;
	private Piece p;
	private Piece[] locs;
	
	private boolean[] jumped;
	
	/**
	 * Creates a new MoveWorker to compute the valid moves for a give Piece
	 * @param p The Piece to be moved
	 * @param locs Location matrix for the Board
	 */
	public MoveWorker(Piece p, Piece[] locs) {
		this.moves = new ArrayDeque<Move>();
		this.p = p;
		this.locs = locs;
		jumped = new boolean[Piece.getNextID()];
	}
	
	/**
	 * Generates list of valid moves for the Piece
	 */
	public ArrayDeque<Move> call() {
		locs[p.getY()*8 + p.getX()] = null;
		ArrayDeque<ArrayDeque<Step>> jumpTree = getJumpTree(jumped, p.getX(), p.getY());
		if(jumpTree != null) {
			for(ArrayDeque<Step> s : jumpTree) {
				moves.add(new Move(p, s));
				if(s.peekLast().getY() == 3.5+p.getTeam()*3.5 && !p.isKing()) {
					moves.peekLast().setPromotion();
				}
			}
		} else {
			ArrayDeque<Step> simpleMoves = getMoves(p.getX(), p.getY(), p.getTeam());
			if(simpleMoves != null) {
				for(Step s : simpleMoves) {
					moves.add(new Move(p, s));
				}
			}
			if(p.isKing()) {
				simpleMoves = getMoves(p.getX(), p.getY(), -p.getTeam());
				if(simpleMoves != null) {
					for(Step s : simpleMoves) {
						moves.add(new Move(p, s));
					}
				}
			}
		}
		locs[p.getY()*8 + p.getX()] = p; //put it back
		return moves;
	}
	
	private ArrayDeque<Step> getMoves(int x, int y, int dy) {
		int destX;
		int destY = y+dy;
		ArrayDeque<Step> validMoves = new ArrayDeque<Step>();
		
		if(destY >= 0 && destY <= 7) {
			for(int i=-1; i<=1; i+=2) {
				destX = x+i;
				if(destX >= 0 && destX <= 7 && locs[destY*8 + destX] == null) {
					validMoves.add(new Step(destX, destY));
				}
			}
		}
		if(validMoves.size() > 0) {
			return validMoves;
		} else {
			return null;
		}
	}
	
	public ArrayDeque<ArrayDeque<Step>> getJumpTree(boolean[] history, int x, int y) {
		ArrayDeque<Step> jumps = getJumps(history, x, y);
		ArrayDeque<ArrayDeque<Step>> result = new ArrayDeque<ArrayDeque<Step>>();
		if(jumps != null) {
			for(Step s: jumps) {
				boolean historyCopy[] = history.clone();
				historyCopy[s.getCapture().getID()] = true;
				ArrayDeque<ArrayDeque<Step>> next = getJumpTree(historyCopy, s.getX(), s.getY());
				if(next != null) {
					for(ArrayDeque<Step> branch : next) {
						branch.push(s);
						result.add(new ArrayDeque<Step>(branch));
					}
				} else {
					result.add(new ArrayDeque<Step>(Arrays.asList(s)));
				}
			}
		} else {
			return null;
		}
		return result;
	}
		
	private ArrayDeque<Step> getJumps(boolean[] history, int x, int y) {
		int destX;
		int destY = y+p.getTeam()*2;
		ArrayDeque<Step> validJumps = new ArrayDeque<Step>();
		boolean[] history2 = null;

		if(destY >= 0 && destY <= 7) {
			for(int i=-2; i<=2; i+=4) { //please unroll this
				destX = x+i;
				if(destX >= 0 && destX <= 7) {
					Piece target = locs[(destY+y)/2*8 + (destX+x)/2];
					if(locs[destY*8 + destX] == null && canCapture(history, target)) {
						validJumps.add(new Step(destX, destY, target));
					}
				}
			}
		}
		if(p.isKing()) { //repeat for backwards jump
			destY = y-p.getTeam()*2;
			if(destY >= 0 && destY <= 7) {
				for(int i=-2; i<=2; i+=4) { //please unroll this
					destX = x+i;
					if(destX >= 0 && destX <= 7) {
						Piece target = locs[(destY+y)/2*8 + (destX+x)/2];
						if(locs[destY*8 + destX] == null && canCapture(history, target)) {
							validJumps.add(new Step(destX, destY, target));
						}
					}
				}
			}
		}
		if(validJumps.size() > 0) {
			return validJumps;
		} else {
			return null;
		}
		
	}
	
	private boolean canCapture(boolean[] history, Piece target) {
		return target != null && p.getTeam() != target.getTeam() && !history[target.getID()];
	}
}