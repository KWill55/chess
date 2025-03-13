package chess;

public class KingMovesCalculator extends AbstractFixedMovesCalculator {
    @Override
    protected int[][] getOffsets() {
        // The king can move one square in any direction.
        return new int[][] {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
    }
}
