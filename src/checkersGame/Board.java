package checkersGame;

public class Board {
	private int size;
	private int cols;
	private int thresh1;
	private int thresh2;
	private String bk = (char)27 + "[40m  " + (char)27 + "[0m";
	private String wht = (char)27 + "[47m  " + (char)27 + "[0m";
	
	public Board(int squareSize) {
		if (squareSize%2 == 0) {
			System.err.println("Square size must be odd.");
			System.exit(1);
		}
		this.size = squareSize;
		cols = squareSize*8;
		thresh1 = size/2;
		thresh2 = size*2;
	}
	
	public void printBoard() {
		for(int i=0; i<cols; i++) {
			if(i%size == thresh1) {
				System.out.print(' ');
				System.out.print(i/size);
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
		for(int j=0; j<cols; j++) {
			if(j%size == thresh1) {
				System.out.print(j/size);
			} else {
				System.out.print(' ');
			}
			int offset = j/size*size;
			for(int i=offset; i<cols+offset; i++) {
				if(i%thresh2>=size) {
					System.out.print(wht);
				} else {
					System.out.print(bk);
				}
			}
			System.out.println();
		}
	}
}
