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
		while(System.nanoTime() - start < timeLimit) {
			int best = Integer.MIN_VALUE;
			for(Move m : validMoves) {
				int score = search(depth, m, b, playerTeam);
				if(score > best) {
					best = score;
					bestMove = m;
				}
			}
			depth++;
		}
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
		
		int value = Integer.MIN_VALUE;
		for(Move next : branches) {
			int score = search(depth-1, next, b, -team);
			if(score>value) {
				value = score;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//heuristic
	private int evaluate(Move m, Board b, int team) {
		int redScore, blackScore, score;
		redScore = 3*(b.getRedPieces().size()-b.getKingCount(Piece.RED));
		redScore += 5*b.getKingCount(Piece.RED);
		blackScore = 3*(b.getBlackPieces().size()-b.getKingCount(Piece.BLACK));
		blackScore += 5*b.getKingCount(Piece.BLACK);
		
		if(team == Piece.RED) {
			score = (redScore*1000)/blackScore;
		} else {
			score = (blackScore*1000)/redScore;
		}
		
		
//		if(team == Piece.RED) {
//			score = 8*b.getRedPieces().size();
//			score -= 8*b.getBlackPieces().size();
//		} else {
//			score = 8*b.getBlackPieces().size();
//			score -= 8*b.getRedPieces().size();
//		}
//		score += 8*b.getKingCount(team);
//		score += 8*m.getCaptures().size();
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
