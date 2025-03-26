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
        out.print("    "); // Padding for row labels
        for (char c = 'a'; c < 'a' + BOARD_SIZE; c++) {
            out.printf("  %c  ", c);
        }
        out.println();

        // Draw each row
        for (int row = 0; row < BOARD_SIZE; row++) {
//            out.println();

            // Draw the row number on the left
            out.printf(" %d ", BOARD_SIZE - row); // Print row labels from 8 to 1

            // Draw each square
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(SET_TEXT_COLOR_BLACK);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(SET_TEXT_COLOR_WHITE);
                }

                // Get the piece on the current square, if any
                ChessPiece piece = board.squares[row][col];
                String display;
                if (piece == null) {
                    display = "     "; // Empty square
                } else {
                    display = "  " + piece.getPieceType().name().charAt(0) + "  ";
                }
                out.print(display);

                // Reset color for the separator
                out.print(RESET);
            }
            out.println();
        }

        out.println();

        // Draw column labels below (optional)
        out.print("    ");
        for (char c = 'a'; c < 'a' + BOARD_SIZE; c++) {
            out.printf("  %c  ", c);
        }
        out.println();

        // Reset terminal colors to default
        out.print(RESET);
    }
}

























//
//package ui;
//
//import java.io.PrintStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Random;
//
//import static ui.EscapeSequences.*;
//
//import chess.*;
//
//public class DrawBoard {
//
//    // Board dimensions.
//    private static final int BOARD_LENGTH = 8;
//    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
//    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
//
//    // Padded characters.
//    private static final String EMPTY = "   ";
//    private static final String X = " X ";
//    private static final String O = " O ";
//
//    // 8x8 board array to hold ChessPiece objects.
//    private ChessPiece[][] squares = new ChessPiece[8][8];
//
//    public DrawBoard() {
//    }
//
//    // Method to print the chessboard to the console.
//    public void drawBoard() {
//        for (int row = 0; row < squares.length; row++) {
//            for (int col = 0; col < squares[row].length; col++) {
//                ChessPiece piece = squares[row][col];
//            }
//        }
//    }
//
//
//
//
////                if (piece == null) {
////                    System.out.print("_ ");
////                } else {
////                    if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
////                        System.out.print("N ");
////                    } else {
////                        // Print the first letter of the piece type.
////                        System.out.print(piece.getPieceType().name().charAt(0) + " ");
////                    }
////                }
////            }
////            System.out.println();
////        }
////    }
//
//
//    //        // Columns are labeled a-h.
////        String columns = "abcdefgh";
////
////
////
////
////
////
////        // Determine the row order based on perspective.
////        int startRow = whitePerspective ? 1 : 8;
////        int endRow = whitePerspective ? 8 : 1;
////        int rowStep = whitePerspective ? 1 : -1;
////
////        // Print each row.
////        for (int row = startRow; whitePerspective ? row <= endRow : row >= endRow; row += rowStep) {
////            // Print the row number on the left.
////            System.out.print(row + " ");
////            // For each column:
////            for (int col = 0; col < 8; col++) {
////                // For black perspective, reverse the column index.
////                int colIndex = whitePerspective ? col : 7 - col;
////                // Calculate whether this square is light or dark.
////                // A common rule: if row + colIndex is even, it's light; if odd, it's dark.
////                boolean isLight = ((row + colIndex) % 2 == 0);
////                // Choose a symbol (you can change these symbols as desired).
////                String square = isLight ? "□" : "■";
////                System.out.print(square + " ");
////            }
////            System.out.println();
////        }
////
////        // Print the column letters at the bottom.
////        System.out.print("  ");
////        if (whitePerspective) {
////            for (int col = 0; col < 8; col++) {
////                System.out.print(columns.charAt(col) + "  ");
////            }
////        } else {
////            for (int col = 7; col >= 0; col--) {
////                System.out.print(columns.charAt(col) + "  ");
////            }
////        }
////        System.out.println();
////    }
////
//    public static void main(String[] args) {
//        DrawBoard board = new DrawBoard();
//        board.drawBoard();
//    }
//
//
//}
