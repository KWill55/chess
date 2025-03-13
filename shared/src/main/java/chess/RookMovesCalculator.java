package chess;

public class RookMovesCalculator extends AbstractSlidingMovesCalculator {
    @Override
    protected int[][] getDirections() {
        // Rook moves vertically and horizontally.
        return new int[][] { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
    }
}
