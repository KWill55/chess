package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessPosition> calculateMoves(ChessBoard board, ChessPosition position) {
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

        // Debugging: Print all generated moves
        System.out.println("Generated moves for pawn at " + position + ":");
        for (ChessMove move : moves) {
            System.out.println("  " + move);
        }

        // Convert moves (ChessMove) to positions (ChessPosition)
        return moves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toList());
    }

    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {

        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        for (int[] direction : directions) {
            int newInternalRow = internalRow + direction[0];
            int newInternalCol = internalCol + direction[1];
            ChessPosition newInternalPosition = new ChessPosition(newInternalRow, newInternalCol);
            ChessPosition newPosition = ChessBoard.toChessFormat(newInternalPosition);

            // Check if the position is within bounds
            if (!board.isWithinBounds(newInternalRow, newInternalCol)) {
                break;
            }

            // Ensure the square is unoccupied
            if (board.getPiece(newPosition) != null) {
                break;
            }

            // Double move logic
            if (Math.abs(direction[0]) == 2) {
                int midRow = internalRow + direction[0] / 2;
                ChessPosition midInternalPosition = new ChessPosition(midRow, internalCol);
                ChessPosition midPosition = ChessBoard.toChessFormat(midInternalPosition);
                if (board.getPiece(midPosition) != null || !isStartingPosition(position, pawnColor)) {
                    break;
                }
            }

            // Check for promotion
            if (isPromotionRow(newPosition, pawnColor)) {
                addPromotionMoves(moves, position, newPosition);
                continue; // Continue to next direction after adding promotion
            }

            // Add move
            moves.add(new ChessMove(position, newPosition, null));
        }
    }

    private void addCaptureMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        for (int[] direction : directions) {
            int newInternalRow = internalRow + direction[0];
            int newInternalCol = internalCol + direction[1];
            ChessPosition newInternalPosition = new ChessPosition(newInternalRow, newInternalCol);
            ChessPosition newPosition = ChessBoard.toChessFormat(newInternalPosition);

            // Check if the position is within bounds
            if (!board.isWithinBounds(newInternalRow, newInternalCol)) {
                continue;
            }

            // Check if there's an enemy piece to capture
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (targetPiece != null && targetPiece.getTeamColor() != pawnColor) {
                // Check for promotion
                if (isPromotionRow(newPosition, pawnColor)) {
                    addPromotionMoves(moves, position, newPosition);
                    continue;
                }

                // Add capture move
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
    }

    private boolean isStartingPosition(ChessPosition position, ChessGame.TeamColor pawnColor) {
        return (pawnColor == ChessGame.TeamColor.WHITE && position.getRow() == 2)
                || (pawnColor == ChessGame.TeamColor.BLACK && position.getRow() == 7);
    }

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
