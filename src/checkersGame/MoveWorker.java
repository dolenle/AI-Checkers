package checkersGame;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class MoveWorker implements Callable<ArrayDeque<Move>> {
	private ArrayDeque<Move> moves;
	private Piece p;
	private Piece[][] locs;
	
	private boolean[] jumped;
	
	public MoveWorker(Piece p, Piece[][] locs) {
		this.moves = new ArrayDeque<Move>(4);
		this.p = p;
		this.locs = locs;
		jumped = new boolean[Piece.getNextID()];
	}
	
	public ArrayDeque<Move> call() {
		ArrayDeque<Step> simpleMoves = getMoves(p.getX(), p.getY(), p.getTeam());
		if(simpleMoves != null) {
			for(Step s : simpleMoves) {
				moves.add(new Move(p, s));
			}
		}
		
		ArrayDeque<ArrayDeque<Step>> jumpTree = getJumpTree(jumped, p.getX(), p.getY());
		if(jumpTree != null) {
			for(ArrayDeque<Step> s : jumpTree) {
				moves.add(new Move(p, s));
			}
		}
		return moves;
	}
	
	private ArrayDeque<Step> getMoves(int x, int y, int dy) {
		int destX;
		int destY = y+dy;
		ArrayDeque<Step> validMoves = new ArrayDeque<Step>(2);
		
		if(destY >= 0 && destY <= 7) {
			for(int i=-1; i<=1; i+=2) {
				destX = x+i;
				if(destX >= 0 && destX <= 7 && locs[destY][destX] == null) {
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
		ArrayDeque<ArrayDeque<Step>> result = new ArrayDeque<ArrayDeque<Step>>(5);
		if(jumps != null) {
			for(Step s: jumps) {
				ArrayDeque<ArrayDeque<Step>> next = getJumpTree(history.clone(), s.getX(), s.getY());
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
		ArrayDeque<Step> validJumps = new ArrayDeque<Step>(5);
		
		if(destY >= 0 && destY <= 7) {
			for(int i=-2; i<=2; i+=4) { //please unroll this
				destX = x+i;
				if(destX >= 0 && destX <= 7) {
					Piece target = locs[(destY+y)/2][(destX+x)/2];
					if(locs[destY][destX] == null && canCapture(history, target)) {
						validJumps.add(new Step(destX, destY, target));
						history[target.getID()] = true;
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