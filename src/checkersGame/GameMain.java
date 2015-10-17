package checkersGame;

public class GameMain {
	
	static Board b = new Board(3);
	
	//To run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		System.out.println("AvailableProcessors="+Runtime.getRuntime().availableProcessors());
		b.printBoard();
	}
}
