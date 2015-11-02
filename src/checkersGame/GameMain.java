package checkersGame;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import ai.*;

public class GameMain {
	
	static Board b;
	static Player blackPlayer;
	static Player redPlayer;
	
	static boolean blackTurn = true;
	
	static Scanner scan = new Scanner(System.in);
	
	//Run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		int squareSize = 3;
		if(args.length > 0) {
			try {
				squareSize = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Warning - invalid argument.");
			}
		}
		b = new Board(squareSize);
		outer:
		do {
			System.out.print("Load game from file? (Y/N): ");
			String resp = scan.next();
			if(resp.equalsIgnoreCase("y")) {
				System.out.println("Enter filename: ");
				loadBoard(scan.next());
				break;
			} else if(resp.equalsIgnoreCase("n")) {
				System.out.println("\nChoose a player for GREEN:");
				blackPlayer = choosePlayer(Piece.BLACK);
				System.out.println("\nChoose a player for RED:");
				redPlayer = choosePlayer(Piece.RED);
				b.defaultStart();
				do {
					System.out.print("\nWho goes first? (R/G): ");
					String first = scan.next();
					if(first.equalsIgnoreCase("r")) {
						blackTurn = false;
						break outer;
					} else if(first.equalsIgnoreCase("g")) {
						blackTurn = true;
						break outer;
					}
				} while (true);
				
			}
		} while(true);		
		
		ArrayList<Move> blackMoves, redMoves;
		
		b.printBoard();
		
		while(true) {
			if(blackTurn) {
				System.out.println("GREEN's Turn");
				blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
				if(blackMoves.size() == 0) {
					System.out.println("GREEN out of moves... RED wins");
					break;
				}
				Move m = blackPlayer.selectMove(blackMoves, b);
				System.out.print("GREEN plays ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
				for(Step s : m.getSteps()) {
					System.out.print("->("+s.getX()+","+s.getY()+")");
				}
				System.out.println();
				b.applyMove(m);
				b.markMove(m);
				b.printBoard();
				b.isConsistent();
			} else {
				System.out.println("RED's Turn");
				redMoves = b.getValidMovesSingleThread(Piece.RED);
				if(redMoves.size() == 0) {
					System.out.println("RED out of moves... GREEN wins");
					break;
				}
				Move m = redPlayer.selectMove(redMoves, b);
				System.out.print("RED plays ("+m.getPiece().getX()+","+m.getPiece().getY()+")");
				for(Step s : m.getSteps()) {
					System.out.print("->("+s.getX()+","+s.getY()+")");
				}
				System.out.println();
				b.applyMove(m);
				b.markMove(m);
				b.printBoard();
				b.isConsistent();
			}
			blackTurn = !blackTurn;
		}
		b.printBoard();
	}
	
	public static void loadBoard(String filename) {
		try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            if(line.substring(0, 1).equalsIgnoreCase("b")) {
            	blackTurn = true;
            	blackPlayer = choosePlayer(Integer.parseInt(line.split(" ")[1]), Piece.BLACK, Integer.parseInt(line.split(" ")[2]));
            } else if(line.substring(0, 1).equalsIgnoreCase("r")) {
            	blackTurn = false;
            	redPlayer = choosePlayer(Integer.parseInt(line.split(" ")[1]), Piece.RED, Integer.parseInt(line.split(" ")[2]));
            } else {
            	System.out.println("bad file");
        		System.exit(1);
            }
            line = reader.readLine();
            if(line.substring(0, 1).equalsIgnoreCase("b") && !blackTurn) {
            	blackPlayer = choosePlayer(Integer.parseInt(line.split(" ")[1]), Piece.BLACK, Integer.parseInt(line.split(" ")[2]));
            } else if(line.substring(0, 1).equalsIgnoreCase("r") && blackTurn) {
            	redPlayer = choosePlayer(Integer.parseInt(line.split(" ")[1]), Piece.RED, Integer.parseInt(line.split(" ")[2]));
            } else {
            	System.out.println("bad file");
        		System.exit(1);
            }
            if(redPlayer == null || blackPlayer == null) {
            	System.out.println("Player not defined");
        		System.exit(1);
            }
            int y=0;
            while((line = reader.readLine()) != null) {
            	int x = (y+1)%2;
                for(int i=0; i<line.length(); i++) {
                	switch(line.charAt(i)) {
                	case '0':
                		x+=2;
                		break;
                	case 'b':
                		b.addPiece(Piece.BLACK,x,y);
                		x+=2;
                		break;
                	case 'B':
                		b.addPiece(Piece.BLACK,x,y,true);
                		x+=2;
                		break;
                	case 'r':
                		b.addPiece(Piece.RED,x,y);
                		x+=2;
                		break;
                	case 'R':
                		b.addPiece(Piece.RED,x,y,true);
                		x+=2;
                		break;
                	}
                	if(x>9) {
                		System.out.println("bad file");
                		System.exit(1);
                	}
                }
                y++;
            }   

            reader.close();         
        } catch(Exception e) {
            System.out.println("Error loading "+ filename);
            e.printStackTrace();
            System.exit(-1);
        }
	}
	
	public static Player choosePlayer(int team) {
		System.out.println("1. Human Player: On your turn, choose from the list of valid moves.");
		System.out.println("2. RandomAI: Choose a move randomly.");
		System.out.println("3. ConstantAI: Always chooses the first move.");
		System.out.println("4. OkayAI: Minimax search with a simple heuristic.");
		System.out.println("5. AlphabetAI: Adds Alpha-Beta pruning and weighted kings to OkayAI.");
		System.out.println("6. MultiThreadAI: Makes a great CPU warmer, and not much else.");
		System.out.println("7. AggressiveAI: Pushes for the center and opposite side.");
		System.out.println("8. SableAI: Uses something similar to Sable's heuristic.");
		int input = -1;
		do {
			System.out.print("Enter a choice: ");
			if(scan.hasNextInt()) {
				input = scan.nextInt();
				switch(input) {
				case 1:
					return new HumanPlayer(team);
				case 2:
					return new RandomAI(team);
				case 3:
					return new ConstantAI(team);
				case 4:
					return new OkayAI(team);
				case 5:
					return new AlphabetAI(team);
				case 6:
					return new MultiThreadAI(team);
				case 7:
					return new AggressiveAI(team);
				case 8:
					return new SableAI(team);
				}
			} else {
				scan.next();
			}
		} while (input < 1 || input > 7);
		return null;
	}
	
	public static Player choosePlayer(int choice, int team, int time) {
		switch(choice) {
		case 1:
			return new HumanPlayer(team);
		case 2:
			return new RandomAI(team);
		case 3:
			return new ConstantAI(team);
		case 4:
			return new OkayAI(team, time);
		case 5:
			return new AlphabetAI(team, time);
		case 6:
			return new MultiThreadAI(team, time);
		case 7:
			return new AggressiveAI(team, time);
		case 8:
			return new SableAI(team, time);
		}
		return null;
	}
}
