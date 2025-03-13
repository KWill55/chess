package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractFixedMovesCalculator implements PieceMovesCalculator {
    protected abstract int[][] getOffsets();

    @Override
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] offsets = getOffsets();

        ChessPosition internalPos = board.fromChessFormat(position);

        for (int[] offset : offsets) {
            int newRow = internalPos.getRow() + offset[0];
            int newCol = internalPos.getColumn() + offset[1];
            ChessPosition newInternalPos = new ChessPosition(newRow, newCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPos);

            if (!board.isWithinBounds(newPosition)) {
                continue;
            }

            ChessPiece currentPiece = board.getPiece(position);
            ChessPiece targetPiece = board.getPiece(newPosition);
            // Allow the move if the square is empty, or if it's occupied by an opponent
            if (targetPiece == null || currentPiece.getTeamColor() != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }
        }

        return validMoves;
    }
}
