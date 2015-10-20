package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(3);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		int threadCount =  Runtime.getRuntime().availableProcessors();
		
		b.defaultStart();
		b.printBoard();
		
		Player p1 = new HumanPlayer(Piece.BLACK);
		Player p2 = new HumanPlayer(Piece.RED);
		
		ArrayList<Move> blackMoves, redMoves;
		
//		while(true) {
//			long start = System.nanoTime();
//			blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
//			System.out.println("Single Thread Time="+(System.nanoTime()-start));
//			start = System.nanoTime();
//			blackMoves = b.getValidMoves(Piece.BLACK, threadCount);
//			System.out.println("Multi Thread Time="+(System.nanoTime()-start));
//		}
		
		while(true) {
			blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
			if(blackMoves.size() == 0) {
				System.out.println("Player 1 out of moves");
				break;
			}
			b.applyMove(p1.selectMove(blackMoves, b));
			b.printBoard();
			
			redMoves = b.getValidMovesSingleThread(Piece.RED);
			if(redMoves.size() == 0) {
				System.out.println("Player 2 out of moves");
				break;
			}
			b.applyMove(p2.selectMove(redMoves, b));
			b.printBoard();
		}
	}
}
