package checkersGame;

import java.util.ArrayList;

public interface Player {	
	public Move selectMove(ArrayList<Move> validMoves);
	public int getTeam();
}
