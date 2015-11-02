package checkersGame;

import java.util.Random;
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
			System.out.println("Please enter an integer from 1 to "+(counter-1)+", or 'h' to ask an AI for help.");
			try {
				sel = input.nextInt();
			} catch (Exception e) {
				try {
					String line = input.nextLine();
					if(line.charAt(0) == 'h') {
						if(line.length() > 1 && Character.getNumericValue(line.charAt(1)) >= 0) {
							getHelp(validMoves, b, Character.getNumericValue(line.charAt(1)));
						} else {
							getHelp(validMoves, b, -1);
						}
						
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
	
	private void getHelp(ArrayList<Move> validMoves, Board b, int sel) {
		System.out.println("Asking an AI for help...");
		Random rand = new Random();
		int x = sel;
		if(sel == -1) {
			x = rand.nextInt(7);
		}
		String aiName=null, thinkText=null, resultText=null, prologue=null;
		Player ai = null;
		switch(x) {
		case 0:
			aiName = "RandomAI";
			thinkText = "Huh? You need help?... Um.... sure, I guess.";
			resultText = "No idea if this will work or not, but I'll go with";
			prologue = ", but what do I know?";
			ai = new RandomAI(team);
			break;
		case 1:
			aiName = "ConstantAI";
			thinkText = "Ugh, what do you want?";
			resultText = "Look, it doesn't matter anyway, it's just a stupid game. Just choose";
			prologue = ". It'll work, trust me.";
			ai = new ConstantAI(team);
			break;
		case 2:
			aiName = "OkayAI";
			thinkText = "Okay, I'll have a look at the board.";
			resultText = "Okay, I think you should choose";
			prologue = ". Does that sound okay to you?";
			ai = new OkayAI(team, 5);
			break;
		case 3:
			aiName = "MultiThreadAI";
			thinkText = "I'll have my team of highly trained monkeys examine your moves.";
			resultText = "My team of monkeys has reached a conclusion. You should play";
			prologue = ". Good luck.";
			ai = new MultiThreadAI(team, 5);
			break;
		case 4:
			aiName = "AlphabetAI";
			thinkText = "Howdy. I hear you've got some trees that need pruning...?";
			resultText = "Well, job's done. All I have to say is, pick";
			prologue = ". What should I do with all these leftover branches?";
			ai = new AlphabetAI(team, 5);
			break;
		case 5:
			aiName = "AggressiveAI";
			thinkText = "You talking to me? Well I'm the only one here... You talkin' to me?";
			resultText = "I'm standin' here. You make the move. It's your move:";
			prologue = ". Yeah? You like that? Huh?";
			ai = new AggressiveAI(team, 5);
			break;
		case 6:
			aiName = "SableAI";
			thinkText = "Hi.";
			resultText = "Weeeellll, if I were playing, I would choose";
			prologue = ". Thanks!";
			ai = new SableAI(team, 5);
			break;
		default:
			System.out.println("Invalid option");
			return;
		}
		System.out.println(aiName+" says: \""+thinkText+"\"");
		Move help = ai.selectMove(validMoves, b);
		System.out.print(aiName+" says: \""+resultText+" ("+help.getPiece().getX()+","+help.getPiece().getY()+")");
		for(Step s : help.getSteps()) {
			System.out.print("->("+s.getX()+","+s.getY()+")");
		}
		System.out.println(" (option "+(validMoves.indexOf(help)+1)+")"+prologue+"\"");
	}
}
