package chess;

public class BishopMovesCalculator extends AbstractSlidingMovesCalculator {
    @Override
    protected int[][] getDirections() {
        // Bishop moves diagonally.
        return new int[][] { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
    }
}
