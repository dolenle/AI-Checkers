package checkersGame;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.concurrent.*;

public class Board {
	private int size;
	private int cols;
	private int thresh1;
	private int thresh2;
	private String bk = "\u001B[40m";
	private String wht = "\u001B[47m  " + "\u001B[0m";
	private String end =  "\u001B[0m";
	
	private String[] colors = {"\u001B[1m\u001B[31m", null, "\u001B[1m\u001B[32m"}; //34 blue, 32 green
	private static final String UTFLargeDot = "\u2B24 ";
	
	private String blank = "  ";
	
	private Piece[] pieceLocs = new Piece[64];
	private LinkedHashMap<Integer, Piece> redPieces = new LinkedHashMap<Integer, Piece>(20);
	private LinkedHashMap<Integer, Piece> blackPieces = new LinkedHashMap<Integer, Piece>(20);
	
	private int kingCount[] = {0, 0, 0};
	
	private int lastX=-1, lastY=-1;
	private Piece lastMovePiece = null;
	
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
				System.out.print("\u001B[1m"+i/size);
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
		for(int j=0; j<cols; j++) {
			if(j%size == thresh1) {
				System.out.print("\u001B[1m"+j/size+end);
			} else {
				System.out.print(' ');
			}
			int offset = j/size*size;
			for(int i=offset; i<cols+offset; i++) {
				if(i%thresh2>=size) {
					System.out.print(bk);
					if(j%size == thresh1 && i%size == thresh1) {
						Piece p = pieceLocs[j/size*8+(i-offset)/size];
						if(p == null) {
							if(j/size == lastY && (i-offset)/size == lastX) {
								System.out.print(colors[lastMovePiece.getTeam()+1]+"* "+end);
								lastX = lastY = -1; //unmark
							} else {
								System.out.print(blank+end);
							}
						} else {
							if(p.isKing()) {
								System.out.print("\u001B[7m"); //invert colors
							}
							System.out.print(colors[p.getTeam()+1]+UTFLargeDot+end);
						}
					} else if(j%size == 0) {
						if(i%size == 0) {
							System.out.print("\u001B[32m"+((i-offset)/size)+" "+end);
						} else if(i%size == size-1) {
							System.out.print("\u001B[32m "+j/size+end);
						} else {
							System.out.print(blank+end);
						}
					} else {
						System.out.print(blank+end);
					}
				} else {
					System.out.print(wht);
				}
			}
			if(j%size == thresh1) {
				System.out.print("\u001B[1m"+j/size+end);
			} else {
				System.out.print(' ');
			}
			System.out.println();
		}
		for(int i=0; i<cols; i++) {
			if(i%size == thresh1) {
				System.out.print(' ');
				System.out.print("\u001B[1m"+i/size+end);
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
	}
	
	public Board clone() { //what a terrible terrible thing
		Board b = new Board(size);
		Piece[] newLocs = new Piece[64];
		b.setLocs(newLocs);
		LinkedHashMap<Integer, Piece> blackCopy = new LinkedHashMap<Integer, Piece>(20);
		LinkedHashMap<Integer, Piece> redCopy = new LinkedHashMap<Integer, Piece>(20);
		for(Piece bPiece : blackPieces.values()) {
			Piece newB = new Piece(bPiece);
			blackCopy.put(newB.getID(), newB);
			newLocs[bPiece.getY()*8 + bPiece.getX()] = newB;
		}
		for(Piece rPiece : redPieces.values()) {
			Piece newR = new Piece(rPiece);
			redCopy.put(newR.getID(), newR);
			newLocs[rPiece.getY()*8 + rPiece.getX()] = newR;
		}
		b.setPieces(blackCopy, redCopy);
		b.kingCount = kingCount.clone();
		return b;
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
				blackPieces.put(p.getID(), p);
			} else {
				redPieces.put(p.getID(), p);
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
			kingCount[team+1]++;
		}
	}
	
	
	//HAS BUG. DO NOT USE. DEPRECATED?
	public ArrayList<Move> getValidMoves(int team, int threads) {
		LinkedHashMap<Integer, Piece> playerPieces;
		if(team == Piece.BLACK) {
			playerPieces = blackPieces;
		} else {
			playerPieces = redPieces; //careful; no error checking here!
		}
		ExecutorService deadPool = Executors.newFixedThreadPool(threads);
		ArrayList<Move> validMoves = new ArrayList<Move>();
		ArrayList<Future<ArrayDeque<Move>>> results = new ArrayList<Future<ArrayDeque<Move>>>();
		for(Piece p : playerPieces.values()) {
			results.add(deadPool.submit(new MoveWorker(p, pieceLocs, false)));
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
		LinkedHashMap<Integer, Piece> playerPieces = redPieces;
		if(team == Piece.BLACK) {
			playerPieces = blackPieces;
		}
		ArrayList<Move> allMoves = new ArrayList<Move>();
		ArrayList<Move> jumpMoves = new ArrayList<Move>();
		boolean capture = false;
		for(Piece p : playerPieces.values()) {
			MoveWorker moveFinder = new MoveWorker(p, pieceLocs, capture);
			ArrayDeque<Move> result = moveFinder.call();
			if(result != null) {
				for(Move m : result) {
					if(m.getCaptures().size() > 0) {
						capture = true;
						jumpMoves.add(m);
					} else if(!capture) {
						allMoves.add(m);
					}
				}
			}
		}
		if(capture) {
			return jumpMoves;
		} else {
			return allMoves;
		}
	}
	
	public Board applyMove(Move m) {
		Piece p = m.getPiece();
		int team = p.getTeam();
		Step s = m.getSteps().peekLast();
		pieceLocs[p.getY()*8 + p.getX()] = null;
		p.moveTo(s.getX(), s.getY());
		pieceLocs[s.getY()*8 + s.getX()] = p;
		
		LinkedHashMap<Integer, Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
		}
		for(Piece cp : m.getCaptures()) {
			pieceLocs[cp.getY()*8 + cp.getX()] = null;
			opponent.remove(cp.getID());
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
		LinkedHashMap<Integer, Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
		}
		for(Piece cp : m.getCaptures()) {
			pieceLocs[cp.getY()*8 + cp.getX()] = cp;
			opponent.put(cp.getID(), cp);
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
	
	/**
	 * Apply a move regardless of the Board object it was derived from
	 */
	public ArrayList<Piece> applyAnonymousMove(Move m) {
		ArrayList<Piece> captures = new ArrayList<Piece>();
		Piece p = pieceLocs[m.getStartY()*8 + m.getStartX()];
		int team = p.getTeam();
		Step s = m.getSteps().peekLast();
		pieceLocs[p.getY()*8 + p.getX()] = null;
		p.moveTo(s.getX(), s.getY());
		pieceLocs[s.getY()*8 + s.getX()] = p;
		
		LinkedHashMap<Integer, Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
		}
		for(Piece cp : m.getCaptures()) {
			Piece captured = pieceLocs[cp.getY()*8 + cp.getX()];
			pieceLocs[cp.getY()*8 + cp.getX()] = null;
			opponent.remove(captured.getID());
			captures.add(captured);
			if(captured.isKing()) {
				kingCount[-team+1]--;
			}
		}
		if(m.isPromotion()) {
			p.promote();
			kingCount[team+1]++;
		}
		return captures;
	}
	
	public Board undoAnonymousMove(Move m, ArrayList<Piece> captures) {
		Step s = m.getSteps().peekLast();
		Piece p = pieceLocs[s.getY()*8 + s.getX()];
		int team = p.getTeam();
		LinkedHashMap<Integer, Piece> opponent;
		if(team == Piece.BLACK) {
			opponent = redPieces;
		} else {
			opponent = blackPieces;
		}
		for(Piece cp :captures) {
			pieceLocs[cp.getY()*8 + cp.getX()] = cp;
			opponent.put(cp.getID(), cp);
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
	
	public void markMove(Move m) {
		lastX = m.getStartX();
		lastY = m.getStartY();
		lastMovePiece = m.getPiece();
	}
	
	public Piece getPiece(int x, int y) {
		return pieceLocs[y*8 + x];
	}
	
	private void setLocs(Piece[] locs) {
		pieceLocs = locs;
	}
	
	private void setPieces(LinkedHashMap<Integer, Piece> black, LinkedHashMap<Integer, Piece> red) {
		blackPieces = black;
		redPieces = red;
	}
	
	public LinkedHashMap<Integer, Piece> getBlackPieces() {
		return blackPieces;
	}
	
	public LinkedHashMap<Integer, Piece> getRedPieces() {
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
					if(!blackPieces.containsKey(p.getID())) {
						System.out.println("Black Piece not in list");
						status = false;
					}
				} else if(team == Piece.RED) {
					redPieceCount++;
					if(p.isKing()) {
						redKings++;
					}
					if(!redPieces.containsKey(p.getID())) {
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
	
	public int[] boardToArray() {
		int[] result = new int[64];
		for(int i = 0; i<64; i++) {
			Piece p = pieceLocs[i];
			if(p == null) {
				result[i] = 0;
			} else if(p.getTeam() == Piece.RED) {
				if(p.isKing()) {
					result[i] = 3;
				} else {
					result[i] = 1;
				}
			} else if(p.getTeam() == Piece.BLACK) {
				if(p.isKing()) {
					result[i] = 4;
				} else {
					result[i] = 2;
				}
			} else {
				System.out.println("BAD");
			}
		}
		return null;
	}
}
