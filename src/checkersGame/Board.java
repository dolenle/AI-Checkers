package checkersGame;

public class Board {
	private int size;
	private String bk = (char)27 + "[40m  " + (char)27 + "[0m";
	private String wht = (char)27 + "[47m  " + (char)27 + "[0m";
	
	public Board(int squareSize) {
		this.size = squareSize;
	}
	
	public void printBoard() {
		if (size%2 == 0) {
			System.err.println("Square size must be odd.");
			System.exit(1);
		}
		for(int i=0; i<8*size; i++) {
			if(i%size == size/2) {
				System.out.print(' ');
				System.out.print(i/size);
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
		for(int j=0; j<8*size; j++) {
			if(j%size == size/2) {
				System.out.print(j/size);
			} else {
				System.out.print(' ');
			}
			for(int i=j/size*size; i<8*size+(j/size*size); i++) {
				if(i%(2*size)>=size) {
					System.out.print(wht);
				} else {
					System.out.print(bk);
				}
			}
			System.out.println();
		}
	}
}
