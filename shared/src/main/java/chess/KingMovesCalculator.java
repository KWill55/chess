package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements move calculation logic for a King chess piece.
 * The King moves one square in any direction:
 * - Horizontally, vertically, or diagonally.
 * - Can capture opponent pieces by landing on them.
 * - Cannot move into a square occupied by a piece of the same color.
 */
public class KingMovesCalculator implements PieceMovesCalculator {

    /**
     * Calculates all valid moves for a King from the given position on the board.
     *
     * @param position The current position of the King on the board.
     * @param board    The chess board containing all pieces.
     * @return A collection of valid moves the King can make.
     */
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // The King can move one square in any direction
        int[][] directions = {
                {1, 0},  {-1, 0},  {0, 1},  {0, -1},  // Vertical & Horizontal moves
                {1, 1},  {1, -1},  {-1, 1},  {-1, -1} // Diagonal moves
        };

        // Iterate through all possible moves
        for (int[] direction : directions) {
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            // Move the King one step in the current direction
            internalRow += direction[0];
            internalCol += direction[1];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);

            // Check if the new position is within board boundaries
            if (!board.isWithinBounds(newPosition)) {
                continue; // Skip out-of-bounds positions
            }

            ChessPiece piece = board.getPiece(position);  // The King piece
            ChessPiece newPiece = board.getPiece(newPosition); // Piece at the destination square
            ChessMove move = new ChessMove(position, newPosition, null);

            // If the destination square is occupied
            if (newPiece != null) {
                // Allow capturing only if the piece is an opponent's
                if (piece.getTeamColor() != newPiece.getTeamColor()) {
                    validMoves.add(move);
                }
                continue; // Skip further processing
            }

            // If the square is empty, add the move
            validMoves.add(move);
        }

        return validMoves;
    }
}
