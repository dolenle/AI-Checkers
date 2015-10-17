public class Test {
	static int zoom = 7;
	static String bk = (char)27 + "[40m  " + (char)27 + "[0m";
	static String wht = (char)27 + "[47m  " + (char)27 + "[0m";
	
	public static void main(String[] args) {
		if (zoom%2 == 0) {
			System.err.println("Zoom must be odd.");
			System.exit(1);
		}
		for(int j=0; j<8*zoom; j++) {
			for(int i = j/zoom*zoom; i < 8*zoom+(j/zoom*zoom); i++) {
				if(i%(2*zoom)>=zoom) {
					System.out.print(wht);
				} else {
					System.out.print(bk);
				}
			}
			System.out.println();
		}
	}
}
