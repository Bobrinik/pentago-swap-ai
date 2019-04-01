package student_player;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import student_player.MyTools.Node;
import pentago_swap.PentagoBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses to
	 * associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer() {
		super("260633499");
	}

	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */
	public Move chooseMove(PentagoBoardState boardState) {
		// You probably will make separate functions in MyTools.
		// For example, maybe you'll need to load some pre-processed best opening
		// strategies...
		int turn = boardState.getTurnNumber();
		System.out.printf("Turn: %d\n", turn);
	
		if( turn == 0 || turn == 2) {
			return MyTools.firstOrSecondMove(boardState);
		} 
		else {
			int depth = turn < 9 ? 2 : 4;
			Node node = MyTools.a_b(boardState, new Node(Integer.MAX_VALUE, -Integer.MAX_VALUE, null),  depth, true);
			return node.move;
		}
	}
}