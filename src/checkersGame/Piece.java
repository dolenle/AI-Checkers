package checkersGame;

public class Piece implements Cloneable{
	private int team;
	private int xLoc;
	private int yLoc;
	private boolean king = false;
	private String color;
	private String text;
	private int id;
	
	private static int nextID = 0;

	public static final int RED = -1;
	public static final int BLACK = 1;
	
	private static final String UTFLargeDot = "\u2B24 ";
	
	public Piece(int team, int xStart, int yStart) {
		this.team = team;
		xLoc = xStart;
		yLoc = yStart;
		color = "\u001B[1m\u001B[31m"; //red
		if(team>0) {
			color = new String("\u001B[1m\u001B[32m"); //34 cyan, 32 green
		}
		this.text = new String(color+UTFLargeDot);
		id = nextID++;
	}
	
	public Piece(Piece original) {
		team = original.getTeam();
		xLoc = original.getX();
		yLoc = original.getY();
		king = original.isKing();
		id = original.getID();
	}
	
	/**
	 * Create a Piece with a custom character
	 */
	public Piece(int team, int xStart, int yStart, String customText) {
		this(team, xStart, yStart);
		this.text = new String(color+customText);
	}
	
	public void moveTo(int x, int y) {
		xLoc = x;
		yLoc = y;
	}
	
	public void promote() {
		king = true;
		text = "\u001B[7m"+text; //invert
	}
	
	public boolean isKing() {
		return king;
	}
	
	public String getText() {
		return text;
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
