package checkersGame;

public class Piece implements Cloneable{
	private int team;
	private int xLoc;
	private int yLoc;
	private boolean king = false;
	private int id;
	
	private static int nextID = 0;

	public static final int RED = -1;
	public static final int BLACK = 1;
		
	public Piece(int team, int xStart, int yStart) {
		this.team = team;
		xLoc = xStart;
		yLoc = yStart;
		id = nextID++;
	}
	
	public Piece(Piece original) {
		team = original.getTeam();
		xLoc = original.getX();
		yLoc = original.getY();
		king = original.isKing();
		id = original.getID();
	}
	
	public void moveTo(int x, int y) {
		xLoc = x;
		yLoc = y;
	}
	
	public void promote() {
		king = true;
	}
	
	public void demote() {
		king = false;
	}
	
	public boolean isKing() {
		return king;
	}
	
	public int getX() {
		return xLoc;
	}
	
	public int getY() {
		return yLoc;
	}
	
	public int getTeam() {
		return team;
	}
	
	public int getID() {
		return id;
	}
	
	public static int getNextID() {
		return nextID;
	}
	
	public static void resetID() {
		nextID = 0;
	}
}
