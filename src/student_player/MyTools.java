package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
	private static final int DRAW = 0;
	private static final int OPPONENT_WIN = -5;

	
	private static final int WHITE = 0;
	private static final int BLACK = 1;
	
	private static String regex_black = "(b+)";
	private static String regex_white = "(w+)";

	private static Pattern pattern_black = Pattern.compile(regex_black);
	private static Pattern pattern_white = Pattern.compile(regex_white);

	

	public static HashMap<String, int[]> f_cache = new HashMap<>();

	public static int[] getLongestSequence(String search) {		
		ArrayList<Integer> black_match = new ArrayList<Integer>();
		ArrayList<Integer> white_match = new ArrayList<Integer>();
		
		Matcher m = pattern_black.matcher(search);
		while (m.find()) {
		  black_match.add(m.group().length());
		}

		m = pattern_white.matcher(search);
		while (m.find()) {
			white_match.add(m.group().length());
		}
		

		int[] result = {getMax(white_match), getMax(black_match)};
		
		return result;
	}
	
	// {row: [white_longest_match, balck_longest_match, empty_longest_match]}
	public static int[] getLongesConsecutiveInRow(PentagoBoardState board) {
		int[] row_white = new int[6];
		int[] row_black = new int[6];
		
		for (int xPos = 0; xPos < 6; xPos++) {
			StringBuilder str = new StringBuilder();
			for (int yPos = 0; yPos < 6; yPos++) {
				str.append(board.getPieceAt(xPos, yPos)); // longest match of 1ns and 0s
			}
			int[] temp = getLongestSequence(str.toString());
			row_white[xPos] = temp[WHITE];
			row_black[xPos] = temp[BLACK];
		}
		
		int[] result = {getMax(row_white), getMax(row_black)};
		return result;
	}
	
	public static int[] getLongesConsecutiveInColumn(PentagoBoardState board) {
		int[] row_white = new int[6];
		int[] row_black = new int[6];
		int[] row_empty = new int[6];
		
		for (int yPos = 0; yPos < 6; yPos++) {
			StringBuilder str = new StringBuilder();
			for (int xPos = 0; xPos < 6; xPos++) {
				str.append(board.getPieceAt(xPos, yPos)); // longest match of 1ns and 0s
			}
			
			int[] temp = getLongestSequence(str.toString());
			row_white[yPos] = temp[WHITE];
			row_black[yPos] = temp[BLACK];
		}
		
		int[] result = {getMax(row_white), getMax(row_black), getMax(row_empty)};
		return result;
	}
	
	public static int[] getLongestConsecutiveLToR(PentagoBoardState board) {
		int[] diag_white = new int[12];
		int[] diag_black = new int[12];
		
		int idx = 0;
		int dim = 6;
	    for( int k = 0 ; k < dim * 2 ; k++ ) {
	    	StringBuilder str = new StringBuilder();
	        for( int yPos = 0 ; yPos <= k ; yPos++ ) {
	            int xPos = k - yPos;
	            if( xPos < dim && yPos < dim ) {
	                str.append(board.getPieceAt(xPos, yPos));
	            }
	        }
	        
			int[] temp = getLongestSequence(str.toString());
			diag_white[idx] = temp[WHITE];
			diag_black[idx] = temp[BLACK];
			idx++;
	    }
				
		int[] result = {getMax(diag_white), getMax(diag_black)};
		return result;
	}
	
	
	public static int[] getLongestConsecutiveRToL(PentagoBoardState board) {
		int[] diag_white = new int[12];
		int[] diag_black = new int[12];

		int idx = 0;
		int dim = 6;
		for (int k = (dim - 1) * 2; k >= 0; k--) {
			StringBuilder str = new StringBuilder();
			for (int j = k; j >= 0; j--) {
				int i = k - j;
				if (i < dim && j < dim) {
					str.append(board.getPieceAt(j, i));
				}
			}
			int[] temp = getLongestSequence(str.toString());
			diag_white[idx] = temp[WHITE];
			diag_black[idx] = temp[BLACK];
			idx++;
		}

		int[] result = { getMax(diag_white), getMax(diag_black) };
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
	
	public static int[] computeConsecutive(PentagoBoardState board) {

		int[] row    = getLongesConsecutiveInRow(board);
		int[] column = getLongesConsecutiveInColumn(board);
		
		int[] lToR   = getLongestConsecutiveLToR(board);
		
		int[] rToL   = getLongestConsecutiveRToL(board);
		
		int[] white = {row[WHITE], column[WHITE], lToR[WHITE], rToL[WHITE]};
		int[] black = {row[BLACK], column[BLACK], lToR[BLACK], rToL[BLACK]};
		
		int[] result = {getMax(white), getMax(black)};
		
		return result;
	}

	public static int memoize_f(PentagoBoardState board, boolean isMax) {
		if (board.gameOver()) {
			if (board.getWinner() == Board.DRAW)
				return DRAW;

			return isMax ? MY_WIN : OPPONENT_WIN;
		}

		String hash = toString(board);

		int[] result;
		if (f_cache.containsKey(hash)) {
			result = f_cache.get(hash);
//			if(writer != null)
//				writer.println(hash+"<-->"+result[0]+","+result[1]);		
		} else {
			result = computeConsecutive(board);
		}

		f_cache.put(hash, result);

		return result[board.getTurnPlayer()] - result[board.getOpponent()];
	}

	public static int memoize_f_2(PentagoBoardState board, boolean isMax) {
		if (board.gameOver()) {
			if (board.getWinner() == Board.DRAW)
				return DRAW;

			return isMax ? MY_WIN : OPPONENT_WIN;
		}

		String hash = toString(board);

		int[] result;
		if (f_cache.containsKey(hash)) {
			result = f_cache.get(hash);
		} else {
			result = computeConsecutive(board);
		}

		f_cache.put(hash, result);
		
		System.out.println(Arrays.toString(result));

		return result[board.getTurnPlayer()] - result[board.getOpponent()];
	}
	public static List<Node> orderMoves(PentagoBoardState board, boolean isMax){
		ArrayList<Node> my_nodes = new ArrayList<>();

		for(PentagoMove mv :board.getAllLegalMoves()) {
			PentagoBoardState tmp = (PentagoBoardState) board.clone();
			tmp.processMove(mv);

			Node my_node;
			
			if(isMax) {
				my_node = new Node(memoize_f(tmp, isMax), Integer.MAX_VALUE, mv, tmp);
			}
			else {
				my_node = new Node(-Integer.MAX_VALUE, memoize_f(tmp, isMax), mv, tmp);
			}

			my_nodes.add(my_node);
		}
		
		Collections.sort(my_nodes, new Comparator<Node>(){
		       public int compare(Node o1, Node o2) {
		    	   if(isMax) {
		    		   if(o1.bestMax < o2.bestMax) return 1;
		    		   else if(o1.bestMax > o2.bestMax) return -1;
		    		   return 0;
		    	   }
		    	   else {
		    		   if(o1.bestMin > o2.bestMin) return 1;
		    		   else if(o1.bestMin < o2.bestMin) return -1;
		    		   return 0;
		    	   }
		        }
		    });
		int end = 0;
		if(isMax) {
			int max = my_nodes.get(0).bestMax;

			for(Node m : my_nodes) {
				if(max != m.bestMax) break;
				end++;
			}
		}
		else {
			int min = my_nodes.get(0).bestMin;
			for(Node m : my_nodes) {
				if(min != m.bestMin) break;
				end++;
			}
		}

		return my_nodes.subList(0, end);
	}
	
	public static String toString(PentagoBoardState board) {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 6; j++) {
				switch(board.getPieceAt(i, j)) {
				case WHITE:
					str.append("[x]");
					break;
				case BLACK:
					str.append("[o]");
					break;
				case EMPTY:
					str.append("[ ]");
					break;
				}
			}
			str.append("\n");
		}
		return str.toString();
	}
	
	public static String toString(ArrayList<Node> res, boolean IsMax) {
		StringBuilder str = new StringBuilder();
		
		for(Node m : res) {
			if(IsMax)str.append(m.bestMax);
			else str.append(m.bestMin);
			str.append(",");
		}
		
		return str.toString();
	}
	/* =================================== */

	static class Node {
		int bestMax;
		int bestMin;
		PentagoMove move;
		PentagoBoardState node_board;

		Node(int bestMax, int bestMin, PentagoMove move, PentagoBoardState node_board) {
			this.bestMax = bestMax;
			this.bestMin = bestMin;
			this.move = move;
			this.node_board = node_board;
		}
	}


	public static boolean isTimeout(long start_time) {
		long time_passed = 	System.currentTimeMillis() - start_time;
		
		if(time_passed > 1800)	{
			return true;
		}
		return false;
	}

	
	public static PentagoMove findMoveFor(PentagoBoardState current_board, long start_time) {
		int a = -Integer.MAX_VALUE;
		int b =  Integer.MAX_VALUE;
		int value = a;
		int depth = 5;
		Node best = null;

		for (Node current_node : orderMoves(current_board, false)) {
			
			value = alpha_beta(current_node.node_board, a, b, depth, true, start_time);
			current_node.bestMax = value;
			
			if(best == null) best = current_node;
			
			if(best.bestMax < current_node.bestMax) {
				best = current_node;
			}
				
			if(isTimeout(start_time)) {
				return best.move;
			}
		}

		return best.move;
	}
	
	
	public static int alpha_beta(PentagoBoardState current_board, int a, int b, int depth, boolean isMax, long start_time) {
		if (depth == 0 || current_board.gameOver()) return memoize_f(current_board, isMax);

		if (isMax) {
			int value = -Integer.MAX_VALUE;

			for (Node current_node : orderMoves(current_board, isMax)) {
				value = alpha_beta(current_node.node_board, a, b, depth-1, !isMax, start_time);
				a = Math.max(a, value);
				if(a >= b) break;
				if(isTimeout(start_time)) return value;
			}
			return value;
		} else {
			int value = Integer.MAX_VALUE;

			for (Node current_node : orderMoves(current_board, isMax)) {
				value = alpha_beta(current_node.node_board, a, b, depth-1, !isMax, start_time);
				b = Math.min(b, value);
				if(a >= b) break;
				if(isTimeout(start_time)) return value;
			}
			return value;
		}
	}

	/* ============== OPENING ===================== */
	public static Move opening(PentagoBoardState board) {
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