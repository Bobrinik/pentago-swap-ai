package student_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;

public class MyTools {

	private static final int MY_WIN = 5;
	private static final int OPPONENT_WIN = -5;
	private static final int DRAW = 0;
	
	private static final int WHITE = 0;
	private static final int BLACK = 1;
	private static final int EMPTY = 1;
	
	private static String regex_black = "(0+)";
	private static String regex_white = "(1+)";
	private static String regex_empty = "(2+)";

	private static Pattern pattern_black = Pattern.compile(regex_black);
	private static Pattern pattern_white = Pattern.compile(regex_white);
	private static Pattern pattern_empty = Pattern.compile(regex_empty);
	

	public static HashMap<String, Integer> f_cache = new HashMap<>();

	public static String toString(PentagoBoardState board) {
		StringBuilder builder = new StringBuilder();

		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				builder.append(Integer.toString(board.getPieceAt(x, y).ordinal()) + ",");
			}
		}

		return builder.toString();
	}

	public static int[][] getRowCounts(PentagoBoardState board) {
		int[][] result = new int[3][6];

		for (int xPos = 0; xPos < 6; xPos++) {
			for (int yPos = 0; yPos < 6; yPos++) {
				switch (board.getPieceAt(xPos, yPos)) {
				case WHITE:
					result[0][xPos]++;
					break;
				case BLACK:
					result[1][xPos]++;
					break;
				case EMPTY:
					result[2][xPos]++;
					break;
				}
			}
		}
		return result;
	}

	public static int[][] getColumntCounts(PentagoBoardState board) {
		int[][] result = new int[3][6];

		for (int yPos = 0; yPos < 6; yPos++) {
			for (int xPos = 0; xPos < 6; xPos++) {
				switch (board.getPieceAt(xPos, yPos)) {
				case WHITE:
					result[0][yPos]++;
					break;
				case BLACK:
					result[1][yPos]++;
					break;
				case EMPTY:
					result[2][yPos]++;
					break;
				}
			}
		}
		return result;
	}

	public static int[][] getLtoRDiagonalCounts(PentagoBoardState board) {
		int[][] result = new int[3][3];

		for (int xPos = 0, yPos = 1; yPos < 6 && xPos < 6; yPos++, xPos++) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][0]++;
				break;
			case BLACK:
				result[1][0]++;
				break;
			case EMPTY:
				result[2][0]++;
				break;
			}
		}

		for (int xPos = 0, yPos = 0; yPos < 6 && xPos < 6; yPos++, xPos++) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][1]++;
				break;
			case BLACK:
				result[1][1]++;
				break;
			case EMPTY:
				result[2][1]++;
				break;
			}
		}

		for (int xPos = 1, yPos = 0; yPos < 6 && xPos < 6; yPos++, xPos++) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][2]++;
				break;
			case BLACK:
				result[1][2]++;
				break;
			case EMPTY:
				result[2][2]++;
				break;
			}
		}

		return result;
	}

	public static int[][] getRtoLDiagonalCounts(PentagoBoardState board) {
		int[][] result = new int[3][3];

		for (int xPos = 5, yPos = 1; yPos < 6 && xPos >= 0; yPos++, xPos--) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][0]++;
				break;
			case BLACK:
				result[1][0]++;
				break;
			case EMPTY:
				result[2][0]++;
				break;
			}
		}

		for (int xPos = 5, yPos = 0; yPos < 6 && xPos >= 0; yPos++, xPos--) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][1]++;
				break;
			case BLACK:
				result[1][1]++;
				break;
			case EMPTY:
				result[2][1]++;
				break;
			}
		}

		for (int xPos = 4, yPos = 0; yPos < 6 && xPos >= 0; yPos++, xPos--) {
			switch (board.getPieceAt(xPos, yPos)) {
			case WHITE:
				result[0][2]++;
				break;
			case BLACK:
				result[1][2]++;
				break;
			case EMPTY:
				result[2][2]++;
				break;
			}
		}

		return result;
	}


	public static int[] getLongestSequence(String search) {		
		ArrayList<Integer> black_match = new ArrayList<Integer>();
		ArrayList<Integer> white_match = new ArrayList<Integer>();
		ArrayList<Integer> empty_match = new ArrayList<Integer>();
		
		Matcher m = pattern_black.matcher(search);
		while (m.find()) {
		  black_match.add(m.group().length());
		}

		m = pattern_white.matcher(search);
		while (m.find()) {
			white_match.add(m.group().length());
		}
		
		m = pattern_empty.matcher(search);
		while (m.find()) {
			empty_match.add(m.group().length());
		}

		int[] result = {getMax(white_match), getMax(black_match), getMax(empty_match)};
		
		return result;
	}
	
	// {row: [white_longest_match, balck_longest_match, empty_longest_match]}
	// it should return longest consecutive for all rows
	public static int[] getLongesConsecutiveInRow(PentagoBoardState board) {
		int[] row_white = new int[6];
		int[] row_black = new int[6];
		int[] row_empty = new int[6];
		
		for (int xPos = 0; xPos < 6; xPos++) {
			StringBuilder str = new StringBuilder();
			for (int yPos = 0; yPos < 6; yPos++) {
				str.append(board.getPieceAt(xPos, yPos).ordinal()); // longest match of 1ns and 0s
			}
			int[] temp = getLongestSequence(str.toString());
			row_white[xPos] = temp[WHITE];
			row_black[xPos] = temp[BLACK];
			row_empty[xPos] = temp[EMPTY];
		}
		
		int[] result = {getMax(row_white), getMax(row_black), getMax(row_empty)};
		return result;
	}
	
	public static int[] getLongesConsecutiveInColumn(PentagoBoardState board) {
		int[] row_white = new int[6];
		int[] row_black = new int[6];
		int[] row_empty = new int[6];
		
		for (int yPos = 0; yPos < 6; yPos++) {
			StringBuilder str = new StringBuilder();
			for (int xPos = 0; xPos < 6; xPos++) {
				str.append(board.getPieceAt(xPos, yPos).ordinal()); // longest match of 1ns and 0s
			}
			
			int[] temp = getLongestSequence(str.toString());
			row_white[yPos] = temp[WHITE];
			row_black[yPos] = temp[BLACK];
			row_empty[yPos] = temp[EMPTY];
		}
		
		int[] result = {getMax(row_white), getMax(row_black), getMax(row_empty)};
		return result;
	}
	
	public static int[] getLongestConsecutiveLToR(PentagoBoardState board) {
		int[] diag_white = new int[3];
		int[] diag_black = new int[3];
		int[] diag_empty = new int[3];
		
		StringBuilder str_1 = new StringBuilder();
		for (int xPos = 0, yPos = 0; xPos < 6 && yPos < 6; yPos++, xPos++) {
				str_1.append(board.getPieceAt(xPos, yPos).ordinal());
		}
		
		StringBuilder str_2 = new StringBuilder();
		for (int xPos = 0, yPos = 1; xPos < 6 && yPos < 6; yPos++, xPos++) {
			str_2.append(board.getPieceAt(xPos, yPos).ordinal());
		}

		StringBuilder str_3 = new StringBuilder();
		for (int xPos = 0, yPos = 1; xPos < 6 && yPos < 6; yPos++, xPos++) {
			str_3.append(board.getPieceAt(xPos, yPos).ordinal());
		}
		
		int[] temp = getLongestSequence(str_1.toString());
		diag_white[0] = temp[WHITE];
		diag_black[0] = temp[BLACK];
		diag_empty[0] = temp[EMPTY];

		temp = getLongestSequence(str_2.toString());
		diag_white[1] = temp[WHITE];
		diag_black[1] = temp[BLACK];
		diag_empty[1] = temp[EMPTY];

		temp = getLongestSequence(str_3.toString());
		diag_white[2] = temp[WHITE];
		diag_black[2] = temp[BLACK];
		diag_empty[2] = temp[EMPTY];
		
		int[] result = {getMax(diag_white), getMax(diag_black), getMax(diag_empty)};
		return result;
	}
	
	
	public static int[] getLongestConsecutiveRToL(PentagoBoardState board) {
		int[] diag_white = new int[3];
		int[] diag_black = new int[3];
		int[] diag_empty = new int[3];
		
		StringBuilder str_1 = new StringBuilder();
		for (int xPos = 5, yPos = 0; xPos >= 0 && yPos < 6; yPos++, xPos--) {
				str_1.append(board.getPieceAt(xPos, yPos).ordinal());
		}
		
		StringBuilder str_2 = new StringBuilder();
		for (int xPos = 5, yPos = 1; xPos >= 0 && yPos < 6; yPos++, xPos--) {
			str_2.append(board.getPieceAt(xPos, yPos).ordinal());
		}

		StringBuilder str_3 = new StringBuilder();
		for (int xPos = 4, yPos = 1; xPos >= 0 && yPos < 6; yPos++, xPos--) {
			str_3.append(board.getPieceAt(xPos, yPos).ordinal());
		}
		
		int[] temp = getLongestSequence(str_1.toString());
		diag_white[0] = temp[WHITE];
		diag_black[0] = temp[BLACK];
		diag_empty[0] = temp[EMPTY];

		temp = getLongestSequence(str_2.toString());
		diag_white[1] = temp[WHITE];
		diag_black[1] = temp[BLACK];
		diag_empty[1] = temp[EMPTY];

		temp = getLongestSequence(str_3.toString());
		diag_white[2] = temp[BLACK];
		diag_black[2] = temp[WHITE];
		diag_empty[2] = temp[EMPTY];
		
		int[] result = {getMax(diag_white), getMax(diag_black), getMax(diag_empty)};
		return result;
	}
	
	public static int getMax(int[] arr) {
		if(arr.length == 0) return 0;
		
		int t = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (t < arr[i])
				t = arr[i];
		}

		return t;
	}

	public static int getMax(ArrayList<Integer> arr) {
		if(arr.size() == 0) return 0;
		
		int t = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (t < arr.get(i))
				t = arr.get(i);
		}
		return t;
	}
	
	public static int[] compute(PentagoBoardState board) {
		int[][] rows = getRowCounts(board);
		int[][] columns = getRowCounts(board);
		int[][] lToR = getLtoRDiagonalCounts(board);
		int[][] rToL = getRtoLDiagonalCounts(board);

		int[] maxWhite = { getMax(rows[WHITE]), getMax(columns[WHITE]), getMax(lToR[WHITE]), getMax(rToL[WHITE]) };
		int[] maxBlack = { getMax(rows[BLACK]), getMax(columns[BLACK]), getMax(lToR[BLACK]), getMax(rToL[BLACK]) };

		int[] result = { getMax(maxWhite), getMax(maxBlack) };

		return result;
	}

	public static int[] computeConsecutive(PentagoBoardState board) {

		int[] row    = getLongesConsecutiveInRow(board);
		int[] column = getLongesConsecutiveInColumn(board);
		int[] lToR   = getLongestConsecutiveLToR(board);
		int[] rToL   = getLongestConsecutiveRToL(board);
		
		int[] white = {row[WHITE], column[WHITE], lToR[WHITE], rToL[WHITE]};
		int[] black = {row[BLACK], column[BLACK], lToR[BLACK], rToL[BLACK]};
		int[] empty = {row[EMPTY], column[EMPTY], lToR[EMPTY], rToL[EMPTY]};
		
		int[] result = {getMax(white), getMax(black), getMax(empty)};
		
		return result;
	}

	public static int memoize_f(PentagoBoardState board, boolean isMax) {
		String hash = toString(board);

		if (f_cache.containsKey(hash))
			return f_cache.get(toString(board));

		int result = eval_F(board, isMax);

		f_cache.put(hash, result);

		return result;
	}

	public static int eval_F(PentagoBoardState board, boolean isMax) {
		if (board.gameOver()) {
			if (board.getWinner() == Board.DRAW)
				return DRAW;

			return isMax ? MY_WIN : OPPONENT_WIN;
		}
		
		int[] result = computeConsecutive(board);

		if (board.getTurnPlayer() == WHITE)
			return result[WHITE] - result[BLACK];
		else
			return result[BLACK] - result[WHITE];
	}

	/* =================================== */

	static class Node {
		int alpha;
		int beta;
		PentagoMove move;

		Node(int alpha, int beta, PentagoMove move) {
			this.alpha = alpha;
			this.beta = beta;
			this.move = move;
		}
	}

	public static Node a_b(PentagoBoardState board, Node prevNode, int depth, boolean isMax) {

		if (depth == 0 || board.gameOver()) {
			int r = memoize_f(board, isMax);
			return isMax ? new Node(r, prevNode.beta, prevNode.move) : new Node(prevNode.alpha, r, prevNode.move);
		}

		Node result = null, current = null;

		for (PentagoMove m : board.getAllLegalMoves()) {

			PentagoBoardState tmpBoard = (PentagoBoardState) board.clone();

			tmpBoard.processMove(m);

			current = a_b(tmpBoard, prevNode, depth - 1, !isMax);
			current.move = m;

			if (isMax && current.alpha == MY_WIN)
				return current;
			else if (!isMax && current.beta == OPPONENT_WIN)
				return current;

			if (result == null)
				result = current;
			else {

				if (isMax) {
					result = result.alpha < current.alpha ? current : result;

				} else {
					result = result.beta > current.beta ? current : result;
				}
			}
		}

		return result;
	}

	/* ============== OPENING ===================== */
	public static Move firstOrSecondMove(PentagoBoardState board) {
		// these center points give strategic advantage.
		int[][] center_points = { { 1, 1 }, { 1, 4 }, { 4, 1 }, { 4, 4 } };
		for (int[] p : center_points) {
			if (board.getPieceAt(p[0], p[1]) == Piece.EMPTY) {
				PentagoMove mv = new PentagoMove(p[0], p[1], Quadrant.BL, Quadrant.BR, board.getTurnPlayer());
				return mv;
			}
		}
		return board.getRandomMove();
	}
}