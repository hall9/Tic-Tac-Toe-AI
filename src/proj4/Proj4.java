package proj4;
import proj4.TicTacToe;

public class Proj4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int numGames = 5;
		
		TicTacToe Game = new TicTacToe();
		
		Game.play(numGames);
	}

}
