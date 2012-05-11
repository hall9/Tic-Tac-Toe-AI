package proj4;
import java.io.IOException;

import proj4.TicTacToe;

public class Proj4 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String[] flags = new String[4];
		int numGames = 1;
		
		/*
		numGames = 5000;
		
		flags[0] = "x"; // Note: This is will be false b/c it should be 'h' for History
		flags[1] = "s"; // Note: This is will be false b/c it should be 's' for Save
		flags[2] = "x"; // Note: This is will be false b/c it should be 'd' for display
		flags[3] = "x"; // Note: This is will be false b/c it should be 'p' for all Potential Moves hased per move
		*/
		
		for (String s: args) {
			if (s.contains("-h") | s.contains("-H")) {
				flags[0] = "h";
			}
			else if (s.contains("-s") | s.contains("-S")) {
				flags[1] = "s";
			}
			else if (s.contains("-d") | s.contains("-D")) {
				flags[2] = "d";
			}
			else if (s.contains("-p") | s.contains("-p")) {
				flags[3] = "p";
			}
			else {
				try {
					numGames = Integer.parseInt(s.replaceAll("[\\D]", ""));
				  } catch (NumberFormatException e) {
					  
				  }

			}
		}
		
		TicTacToe Game = new TicTacToe();
		
		Game.play(numGames, flags);
	}

}
