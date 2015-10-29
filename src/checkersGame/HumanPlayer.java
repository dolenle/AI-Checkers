package checkersGame;

import java.util.Scanner;
import java.util.ArrayList;

import ai.*;

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
			System.out.println("Please enter an integer from 1 to "+(counter-1)+", or 'h' to ask the AI for help.");
			try {
				sel = input.nextInt();
			} catch (Exception e) {
				try {
					if(input.nextLine().charAt(0) == 'h') {
						System.out.println("Asking AI for help...");
						OkayAI2 ai = new OkayAI2(team, 5);
						System.out.println("AI is thinking...");
						Move help = ai.selectMove(validMoves, b);
						System.out.print("AI says, \"Weeeellll.... If it were up to me, I would choose ("+help.getPiece().getX()+","+help.getPiece().getY()+")");
						for(Step s : help.getSteps()) {
							System.out.print("->("+s.getX()+","+s.getY()+")");
						}
						System.out.println(" (option "+(validMoves.indexOf(help)+1)+")\"");
						continue;
					}
				} catch (Exception e2) {
					input.next();
				}
				input.next();
			}
		} while(sel < 1 || sel > counter-1);
		
		return validMoves.get(sel-1);
	}
	
	public int getTeam() {
		return team;
	}
}
