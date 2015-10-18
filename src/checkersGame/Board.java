package checkersGame;

import java.util.ArrayList;

public class Board {
	private int size;
	private int cols;
	private int thresh1;
	private int thresh2;
	private String bk = "\u001B[40m";
	private String wht = "\u001B[47m  " + "\u001B[0m";
	private String end =  "\u001B[0m";
	private String blank = "  ";
	
	private Piece[][] pieceLocs = new Piece[8][8];
	private ArrayList<Piece> redPieces = new ArrayList<Piece>();
	private ArrayList<Piece> blackPieces = new ArrayList<Piece>();
	
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
					if(j%size == thresh1 && i%size == thresh1) {
						if(pieceLocs[j/size][(i-offset)/size] == null) {
							System.out.print(blank+end);
						} else {
							System.out.print(pieceLocs[j/size][(i-offset)/size].getText()+end);
						}
					} else {
						System.out.print(blank+end);
					}
					
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * Set up the board with the normal starting positions
	 */
	public void defaultStart() {
		for(int j=0; j<3; j++) {
			for(int i=j%2; i<8; i+=2) {
				addPiece(Piece.BLACK, i, j);
				addPiece(Piece.RED, 7-i, 7-j);
			}
		}
	}
	
	public void addPiece(int team, int x, int y) {
		Piece p = new Piece(team, x, y);
		pieceLocs[y][x] = p;
		//p.promote();
	}
}
