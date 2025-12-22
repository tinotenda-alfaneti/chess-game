package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;

    private final Player currentPlayer;

    private final Pawn enPassantPawn;
    
    private final Map<String, Integer> positionHistory;

    public Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);


        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
        
        // Initialize position history from builder or create new map
        if (builder.positionHistory != null) {
            this.positionHistory = new HashMap<>(builder.positionHistory);
        } else {
            this.positionHistory = new HashMap<>();
        }
        // Record current position
        final String currentPositionKey = generatePositionKey();
        this.positionHistory.put(currentPositionKey, this.positionHistory.getOrDefault(currentPositionKey, 0) + 1);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {

        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);

    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for (final Tile tile : gameBoard) {
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    private static List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        //Black layout
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK, true, true));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        //White layout
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE, true, true));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }
    
    /**
     * Generates a unique key representing the current board position.
     * The key includes piece positions and types, and the current player to move.
     * This is used for detecting threefold repetition.
     */
    private String generatePositionKey() {
        final StringBuilder keyBuilder = new StringBuilder();
        
        // Add all pieces to the key
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final Tile tile = this.gameBoard.get(i);
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                keyBuilder.append(i)
                          .append(":")
                          .append(piece.getPieceAlliance())
                          .append(piece.getPieceType())
                          .append(";");
            }
        }
        
        // Add current player to move (important for threefold repetition)
        keyBuilder.append("|").append(this.currentPlayer.getAlliance());
        
        // Add castling rights (important for position uniqueness)
        keyBuilder.append("|")
                  .append(this.whitePlayer.isKingSideCastleCapable()).append(",")
                  .append(this.whitePlayer.isQueenSideCastleCapable()).append(",")
                  .append(this.blackPlayer.isKingSideCastleCapable()).append(",")
                  .append(this.blackPlayer.isQueenSideCastleCapable());
        
        // Add en passant pawn position if exists
        if (this.enPassantPawn != null) {
            keyBuilder.append("|EP:").append(this.enPassantPawn.getPiecePosition());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * Checks if the current position has occurred three times in the game.
     * @return true if threefold repetition has occurred, false otherwise
     */
    public boolean isThreefoldRepetition() {
        final String currentPositionKey = generatePositionKey();
        final Integer occurrences = this.positionHistory.get(currentPositionKey);
        return occurrences != null && occurrences >= 3;
    }
    
    /**
     * Gets the position history map for propagation to new boards.
     * @return the position history map
     */
    public Map<String, Integer> getPositionHistory() {
        return this.positionHistory;
    }

    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;
        Map<String, Integer> positionHistory;

        public Builder(){

            boardConfig = new HashMap<>();

        }
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }
        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }
        
        public Builder setPositionHistory(final Map<String, Integer> positionHistory) {
            this.positionHistory = positionHistory;
            return this;
        }
    }
}
