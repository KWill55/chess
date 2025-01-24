package chess;

import java.util.Collection;

/**
 * Responsibility: calculate legal moves for each piece type
 * Represents a single chess piece
 * Purpose: represent data of a chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public interface PieceMovesCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition);
}
