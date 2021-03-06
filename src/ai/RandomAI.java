package ai;

import java.util.ArrayList;
import java.util.Random;

import checkersGame.Board;
import checkersGame.Move;
import checkersGame.Player;

public class RandomAI implements Player {
	int team;
	Random rand = new Random();
	
	public RandomAI(int team) {
		this.team = team;
	}
	
	//Random move
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		Move m = validMoves.get(rand.nextInt(validMoves.size()));
		return m;
	}

	public int getTeam() {
		return team;
	}
}
