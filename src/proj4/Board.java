package proj4;
public class Board {

	public Board (long newSerial, int[][] newGameState ) {
		GameState = newGameState;
		Serial = newSerial;
	}
	
	public int[][] GameState;
	public long Serial;
	public int Wins = 0;
	public int Losses = 0;
	public int Ties = 0;
	public double Probability = 0.00;
	public int Played = 0;
	public int Weight = 0;
	
	/**
	 * This fucntion is mainly used by the filegrab function in TicTacToe class, When reading the "configs.txt" it needs a way to se the stats
	 * 
	 * @param played
	 * @param wins
	 * @param losses
	 * @param weight
	 */
	public void setStats (int played, int wins, int losses, int weight) {
		 
		Played = played;
		Wins = wins;
		Losses = losses;
		Weight = weight;
		Ties = played - (wins + losses);
		
		double ratio = ( (double) Wins / (double) Played );
		
		Probability = ratio * 100;
		
	}
	
	/**
	 * When called this updates the baord based on if it was a win, loss, or tie
	 * 
	 * @param WLT
	 */
	public void updateProb(String WLT) {
		 
		if (WLT == "win") {
			Wins++;
			Played++;
			Weight--;
		}
		else if (WLT == "lost") {
			Losses++;
			Played++;
			Weight++;
		}
		else if (WLT == "tie") {
			Ties++;
			Played++;
		}
		else {
			//invalid string passed
			
			System.out.println("bad String: WLT");
		}
		
		double ratio = ( (double) Wins / (double) Played );
		
		Probability = ratio * 100;
		
	}
}
