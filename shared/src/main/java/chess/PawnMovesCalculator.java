package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Implements move calculation logic for a Pawn chess piece.
 * The Pawn moves forward one square, with an optional two-square move from its starting position.
 * Pawns also capture diagonally and can promote upon reaching the last rank.
 */
public class PawnMovesCalculator implements PieceMovesCalculator {

    /**
     * Calculates all valid moves for a Pawn from the given position on the board.
     * Pawns move differently than other pieces:
     * - Forward movement (one or two squares)
     * - Diagonal captures
     * - Promotion when reaching the last rank
     *
     * @param position The current position of the Pawn on the board.
     * @param board    The chess board containing all pieces.
     * @return A collection of valid moves the Pawn can make.
     */
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);

        int[][] forwardMoves;
        int[][] captureMoves;

        // Define movement and capture directions based on the Pawn's color
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forwardMoves = new int[][]{{-1, 0}}; // White Pawns move up (-1 row)
            captureMoves = new int[][]{{-1, -1}, {-1, 1}}; // Capture diagonally
        } else {
            forwardMoves = new int[][]{{1, 0}}; // Black Pawns move down (+1 row)
            captureMoves = new int[][]{{1, -1}, {1, 1}}; // Capture diagonally
        }

        // Add valid forward moves
        addForwardMoves(position, board, validMoves, forwardMoves);
        // Add valid capture moves
        addCaptureMoves(position, board, validMoves, captureMoves);

        return validMoves;
    }

    /**
     * Adds valid forward moves for the Pawn.
     * Pawns can move one square forward, or two squares from their starting position.
     * If they reach the last rank, they are promoted.
     */
    public void addForwardMoves(ChessPosition position, ChessBoard board, Collection<ChessMove> validMoves, int[][] forwardMoves) {

        // If the Pawn is in its starting position, allow a two-square move
        if (isStartingRow(position, board)) {
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            // Move forward one square
            internalRow += forwardMoves[0][0];
            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);
            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            if (newPiece == null) {
                validMoves.add(move);
                internalRow += forwardMoves[0][0];

                // Move forward two squares if the first square is empty
                newInternalPosition = new ChessPosition(internalRow, internalCol);
                newPosition = board.toChessFormat(newInternalPosition);
                newPiece = board.getPiece(newPosition);
                move = new ChessMove(position, newPosition, null);

                if (newPiece == null) {
                    validMoves.add(move);
                }
            }
        } else {
            // Normal one-square forward move
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            internalRow += forwardMoves[0][0];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);
            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            // If the square is empty, add the move (with promotion if applicable)
            if (newPiece == null) {
                if (isPromotionRow(position, board)) {
                    addPromotionMoves(position, newPosition, board, validMoves);
                } else {
                    validMoves.add(move);
                }
            }
        }
    }

    /**
     * Adds valid diagonal capture moves for the Pawn.
     * Pawns can only capture diagonally and can be promoted upon capture if reaching the last rank.
     */
    public void addCaptureMoves(ChessPosition position, ChessBoard board, Collection<ChessMove> validMoves, int[][] captureMoves) {
        ChessPiece piece = board.getPiece(position);
        ChessPosition internalPosition = board.fromChessFormat(position);

        for (int[] direction : captureMoves) {
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            internalRow += direction[0];
            internalCol += direction[1];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);

            // Skip if out of bounds
            if (!board.isWithinBounds(newPosition)) {
                continue;
            }

            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            // If there's an opponent's piece, it's a valid capture
            if (newPiece != null && piece.getTeamColor() != newPiece.getTeamColor()) {
                if (isPromotionRow(position, board)) {
                    addPromotionMoves(position, newPosition, board, validMoves);
                } else {
                    validMoves.add(move);
                }
            }
        }
    }

    /**
     * Adds all possible promotion moves for the Pawn when reaching the last rank.
     * A Pawn can promote to a Queen, Rook, Bishop, or Knight.
     */
    public void addPromotionMoves(ChessPosition position, ChessPosition newPosition, ChessBoard board, Collection<ChessMove> validMoves) {
        validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
    }

    /**
     * Checks if the Pawn is in its starting position.
     * Pawns can move two squares forward only from their starting position.
     */
    public boolean isStartingRow(ChessPosition position, ChessBoard board) {
        int row = position.getRow();
        ChessPiece piece = board.getPiece(position);

        return (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7);
    }

    /**
     * Checks if the Pawn is in its promotion position (last rank).
     * If a Pawn reaches this row, it must be promoted.
     */
    public boolean isPromotionRow(ChessPosition position, ChessBoard board) {
        int row = position.getRow();
        ChessPiece piece = board.getPiece(position);

        return (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2);
    }
}
