package chess;

public class QueenMovesCalculator extends AbstractSlidingMovesCalculator {
    @Override
    protected int[][] getDirections() {
        // Queen can move like both a Rook and a Bishop.
        return new int[][] {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
    }
}
