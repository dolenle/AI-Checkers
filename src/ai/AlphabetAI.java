package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

public class AlphabetAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
				
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
		} while(seconds <= 0);
		timeLimit = ((long) seconds)*1000000000;
	}
	
	public AlphabetAI(int team, int time) {
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
		stopTime = startTime + timeLimit;
		
		while(best != Integer.MAX_VALUE) {
			best = Integer.MIN_VALUE;
			lastBest = bestMove;
			long lastTime = System.nanoTime();
			
			int alpha = Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			
			int i=1;
			
			for(Move m : validMoves) {
				try {
					int score = search(depth, m, b, playerTeam, alpha, beta);
					if(score > best) {
						alpha = best = score;
						bestMove = m;
						i = 1;
					} else if(score == best && rand.nextInt(++i)==0) { //uniform randomness
						bestMove = m;
					}
					if(beta < best) {
						break;
					}
				}  catch (TimeoutException te) {
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
	
	private int search(int depth, Move m, Board b, int team, int alpha, int beta) throws TimeoutException {
		if(System.nanoTime() > stopTime) {
			throw new TimeoutException();
		}
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
				try {
					int score = search(depth-1, next, b, -team, alpha, beta);
					if(score > value) {
						value = score;
					}
					if(value > alpha) {
						alpha = value;
					}
					if(beta <= alpha) {
						break;
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
					int score = search(depth-1, next, b, -team, alpha, beta);
					if(score < value) {
						value = score;
					}
					if(value < beta) {
						beta = value;
					}
					if(beta <= alpha) {
						break;
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
		int score;
		if(playerTeam == Piece.RED) {
			score = 3*(b.getRedPieces().size()-b.getBlackPieces().size());
		} else {
			score = 3*(b.getBlackPieces().size()-b.getRedPieces().size());
		}
		score += 2*(b.getKingCount(playerTeam)-b.getKingCount(-playerTeam));
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}