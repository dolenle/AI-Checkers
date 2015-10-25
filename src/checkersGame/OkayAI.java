package checkersGame;

import java.util.ArrayList;
import java.util.Scanner;

public class OkayAI implements Player {
	private int timeLimit;
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
		for(Move m : validMoves) {
			evaluate(m, b.clone(), team);
		}
		return null;
	}
	
	private int evaluate(Move m, Board b, int team) {
		int score = 0;
		b.applyMove(m);
		if(team == Piece.RED) {
			score += b.getRedPieces().size();
		} else {
			score += b.getBlackPieces().size();
		}
		score += b.getKingCount(team);
		score += m.getCaptures().size();
		return score;
	}
	
	public int getTeam() {
		return team;
	}
}
