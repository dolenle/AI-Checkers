package ai;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;
import checkersGame.Step;

public class SableAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
	
	private int opponentHome = 0;
	private int myHome = 7;
	
	private int[] kingWeights = {	0, 32, 8, 8, 8, 8, 8, 8,
									32, 4, 0, 0, 0, 0, 4, 8,
									8, 0, 0, 0, 0, 0, 0, 8,
									8, 0, 0, 16, 16, 0, 0, 8,
									8, 0, 0, 16, 16, 0, 0, 8,
									8, 0, 0, 0, 0, 0, 0, 8,
									8, 4, 0, 0, 0, 0, 4, 32,
									8, 8, 8, 8, 8, 8, 32, 0	};
				
	public SableAI(int team) {
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
		if(team == Piece.BLACK) {
			opponentHome = 7;
			myHome = 0;
		}
	}
	
	public SableAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
		if(team == Piece.BLACK) {
			opponentHome = 7;
			myHome = 0;
		}
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 4;
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
				
		int value = 0;
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
						alpha = value = score;
					}
					if(value > beta) {
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
						beta = value = score;
					}
					if(value < alpha) {
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
		LinkedHashMap<Integer, Piece> myPieces, opponentPieces;
		int myPieceCount, opponentPieceCount;
		if(playerTeam == Piece.RED) {
			myPieces = b.getRedPieces();
			opponentPieces = b.getBlackPieces();
		} else {
			opponentPieces = b.getRedPieces();
			myPieces = b.getBlackPieces();
		}
		myPieceCount = myPieces.size();
		opponentPieceCount = opponentPieces.size();
		
		int score = (3*(myPieceCount-opponentPieceCount)+2*(b.getKingCount(playerTeam)-b.getKingCount(-playerTeam)))*2097152; //base value
		for(Piece p : myPieces.values()) {
			if(!p.isKing()) {
				score += 16384*(playerTeam*(p.getY()-myHome)); //distance of my pawns to opposite side
			} else {
				score += kingWeights[p.getY()*8+p.getX()];
			}
		}
		for(Piece p : opponentPieces.values()) {
			if(!p.isKing()) {
				score -= 16384*(playerTeam*(p.getY()-opponentHome)); //distance of opponent's pawns to my side
			} else {
				score -= kingWeights[p.getY()*8+p.getX()];
			}
		}
		if(myPieceCount > opponentPieceCount) {
			score += 2048*24/(myPieceCount+opponentPieceCount);
		} else if(myPieceCount < opponentPieceCount) {
			score -= 2048*24/(myPieceCount+opponentPieceCount);
		}
		
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}