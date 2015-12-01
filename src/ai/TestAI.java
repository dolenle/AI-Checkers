package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Piece;
import checkersGame.Player;
import checkersGame.Step;

public class TestAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
				
	public TestAI(int team) {
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
	
	public TestAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		System.out.println("AI has "+validMoves.size()+" moves:");
		int counter = 1;
		
		for(Move m : validMoves) {
			System.out.print(counter++ +". Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
			for(Step s : m.getSteps()) {
				System.out.print("->("+s.getX()+","+s.getY()+")");
			}
			if(m.isPromotion()) {
				System.out.println('*');
			} else {
				System.out.println();
			}
		}
		int depth = 2;
		int move = 1;
		Move bestMove = validMoves.get(0);
		Move lastBest = bestMove;
		int best = Integer.MIN_VALUE;
		if(validMoves.size() == 1) {
			return bestMove;
		}
		long startTime = System.nanoTime();
		stopTime = startTime+timeLimit;
		
		//while(best != Integer.MAX_VALUE) { //stop early if guaranteed win
			best = Integer.MIN_VALUE;
			lastBest = bestMove;
			long lastTime = System.nanoTime();
//			for(Move m : validMoves) {
			Move m = validMoves.get(1);
			{
				try {
					
					int score = search(depth, m, b, playerTeam);
					if(score > best || score == best && rand.nextBoolean()) {
						best = score;
						bestMove = m;
					}
					System.out.println("EVAL move"+(move++)+":"+score);
				} catch(TimeoutException te) {
					System.out.println("Search time limit reached. Reverting to depth "+--depth);
					System.out.println("Reached depth "+depth+" in "+(lastTime - startTime)/1000000000.0+"s");
					return lastBest;
				}
			}
			//depth++;
		//}
		System.out.println("Reached depth "+(depth)+" in "+(System.nanoTime() - startTime)/1000000000.0+"s");
		return bestMove;
	}
	
	private int search(int depth, Move m, Board b, int team) throws TimeoutException {
		System.out.println("Searching to depth "+depth+" for "+team);
		System.out.print("Eval Move ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
		for(Step s : m.getSteps()) {
			System.out.print("->("+s.getX()+","+s.getY()+")");
		}
		if(m.isPromotion()) {
			System.out.println('*');
		} else {
			System.out.println();
		}
		if(System.nanoTime() > stopTime) {
			throw new TimeoutException();
		}
		int[] test = b.boardToArray();
		if(depth == 0) {
			int score = evaluate(m, b.applyMove(m));
			//b.printBoard();
			b.undoMove(m);
			int[] test2 = b.boardToArray();
			if(!Arrays.equals(test, test2)) {
				System.out.println("ERROR IN UNDOMOVE!!!");
			}
//			System.out.println(" Heuristic = "+score);
			return score;
		}

		b.applyMove(m);
		ArrayList<Move> branches = b.getValidMovesSingleThread(-team);
				
		int value;// = (Integer.MIN_VALUE+1)*team*playerTeam;
		if(team == playerTeam) {
			value = Integer.MIN_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				int[] test2 = b.boardToArray();
				if(!Arrays.equals(test, test2)) {
					System.out.println("ERROR IN UNDOMOVE!!!");
				}
//				System.out.println(" Heuristic = "+Integer.MAX_VALUE);
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
			System.out.println("min");
			value = Integer.MAX_VALUE;
			if(branches.size() == 0) {
				b.undoMove(m);
				int[] test2 = b.boardToArray();
				if(!Arrays.equals(test, test2)) {
					System.out.println("ERROR IN UNDOMOVE!!!");
				}
				System.out.println(" Heuristic = "+Integer.MIN_VALUE);
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
			System.out.println("min2");
		}
		b.undoMove(m);
		int[] test2 = b.boardToArray();
		if(!Arrays.equals(test, test2)) {
			System.out.println("ERROR IN UNDOMOVE!!!");
		}
		System.out.println(" Heuristic = "+value);
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
