package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

//    @Override
//    public String toString() {
//        return "Pos{" +
//                "row=" + row +
//                ", col=" + col +
//                '}';
//    }

//    @Override
//    public String toString() {
//        char colChar = (char) ('a' + col);  // 0 → 'a', 1 → 'b', ...
//        int rowNum = row + 1;               // 0 → 1, 1 → 2, ...
//        return "" + colChar + rowNum;       // e.g., "e2"
//    }

//    @Override
//    public String toString() {
//        // Internal array coordinates (0-indexed)
//        String arrayFormat = "(" + row + ", " + col + ")";
//
//        // Chess notation: assume that column 0 maps to 'a'
//        // and that row 0 represents the bottom row.
//        // (If your internal board uses 0 for bottom, then chess rank = row + 1)
//        char fileChar = (char) ('a' + col);
//        int rank = row + 1;
//        String chessFormat = "" + fileChar + rank;
//
//        return "ChessPosition{" + "array=" + arrayFormat + ", chess=" + chessFormat + "}";
//    }

    @Override
    public String toString() {
        // Subtract 1 to convert 1-indexed column to 0-indexed for mapping to a letter.
        char fileChar = (char) ('a' + (col - 1));
        return "" + fileChar + row;
    }



}