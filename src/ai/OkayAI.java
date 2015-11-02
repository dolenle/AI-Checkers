package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

public class OkayAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
				
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
		} while(seconds <= 0);
		timeLimit = ((long) seconds)*1000000000;
	}
	
	public OkayAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 3;
		Move bestMove = validMoves.get(0);
		Move lastBest = bestMove;
		int best = Integer.MIN_VALUE;
		if(validMoves.size() == 1) {
			return bestMove;
		}
		long startTime = System.nanoTime();
		stopTime = startTime+timeLimit;
		
		while(best != Integer.MAX_VALUE) { //stop early if guaranteed win
			best = Integer.MIN_VALUE;
			lastBest = bestMove;
			long lastTime = System.nanoTime();
			for(Move m : validMoves) {
				try {
					int score = search(depth, m, b, playerTeam);
					if(score > best || score == best && rand.nextBoolean()) {
						best = score;
						bestMove = m;
					}
				} catch(TimeoutException te) {
					System.out.println("Search time limit reached. Reverting to depth "+--depth);
					System.out.println("Reached depth "+depth+" in "+(lastTime - startTime)/1000000000.0+"s");
					return lastBest;
				}
			}
			depth++;
		}
		System.out.println("Reached depth "+(depth-1)+" in "+(System.nanoTime() - startTime)/1000000000.0+"s");
		return bestMove;
	}
	
	private int search(int depth, Move m, Board b, int team) throws TimeoutException {
		if(System.nanoTime() > stopTime) {
			throw new TimeoutException();
		}
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			//b.printBoard();
			b.undoMove(m);
			return score;
		}

		b.applyMove(m);
		//b.printBoard();
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;// = (Integer.MIN_VALUE+1)*team*playerTeam;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				return Integer.MAX_VALUE;
			}
			
			for(Move next : branches) {
				try {
					int score = search(depth-1, next, b, -team);
					if(score > value) {
						value = score;
					}
				} catch (TimeoutException te) {
					b.undoMove(m);
					throw te;
				}
			}
		} else {
			value = Integer.MAX_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				return Integer.MIN_VALUE;
			}
			for(Move next : branches) {
				try {
					int score = search(depth-1, next, b, -team);
					if(score < value) {
						value = score;
					}
				} catch (TimeoutException te) {
					b.undoMove(m);
					throw te;
				}
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//heuristic
	public int evaluate(Move m, Board b) {
//		int redScore, blackScore, score;
//		redScore = 3*(b.getRedPieces().size());
//		redScore += 2*b.getKingCount(Piece.RED);
//		blackScore = 3*(b.getBlackPieces().size());
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
			return b.getRedPieces().size()-b.getBlackPieces().size();
		} else {
			return b.getBlackPieces().size()-b.getRedPieces().size();
		}
	}
	
	public int getTeam() {
		return playerTeam;
	}
	
}
