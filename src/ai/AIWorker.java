package ai;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;

public class AIWorker implements Callable<Integer> {
	
	private int playerTeam;
	private int maxDepth;
	private Board initialBoard;
	private Move initialMove;
	
	public AIWorker(int team, int maxDepth, Board initialBoard, Move initialMove) {
		playerTeam = team;
		this.maxDepth = maxDepth;
		this.initialBoard = initialBoard;
		this.initialMove = initialMove;
	}
	
	public Integer call() {
		return new Integer(search(maxDepth, initialMove, initialBoard.clone(), playerTeam));
	}
	
	private int search(int depth, Move m, Board b, int team) {
		if(depth == 0) {
			ArrayList<Piece> cap = b.applyAnonymousMove(m);
			int score = evaluate(m, b);
			b.isConsistent();
			b.undoAnonymousMove(m, cap);
			return score;
		}

		ArrayList<Piece> cap = b.applyAnonymousMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				return Integer.MAX_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team);
				if(score > value) {
					value = score;
				}
			}
		} else {
			value = Integer.MAX_VALUE;
			if(branches.size() == 0) {
				return Integer.MIN_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team);
				if(score < value) {
					value = score;
				}
			}
		}
		b.undoAnonymousMove(m, cap);
		return value;
	}
	
	public int evaluate(Move m, Board b) {
		if(playerTeam == Piece.RED) {
			return b.getRedPieces().size()+b.getKingCount(Piece.RED)-b.getBlackPieces().size()+(m.getCaptures().size()*m.getPiece().getTeam()*playerTeam)-b.getKingCount(Piece.BLACK);
		} else {
			return b.getBlackPieces().size()+b.getKingCount(Piece.BLACK)-b.getRedPieces().size()+(m.getCaptures().size()*m.getPiece().getTeam()*playerTeam)-b.getKingCount(Piece.RED);
		}
	}
}
