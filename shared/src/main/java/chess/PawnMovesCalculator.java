package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Get starting position and color of the pawn
        ChessPiece pawnPiece = board.getPiece(position);
        ChessGame.TeamColor pawnColor = pawnPiece.getTeamColor();

        // Define forward and capture directions
        int[][] forwardDirections;
        int[][] captureDirections;
        if (pawnColor == ChessGame.TeamColor.WHITE) {
            forwardDirections = new int[][]{{-1, 0}, {-2, 0}};  // Forward up
            captureDirections = new int[][]{{-1, -1}, {-1, 1}}; // Diagonal capture up
        } else {
            forwardDirections = new int[][]{{1, 0}, {2, 0}};    // Forward down
            captureDirections = new int[][]{{1, -1}, {1, 1}};   // Diagonal capture down
        }

        // Add forward moves
        addForwardMoves(moves, board, position, forwardDirections, pawnColor);

        // Add capture moves
        addCaptureMoves(moves, board, position, captureDirections, pawnColor);

        return moves;
    }

    /**
     * Adds valid forward moves for the pawn.
     */
    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {

        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        for (int[] direction : directions) {
            internalRow = internalRow + direction[0];
            internalCol = internalCol + direction[1];
            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = ChessBoard.toChessFormat(newInternalPosition);

            // Check if the position is within bounds
            if (!board.isWithinBounds(internalRow, internalCol)) {
                break; // Stop if out of bounds
            }

            // Ensure the square is unoccupied
            if (board.getPiece(newPosition) != null) {
                break; // Stop if a piece is blocking forward movement
            }

            //break if its trying to move two when not at starting position
            if ((direction[0] == 2) || (direction[0] == -2)) {
                if (!isStartingPosition(position, pawnColor)) {
                    break;
                }

                //maybe delete
                int midRow = internalRow + direction[0] / 2;
                ChessPosition midInternalPosition = new ChessPosition(midRow, internalCol);
                ChessPosition midPosition = ChessBoard.toChessFormat(midInternalPosition);
                if (board.getPiece(midPosition) != null) {
                    break; // Stop if the square in between is occupied
                }
            }

            //double move

            // Add valid move
            // Check for promotion
            if (isPromotionRow(newPosition, pawnColor)) {
                addPromotionMoves(moves, position, newPosition);
                break; // Stop after adding promotion moves
            }

            moves.add(new ChessMove(position, newPosition, null));


        }
        }

    /**
     * Adds valid capture moves for the pawn.
     */
    private void addCaptureMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        for (int[] direction : directions) {
            internalRow = internalRow + direction[0];
            internalCol = internalCol + direction[1];
            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = ChessBoard.toChessFormat(newInternalPosition);

            // Check if the position is within bounds
            if (!board.isWithinBounds(internalRow, internalCol)) {
                continue; // Skip if out of bounds
            }

            // Check if there's an enemy piece to capture
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (targetPiece != null && targetPiece.getTeamColor() != pawnColor) {
                // Check for promotion
                if (isPromotionRow(newPosition, pawnColor)) {
                    addPromotionMoves(moves, position, newPosition);
                    break; // Stop after adding promotion moves
                }
                // Add capture move
                moves.add(new ChessMove(position, newPosition, null));

//                moves.add(newPosition);
            }
        }
    }

    /**
     * Checks if the pawn is in its starting position.
     */
    private boolean isStartingPosition(ChessPosition position, ChessGame.TeamColor pawnColor) {
        return (pawnColor == ChessGame.TeamColor.WHITE && position.getRow() == 2)
                || (pawnColor == ChessGame.TeamColor.BLACK && position.getRow() == 7);
    }

    /**
     * Checks if the pawn is at the promotion row.
     */
    private boolean isPromotionRow(ChessPosition position, ChessGame.TeamColor pawnColor) {
        return (pawnColor == ChessGame.TeamColor.WHITE && position.getRow() == 8)
                || (pawnColor == ChessGame.TeamColor.BLACK && position.getRow() == 1);
    }

    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
    }
}

