package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

public class AlphabetAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
				
	public AlphabetAI(int team) {
		playerTeam = team;
		int seconds = 0;
		do {
			System.out.print("Please enter a time limit in seconds: ");
			try {
				seconds = input.nextInt();
			} catch (Exception e) {
				input.next();
			}
		} while(seconds < 0 || seconds > 30);
		timeLimit = ((long) seconds)*1000000000;
	}
	
	public AlphabetAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 7;
		Move bestMove = validMoves.get(0);
		Move lastBest = bestMove;
		if(validMoves.size() == 1) {
			return bestMove;
		}
		long start = System.nanoTime();
		long lastTime = 0;
		long now = start;
		while(now - start < timeLimit) {
			lastTime = now - start;
			int best = Integer.MIN_VALUE;
			lastBest = bestMove;
			int alpha = Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			for(Move m : validMoves) {
				int score = search(depth, m, b, playerTeam, alpha, beta);
				if(score > best || score == best && rand.nextBoolean()) {
					alpha = best = score;
					bestMove = m;
				}
				if(beta < best) {
					break;
				}
			}
//			int best2 = best;
//			best = Integer.MIN_VALUE;
//			for(Move m : validMoves) {
//				int score = regularSearch(depth, m, b, playerTeam);
//				if(score > best || score == best && rand.nextBoolean()) {
//					best = score;
//					bestMove = m;
//				}
//			}
//			if(best2 != best) {
//				System.out.println("AlphaBeta score mismatch!");
//				System.exit(1);
//			}
			
			now = System.nanoTime();
			depth++;
		}
		if(now - start > timeLimit) {
			System.out.println("Time limit exceeded! Reverting to depth "+--depth);
			bestMove = lastBest;
		}
		System.out.println("Reached depth "+depth+" in "+(lastTime)/1000000000.0+"s");
		return bestMove;
	}
	
	private int search(int depth, Move m, Board b, int team, int alpha, int beta) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;
		if(team == playerTeam) { //Maximizing
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				return Integer.MAX_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team, alpha, beta);
				int score2 = regularSearch(depth-1, next, b, -team);
				if(score > value) {
					alpha = value = score;
				}
				if(beta < value) {
					break;
				}
			}
		} else {
			value = Integer.MAX_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				return Integer.MIN_VALUE;
			}
			for(Move next : branches) {
				int score = search(depth-1, next, b, -team, alpha, beta);
				int score2 = regularSearch(depth-1, next, b, -team);
				if(score < value) {
					beta = value = score;
				}
				if(value < alpha) {
					break;
				}
			}
		}
		b.undoMove(m);
		return value;
	}
	
	private int regularSearch(int depth, Move m, Board b, int team) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				return Integer.MAX_VALUE;
			}
			for(Move next : branches) {
				int score = regularSearch(depth-1, next, b, -team);
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
				int score = regularSearch(depth-1, next, b, -team);
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
		if(playerTeam == Piece.RED) {
			return b.getRedPieces().size()-b.getBlackPieces().size();
		} else {
			return b.getBlackPieces().size()-b.getRedPieces().size();
	}
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
