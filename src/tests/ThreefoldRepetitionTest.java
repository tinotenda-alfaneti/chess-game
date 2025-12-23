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
    
    @Test
    public void testThreefoldRepetitionWithRookMoves() {
        // Test with rook movements creating repetition
        final Builder builder = new Builder();
        
        // Black pieces
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new Rook(0, Alliance.BLACK));
        
        // White pieces
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        assertFalse(board.isThreefoldRepetition());
        
        // White Rook: a1 to a2
        board = makeMove(board, 56, 48);
        // Black Rook: a8 to a7
        board = makeMove(board, 0, 8);
        // White Rook: a2 to a1 (returning)
        board = makeMove(board, 48, 56);
        // Black Rook: a7 to a8 (returning) - 2nd occurrence
        board = makeMove(board, 8, 0);
        assertFalse(board.isThreefoldRepetition(), "After 2 occurrences, should not be threefold");
        
        // Repeat sequence
        board = makeMove(board, 56, 48);
        board = makeMove(board, 0, 8);
        board = makeMove(board, 48, 56);
        board = makeMove(board, 8, 0); // 3rd occurrence
        
        assertTrue(board.isThreefoldRepetition(), "After 3 occurrences, should be threefold repetition");
    }
    
    @Test
    public void testFourOccurrencesStillDetectedAsThreefold() {
        // Verify that more than 3 occurrences is still detected
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Create 4 occurrences of the same position
        for (int i = 0; i < 3; i++) {
            board = makeMove(board, 57, 42);  // White knight out
            board = makeMove(board, 1, 18);   // Black knight out
            board = makeMove(board, 42, 57);  // White knight back
            board = makeMove(board, 18, 1);   // Black knight back
        }
        
        assertTrue(board.isThreefoldRepetition(), "After 4 occurrences, should still detect threefold");
    }
    
    @Test
    public void testDifferentPlayerToMovePreventsFalsePositive() {
        // Same piece positions but different player to move should NOT be threefold
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Make a move and undo it - positions look same but player to move differs
        board = makeMove(board, 56, 48); // White rook moves
        assertFalse(board.isThreefoldRepetition());
        
        // Even if we create similar patterns, different player to move means different position
        board = makeMove(board, 4, 5);   // Black king moves
        board = makeMove(board, 48, 56); // White rook back
        board = makeMove(board, 5, 4);   // Black king back
        
        // This creates second occurrence with WHITE to move
        assertFalse(board.isThreefoldRepetition(), "Should not be threefold yet");
    }
    
    @Test
    public void testCastlingRightsAffectPositionUniqueness() {
        // Positions with different castling rights should be considered different
        final Builder builder1 = new Builder();
        builder1.setPiece(new King(4, Alliance.BLACK, false, false, true, true));
        builder1.setPiece(new King(60, Alliance.WHITE, false, false, true, true));
        builder1.setMoveMaker(Alliance.WHITE);
        Board boardWithCastling = builder1.build();
        
        final Builder builder2 = new Builder();
        builder2.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder2.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder2.setMoveMaker(Alliance.WHITE);
        Board boardNoCastling = builder2.build();
        
        // These are different positions due to castling rights
        assertFalse(boardWithCastling.isThreefoldRepetition());
        assertFalse(boardNoCastling.isThreefoldRepetition());
    }
    
    @Test
    public void testEnPassantAffectsPositionUniqueness() {
        // Test that en passant availability affects position key
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Make a pawn jump (creates en passant opportunity)
        Move pawnJump = Move.MoveFactory.createMove(board, 48, 32);
        MoveTransition transition = board.currentPlayer().makeMove(pawnJump);
        assertTrue(transition.getMoveStatus().isDone());
        board = transition.getTransitionBoard();
        
        // Position with en passant available is different from position without
        assertNotNull(board.getEnPassantPawn(), "En passant pawn should be set");
        assertFalse(board.isThreefoldRepetition());
    }
    
    @Test
    public void testComplexScenarioWithMultiplePieces() {
        // More realistic scenario with multiple pieces
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Create repetition with multiple pieces present
        for (int i = 0; i < 2; i++) {
            board = makeMove(board, 57, 42);  // White knight
            board = makeMove(board, 1, 18);   // Black knight
            board = makeMove(board, 42, 57);  // White knight back
            board = makeMove(board, 18, 1);   // Black knight back
        }
        
        assertTrue(board.isThreefoldRepetition(), "Should detect threefold with multiple pieces");
    }
    
    @Test
    public void testExactlyTwoOccurrencesNotThreefold() {
        // Verify that exactly 2 occurrences is NOT threefold
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Create exactly 2 occurrences
        board = makeMove(board, 56, 48);
        board = makeMove(board, 4, 5);
        board = makeMove(board, 48, 56);
        board = makeMove(board, 5, 4);
        
        assertFalse(board.isThreefoldRepetition(), "Exactly 2 occurrences should NOT be threefold");
    }
    
    @Test
    public void testPositionHistoryPersistsAcrossMoves() {
        // Verify position history accumulates correctly
        final Builder builder = new Builder();
        
        builder.setPiece(new King(4, Alliance.BLACK, false, false, false, false));
        builder.setPiece(new King(60, Alliance.WHITE, false, false, false, false));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        
        // Make several moves
        board = makeMove(board, 57, 42);
        assertNotNull(board.getPositionHistory(), "Position history should exist");
        assertTrue(board.getPositionHistory().size() > 0, "Position history should have entries");
        
        board = makeMove(board, 4, 5);
        assertTrue(board.getPositionHistory().size() > 1, "Position history should accumulate");
    }
    
    /**
     * Helper method to make a move and return the resulting board
     */
    private Board makeMove(Board board, int from, int to) {
        Move move = Move.MoveFactory.createMove(board, from, to);
        MoveTransition transition = board.currentPlayer().makeMove(move);
        assertTrue(transition.getMoveStatus().isDone(), 
                  "Move from " + from + " to " + to + " should be legal");
        return transition.getTransitionBoard();
    }
}
