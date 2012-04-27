package proj4;
import proj4.TicTacToe;

public class Proj4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] flags = new String[3];
		
		int numGames = 5;
		flags[0] = "H";
		flags[1] = "S";
		flags[2] = "D";
		
		TicTacToe Game = new TicTacToe();
		
		Game.play(numGames, flags);
	}

}
