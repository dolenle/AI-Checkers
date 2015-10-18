package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(3);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		int threadCount =  Runtime.getRuntime().availableProcessors();
		System.out.println("AvailableProcessors="+threadCount);
		
		b.defaultStart();
		b.addPiece(Piece.BLACK,4,4);
		b.printBoard();
		
		ArrayList<Move> rMoves = b.getValidMoves(Piece.BLACK, threadCount);
		System.out.println("Black has "+rMoves.size()+" moves.");
		for(Move m : rMoves) {
			System.out.print("Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
			for(Step s : m.getSteps()) {
				System.out.print("->("+s.getX()+","+s.getY()+")");
			}
			System.out.println();
		}
		rMoves = b.getValidMoves(Piece.RED, threadCount);
		System.out.println("Red has "+rMoves.size()+" moves.");
		for(Move m : rMoves) {
			System.out.print("Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
			for(Step s : m.getSteps()) {
				System.out.print("->("+s.getX()+","+s.getY()+")");
			}
			System.out.println();
		}
	}
}
