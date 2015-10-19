package checkersGame;

import java.util.ArrayList;
import java.util.Random;

public class RandomAI implements Player {
	int team;
	Random rand = new Random();
	
	public RandomAI(int team) {
		this.team = team;
	}
	
	//Random move
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		Move m = validMoves.get(rand.nextInt(validMoves.size()));
		System.out.print("DumbAI plays ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
		for(Step s : m.getSteps()) {
			System.out.print("->("+s.getX()+","+s.getY()+")");
		}
		System.out.println();
		return m;
	}

	public int getTeam() {
		return team;
	}
}
