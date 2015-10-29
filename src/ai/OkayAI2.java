package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

public class OkayAI2 implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
				
	public OkayAI2(int team) {
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
	
	public OkayAI2(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 3;
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
			lastBest = bestMove;
			if(playerTeam == Piece.RED) {
				int best = Integer.MIN_VALUE;
				for(Move m : validMoves) {
					int score = max(depth, m, b);
					if(score > best || score == best && rand.nextBoolean()) {
						best = score;
						bestMove = m;
					}
				}
			} else {
				int best = Integer.MAX_VALUE;
				for(Move m : validMoves) {
					int score = min(depth, m, b);
					if(score < best || score == best && rand.nextBoolean()) {
						best = score;
						bestMove = m;
					}
				}
			}
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
	
	
	private int max(int depth, Move m, Board b) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(Piece.BLACK);
				
		int value = Integer.MIN_VALUE;
		if(branches.size() == 0) {
			b.undoMove(m);
			return Integer.MAX_VALUE;
		}
		
		for(Move next : branches) {
			int score = min(depth-1, next, b);
			if(score > value) {
				value = score;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	private int min(int depth, Move m, Board b) {
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(Piece.RED);
				
		int value = Integer.MAX_VALUE;
		if(branches.size() == 0) {
			b.undoMove(m);
			return Integer.MIN_VALUE;
		}
		
		for(Move next : branches) {
			int score = max(depth-1, next, b);
			if(score < value) {
				value = score;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//heuristic
	public int evaluate(Move m, Board b) {
		return b.getRedPieces().size()-b.getBlackPieces().size();
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
