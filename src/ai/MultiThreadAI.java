package ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Player;

/**
 * A feeble attempt at making a multithreaded version of OkayAI
 * At least it works well as a CPU warmer
 */

public class MultiThreadAI implements Player {
	private long timeLimit;
	private int playerTeam;
	private Scanner input = new Scanner(System.in);
	private Random rand = new Random();
			
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
		} while(seconds < 0 || seconds > 30);
		timeLimit = ((long) seconds)*1000000000;
	}
	
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		if(validMoves.size() == 1) {
			return validMoves.get(0);
		}
		ExecutorService deadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		int depth = 1;
		long start = System.nanoTime();
		Move bestMove = validMoves.get(0);
		while(System.nanoTime() - start < timeLimit) {
			ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
			int best = Integer.MIN_VALUE;
			for(Move m : validMoves) {
				AIWorker worker = new AIWorker(playerTeam, depth, b, m);
				results.add(deadPool.submit(worker));
			}
			int i = 0;
			for(Future<Integer> r : results) {
				try {
					int score = r.get();
					if(score > best || score == best && rand.nextBoolean()) {
						best = score;
						bestMove = validMoves.get(i);
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			depth++;
		}
		long elapsed = System.nanoTime() - start;
		System.out.println("Reached depth "+depth+" in "+elapsed/1000000000.0+"s");
		deadPool.shutdown();
		return bestMove;
		
	}
	public int getTeam() {
		return playerTeam;
	}
}
