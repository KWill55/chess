package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessPosition> calculateMoves(ChessBoard board, ChessPosition position) {
        // Return an empty collection for now
        return new ArrayList<>();
    }
}