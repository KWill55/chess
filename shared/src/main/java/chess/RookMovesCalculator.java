package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements move calculation logic for a Rook chess piece.
 * The Rook moves in a straight line either horizontally or vertically
 * any number of squares until it reaches another piece or the board's edge.
 */
public class RookMovesCalculator implements PieceMovesCalculator {

    /**
     * Calculates all valid moves for a Rook from the given position on the board.
     * The Rook moves in four possible directions: up, down, left, and right.
     * It continues moving in each direction until it reaches the board's edge or another piece.
     *
     * @param position The current position of the Rook on the board.
     * @param board    The chess board, containing all current piece positions.
     * @return A collection of valid moves the Rook can make.
     */
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Define movement directions for a Rook (horizontal and vertical only)
        int[][] directions = {
                {1, 0},  // Down
                {-1, 0}, // Up
                {0, 1},  // Right
                {0, -1}  // Left
        };

        // Iterate over all possible movement directions
        for (int[] direction : directions) {
            // Convert chess position to internal board coordinates
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            // Continue moving in the current direction until blocked
            while (true) {
                internalRow += direction[0];
                internalCol += direction[1];

                // Convert the new position back to chess notation
                ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
                ChessPosition newPosition = board.toChessFormat(newInternalPosition);

                // Stop if the new position is out of bounds
                if (!board.isWithinBounds(newPosition)) {
                    break;
                }

                ChessPiece piece = board.getPiece(position);  // The Rook itself
                ChessPiece newPiece = board.getPiece(newPosition);  // Piece at the new position
                ChessMove move = new ChessMove(position, newPosition, null);

                // If there's a piece at the destination
                if (newPiece != null) {
                    if (piece.getTeamColor() != newPiece.getTeamColor()) {
                        // Capture the opposing piece and stop further movement in this direction
                        validMoves.add(move);
                    }
                    // Stop moving in this direction (can't move past any piece)
                    break;
                }

                // If the square is empty, add it as a valid move
                validMoves.add(move);
            }
        }

        return validMoves;
    }
}
