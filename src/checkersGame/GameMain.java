package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(3);
	static Piece[] pieceLocs = new Piece[32];
	ArrayList<Piece> pieceList = new ArrayList<Piece>();
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		System.out.println("AvailableProcessors="+Runtime.getRuntime().availableProcessors());
		b.printBoard();
	}
}
