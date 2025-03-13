package chess;

public class KnightMovesCalculator extends AbstractFixedMovesCalculator {
    @Override
    protected int[][] getOffsets() {
        // The knight's moves: two in one direction and one perpendicular.
        return new int[][] {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };
    }
}
