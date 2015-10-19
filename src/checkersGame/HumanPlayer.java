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
	
	public HumanPlayer(int team) {
		this(team, "Human");
	}
		
	public Move selectMove(ArrayList<Move> validMoves, Board b) {
		System.out.println(name+" has "+validMoves.size()+" moves:");
		int counter = 1;
		
		for(Move m : validMoves) {
			System.out.print(counter++ +". Piece at ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
			for(Step s : m.getSteps()) {
				System.out.print("->("+s.getX()+","+s.getY()+")");
			}
			if(m.isPromotion()) {
				System.out.println('*');
			} else {
				System.out.println();
			}
		}
		int sel = -1;
		do {
			System.out.println("Please enter an integer from 1 to "+(counter-1));
			try {
				sel = input.nextInt();
			} catch (Exception e) {
				input.next();
			}
		} while(sel < 1 || sel > counter-1);
		
		return validMoves.get(sel-1);
	}
	
	public int getTeam() {
		return team;
	}
}
