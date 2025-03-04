package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements move calculation logic for a Knight chess piece.
 * The Knight moves in an "L" shape:
 * - Two squares in one direction, then one square perpendicular
 * - Can jump over other pieces
 * - Captures only when landing on an opponent's piece
 */
public class KnightMovesCalculator implements PieceMovesCalculator {

    /**
     * Calculates all valid moves for a Knight from the given position on the board.
     * The Knight's unique movement pattern allows it to "jump" over other pieces.
     *
     * @param position The current position of the Knight on the board.
     * @param board    The chess board containing all pieces.
     * @return A collection of valid moves the Knight can make.
     */
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Define all possible "L" shape movements for the Knight
        int[][] directions = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        // Iterate through all possible moves
        for (int[] direction : directions) {
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            // Calculate the new position after moving
            internalRow += direction[0];
            internalCol += direction[1];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);

            // Check if the new position is within board boundaries
            if (!board.isWithinBounds(newPosition)) {
                continue; // Skip out-of-bounds positions
            }

            ChessPiece piece = board.getPiece(position);
            ChessPiece newPiece = board.getPiece(newPosition);
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
