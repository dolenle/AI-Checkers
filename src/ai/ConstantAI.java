package ai;

import java.util.ArrayList;
import java.util.Random;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Player;

public class ConstantAI implements Player {
	int team;
	Random rand = new Random();
	
	public ConstantAI(int team) {
		this.team = team;
	}
	
	//Always play choice 0
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		Move m = validMoves.get(0);
		return m;
	}

	public int getTeam() {
		return team;
	}
}