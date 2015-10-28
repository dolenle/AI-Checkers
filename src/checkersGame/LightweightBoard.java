package checkersGame;

/**
 * A lightweight, self-contained Board representation for internal AI use
 *
 */
public class LightweightBoard {
	private int[] pieces = new int[64];
	private int pieceCount[] = {0, 0, 0};
	private int kingCount[] = {0, 0, 0};
	
	public LightweightBoard(Board orig) {
		Piece[] pieceLocs = orig.getPieceLocations();
		for(int i=0; i<64; i+=2) {
			Piece p = pieceLocs[i];
			if(p != null) {
				if(p.isKing()) {
					pieces[i] = p.getTeam()*2;
					kingCount[p.getTeam()+1]++;
				} else {
					pieces[i] = p.getTeam();
				}
				pieceCount[p.getTeam()+1]++;
			}
		}
	}
	
	public void debugPrint() {
		for(int y=0; y<7; y++) {
			for(int x=0; x<7; x++) {
				System.out.print(pieces[y*8+x]+"\t");
			}
			System.out.println();
		}
	}
}
