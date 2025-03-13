package chess;


import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractSlidingMovesCalculator implements PieceMovesCalculator {
    protected abstract int[][] getDirections();

    @Override
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = getDirections();
        ChessPosition start = board.fromChessFormat(position);
        for (int[] direction : directions) {
            int row = start.getRow();
            int col = start.getColumn();
            while (true) {
                row += direction[0];
                col += direction[1];
                ChessPosition newInternalPos = new ChessPosition(row, col);
                ChessPosition newPos = board.toChessFormat(newInternalPos);
                if (!board.isWithinBounds(newPos)) {
                    break;
                }
                ChessMove move = new ChessMove(position, newPos, null);
                ChessPiece currentPiece = board.getPiece(position);
                ChessPiece encountered = board.getPiece(newPos);
                if (encountered != null) {
                    if (currentPiece.getTeamColor() != encountered.getTeamColor()) {
                        validMoves.add(move);
                    }
                    break; // Can't jump over pieces.
                }
                validMoves.add(move);
            }
        }
        return validMoves;
    }
}
