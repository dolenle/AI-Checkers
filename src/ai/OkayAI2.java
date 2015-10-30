package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

/**
 * Is a hardcoded heuristic more efficient?
 *
 */
public class OkayAI2 implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private int guaranteedWin;
	private long stopTime;
				
	public OkayAI2(int team) {
		playerTeam = team;
		if(team == Piece.RED) {
			guaranteedWin = Integer.MAX_VALUE;
		} else {
			guaranteedWin = Integer.MIN_VALUE;
		}
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
		if(team == Piece.RED) {
			guaranteedWin = Integer.MAX_VALUE;
		} else {
			guaranteedWin = Integer.MIN_VALUE;
		}
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 1;
		Move bestMove = validMoves.get(0);
		Move lastBest = bestMove;
		int best = 0;
		if(validMoves.size() == 1) {
			return bestMove;
		}
		long startTime = System.nanoTime();
		stopTime = startTime+timeLimit;
		
		
		while(best != guaranteedWin) { //stop early if guaranteed win
			lastBest = bestMove;
			long lastTime = System.nanoTime();
			try {
				if(playerTeam == Piece.RED) {
					for(Move m : validMoves) {
						best = Integer.MIN_VALUE;
						int score = max(depth, m, b);
						if(score > best || score == best && rand.nextBoolean()) {
							best = score;
							bestMove = m;
						}
					}
				} else {
					for(Move m : validMoves) {
						best = Integer.MAX_VALUE;
						int score = min(depth, m, b);
						if(score < best || score == best && rand.nextBoolean()) {
							best = score;
							bestMove = m;
						}
					}
				}
			} catch(TimeoutException te) {
				System.out.println("Search time limit reached. Reverting to depth "+--depth);
				System.out.println("Reached depth "+depth+" in "+(lastTime - startTime)/1000000000.0+"s");
				return lastBest;
			}
			depth++;
		}
		System.out.println("Reached depth "+(depth-1)+" in "+(System.nanoTime() - startTime)/1000000000.0+"s");
		return bestMove;
	}
	
	
	private int max(int depth, Move m, Board b) throws TimeoutException {
		if(System.nanoTime() > stopTime) {
			throw new TimeoutException();
		}
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
			try {
				int score = min(depth-1, next, b);
				if(score > value) {
					value = score;
				}
			} catch (TimeoutException te) {
				b.undoMove(m);
				throw te;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	private int min(int depth, Move m, Board b) throws TimeoutException{
		if(System.nanoTime() > stopTime) {
			throw new TimeoutException();
		}
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
			try {
				int score = max(depth-1, next, b);
				if(score < value) {
					value = score;
				}
			} catch (TimeoutException te) {
				b.undoMove(m);
				throw te;
			}
		}
		b.undoMove(m);
		return value;
	}
	
	//fixed heuristic
	public int evaluate(Move m, Board b) {
		return b.getRedPieces().size()-b.getBlackPieces().size();
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
