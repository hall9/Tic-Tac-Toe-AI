package proj4;
import java.security.SecureRandom;
import proj4.Board;
import proj4.HashTable;
import proj4.ModuloAddressCalculator;
import proj4.QuadraticCollisionResolver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;

public class TicTacToe {

	/* prime Numbers for Heap Size
	 * 
	 *  In the Tic-Tac-Toe Array:
	 *  ----------------------------
	 * 		X   = 01
	 * 		O   = 10
	 * 	  Empty = 00
	*/
	
	private HashTable DictionaryOfBoards;
	LinkedList<Board> HashedBoards = new LinkedList<Board>();
	LinkedList<Board> NotHashedBoards = new LinkedList<Board>();
	LinkedList<Board> Hashing = new LinkedList<Board>();
	LinkedList<Board> HashedPlayed = new LinkedList<Board>();
	int[][] CurrentGameBoard = new int[3][3];
	Board FavoriteFirstMove;
	
	int Played;
	int ties;
	int wins;
	int losses;
	double AIWinPercentage;
	double DumbWinPercentage;
	boolean P;
	
	int hashSlots;
	int hashEntries;
	int hashCollisions;
	double hashPerFull;
	
	/**
	 * This fucntion sets up the state of the hash table and deals with each game after they have finised. It updates
	 * the stats on each board and updates the hash table
	 * 
	 * @param games number of games to play
	 * @param flags -number of games, -h (history flag to print each move out), -s (save flag to save all the entries from the hash table into configs.txt), -p (possble moves flag this prints out the next moves within the hash table)
	 * @throws IOException
	 */
	public void play (int games, String[] flags) throws IOException {
		
		boolean H = (Arrays.asList(flags).contains("h")) ? true : false; 
		boolean S = (Arrays.asList(flags).contains("s")) ? true : false; 
		boolean D = (Arrays.asList(flags).contains("d")) ? true : false;
		P = (Arrays.asList(flags).contains("p")) ? true : false;
		
		hashSlots = 6967;
		hashEntries = 0;
		hashCollisions = 0;
		
		DictionaryOfBoards = new HashTable(new ModuloAddressCalculator(hashSlots), new QuadraticCollisionResolver(), hashSlots);
		
		Played = 0;
		ties = 0;
		wins = 0;
		losses = 0;
		DictionaryOfBoards.makeEmpty();
		
		// used to gett all the things within the configs.txt file and populate the hash table before the games start, only used if the save flag is active
		if (S) {
			fileManagerGrab();
		}
		
		System.out.println("Let the Games Begin!! (It may take up to 15 seconds to display the FINAL REPORT)");
		
		for (int i = 0; i < games; i++ ) {
			newGame();
			
			String WLT = playing(1,H);
			updateStats(H, WLT);
			
			// adding the new moves to the hash table
			while (!Hashing.isEmpty()) {
				Board NewlyHashed = Hashing.remove();
				NewlyHashed.updateProb(WLT);
					DictionaryOfBoards.addNode(NewlyHashed.Serial, NewlyHashed);
					hashEntries++;
			}
			
			// updating the already hashed entries after a game has ened
			for (int u = 0; u < HashedPlayed.size(); u++) {
				Board playedBefore = HashedPlayed.get(u);
				playedBefore.updateProb(WLT);
			}
			
			while (!HashedPlayed.isEmpty()) {
				Board playedHashed = HashedPlayed.remove();
				DictionaryOfBoards.setNode(playedHashed.Serial, playedHashed);
			}
		}
		
		DictionaryOfBoards.findElement(FavoriteFirstMove.Serial);
		if (S) {
			fileManagerDump();
		}
		finalReport();
		
	}
	
	/**
	 * 
	 * @param n This is like a clock, its used to see whos turn it is
	 * @param H History Flag
	 * @return if the game was a win, loss, or tie
	 */
	private String playing (int n, boolean H) {

		if ( n % 2 == 0 ) {
			Board AIBoard = AITurn();

			if (AIBoard.Played > 0 ) {
				printEachMove(H, n,AIBoard.Played, AIBoard.Probability);
				HashedPlayed.add(AIBoard);
			}
			else {
				printEachMove (H, n, 0, 0.0);
				Hashing.add(AIBoard);
			}
			
			if (n == 2 && FavoriteFirstMove == null) {
				FavoriteFirstMove = AIBoard;
			}
			else if (n == 2 && FavoriteFirstMove.Played < AIBoard.Played) {
				FavoriteFirstMove = AIBoard;
			}
			
		}
		else {
			dumbTurn();
			printEachMove (H, n, 0, 0.0);
		}

		String WLT = "null";
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
		else  if (isGameOver == -1){
			WLT = playing(n+1, H);
		}
		
		return WLT;
	}
	 
