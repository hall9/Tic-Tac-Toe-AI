package proj4;

public class Board {

	public Board (int state1[][]) {	
		state = state1;
		wins = 0;
		losses = 0;
		ties = 0;
		probability = 0;
		played = 0;
	}
	
	public int state[][];
	public int wins;
	public int losses;
	public int ties;
	public double probability;
	public int played;
	
	public void updateProb(String WLT) {
		 
		if (WLT == "win") {
			wins += 1;
		}
		else if (WLT == "lost") {
			losses += 1;
		}
		else if (WLT == "tie") {
			ties += 1;
		}
		else {
			//invalid string passed
		}
		
		played = wins + losses + ties;
		
		probability = wins / played;
		
	}
}
