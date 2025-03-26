package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {

    // Define board dimensions for chess
    private static final int BOARD_SIZE = 8;

    // Symbols for empty square
    private static final String EMPTY = "   ";

    private static final String WK = " WK";
    private static final String BK = " BK";

    private ChessBoard board;

    public DrawBoard(ChessBoard board) {
        this.board = board;
    }

    public void drawBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        // Draw column labels
        out.print("   "); // Padding for row labels
        for (char c = 'a'; c < 'a' + BOARD_SIZE; c++) {
            out.printf("  %c  ", c);
        }
        out.println();

        // Draw each row
        for (int row = 0; row < BOARD_SIZE; row++) {
            // Draw the row number on the left
            out.printf(" %d ", BOARD_SIZE - row); // Print row labels from 8 to 1

            // Draw each square
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = board.squares[row][col];
                String display;

                // Set background color based on square color (light or dark)
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(SET_BG_COLOR_DARK_GREY);
                }

                if (piece == null) {
                    // If there is no piece, use an empty string.
                    display = "     ";
                    // Optionally, set a default text color here.
                    out.print(SET_TEXT_COLOR_BLACK);
                } else {
                    // Set the text color based on the piece's team
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        out.print(SET_TEXT_COLOR_WHITE);
                    } else {
                        out.print(SET_TEXT_COLOR_BLACK);
                    }

                    // Use a special abbreviation for knights
                    if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        display = "  N  ";
                    } else {
                        // Use the first letter of the piece type for other pieces
                        display = "  " + piece.getPieceType().name().charAt(0) + "  ";
                    }
                }
                out.print(display);

                // Reset color for the separator or next square
                out.print(RESET);
            }
            out.println();
        }


//        out.println();

        // Draw column labels below (optional)
        out.print("   ");
        for (char c = 'a'; c < 'a' + BOARD_SIZE; c++) {
            out.printf("  %c  ", c);
        }
        out.println();

        // Reset terminal colors to default
        out.print(RESET);
    }
}
