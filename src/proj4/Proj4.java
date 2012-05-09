package proj4;
import java.io.IOException;

import proj4.TicTacToe;

public class Proj4 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String[] flags = new String[3];
		
		int numGames = 100;
		flags[0] = "h"; // Note: This is will be false b/c it should be 'h' for History
		flags[1] = "s"; // Note: This is will be false b/c it should be 's' for Save
		flags[2] = "x"; // Note: This is will be false b/c it should be 'd' for display
		
		TicTacToe Game = new TicTacToe();
		
		Game.play(numGames, flags);
	}

}
