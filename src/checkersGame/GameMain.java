package checkersGame;

import java.io.*;
import java.util.ArrayList;

import ai.*;

public class GameMain {
	
	static Board b = new Board(3);
	
	static boolean blackTurn = false;
	
	//Run from terminal: java checkersGame.GameMain
	public static void main(String[] args) {
		
		//loadBoard("test.txt", b);
		b.defaultStart();
		
		Player p1 = new OkayAI(Piece.BLACK);
		Player p2 = new MultiThreadAI(Piece.RED);
		
		ArrayList<Move> blackMoves, redMoves;
		
		b.printBoard();
		
		while(true) {
			if(blackTurn) {
				blackMoves = b.getValidMovesSingleThread(Piece.BLACK);
				if(blackMoves.size() == 0) {
					System.out.println("GREEN out of moves... RED wins");
					break;
				}
				Move m = p1.selectMove(blackMoves, b);
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
				redMoves = b.getValidMovesSingleThread(Piece.RED);
				if(redMoves.size() == 0) {
					System.out.println("RED out of moves... GREEN wins");
					break;
				}
				Move m = p2.selectMove(redMoves, b);
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
	
	public static void loadBoard(String filename, Board b) {
		try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            
            String line;
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
        }
	}
}
