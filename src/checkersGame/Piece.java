package checkersGame;

public class Piece {
	private int team;
	private int location;
	private boolean isKing = false;
	public String color;
	public String text;

	public static final int RED = -1;
	public static final int BLACK = 1;
	
	private static final String UTFLargeDot = "\u2B24 ";
	
	public Piece(int team, int startingLocation) {
		this.team = team;
		location = startingLocation;
		color = "\u001B[1m\u001B[31m"; //red
		if(team>0) {
			color = new String("\u001B[1m\u001B[34m"); //green
		}
		this.text = new String(color+UTFLargeDot);
	}
	
	/**
	 * Create a Piece with a custom character
	 */
	public Piece(int team, int startingLocation, String customText) {
		this(team, startingLocation);
		this.text = new String(color+customText);
	}
	
	public void promote() {
		isKing = true;
		text = "\u001B[7m"+text;
	}
}
