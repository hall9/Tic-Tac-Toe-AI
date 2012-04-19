package proj4;

public class Board {

	public Board (int state1[][], int wins1, int losses1, double probability1) {
		
		state = state1;
		wins = wins1;
		losses = losses1;
		probability = probability1;
	}
	
	public int state[][];
	public int wins;
	public int losses;
	public double probability;
}
