package proj4;
import java.util.*;
import proj4.QuadraticProbingHashTable;
import proj4.Board;

public class TicTacToe {

	/* prime Numbers for Heap Size
	 * 
	 *  20011  - if only just above 20k is needed
	 * 	29303  - If 80% of the heap has always be open
	 * 
	 *  In the Tic-Tac-Toe Array:
	 *  ----------------------------
	 * 		X   = 01
	 * 		O   = 11
	 * 	  Empty = 00
	*/
	
	private QuadraticProbingHashTable<Integer> HashTable = new QuadraticProbingHashTable<Integer>( );
	
	int[][] currentGameBoard = new int[3][3];
	
	public void play (int games) {
		newGame();
		dumbTurn();
		
		
	}
	
	private void AITurn () {
		LinkedList<int[][]> possibleMoves = new LinkedList<int[][]>();
		LinkedList<Long> serials = new LinkedList<Long>();
		
		possibleMoves = potentialMoves(possibleMoves);
		
		while (!possibleMoves.isEmpty()) {
			int[][] temp = new int[3][3];
			temp = possibleMoves.remove();
			serials.add(findCommonBoard(temp));
		}
		
	 
		
	}
	
	
	private void dumbTurn () {
		int[] Location = new int[2];
		Location = randomSpot();
		currentGameBoard[Location[0]][Location[1]] = 1;
	}
	
	private int[] randomSpot () {
		Random generator = new Random();
		int[] LocationToGo = new int[2];
		LinkedList<int[]> AvaibleSpots = new LinkedList<int[]>();
		
		AvaibleSpots = availableMoves(AvaibleSpots);
		int n = AvaibleSpots.size() - 1;
		
		int randomIndex = generator.nextInt( n );
		LocationToGo = AvaibleSpots.get(randomIndex);
		
		return LocationToGo;
	}
	
	private LinkedList<int[]> availableMoves (LinkedList<int[]> AvaibleMoves) {
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				if (currentGameBoard[i][k] == 0) {
					int[] Location = new int[2];
					Location[0] = i;
					Location[1] = k;
					AvaibleMoves.add(Location);
				}
			}
		}
		
		return AvaibleMoves;	
	}
	
	private boolean movesAvailable () {
		boolean movesAvailable = false;
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				if (currentGameBoard[i][k] == 0) {
					movesAvailable = true;
				}
			}
		}
		
		return movesAvailable;
	}
	
	private LinkedList<int[][]> potentialMoves (LinkedList<int[][]> potentialMoves) {
		LinkedList<int[]> AvaibleMoves = new LinkedList<int[]>();
		
		AvaibleMoves = availableMoves(AvaibleMoves);
		
		while (!AvaibleMoves.isEmpty()) {
			int[][] aMove = new int[3][3];
			int[] aMoveLoc = new int[2];
			
			aMoveLoc = AvaibleMoves.remove();
			aMove = deepCopy(currentGameBoard);
			
			aMove[aMoveLoc[0]][aMoveLoc[1]] = 2;
			
			potentialMoves.add(aMove);
		}
		
		return potentialMoves;
	}
	
	private long findCommonBoard ( int[][] aMove) {
		LinkedList<int[][]> SymmetryMoves = new LinkedList<int[][]>();
		
		long lowestSerial = 0;
		long tempSerial = 0;
		
		SymmetryMoves = findSymmetry(aMove);
		
		while (!SymmetryMoves.isEmpty()) {
			aMove = SymmetryMoves.remove();
			tempSerial = serialize(aMove);
			
			if (tempSerial > lowestSerial) {
				lowestSerial = tempSerial;
			}
		}
		
		return lowestSerial;
	}
	
	private long serialize (int board[][]) {
		String serial = "";
		int[][] temp = new int[3][3];
		temp = deepCopy(board);
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				serial += Integer.toBinaryString(temp[i][k]);
			}
		}
		
		return Integer.parseInt(serial, 2);
	}
	
	private void gameOver (int state[][]) {
		
		if ( state[0][0] == state[0][1] && state[0][0] == state[0][2] ) {
			// Found a win: A cross the Top
		}
		else if ( state[1][0] == state[1][1] && state[1][0] == state[1][2] ) {
			// Found a win: A cross the Middle
		}
		else if ( state[2][0] == state[2][1] && state[2][0] == state[2][2] ) {
			// Found a win: A cross the Bottom
		}
		else if ( state[0][0] == state[1][0] && state[0][0] == state[2][0] ) {
			// Found a win: Down the left
		}
		else if ( state[0][1] == state[1][1] && state[0][1] == state[2][1] ) {
			// Found a win: Down the middle
		}
		else if ( state[0][2] == state[1][2] && state[0][2] == state[2][2] ) {
			// Found a win: Down the right
		}
		else if ( state[0][0] == state[1][1] && state[0][0] == state[2][2] ) {
			// Found a win: Diagonally from left to right 
		}
		else if ( state[0][2] == state[1][1] && state[0][2] == state[2][0] ) {
			// Found a win: Diagonally from top left to bottom right 
		}
		else {
			// No win found
			
			if (movesAvailable()) {
				// Game is not Over Yet there is still spots available to go
			}
			else {
				// This Game ended with a tie
			}
		}
	}
	
	private LinkedList<int[][]> findSymmetry (int board[][]) {
		LinkedList<int[][]> Symmetry = new LinkedList<int[][]>();
		
		Symmetry.add(board);
		Symmetry = rotated(board, Symmetry, board);
		int[][] mirror = new int[3][3];
		mirror = mirrored(board);
		Symmetry = rotated(mirror, Symmetry, mirror);
		
		return Symmetry;
	}
	
	private LinkedList<int[][]> rotated (int state[][], LinkedList<int[][]> rotatedList, int gameBoard[][]) {
		int[][] rotated = new int[3][3];
		int[][] temp = new int[3][3];
		temp = deepCopy(state);
		
		rotated[0][0] = temp[2][0];
		rotated[0][1] = temp[1][0];
		rotated[0][2] = temp[0][0];
		rotated[1][0] = temp[2][1];
		rotated[1][1] = temp[1][1]; // Doesn't Change
		rotated[1][2] = temp[0][1];
		rotated[2][0] = temp[2][2];
		rotated[2][1] = temp[1][2];
		rotated[2][2] = temp[0][2];
		
		if (!Arrays.deepEquals(rotated, gameBoard)) {
			rotatedList.add(rotated);
			rotatedList = rotated(rotated,rotatedList, gameBoard);
		}
		
		return rotatedList;
	}
	
	private int[][] mirrored(int board[][]) {
		int[][] mirrored = new int[3][3];
		int[][] temp = new int[3][3];
		temp = deepCopy(board);
		
		mirrored[0][0] = temp[0][2];
		mirrored[0][1] = temp[0][1]; // Doesn't Change
		mirrored[0][2] = temp[0][0];
		mirrored[1][0] = temp[1][2];
		mirrored[1][1] = temp[1][1]; // Doesn't Change
		mirrored[1][2] = temp[1][0];
		mirrored[2][0] = temp[2][2];
		mirrored[2][1] = temp[2][1]; // Doesn't Change
		mirrored[2][2] = temp[2][0];
		
		return mirrored;
	}
	
	private static int[][] deepCopy(int[][] array) {
		int rows = array.length;

		int[][] newArray = (int[][]) array.clone();

		for( int row = 0; row<rows; row++) {
			newArray[row] = (int[]) array[row].clone();
		}

		return newArray;
	}
	
	private void newGame () {
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				currentGameBoard[i][k] = 0;
			}
		}
	}
	
	
	
	
}
