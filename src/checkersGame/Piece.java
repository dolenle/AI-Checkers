package checkersGame;

public class Piece {
	private int team;
	private int xLoc;
	private int yLoc;
	private boolean isKing = false;
	private String color;
	private String text;

	public static final int RED = -1;
	public static final int BLACK = 1;
	
	private static final String UTFLargeDot = "\u2B24 ";
	
	public Piece(int team, int xStart, int yStart) {
		this.team = team;
		xLoc = xStart;
		yLoc = yStart;
		color = "\u001B[1m\u001B[31m"; //red
		if(team>0) {
			color = new String("\u001B[1m\u001B[34m"); //cyan
		}
		this.text = new String(color+UTFLargeDot);
	}
	
	/**
	 * Create a Piece with a custom character
	 */
	public Piece(int team, int xStart, int yStart, String customText) {
		this(team, xStart, yStart);
		this.text = new String(color+customText);
	}
	
	public void promote() {
		isKing = true;
		text = "\u001B[7m"+text; //invert
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
}
