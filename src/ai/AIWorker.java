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
		return new Integer(search(maxDepth, initialMove, initialBoard.clone(), playerTeam, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	private int search(int depth, Move m, Board b, int team, int alpha, int beta) {
		if(depth == 0) {
			ArrayList<Piece> cap = b.applyAnonymousMove(m);
			int score = evaluate(m, b);
			b.undoAnonymousMove(m, cap);
			return score;
		}

		ArrayList<Piece> cap = b.applyAnonymousMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoAnonymousMove(m, cap);
				return Integer.MAX_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team, alpha, beta);
				if(score > value) {
					value = score;
				}
				if(value > alpha) {
					alpha = value;
				}
				if(beta <= alpha) {
					break;
				}
			}
		} else {
			value = Integer.MAX_VALUE;
			if(branches.size() == 0) {
				b.undoAnonymousMove(m, cap);
				return Integer.MIN_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team, alpha, beta);
				if(score < value) {
					value = score;
				}
				if(value < score) {
					beta = value;
				}
				if(beta <= alpha) {
					break;
				}
			}
		}
		b.undoAnonymousMove(m, cap);
		return value;
	}
	
	public int evaluate(Move m, Board b) {
		int score;
		if(playerTeam == Piece.RED) {
			score = 3*(b.getRedPieces().size()-b.getBlackPieces().size());
		} else {
			score = 3*(b.getBlackPieces().size()-b.getRedPieces().size());
		}
		score += 2*(b.getKingCount(playerTeam)-b.getKingCount(-playerTeam));
		return score;
	}
}
