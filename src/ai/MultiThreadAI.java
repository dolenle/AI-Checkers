package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Player;

/**
 * A feeble attempt at making a multithreaded AI
 * At least it works well as a CPU warmer
 */

public class MultiThreadAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
	
	private long stopTime;
			
	public MultiThreadAI(int team) {
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
	
	public MultiThreadAI(int team, int time) {
		playerTeam = team;
		timeLimit = ((long) time)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		if(validMoves.size() == 1) {
			return validMoves.get(0);
		}
		ExecutorService deadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		int depth = 4;
		long startTime = System.nanoTime();
		stopTime = startTime+timeLimit;
		Move bestMove = validMoves.get(0);
		Move lastBest = bestMove;
		int best = Integer.MIN_VALUE;
		
		while(best < Integer.MAX_VALUE) {
			ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
			best = Integer.MIN_VALUE;
			lastBest = bestMove;
			long lastTime = System.nanoTime();
			for(Move m : validMoves) {
				AIWorker worker = new AIWorker(playerTeam, depth, b, m);
				results.add(deadPool.submit(worker));
			}
			int i = 0;
			int randCount = 1;
			for(Future<Integer> r : results) {
				try {
					int score = r.get(stopTime-System.nanoTime(), TimeUnit.NANOSECONDS);
					if(score > best) {
						best = score;
						bestMove = validMoves.get(i);
						randCount = 1;
					} else if(score == best && rand.nextInt(++randCount) == 0) {
						bestMove = validMoves.get(i);
					}
					i++;
				} catch (TimeoutException te) {
					System.out.println("Search time limit reached. Reverting to depth "+--depth);
					System.out.println("Reached depth "+depth+" in "+(lastTime - startTime)/1000000000.0+"s");
					deadPool.shutdownNow();
					return lastBest;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			depth++;
		}
		
		System.out.println("Reached depth "+(depth-1)+" in "+(System.nanoTime() - startTime)/1000000000.0+"s");
		deadPool.shutdown();
		return bestMove;
		
	}
	public int getTeam() {
		return playerTeam;
	}
}
