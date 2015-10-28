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
				} else {
					System.out.print(wht);
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
			for(int i=(j+1)%2; i<8; i+=2) {
				addPiece(Piece.BLACK, i, j);
				addPiece(Piece.RED, 7-i, 7-j);
			}
		}
	}
	
	public Piece addPiece(int team, int x, int y) {
		if(pieceLocs[y*8 + x] == null && (x+y%2)%2 == 1) {
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
		ArrayList<Move> nonCaptures = new ArrayList<Move>(8);
		boolean capture = false;
		for(Piece p : playerPieces) {
			MoveWorker moveFinder = new MoveWorker(p, pieceLocs);
			ArrayDeque<Move> result = moveFinder.call();
			if(result != null) {
				for(Move m : result) {
					if(m.getCaptures().size() > 0) {
						capture = true;
						validMoves.add(m);
					} else if(!capture) {
						validMoves.add(m);
						nonCaptures.add(m);
					}
				}
			}
		}
		if(capture) {
			validMoves.removeAll(nonCaptures);
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
		
		ArrayList<Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
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
	
	public Board undoMove(Move m) {
		Piece p = m.getPiece();
		int team = p.getTeam();
		ArrayList<Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
		}
		for(Piece cp : m.getCaptures()) {
			pieceLocs[cp.getY()*8 + cp.getX()] = cp;
			opponent.add(cp);
			if(cp.isKing()) {
				kingCount[-team+1]++;
			}
		}
		pieceLocs[p.getY()*8 + p.getX()] = null;
		p.moveTo(m.getStartX(), m.getStartY());
		pieceLocs[p.getY()*8 + p.getX()] = p;
		if(m.isPromotion()) {
			p.demote();
			kingCount[team+1]--;
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
	
	public Board clone() { //what a terrible terrible thing
		Board b = new Board(size);
		b.setLocs(new Piece[64]);
		ArrayList<Piece> blackCopy = new ArrayList<Piece>(blackPieces.size());
		ArrayList<Piece> redCopy = new ArrayList<Piece>(redPieces.size());
		for(Piece bPiece : blackPieces) {
			Piece newB = new Piece(bPiece);
			blackCopy.add(newB);
			b.pieceLocs[bPiece.getY()*8 + bPiece.getX()] = newB;
		}
		for(Piece rPiece : redPieces) {
			Piece newR = new Piece(rPiece);
			redCopy.add(newR);
			b.pieceLocs[rPiece.getY()*8 + rPiece.getX()] = newR;
		}
		b.setPieces(blackCopy, redCopy);
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
	
	public Piece[] getPieceLocations() {
		return pieceLocs;
	}
	
	public boolean isConsistent() {
		boolean status = true;
		int blackKings = 0;
		int redKings = 0;
		int blackPieceCount = 0;
		int redPieceCount = 0;
		for(int i=0; i<pieceLocs.length; i++) {
			Piece p = pieceLocs[i];
			if(p != null) {
				int team = p.getTeam();
				if(team == Piece.BLACK) {
					blackPieceCount++;
					if(p.isKing()) {
						blackKings++;
					}
					if(!blackPieces.contains(p)) {
						System.out.println("Black Piece not in list");
						status = false;
					}
				} else if(team == Piece.RED) {
					redPieceCount++;
					if(p.isKing()) {
						redKings++;
					}
					if(!redPieces.contains(p)) {
						System.out.println("Red Piece not in list");
						status = false;
					}
				}
				if(p.getY()*8+p.getX() != i) {
					System.out.println("Piece location mismatch");
					status = false;
				}
			}
		}
		if(blackKings != getKingCount(Piece.BLACK)) {
			System.out.println("Black King Count Mismatch - counted "+blackKings+", recorded "+getKingCount(Piece.BLACK));
			status = false;
		}
		if(redKings != getKingCount(Piece.RED)) {
			System.out.println("Red King Count Mismatch - counted "+redKings+", recorded "+getKingCount(Piece.RED));
			status = false;
		}
		if(blackPieceCount != blackPieces.size()) {
			System.out.println("Black Piece Count Mismatch - counted "+blackPieceCount+", recorded "+blackPieces.size());
			status = false;
		}
		if(redPieceCount != redPieces.size()) {
			System.out.println("Red Piece Count Mismatch - counted "+redPieceCount+", recorded "+redPieces.size());
			status = false;
		}
		if(!status) {
			System.out.println("Inconsistent Board State:");
			printBoard();
		}
		return status;
	}
}
