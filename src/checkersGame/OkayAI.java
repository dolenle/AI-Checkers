package checkersGame;

import java.util.ArrayList;
import java.util.Scanner;

public class OkayAI implements Player {
	private long timeLimit;
	private int team;
	private Scanner input = new Scanner(System.in);
	
	private int alpha = Integer.MIN_VALUE;
	private int beta = Integer.MAX_VALUE;
		
	public OkayAI(int team) {
		this.team = team;
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
				int score = search(depth, m, b.clone(), team);
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
		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
		int value = Integer.MIN_VALUE;
		for(Move next : branches) {
			int score = -search(depth-1, next, b.clone(), -team);
			if(score > value) {
				value = score;
			}
		}
		return value;
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
		return team;
	}
}