	/**
	 * 
	 * @param H Hisrtoy flag
	 * @param WLT 
	 */
	private void updateStats(boolean H, String WLT) {
		 
		if (WLT == "win") {
			wins++;
			if (H) {
				System.out.println("The game has been won by O");
			}
		}
		else if (WLT == "lost") {
			losses++;
			if (H) {
				System.out.println("The game has been won by X");
			}
		}
		else if (WLT == "tie") {
			ties++;
			if (H) {
				System.out.println("The game ended with a draw");
			}
		}
		else {
			//invalid string passed
		}
		
		Played = wins + losses + ties;
		
		double winRatio = ( (double) wins / (double) Played );
		double lostRatio = ( (double) losses / (double) Played );
		
		AIWinPercentage = winRatio * 100;
		DumbWinPercentage = lostRatio * 100;
		
		if (H) {
			System.out.println("Smart 'O' player has won " + wins + " times.");
			System.out.println("Random 'X' Player has won " + losses + " times.");
			System.out.println("Ties: " + ties);
			System.out.println("");
		}
	}
	
	private void printEachMove (boolean H, int n, int played, double Probability) {
		if (H) {
			if (n == 1) {
				System.out.println("Game Nr: " + (Played + 1));
			}

			if (n % 2 == 0) {
				System.out.println("The learning player (O) just moved:");
				printBoard ();

				if (played > 0) {
					DecimalFormat fmtObj = new DecimalFormat("##0.00");
					System.out.println("In the past, this move has led us to a win " + fmtObj.format(Probability) + " of the time.");
				}
				else {
					System.out.println("The move is new and was chosen at random.");
				}
			}
			else {
				System.out.println("The random player (X) just moved: ");
				printBoard();
			}

			System.out.println("");
		}
	}
	
	/**
	 * This fucntion just prints the current board out to the screen
	 * 
	 */
	public void printBoard () {
		printBoard (CurrentGameBoard);
	}
	
