package checkersGame;

public class Step {
	private int x;
	private int y;
	private Piece capture = null;
	
	public Step(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Step(int x, int y, Piece capture) {
		this.x = x;
		this.y = y;
		this.capture = capture;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Piece getCapture() {
		return capture;
	}
}
