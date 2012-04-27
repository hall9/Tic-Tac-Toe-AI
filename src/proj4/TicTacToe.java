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
	 * 		O   = 10
	 * 	  Empty = 00
	*/
	
	private QuadraticProbingHashTable<Board> DictionaryOfBoards = new QuadraticProbingHashTable<Board>( );
	LinkedList<Board> HashedBoards = new LinkedList<Board>();
	LinkedList<Board> NotHashedBoards = new LinkedList<Board>();
	int[][] CurrentGameBoard = new int[3][3];
	
	int played;
	int ties;
	int wins;
	int losses;
	double AIWinPercentage;
	double DumbWinPercentage;
	
	public void play (int games, String[] flags) {
		
		boolean H = (flags[0].equals("H")) ? true : false; 
		boolean S = (flags[0].equals("S")) ? true : false;
		boolean D = (flags[0].equals("D")) ? true : false;
		
		played = 0;
		ties = 0;
		wins = 0;
		losses = 0;
		AIWinPercentage = 0.0;
		DumbWinPercentage = 0.0;
		DictionaryOfBoards.makeEmpty();
		
		for (int i = 0; i < games; i++ ) {
			newGame();
			
			System.out.println("Game Nr: " + (i + 1));
			String WLT = playing(1,H);
			updateStats(WLT);
			
		}
		
		
	}
	
	 
	
	private void updateStats(String WLT) {
		 
		if (WLT == "win") {
			wins += 1;
			System.out.println("The game has been won by O");
		}
		else if (WLT == "lost") {
			losses += 1;
			System.out.println("The game has been won by X");
		}
		else if (WLT == "tie") {
			ties += 1;
			System.out.println("The game ended with a draw");
		}
		else {
			//invalid string passed
		}
		
		played = wins + losses + ties;
		
		AIWinPercentage = wins / played;
		DumbWinPercentage = losses / played;
		
		System.out.println("Smart 'O' player has won " + wins + " times.");
		System.out.println("Random 'X' Player has won " + losses + " times.");
		System.out.println("");
		System.out.println("");
	}
	
	private String playing (int n, boolean H) {

		if (n % 2 == 0) {
			Board AIBoard = AITurn();
			System.out.println("The learning player (O) just moved:");
			printBoard();

			if (AIBoard.Played > 0 ) {
				System.out.println("In the past, this move has led us to a win " + AIBoard.Probability + " of the time.");
			}
			else {
				System.out.println("The move is new and was chosen at random.");
			}
			
			System.out.println("");
		}
		else {
			dumbTurn();
			System.out.println("The random player (X) just moved: ");
			printBoard();
			System.out.println("");
		}

		String WLT = "";
		int isGameOver = gameOver();
		
		if (isGameOver == 0){
			// tie
			WLT = "tie";
		}
		else if (isGameOver == 1) {
			// X wins
			WLT = "lost";

		}
		else if (isGameOver == 2) {
			// O wins
			WLT = "win";
		}
		else {
			WLT = playing(n+1, H);
		}
		
		return WLT;
	}
	
	private void printBoard () {
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				int serialMark = CurrentGameBoard[i][k];
				String readMark = ".";
				if (serialMark == 1) {
					readMark = "X";
				}
				else if (serialMark == 2) {
					readMark = "O";
				}
				
				System.out.print(readMark);
				System.out.print(" ");
			}
			System.out.println("");
		}
	}
	
	private Board AITurn () {
		LinkedList<int[][]> possibleMoves = new LinkedList<int[][]>();
		LinkedList<Board> preHashCheckedMoves = new LinkedList<Board>();
		
		possibleMoves = potentialMoves(possibleMoves);
		
		for (int i = 0; i < possibleMoves.size() - 1; i++) {
			Board gameBoard;
			int[][] gameBoardSate = new int[3][3];
			
			gameBoardSate = possibleMoves.get(i);
			gameBoard = new Board(findCommonBoard(gameBoardSate),gameBoardSate);
			preHashCheckedMoves.add(gameBoard);
		}
		
		checkHashed(preHashCheckedMoves);
		Board BestMove = nextBestMove();
		CurrentGameBoard = deepCopy(BestMove.GameState);
		
		
		return BestMove;
	}
	
	private Board nextBestMove () {
		Board BestMove = null;
		
		if (HashedBoards.isEmpty()) {
			int numSpots = NotHashedBoards.size();
			int randomIndex = randomSpot((numSpots == 1) ? 1 : numSpots -1);
			BestMove = NotHashedBoards.get(randomIndex);
		}
		else {
			
		}
		
		
		
		return BestMove; 
	}
	
	private void checkHashed (LinkedList<Board> CheckedMoves) {
		HashedBoards.clear();
		NotHashedBoards.clear();
		
		for (int i = 0; i < CheckedMoves.size(); i++) {
			Board gameBoard = CheckedMoves.get(i);
			Board HashedBoard = DictionaryOfBoards.contains(gameBoard);
			
			if (HashedBoard != null) {
				HashedBoards.add(HashedBoard);
			}
			else {
				NotHashedBoards.add(gameBoard);
			}
		}
	}
	
	private void dumbTurn () {
		int[] Location = new int[2];
		LinkedList<int[]> AvaibleSpots = new LinkedList<int[]>();
		
		AvaibleSpots = availableMoves(AvaibleSpots);
		int numAvaibleSpots = AvaibleSpots.size();
		int randomIndex = randomSpot((numAvaibleSpots == 1) ? 1 : numAvaibleSpots -1 ); 
		Location = AvaibleSpots.get(randomIndex);
		
		CurrentGameBoard[Location[0]][Location[1]] = 1;
	}
	
	private int randomSpot (int n) {
		Random generator = new Random();
		
		int randomIndex = generator.nextInt( n );
		
		return randomIndex;
	}
	
	private boolean movesAvailable () {
		boolean movesAvailable = false;
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				if (CurrentGameBoard[i][k] == 0) {
					movesAvailable = true;
				}
			}
		}
		
		return movesAvailable;
	}
	
	private LinkedList<int[]> availableMoves (LinkedList<int[]> AvaibleMoves) {
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				if (CurrentGameBoard[i][k] == 0) {
					int[] Location = new int[2];
					Location[0] = i;
					Location[1] = k;
					AvaibleMoves.add(Location);
				}
			}
		}
		
		return AvaibleMoves;	
	}
	
	private LinkedList<int[][]> potentialMoves (LinkedList<int[][]> potentialMoves) {
		LinkedList<int[]> AvaibleMoves = new LinkedList<int[]>();
		
		AvaibleMoves = availableMoves(AvaibleMoves);
		
		while (!AvaibleMoves.isEmpty()) {
			int[][] aMove = new int[3][3];
			int[] aMoveLoc = new int[2];
			
			aMoveLoc = AvaibleMoves.remove();
			aMove = deepCopy(CurrentGameBoard);
			
			aMove[aMoveLoc[0]][aMoveLoc[1]] = 2;
			
			potentialMoves.add(aMove);
		}
		
		return potentialMoves;
	}
	
	private long findCommonBoard ( int[][] aMove) {
		LinkedList<int[][]> SymmetryMoves = new LinkedList<int[][]>();
		
		long lowestSerial = 174678;
		long tempSerial = 0;
		
		SymmetryMoves = findSymmetry(aMove);
		
		while (!SymmetryMoves.isEmpty()) {
			aMove = SymmetryMoves.remove();
			tempSerial = serialize(aMove);
			
			if (tempSerial < lowestSerial) {
				lowestSerial = tempSerial;
			}
		}
		
		return lowestSerial;
	}
	
	private long serialize (int board[][]) {
		String serial = "";
		String tempString = "";
		int[][] temp = new int[3][3];
		temp = deepCopy(board);
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				tempString = Integer.toBinaryString(temp[i][k]);
				
				if (tempString.equals("0")) {
					serial = serial + "00";
				}
				else if (tempString.equals("10")) {
					serial = serial + "10";
				}
				else if (tempString.equals("1")) {
					serial = serial + "01";
				}
			}
		}
		
		return Integer.parseInt(serial, 2);
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
	
	private void newGame () {
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				CurrentGameBoard[i][k] = 0;
			}
		}
	}
	
	private int gameOver () {
		
		int winner = -1;
		
		if ( CurrentGameBoard[0][0] == CurrentGameBoard[0][1] && CurrentGameBoard[0][0] == CurrentGameBoard[0][2] &&  CurrentGameBoard[0][0] != 0) {
			// Found a win: A cross the Top
			winner = CurrentGameBoard[0][0];
		}
		else if ( CurrentGameBoard[1][0] == CurrentGameBoard[1][1] && CurrentGameBoard[1][0] == CurrentGameBoard[1][2] &&  CurrentGameBoard[1][0] != 0 ) {
			// Found a win: A cross the Middle
			winner = CurrentGameBoard[1][0];
		}
		else if ( CurrentGameBoard[2][0] == CurrentGameBoard[2][1] && CurrentGameBoard[2][0] == CurrentGameBoard[2][2] &&  CurrentGameBoard[2][0] != 0 ) {
			// Found a win: A cross the Bottom
			winner = CurrentGameBoard[2][0];
		}
		else if ( CurrentGameBoard[0][0] == CurrentGameBoard[1][0] && CurrentGameBoard[0][0] == CurrentGameBoard[2][0]  &&  CurrentGameBoard[0][0] != 0) {
			// Found a win: Down the left
			winner = CurrentGameBoard[0][0];
		}
		else if ( CurrentGameBoard[0][1] == CurrentGameBoard[1][1] && CurrentGameBoard[0][1] == CurrentGameBoard[2][1] &&  CurrentGameBoard[0][1] != 0 ) {
			// Found a win: Down the middle
			winner = CurrentGameBoard[0][1];
		}
		else if ( CurrentGameBoard[0][2] == CurrentGameBoard[1][2] && CurrentGameBoard[0][2] == CurrentGameBoard[2][2] &&  CurrentGameBoard[0][2] != 0 ) {
			// Found a win: Down the right
			winner = CurrentGameBoard[0][2];
		}
		else if ( CurrentGameBoard[0][0] == CurrentGameBoard[1][1] && CurrentGameBoard[0][0] == CurrentGameBoard[2][2] &&  CurrentGameBoard[0][0] != 0 ) {
			// Found a win: Diagonally from left to right 
			winner = CurrentGameBoard[0][0];
		}
		else if ( CurrentGameBoard[0][2] == CurrentGameBoard[1][1] && CurrentGameBoard[0][2] == CurrentGameBoard[2][0] &&  CurrentGameBoard[0][2] != 0 ) {
			// Found a win: Diagonally from top left to bottom right 
			winner = CurrentGameBoard[0][2];
		}
		else {
			// No win found
			if (!movesAvailable()) {
				// This Game ended with a tie
				winner = 0;
			}
		}
		
		return winner;
	}
	
	private static int[][] deepCopy(int[][] array) {
		int rows = array.length;

		int[][] newArray = (int[][]) array.clone();

		for( int row = 0; row<rows; row++) {
			newArray[row] = (int[]) array[row].clone();
		}

		return newArray;
	}
	
	
	
}
