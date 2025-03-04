package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements move calculation logic for a Bishop chess piece.
 * The Bishop moves diagonally in any direction:
 * - It can move any number of squares until it encounters another piece or the board edge.
 * - It captures an opponent's piece by landing on its square.
 * - It cannot jump over other pieces.
 */
public class BishopMovesCalculator implements PieceMovesCalculator {

    /**
     * Calculates all valid moves for a Bishop from the given position on the board.
     *
     * @param position The current position of the Bishop on the board.
     * @param board    The chess board containing all pieces.
     * @return A collection of valid moves the Bishop can make.
     */
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // The Bishop moves diagonally in all four diagonal directions
        int[][] directions = {
                {1, 1},   {1, -1},   // Down-right, Down-left
                {-1, 1},  {-1, -1}   // Up-right, Up-left
        };

        // Iterate through all diagonal directions
        for (int[] direction : directions) {
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            // Keep moving in the same direction until an obstacle is found
            while (true) {
                internalRow += direction[0]; // Move row-wise
                internalCol += direction[1]; // Move column-wise

                ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
                ChessPosition newPosition = board.toChessFormat(newInternalPosition);

                // Stop if the move goes out of bounds
                if (!board.isWithinBounds(newPosition)) {
                    break;
                }

                ChessPiece piece = board.getPiece(position);  // The Bishop piece
                ChessPiece newPiece = board.getPiece(newPosition); // Piece at the destination square
                ChessMove move = new ChessMove(position, newPosition, null);

                // If the destination square is occupied
                if (newPiece != null) {
                    // If it's an opponent's piece, add it as a capture move
                    if (piece.getTeamColor() != newPiece.getTeamColor()) {
                        validMoves.add(move);
                    }
                    break; // Stop moving in this direction (Bishops cannot jump over pieces)
                }

                // If the square is empty, add the move
                validMoves.add(move);
            }
        }
        return validMoves;
    }
}