	private void printBoard (int[][] GameBoard) {
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				int serialMark = GameBoard[i][k];
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
	
	private void finalReport () {
		
		DecimalFormat fmtObj = new DecimalFormat("####0.00");
		
		double tiePercentage = 0.00;
		double tieRatio = ( (double) ties / (double) Played );
		
		tiePercentage = tieRatio * 100;
		
		hashPerFull = ( (double)hashEntries / (double)hashSlots ) * 100;
		
		System.out.println("*************************");
		System.out.println("");
		System.out.println("FINAL REPORT:");
		System.out.println("");
		System.out.println("The number of slots is: " + hashSlots);
		System.out.println("The number of entries is: " + hashEntries);
		System.out.println("The % full is: " + fmtObj.format(hashPerFull));
		System.out.println("The number of collisions is: " +  DictionaryOfBoards.getNumCollisions());
		System.out.println("Played: " + Played);
		System.out.println("Smart player has won " + wins + " times which is " + fmtObj.format(AIWinPercentage) + "%");
		System.out.println("Random has won " + losses + " times which is " + fmtObj.format(DumbWinPercentage) + "%");
		System.out.println("Draws " + ties + " times which is " + fmtObj.format(tiePercentage) + "%");
		System.out.println("My favorite first move is:");
		printBoard(FavoriteFirstMove.GameState);
		System.out.println("Won "+ FavoriteFirstMove.Wins + " out of " + FavoriteFirstMove.Played + " which is " + fmtObj.format(FavoriteFirstMove.Probability) + "%");
		System.out.println("");
		System.out.println("");
	}
	
	/**
	 * this is the AI controller which does all the finding and the best moves
	 * 
	 * @return
	 */
	private Board AITurn () {
		LinkedList<int[][]> possibleMoves = new LinkedList<int[][]>();
		LinkedList<Board> preHashCheckedMoves = new LinkedList<Board>();
		
		possibleMoves = potentialMoves(possibleMoves);
		
		for (int i = 0; i < possibleMoves.size(); i++) {
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
	
	/**
	 * This get the best move based on Probability and weights
	 * 
	 * @return best move to go
	 */
	private Board nextBestMove () {
		Board BestMove = new Board(0, null);
		int HashedNum = HashedBoards.size();
		int NonHashedNum = NotHashedBoards.size();
		double minimumProbability = 60.00;
		
		if (HashedBoards.isEmpty()) {
			int numSpots = NonHashedNum;
			int randomIndex = randomNumber((numSpots == 1) ? 1 : numSpots);
			BestMove = NotHashedBoards.get(randomIndex);
		}
		else if (!HashedBoards.isEmpty() && NotHashedBoards.isEmpty()) {
			// All moves have been seen(hashed)
			
			BestMove = HashedBoards.get(0);
			
			for (int i = 0; i < HashedNum; i++) {
				if  ( BestMove.Probability < HashedBoards.get(i).Probability ) {
					BestMove = HashedBoards.get(i);
				}
				else if ( BestMove.Probability == HashedBoards.get(i).Probability ) {
					if ( BestMove.Weight > HashedBoards.get(i).Weight) {
						BestMove = HashedBoards.get(i);
					}
				}
			}
			
			if (minimumProbability >= BestMove.Probability) {
				for (int t = 0; t < HashedNum; t++) {
					if ( BestMove.Weight > HashedBoards.get(t).Weight) {
						BestMove = HashedBoards.get(t);
					}
					else if ( BestMove.Weight == HashedBoards.get(t).Weight) {
						if ( BestMove.Probability < HashedBoards.get(t).Probability) {
							BestMove = HashedBoards.get(t);
						}
					}
				}
			}
		}
		else if (!HashedBoards.isEmpty() && !NotHashedBoards.isEmpty()) {
			// Some moves have not been seen(hashed)
			BestMove = HashedBoards.get(0);
			
			for (int i = 0; i < HashedNum; i++) {
				if  ( BestMove.Probability < HashedBoards.get(i).Probability ) {
					BestMove = HashedBoards.get(i);
				}
				else if ( BestMove.Probability == HashedBoards.get(i).Probability ) {
					if ( BestMove.Weight > HashedBoards.get(i).Weight) {
						BestMove = HashedBoards.get(i);
					}
				}
			}
			
			if (minimumProbability > BestMove.Probability) {
				int numSpots = NonHashedNum;
				int randomIndex = randomNumber((numSpots == 1) ? 1 : numSpots);
				BestMove = NotHashedBoards.get(randomIndex);
			}
		}
		
		return BestMove; 
	}
	
	/**
	 * Check to see if the any of the moves given are in the hashtable
	 * 
	 * @param CheckedMoves
	 */
	private void checkHashed (LinkedList<Board> CheckedMoves) {
		HashedBoards.clear();
		NotHashedBoards.clear();
		
		for (int i = 0; i < CheckedMoves.size(); i++) {
			Board gameBoard = CheckedMoves.get(i);
			Board HashedBoard = (Board) DictionaryOfBoards.findElement(gameBoard.Serial);
			
			if (HashedBoard != null) {
				HashedBoard.GameState = gameBoard.GameState;
				if (P) {
				System.out.println("Potential Moves (found in hashed) =  serializedBoard:" + HashedBoard.Serial + "  %P:" + HashedBoard.Probability + " Wins:" + HashedBoard.Wins + " Ties:" + HashedBoard.Ties + " Losses:" + HashedBoard.Losses + " P:" + HashedBoard.Played + " Weight:" + HashedBoard.Weight );
				}
				
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
		int randomIndex = randomNumber((numAvaibleSpots == 1) ? 1 : numAvaibleSpots); 
		Location = AvaibleSpots.get(randomIndex);
		
		CurrentGameBoard[Location[0]][Location[1]] = 1;
	}
	
	private int randomNumber (int n) {
		Random generator = new SecureRandom();
		
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
		
		//System.out.println(" Lowiest " + lowestSerial);
		return lowestSerial;
	}
	
	private int serialize (int board[][]) {
		String serial = "";
		String tempString = "";
		int[][] temp = new int[3][3];
		temp = deepCopy(board);
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				tempString = Integer.toBinaryString(temp[i][k]);
				
				if ("0".equals(tempString)) {
					serial = serial + "00";
				}
				else if ("10".equals(tempString)) {
					serial = serial + "10";
				}
				else if ("1".equals(tempString)) {
					serial = serial + "01";
				}
			}
		}
		/*
		System.out.println("*");
		printBoard(board);
		System.out.println("");
		System.out.println(serial);
		System.out.println(Integer.parseInt(serial, 2));
		System.out.println("*");
		System.out.println("");
		*/
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
	
	
	private int gameOver() {
		return gameOver(CurrentGameBoard);
	}
	
	private int gameOver (int[][] gameBoardSate) {
		
		int winner = -1;
		
		if ( gameBoardSate[0][0] == gameBoardSate[0][1] && gameBoardSate[0][0] == gameBoardSate[0][2] &&  gameBoardSate[0][0] != 0) {
			// Found a win: A cross the Top
			winner = gameBoardSate[0][0];
		}
		else if ( gameBoardSate[1][0] == gameBoardSate[1][1] && gameBoardSate[1][0] == gameBoardSate[1][2] &&  gameBoardSate[1][0] != 0 ) {
			// Found a win: A cross the Middle
			winner = gameBoardSate[1][0];
		}
		else if ( gameBoardSate[2][0] == gameBoardSate[2][1] && gameBoardSate[2][0] == gameBoardSate[2][2] &&  gameBoardSate[2][0] != 0 ) {
			// Found a win: A cross the Bottom
			winner = gameBoardSate[2][0];
		}
		else if ( gameBoardSate[0][0] == gameBoardSate[1][0] && gameBoardSate[0][0] == gameBoardSate[2][0]  &&  gameBoardSate[0][0] != 0) {
			// Found a win: Down the left
			winner = gameBoardSate[0][0];
		}
		else if ( gameBoardSate[0][1] == gameBoardSate[1][1] && gameBoardSate[0][1] == gameBoardSate[2][1] &&  gameBoardSate[0][1] != 0 ) {
			// Found a win: Down the middle
			winner = gameBoardSate[0][1];
		}
		else if ( gameBoardSate[0][2] == gameBoardSate[1][2] && gameBoardSate[0][2] == gameBoardSate[2][2] &&  gameBoardSate[0][2] != 0 ) {
			// Found a win: Down the right
			winner = gameBoardSate[0][2];
		}
		else if ( gameBoardSate[0][0] == gameBoardSate[1][1] && gameBoardSate[0][0] == gameBoardSate[2][2] &&  gameBoardSate[0][0] != 0 ) {
			// Found a win: Diagonally from left to right 
			winner = gameBoardSate[0][0];
		}
		else if ( gameBoardSate[0][2] == gameBoardSate[1][1] && gameBoardSate[0][2] == gameBoardSate[2][0] &&  gameBoardSate[0][2] != 0 ) {
			// Found a win: Diagonally from top left to bottom right 
			winner = gameBoardSate[0][2];
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
	
	private void fileManagerGrab () throws IOException {
		File f;
		f = new File("configs.txt");
		if(!f.exists()){
			f.createNewFile();
		}
		
		BufferedReader r;
		Scanner in = null;
		try {
			r = new BufferedReader(new FileReader("configs.txt"));
			in = new Scanner(r);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(in.hasNext()) {
			Board board = new Board(in.nextInt(), null);
			board.setStats(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
			DictionaryOfBoards.addNode(board.Serial, board);
			hashEntries++;
		}
		
		
	}
	
	private void fileManagerDump () throws IOException {
		File f;

		f = new File("configs.txt");

		if(!f.exists()){
			System.out.println("New file \"configs.txt\" has been created to the current directory");
		}
		else {
			System.out.println("File \"configs.txt\" already exists in the current directory");
		}

		System.out.println("Writing to file \"configs.txt\" please wait....");

			Writer output = null;
			File file = new File("configs.txt");
			output = new BufferedWriter(new FileWriter(file));

			int newhashEntries = hashEntries;
			
			while (newhashEntries != 0) {
				Board boardToWrite = null;
				
				if (newhashEntries != 0) {
					boardToWrite = (Board) DictionaryOfBoards.getNexttoDump();
				}
				
				if (boardToWrite != null) {
					output.write(boardToWrite.Serial + " " + boardToWrite.Played + " " + boardToWrite.Wins + " " + boardToWrite.Losses + " " + boardToWrite.Weight + " ");
					newhashEntries--;
				}
				
			}
			
		output.close();
		System.out.println("Done Writing to \"configs.txt\"");
	}

}
