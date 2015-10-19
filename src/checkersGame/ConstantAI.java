package checkersGame;

import java.util.ArrayList;
import java.util.Random;

public class ConstantAI implements Player {
	int team;
	Random rand = new Random();
	
	public ConstantAI(int team) {
		this.team = team;
	}
	
	//Always play choice 0
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		Move m = validMoves.get(0);
		System.out.print("ConstantAI plays ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
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