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
		int depth = 0;
		Move bestMove = validMoves.get(0);
		while(System.nanoTime() - start < timeLimit) {
			int best = Integer.MIN_VALUE;
			for(Move m : validMoves) {
				int score = search(depth, m, b.clone(), playerTeam);
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
			return evaluate(m, b.applyMove(m), team);
		}
		System.out.print("Examining move ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
		for(Step s : m.getSteps()) {
			System.out.print("->("+s.getX()+","+s.getY()+")");
		}
		System.out.println();
		b.applyMove(m);
		b.printBoard();
//		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
//		
//		System.out.println((team==playerTeam?"player":"opponent")+" has "+branches.size()+" moves:");		
//		for(Move c : branches) {
//			System.out.print("Piece at ("+c.getPiece().getX()+","+c.getPiece().getY()+")");
//			for(Step s : c.getSteps()) {
//				System.out.print("->("+s.getX()+","+s.getY()+")");
//			}
//			System.out.println();
//		}
//		
//		int value = Integer.MIN_VALUE;
//		for(Move next : branches) {
//			int score = -search(depth-1, next, b.clone(), -team);
//			if(score > value) {
//				value = score;
//			}
//		}
//		return value;
		return 0;
	}
	
	//heuristic
	private int evaluate(Move m, Board b, int team) {
		int score = 10;
		if(team == Piece.RED) {
			score += b.getRedPieces().size();
		} else {
			score += b.getBlackPieces().size();
		}
		score += b.getKingCount(team);
		score += m.getCaptures().size();
		score -= b.getKingCount(-team);
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
