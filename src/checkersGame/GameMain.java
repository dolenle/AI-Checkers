package checkersGame;

import java.util.ArrayList;

public class GameMain {
	
	static Board b = new Board(1);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		//System.out.println("AvailableProcessors="+Runtime.getRuntime().availableProcessors());
		b.defaultStart();
		b.printBoard();
	}
}
