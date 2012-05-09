package proj4;
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

import HashTable.HashTable;
import HashTable.ModuloAddressCalculator;
import HashTable.QuadraticCollisionResolver;
import proj4.Board;

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
	
	int played;
	int ties;
	int wins;
	int losses;
	double AIWinPercentage;
	double DumbWinPercentage;
	
	int hashSlots;
	int hashEntries;
	int hashCollisions;
	double hashPerFull;
	
	public void play (int games, String[] flags) throws IOException {
		
		boolean H = (Arrays.asList(flags).contains("h")) ? true : false; 
		boolean S = (Arrays.asList(flags).contains("s")) ? true : false; 
		//boolean D = (Arrays.asList(flags).contains("d")) ? true : false;
		
		hashSlots = 2017;
		hashEntries = 0;
		hashCollisions = 0;
		
		DictionaryOfBoards = new HashTable(new ModuloAddressCalculator(hashSlots), new QuadraticCollisionResolver(), hashSlots);
		
		played = 0;
		ties = 0;
		wins = 0;
		losses = 0;
		DictionaryOfBoards.makeEmpty();
		
		if (S) {
			fileManagerGrab();
		}
		
		for (int i = 0; i < games; i++ ) {
			newGame();
			
			String WLT = playing(1,H);
			updateStats(H, WLT);
			
			while (!Hashing.isEmpty()) {
				Board NewlyHashed = Hashing.remove();
				NewlyHashed.updateProb(WLT);
				DictionaryOfBoards.addNode(NewlyHashed.Serial, NewlyHashed);
				hashEntries += 1;
				hashPerFull = ( (double)hashEntries / (double)hashSlots ) * 100;
			}
			
			
			for (int u = 0; u < HashedPlayed.size(); u++) {
				Board playedBefore = HashedPlayed.remove();
				playedBefore.updateProb(WLT);
				//DictionaryOfBoards.removeNode(playedBefore.Serial, playedBefore);
			}
			
			/*
			while (!HashedPlayed.isEmpty()) {
				Board playedHashed = HashedPlayed.remove();
				DictionaryOfBoards.addNode(playedHashed.Serial, playedHashed);
			}
			*/
		}
		
		DictionaryOfBoards.findElement(FavoriteFirstMove.Serial);
		if (S) {
			fileManagerDump();
		}
		finalReport();
		
	}
	
	private String playing (int n, boolean H) {

		if (n % 2 == 0) {
			Board AIBoard = AITurn();

			if (AIBoard.Played > 0 ) {
				printEachMove(H, n,AIBoard.Probability);
				HashedPlayed.add(AIBoard);
			}
			else {
				printEachMove (H, n,-1);
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
			printEachMove (H, n,-1);
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
	 
	private void updateStats(boolean H, String WLT) {
		 
		if (WLT == "win") {
			wins += 1;
			if (H) {
				System.out.println("The game has been won by O");
			}
		}
		else if (WLT == "lost") {
			losses += 1;
			if (H) {
				System.out.println("The game has been won by X");
			}
		}
		else if (WLT == "tie") {
			ties += 1;
			if (H) {
				System.out.println("The game ended with a draw");
			}
		}
		else {
			//invalid string passed
		}
		
		played = wins + losses + ties;
		
		AIWinPercentage = ((double)wins / (double)played)*100;
		DumbWinPercentage = ((double)losses / (double)played)*100;
		
		if (H) {
			System.out.println("Smart 'O' player has won " + wins + " times.");
			System.out.println("Random 'X' Player has won " + losses + " times.");
			System.out.println("Ties: " + ties);
			System.out.println("");
		}
	}
	
	private void printEachMove (boolean H, int n, double Probability ) {
		if (H) {
			if (n == 1) {
				System.out.println("Game Nr: " + (played + 1));
			}

			if (n % 2 == 0) {
				System.out.println("The learning player (O) just moved:");
				printBoard ();

				if (Probability != -1) {
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
		
		System.out.println("*************************");
		System.out.println("");
		System.out.println("FINAL REPORT:");
		System.out.println("");
		System.out.println("The number of slots is: " + hashSlots);
		System.out.println("The number of entries is: " + hashEntries);
		System.out.println("The % full is: " + fmtObj.format(hashPerFull));
		System.out.println("The number of collisions is: " +  DictionaryOfBoards.getNumCollisions());
		System.out.println("Played: " + played);
		System.out.println("Smart player has won " + wins + " times which is " + fmtObj.format(AIWinPercentage) + "%");
		System.out.println("Random has won " + losses + " times which is " + fmtObj.format(DumbWinPercentage) + "%");
		System.out.println("My favorite first move is:");
		printBoard(FavoriteFirstMove.GameState);
		System.out.println("Won "+ FavoriteFirstMove.Wins + " out of " + FavoriteFirstMove.Played + " which is " + fmtObj.format(FavoriteFirstMove.Probability) + "%");
		System.out.println("");
		System.out.println("");
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
		int HashedNum = HashedBoards.size();
		int NonHashedNum = NotHashedBoards.size();
		int TotSpotsOpen = HashedNum + NonHashedNum;
		
		if (HashedBoards.isEmpty()) {
			int numSpots = NonHashedNum;
			int randomIndex = randomSpot((numSpots == 1) ? 1 : numSpots);
			BestMove = NotHashedBoards.get(randomIndex);
		}
		else if (NotHashedBoards.isEmpty())  {
			for (int i = 0; i < HashedNum; i++) {
				if (BestMove == null) {
					BestMove = HashedBoards.get(0);
				}
				else if  (BestMove.Losses >= HashedBoards.get(i).Losses ) {
					if (BestMove.Losses == HashedBoards.get(i).Losses) {
						if (BestMove.Probability < HashedBoards.get(i).Probability) {
							BestMove = HashedBoards.get(i);
						}
					}
					else {
						BestMove = HashedBoards.get(i);
					}
				}
			}
		}
		else {
			
			boolean AllZero = true;
			
			for (int z = 0; z < HashedNum; z++) {
				if (HashedBoards.get(z).Probability > 0) {
					AllZero = false;
				}
			}
			
			if (AllZero) {
				int numSpots = NonHashedNum;
				int randomIndex = randomSpot((numSpots == 1) ? 1 : numSpots);
				BestMove = NotHashedBoards.get(randomIndex);
			}
			else {
				
				Board Highprob = HashedBoards.get(0);
				
				for (int z = 0; z < HashedNum; z++) {
					if (Highprob.Probability < HashedBoards.get(z).Probability ) {
						Highprob = HashedBoards.get(z);
					}
				}
				
				double minimumProbability = ((double)HashedNum /( double)TotSpotsOpen) * 100;
				
				if (minimumProbability > Highprob.Probability ) {
					int numSpots = NonHashedNum;
					int randomIndex = randomSpot((numSpots == 1) ? 1 : numSpots);
					BestMove = NotHashedBoards.get(randomIndex);
				}
				else {
					BestMove = Highprob;
				}
			}
		}
		
		return BestMove; 
	}
	
	private void checkHashed (LinkedList<Board> CheckedMoves) {
		HashedBoards.clear();
		NotHashedBoards.clear();
		
		for (int i = 0; i < CheckedMoves.size(); i++) {
			Board gameBoard = CheckedMoves.get(i);
			Board HashedBoard = (Board) DictionaryOfBoards.findElement(gameBoard.Serial);
			
			if (HashedBoard != null) {
				HashedBoard.GameState = gameBoard.GameState;
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
		int randomIndex = randomSpot((numAvaibleSpots == 1) ? 1 : numAvaibleSpots); 
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
	
	private int findCommonBoard ( int[][] aMove) {
		LinkedList<int[][]> SymmetryMoves = new LinkedList<int[][]>();
		
		int lowestSerial = 174678;
		int tempSerial = 0;
		
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
	
	private int serialize (int board[][]) {
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
			board.setStats(in.nextInt(), in.nextInt(), in.nextInt());
			DictionaryOfBoards.addNode(board.Serial, board);
			hashEntries += 1;
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

		try{
			Writer output = null;
			File file = new File("configs.txt");
			output = new BufferedWriter(new FileWriter(file));

			for (int i = 0; i < hashEntries; i++) {
				Board boardToWrite = (Board) DictionaryOfBoards.getNexttoDump();
				if (boardToWrite != null ) {
					output.write(boardToWrite.Serial + " " + boardToWrite.Played + " " + boardToWrite.Wins + " " + boardToWrite.Losses + " ");
				}
			}
			
		output.close();
		
		}catch (Exception e){ //Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		System.out.println("Done Writing to \"configs.txt\"");
	}

}
