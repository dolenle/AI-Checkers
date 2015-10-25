package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(3);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		int threadCount =  Runtime.getRuntime().availableProcessors();
		
		//b.defaultStart();
		b.addPiece(Piece.RED, 3, 3, true);
		b.addPiece(Piece.BLACK,2,2);
		b.addPiece(Piece.BLACK,4,2);
		b.addPiece(Piece.BLACK,2,4);
		b.addPiece(Piece.BLACK,4,4);
		b.addPiece(Piece.BLACK,6,4);
		b.addPiece(Piece.BLACK,4,6);
		b.addPiece(Piece.BLACK,6,2);
		b.addPiece(Piece.BLACK,6,6);
		b.addPiece(Piece.BLACK,2,6);
		b.printBoard();
		
		Player p1 = new HumanPlayer(Piece.RED);
		Player p2 = new RandomAI(Piece.BLACK);
		
		ArrayList<Move> blackMoves, redMoves;
		
		while(true) {
//			blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
//			if(blackMoves.size() == 0) {
//				System.out.println("Player 1 out of moves");
//				break;
//			}
//			b.applyMove(p1.selectMove(blackMoves, b));
//			b.printBoard();
//			redMoves = b.getValidMovesSingleThread(Piece.RED);
//			if(redMoves.size() == 0) {
//				System.out.println("Player 2 out of moves");
//				break;
//			}
//			b.applyMove(p2.selectMove(redMoves, b));
			redMoves = b.getValidMovesSingleThread(Piece.RED);
			b.applyMove(p1.selectMove(redMoves, b));
			b.printBoard();
		}
	}
}
