package proj4;

public class Board {

	public Board (Comparable newSerial, int[][] newGameState ) {
		GameState = newGameState;
		Serial = newSerial;
		Wins = 0;
		Losses = 0;
		Ties = 0;
		Probability = 0;
		Played = 0;
	}
	
	public int[][] GameState;
	public Comparable Serial;
	public int Wins;
	public int Losses;
	public int Ties;
	public double Probability;
	public int Played;
	
	public void updateProb(String WLT) {
		 
		if (WLT == "win") {
			Wins += 1;
		}
		else if (WLT == "lost") {
			Losses += 1;
		}
		else if (WLT == "tie") {
			Ties += 1;
		}
		else {
			//invalid string passed
		}
		
		Played = Wins + Losses + Ties;
		
		Probability = Wins / Played;
		
	}
}
