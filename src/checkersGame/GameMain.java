package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(3);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		
		//b.defaultStart();
		
		b.addPiece(Piece.BLACK, 1, 1, true);
		b.addPiece(Piece.RED,2,2);
		b.addPiece(Piece.RED,4,2);
		b.addPiece(Piece.RED,2,4);
		b.addPiece(Piece.RED,4,4);
		b.addPiece(Piece.RED,6,4);
		b.addPiece(Piece.RED,4,6);
		b.addPiece(Piece.RED,6,2);
		b.addPiece(Piece.RED,6,6);
		b.addPiece(Piece.RED,2,6);
		
		Player p1 = new OkayAI(Piece.BLACK);
		Player p2 = new HumanPlayer(Piece.RED);
		
		ArrayList<Move> blackMoves, redMoves;
		
		while(true) {
			b.printBoard();
			blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
			if(blackMoves.size() == 0) {
				System.out.println("GREEN out of moves");
				break;
			}
			Move m = p1.selectMove(blackMoves, b);
			b.applyMove(m);
			b.printBoard();
			//System.out.println("Player 1 Heuristic: "+p1.evaluate(m, b, p1.getTeam()));
			
			redMoves = b.getValidMovesSingleThread(Piece.RED);
			if(redMoves.size() == 0) {
				System.out.println("RED out of moves");
				break;
			}
			m = p2.selectMove(redMoves, b);
			b.applyMove(m);
			//System.out.println("Player 2 Heuristic: "+p1.evaluate(m, b, p2.getTeam()));
		}
		b.printBoard();
	}
}
