package checkersGame;

import java.util.ArrayList;
import java.util.Scanner;

public class OkayAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
		
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
		timeLimit = seconds*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		long start = System.nanoTime();
		int depth = 1;
		Move bestMove = validMoves.get(0);
		if(validMoves.size() == 1) {
			return bestMove;
		}
		//while(System.nanoTime() - start < timeLimit) {
			int best = 0;
			for(Move m : validMoves) {
				int score = search(depth, m, b, playerTeam);
				if(score > best) {
					best = score;
					bestMove = m;
				}
			}
			//depth++;
		//}
		System.out.println("Reached depth "+depth);
		System.out.print("TestAI plays ("+bestMove.getPiece().getX()+","+bestMove.getPiece().getY()+")");
		for(Step s : bestMove.getSteps()) {
			System.out.print("->("+s.getX()+","+s.getY()+")");
		}
		System.out.println();
		return bestMove;
	}
	
	private int search(int depth, Move m, Board b, int team) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m), team);
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
		
		if(branches.size() == 0) {
			return Integer.MAX_VALUE;
		}
				
		int value = (Integer.MIN_VALUE+1)*team*playerTeam;
//		if(team == playerTeam) {
//			value = Integer.MIN_VALUE;
//		} else {
//			value = Integer.MAX_VALUE;
//		}
		for(Move next : branches) {
			int score = search(depth-1, next, b, -team);
			if(score*team > value*playerTeam) {
				value = score;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//heuristic
	public int evaluate(Move m, Board b, int team) {
		int redScore, blackScore, score;
		redScore = 4*(b.getRedPieces().size());
		redScore += 2*b.getKingCount(Piece.RED);
		blackScore = 4*(b.getBlackPieces().size());
		blackScore += 2*b.getKingCount(Piece.BLACK);
		
		if(playerTeam == Piece.RED) {
			if(blackScore == 0) {
				return Integer.MAX_VALUE;
			}
			score = (redScore*1024)/blackScore;
		} else {
			if(redScore == 0) {
				return Integer.MAX_VALUE;
			}
			score = (blackScore*1024)/redScore;
		}
		if(m.isPromotion()) {
			score += 1000;
		}
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
