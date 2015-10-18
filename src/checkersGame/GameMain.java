package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(1);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		int threadCount =  Runtime.getRuntime().availableProcessors();
		System.out.println("AvailableProcessors="+threadCount);
		
		b.defaultStart();
		b.printBoard();
		
		Player p1 = new DumbAI(Piece.BLACK);
		Player p2 = new DumbAI(Piece.RED);
		
		ArrayList<Move> blackMoves, redMoves;
		while(true) {
			blackMoves = b.getValidMoves(Piece.BLACK, threadCount);
			if(blackMoves.size() == 0) {
				break;
			}
			b.applyMove(p1.selectMove(blackMoves, b));
			b.printBoard();
			
			redMoves = b.getValidMoves(Piece.RED, threadCount);
			if(redMoves.size() == 0) {
				break;
			}
			b.applyMove(p2.selectMove(redMoves, b));
			b.printBoard();
		}
		
		
//		ArrayList<Move> rMoves = b.getValidMoves(Piece.RED, threadCount);
//		System.out.println("Red has "+rMoves.size()+" moves.");
//		for(Move m : rMoves) {
//			System.out.print("Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
//			for(Step s : m.getSteps()) {
//				System.out.print("->("+s.getX()+","+s.getY()+")");
//			}
//			System.out.println();
//		}
	}
}
