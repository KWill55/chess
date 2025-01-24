package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines possible moves a pawn at a certain position is allowed to take
 */
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

        // Debugging: Print all generated moves
        System.out.println("Generated moves for pawn at " + position + ":");
        for (ChessMove move : moves) {
            System.out.println("  " + move);
        }

        // Convert moves (ChessMove) to positions (ChessPosition)
        return moves;
    }


    /**
     * this method adds the valid forward moves a pawn can take
     */
    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {

        //determine internal positions of the given position
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        //iterate through possible directions of a pawn
        for (int[] direction : directions) {

            //calcualte and store new location of the pawn
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

    /**
     * This method add the valid capture moves a pawn can take
     */
    private void addCaptureMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position,
                                 int[][] directions, ChessGame.TeamColor pawnColor) {

        //convert and store internal positions given the position argument
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        int internalRow = internalPosition.getRow();
        int internalCol = internalPosition.getColumn();

        //go through each possible direction when capturing an enemy piece
        for (int[] direction : directions) {
            //new location of the pawn if capture is valid
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

    /**
     * returns whether or not a pawn is in its starting position
     */
    private boolean isStartingPosition(ChessPosition position, ChessGame.TeamColor pawnColor) {
        return (pawnColor == ChessGame.TeamColor.WHITE && position.getRow() == 2)
                || (pawnColor == ChessGame.TeamColor.BLACK && position.getRow() == 7);
    }

    /**
     * returns if a pawn arrives at the last row of the board, awarding it a promotion
     */
    private boolean isPromotionRow(ChessPosition position, ChessGame.TeamColor pawnColor) {
        return (pawnColor == ChessGame.TeamColor.WHITE && position.getRow() == 8)
                || (pawnColor == ChessGame.TeamColor.BLACK && position.getRow() == 1);
    }

    /**
     * Adds the possible promotions of the pawns to moves
     */
    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
    }
}
