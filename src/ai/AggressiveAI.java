package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;

public class AggressiveAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
	
	private int[] aggressiveWeights = {	0, 0, 0, 0, 0, 0, 0, 0,
										0, 1, 1, 1, 1, 1, 1, 0,
										0, 1, 2, 4, 4, 2, 1, 0,
										2, 4, 8, 8, 8, 8, 4, 2,
										2, 4, 8, 8, 8, 8, 4, 2,
										0, 1, 2, 4, 4, 2, 1, 0,
										0, 1, 1, 1, 1, 1, 1, 0,
										0, 0, 0, 0, 0, 0, 0, 0 };
	
	public AggressiveAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
		int opposite, home;
		if(team == Piece.RED) {
			opposite = 0;
			home = 56;
		} else {
			home = 0;
			opposite = 56;
		}
		for(int i=0; i<8; i++) {
			aggressiveWeights[opposite+i] = 8;
			aggressiveWeights[opposite+i-team*8]*=4;
			aggressiveWeights[home+i] = -1;
		}
	}
	
	public AggressiveAI(int team) {
		this(team, 1);
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
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		int depth = 1;
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
			
			for(Move m : validMoves) {
				try {
					int score = search(depth, m, b, playerTeam, alpha, beta);
					if(score > best || score == best && rand.nextBoolean()) {
						alpha = best = score;
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
						alpha = value = score;
					}
					if(beta < value) {
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
		int score;
		ArrayList<Piece> myPieces, opponentPieces;
		if(playerTeam == Piece.RED) {
			myPieces = b.getRedPieces();
			opponentPieces = b.getBlackPieces();
		} else {
			opponentPieces = b.getRedPieces();
			myPieces = b.getBlackPieces();
		}
		score = (myPieces.size()-opponentPieces.size())*2097152;
		score += (b.getKingCount(playerTeam) - b.getKingCount(-playerTeam))*1024;
		score += m.getCaptures().size()*m.getPiece().getTeam()*playerTeam;
		for(Piece p : myPieces) {
			score += 64*aggressiveWeights[p.getY()*8+p.getX()];
			if(p.isKing()) {
				score += 16*aggressiveWeights[p.getY()*8+p.getX()];
			}
			if(opponentPieces.size() <= 5) {
				for(Piece o : opponentPieces) {
					score += 512/(Math.abs(p.getX()-o.getX())+Math.abs(p.getY()-o.getY()));
				}
			}
		}
		return score;
	}
	
	public int getTeam() {
		return playerTeam;
	}
}
