package checkersGame;

import java.util.ArrayList;

public class Move {	
	private ArrayList<Step> steps = new ArrayList<Step>(4);
	
	//Create a Move with an initial step
	public Move(int x, int y) {
		steps.add(new Step(x,y));
	}
	
	public void addStep(int x, int y) {
		steps.add(new Step(x,y));
	}
	
	public class Step {
		private int x;
		private int y;
		public Step(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
	public class MoveWorker implements Runnable {
		private ArrayList<Move> moves;
		private Piece p;
		private Piece[][] locs;
		
		public MoveWorker(ArrayList<Move> moves, Piece p, Piece[][] locs) {
			this.moves = moves;
			this.p = p;
			this.locs = locs;
		}
		
		public void run() {
			
		} 
	}
}
