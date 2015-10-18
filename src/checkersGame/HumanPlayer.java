package checkersGame;

import java.util.Scanner;
import java.util.ArrayList;

public class HumanPlayer implements Player {
	private int team;
	private String name;
	private Scanner input = new Scanner(System.in);
	
	public HumanPlayer(int team, String name) {
		this.name = name;
		this.team = team;
	}
		
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		System.out.println(name+" has "+validMoves.size()+" moves:");
		int counter = 0;
		
		for(Move m : validMoves) {
			System.out.print(counter++ +". Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
			for(Step s : m.getSteps()) {
				System.out.print("->("+s.getX()+","+s.getY()+")");
			}
			System.out.println();
		}
		int sel = -1;
		do {
			System.out.println("Please enter an integer between 0 and "+(counter-1));
			try {
				sel = input.nextInt();
			} catch (Exception e) {
				input.next();
			}
		} while(sel < 0 || sel > counter-1);
		
		return validMoves.get(sel);
	}
	
	public int getTeam() {
		return team;
	}
}
