package checkersGame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class OkayAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
			
	public OkayAI(int team) {
		playerTeam = team;
		int seconds = 0;
		do {
			System.out.print("Please enter a time limit in seconds: ");
			try {
				seconds = input.nextInt();
			} catch (Exception e) {
				input.next();
			}
		} while(seconds < 3 || seconds > 30);
		timeLimit = ((long) seconds)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 1;
		Move bestMove = validMoves.get(0);
		if(validMoves.size() == 1) {
			return bestMove;
		}
		long start = System.nanoTime();
		while(System.nanoTime() - start < timeLimit) {
			int best = 0;
			for(Move m : validMoves) {
				int score = search(depth, m, b, playerTeam);
				if(score > best || score == best && rand.nextBoolean()) {
					best = score;
					bestMove = m;
				}
			}
			depth++;
		}
		System.out.println("Reached depth "+depth);
		return bestMove;
	}
	
	private int search(int depth, Move m, Board b, int team) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;// = (Integer.MIN_VALUE+1)*team*playerTeam;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
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
				b.undoMove(m);
				return Integer.MIN_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team);
				if(score < value) {
					value = score;
				}
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//heuristic
	public int evaluate(Move m, Board b) {
//		int redScore, blackScore, score;
//		redScore = 4*(b.getRedPieces().size());
//		redScore += 2*b.getKingCount(Piece.RED);
//		blackScore = 4*(b.getBlackPieces().size());
//		blackScore += 2*b.getKingCount(Piece.BLACK);
//		
//		if(playerTeam == Piece.RED) {
//			if(blackScore == 0) {
//				return Integer.MAX_VALUE;
//			}
//			score = (redScore*1024)/blackScore;
//		} else {
//			if(redScore == 0) {
//				return Integer.MAX_VALUE;
//			}
//			score = (blackScore*1024)/redScore;
//		}
//		if(m.isPromotion()) {
//			score += 1000;
//		}
//		return score;
		if(playerTeam == Piece.RED) {
			return b.getRedPieces().size()-b.getBlackPieces().size()+(m.getCaptures().size()*m.getPiece().getTeam()*playerTeam);
		} else {
			return b.getBlackPieces().size()-b.getRedPieces().size()+(m.getCaptures().size()*m.getPiece().getTeam()*playerTeam);
		}
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
