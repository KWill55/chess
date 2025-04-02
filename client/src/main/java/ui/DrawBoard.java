package ui;

import chess.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

public class DrawBoard {

    // Define board dimensions for chess
    private static final int BOARD_SIZE = 8;

    private ChessBoard board;
    private String playerColor;

    public DrawBoard(ChessBoard board, String playerColor) {
        this.board = board;
        this.playerColor = playerColor;
    }

    public void drawBoard() {

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        // Determine column order based on perspective.
        // White view: columns a-h (left to right)
        // Black view: columns h-a (left to right)
        char startCol = (playerColor.equals("WHITE")) ? 'a' : (char)('a' + BOARD_SIZE - 1);
        char endCol   = (playerColor.equals("WHITE")) ? (char)('a' + BOARD_SIZE - 1) : 'a';
        int colStep   = (playerColor.equals("WHITE")) ? 1 : -1;

        // Determine row order.
        // White POV: rows from 1 (bottom) to 8 (top)
        // Black POV: rows from 8 (bottom) to 1 (top)
        int startRow, endRow, rowStep;
        if (playerColor.equals("WHITE")) {
            startRow = BOARD_SIZE - 1;  // bottom of board in array corresponds to row 1
            endRow   = 0;               // top of board corresponds to row 8
            rowStep  = -1;
        } else {
            startRow = 0;
            endRow   = BOARD_SIZE - 1;
            rowStep  = 1;
        }

        // Draw column labels at the top.
        out.print("   ");
        for (char c = startCol; (colStep > 0 ? c <= endCol : c >= endCol); c += colStep) {
            out.printf("  %c  ", c);
        }
        out.println();

        // Draw each row.
        for (int row = startRow; (rowStep > 0 ? row <= endRow : row >= endRow); row += rowStep) {

            // Calculate the row label.
            int rowLabel = row + 1;
            out.printf(" %d ", rowLabel);

            // Draw each square in the row.
            for (int col = (colStep > 0 ? 0 : BOARD_SIZE - 1); (colStep > 0 ? col < BOARD_SIZE : col >= 0); col += colStep) {
                ChessPiece piece = board.squares[row][col];
                String display;

                // Set background color for the square.
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                } else {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }

                if (piece == null) {
                    display = "     "; // Empty square
                    out.print(SET_TEXT_COLOR_BLACK);
                } else {
                    // Set text color based on the team.
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        out.print(SET_TEXT_COLOR_BLACK);
                    } else {
                        out.print(SET_TEXT_COLOR_WHITE);
                    }
                    // For knights, use "N" as abbreviation.
                    if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        display = "  N  ";
                    } else {
                        // Otherwise, use the first letter of the piece type.
                        display = "  " + piece.getPieceType().name().charAt(0) + "  ";
                    }
                }
                out.print(display);
                out.print(RESET); // Reset for next square.
            }
            out.println();
        }

        // Draw column labels at the bottom.
        out.print("   ");
        for (char c = startCol; (colStep > 0 ? c <= endCol : c >= endCol); c += colStep) {
            out.printf("  %c  ", c);
        }
        out.println();
        out.print(RESET);
    }
}
