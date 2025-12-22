package tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.chess.engine.Alliance;
import com.chess.engine.board.*;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.*;
import com.chess.engine.player.MoveTransition;

class ThreefoldRepetitionTest {

    @Test
    public void testThreefoldRepetitionWithKnightMoves() {
        // Create a simple board with kings (without castling rights) and a knight
        final Builder builder = new Builder();
        
        // Black Layout - King already moved (no castling rights)
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        
        // White Layout - King already moved (no castling rights)
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Verify initial board does not have threefold repetition
        assertFalse(board.isThreefoldRepetition(), "Initial board should not have threefold repetition");
        
        // Make moves that will repeat the position three times
        // White: Knight b1 to c3
        Move move1 = Move.MoveFactory.createMove(board, 57, 42);
        MoveTransition transition1 = board.currentPlayer().makeMove(move1);
        assertTrue(transition1.getMoveStatus().isDone());
        board = transition1.getTransitionBoard();
        
        // Black: King e8 to f8
        Move move2 = Move.MoveFactory.createMove(board, 4, 5);
        MoveTransition transition2 = board.currentPlayer().makeMove(move2);
        assertTrue(transition2.getMoveStatus().isDone());
        board = transition2.getTransitionBoard();
        
        // White: Knight c3 to b1 (back to original position)
        Move move3 = Move.MoveFactory.createMove(board, 42, 57);
        MoveTransition transition3 = board.currentPlayer().makeMove(move3);
        assertTrue(transition3.getMoveStatus().isDone());
        board = transition3.getTransitionBoard();
        
        // Black: King f8 to e8 (back to original position) - First repetition
        Move move4 = Move.MoveFactory.createMove(board, 5, 4);
        MoveTransition transition4 = board.currentPlayer().makeMove(move4);
        assertTrue(transition4.getMoveStatus().isDone());
        board = transition4.getTransitionBoard();
        assertFalse(board.isThreefoldRepetition(), "After 2 occurrences, should not have threefold repetition yet");
        
        // Repeat the sequence again
        // White: Knight b1 to c3
        Move move5 = Move.MoveFactory.createMove(board, 57, 42);
        MoveTransition transition5 = board.currentPlayer().makeMove(move5);
        assertTrue(transition5.getMoveStatus().isDone());
        board = transition5.getTransitionBoard();
        
        // Black: King e8 to f8
        Move move6 = Move.MoveFactory.createMove(board, 4, 5);
        MoveTransition transition6 = board.currentPlayer().makeMove(move6);
        assertTrue(transition6.getMoveStatus().isDone());
        board = transition6.getTransitionBoard();
        
        // White: Knight c3 to b1 (back to original position)
        Move move7 = Move.MoveFactory.createMove(board, 42, 57);
        MoveTransition transition7 = board.currentPlayer().makeMove(move7);
        assertTrue(transition7.getMoveStatus().isDone());
        board = transition7.getTransitionBoard();
        
        // Black: King f8 to e8 (back to original position) - Second repetition, should be threefold now
        Move move8 = Move.MoveFactory.createMove(board, 5, 4);
        MoveTransition transition8 = board.currentPlayer().makeMove(move8);
        assertTrue(transition8.getMoveStatus().isDone());
        board = transition8.getTransitionBoard();
        
        // Now the position has occurred 3 times
        assertTrue(board.isThreefoldRepetition(), "After 3 occurrences, should have threefold repetition");
    }

    @Test
    public void testNoThreefoldRepetitionWithDifferentMoves() {
        // Create a simple board
        final Builder builder = new Builder();
        
        // Black Layout
        builder.setPiece(new King(4, Alliance.BLACK, false, false, true, true));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        
        // White Layout
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, true, true));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Make different moves - no repetition should occur
        // White pawn moves
        Move move1 = Move.MoveFactory.createMove(board, 52, 44);
        MoveTransition transition1 = board.currentPlayer().makeMove(move1);
        assertTrue(transition1.getMoveStatus().isDone());
        board = transition1.getTransitionBoard();
        
        // Black pawn moves
        Move move2 = Move.MoveFactory.createMove(board, 12, 20);
        MoveTransition transition2 = board.currentPlayer().makeMove(move2);
        assertTrue(transition2.getMoveStatus().isDone());
        board = transition2.getTransitionBoard();
        
        // White pawn moves again
        Move move3 = Move.MoveFactory.createMove(board, 44, 36);
        MoveTransition transition3 = board.currentPlayer().makeMove(move3);
        assertTrue(transition3.getMoveStatus().isDone());
        board = transition3.getTransitionBoard();
        
        // Verify no threefold repetition
        assertFalse(board.isThreefoldRepetition(), "Different positions should not trigger threefold repetition");
    }

    @Test
    public void testInitialBoardNoRepetition() {
        final Board board = Board.createStandardBoard();
        assertFalse(board.isThreefoldRepetition(), "Standard initial board should not have threefold repetition");
    }
}
