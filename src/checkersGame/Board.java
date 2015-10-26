package checkersGame;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.concurrent.*;

public class Board {
	private int size;
	private int cols;
	private int thresh1;
	private int thresh2;
	private String bk = "\u001B[40m";
	private String wht = "\u001B[47m  " + "\u001B[0m";
	private String end =  "\u001B[0m";
	private String blank = "  ";
	
	private Piece[] pieceLocs = new Piece[64];
	private ArrayList<Piece> redPieces = new ArrayList<Piece>();
	private ArrayList<Piece> blackPieces = new ArrayList<Piece>();
	
	private int kingCount[] = {0, 0, 0};
	
	public Board(int squareSize) {
		if (squareSize%2 == 0 || squareSize < 0) {
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
						if(pieceLocs[j/size*8+(i-offset)/size] == null) {
							System.out.print(blank+end);
						} else {
							System.out.print(pieceLocs[j/size*8+(i-offset)/size].getText()+end);
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
	
	public Piece addPiece(int team, int x, int y) {
		if(pieceLocs[y*8 + x] == null && (x+y%2)%2 == 0) {
	 		Piece p = new Piece(team, x, y);
			pieceLocs[y*8 + x] = p;
			if(team == Piece.BLACK) {
				blackPieces.add(p);
			} else {
				redPieces.add(p);
			}
			return p;
		} else {
			System.out.println("Cannot add piece at ("+x+","+y+")");
			return null;
		}
	}
	
	public void addPiece(int team, int x, int y, boolean king) {
		Piece p = addPiece(team,x,y);
		if(king) {
			p.promote();
		}
	}
	
	public ArrayList<Move> getValidMoves(int team, int threads) {
		ArrayList<Piece> playerPieces;
		if(team == Piece.BLACK) {
			playerPieces = blackPieces;
		} else {
			playerPieces = redPieces; //careful; no error checking here!
		}
		ExecutorService deadPool = Executors.newFixedThreadPool(threads);
		ArrayList<Move> validMoves = new ArrayList<Move>(8);
		ArrayList<Future<ArrayDeque<Move>>> results = new ArrayList<Future<ArrayDeque<Move>>>();
		for(Piece p : playerPieces) {
			results.add(deadPool.submit(new MoveWorker(p, pieceLocs)));
		}
		for(Future<ArrayDeque<Move>> r : results) {
			try {
				validMoves.addAll(r.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		deadPool.shutdown();
		return validMoves;
	}
	
	public ArrayList<Move> getValidMovesSingleThread(int team) {
		ArrayList<Piece> playerPieces = redPieces;
		if(team == Piece.BLACK) {
			playerPieces = blackPieces;
		}
		ArrayList<Move> validMoves = new ArrayList<Move>(8);
		for(Piece p : playerPieces) {
			MoveWorker moveFinder = new MoveWorker(p, pieceLocs);
			ArrayDeque<Move> result = moveFinder.call();
			if(result != null) {
				validMoves.addAll(result);
			}
		}
		return validMoves;
	}
	
	public Piece getPiece(int x, int y) {
		return pieceLocs[y*8 + x];
	}
	
	public Board applyMove(Move m) {
		Piece p = m.getPiece();
		int team = p.getTeam();
		Step s = m.getSteps().peekLast();
		pieceLocs[p.getY()*8 + p.getX()] = null;
		p.moveTo(s.getX(), s.getY());
		pieceLocs[s.getY()*8 + s.getX()] = p;
		
		ArrayList<Piece> opponent = blackPieces;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		}
		for(Piece cp : m.getCaptures()) {
			pieceLocs[cp.getY()*8 + cp.getX()] = null;
			opponent.remove(cp);
			if(cp.isKing()) {
				kingCount[-team+1]--;
			}
		}
		if(m.isPromotion()) {
			p.promote();
			kingCount[team+1]++;
		}
		return this;
	}
	
	private void setLocs(Piece[] locs) {
		pieceLocs = locs;
	}
	
	private void setPieces(ArrayList<Piece> black, ArrayList<Piece> red) {
		blackPieces = black;
		redPieces = red;
	}
	
	@SuppressWarnings("unchecked")
	public Board clone() {
		Board b = new Board(size);
		b.setLocs(pieceLocs.clone());
		b.setPieces((ArrayList<Piece>) blackPieces.clone(), (ArrayList<Piece>) redPieces.clone());
		return b;
	}
	
	public ArrayList<Piece> getBlackPieces() {
		return blackPieces;
	}
	
	public ArrayList<Piece> getRedPieces() {
		return redPieces;
	}
	
	public int getKingCount(int team) {
		return kingCount[team+1];
	}
}
