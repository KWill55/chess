package chess;

import java.util.Collection;

public interface PieceMovesCalculator{
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board);
}