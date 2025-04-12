package ui;


import chess.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;


import static ui.EscapeSequences.*;


public class DrawBoard {


    // Define board dimensions for chess
    private static final int BOARD_SIZE = 8;


    private ChessBoard board;
    private String playerColor;
    private ChessPosition selectedSquare = null;
    private Collection<ChessPosition> validMovePositions = null;


    public DrawBoard(ChessBoard board, String playerColor) {
        this.board = board;
        this.playerColor = playerColor;
    }


//    public void setValidMoves(Collection<ChessMove> moves, ChessPosition selected) {
//        if (playerColor.equals("BLACK")) {
//            // Convert the selected square to display coordinates for black
//            this.selectedSquare = new ChessPosition(selected.getRow(), 9 - selected.getColumn());
//            // Convert each valid move from standard to black display coordinates.
//            this.validMovePositions = moves.stream()
//                    .map(ChessMove::getEndPosition)
//                    .map(pos -> new ChessPosition(pos.getRow(), 9 - pos.getColumn()))
//                    .collect(Collectors.toList());
//        } else {
//            this.selectedSquare = selected;
//            this.validMovePositions = moves.stream()
//                    .map(ChessMove::getEndPosition)
//                    .collect(Collectors.toList());
//        }
//    }


    public void setValidMoves(Collection<ChessMove> moves, ChessPosition selected) {
        this.selectedSquare = selected;
        this.validMovePositions = moves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toList());


        System.out.println("=== setValidMoves() ===");
        System.out.println("Selected: " + selected);
        for (ChessMove move : moves) {
            System.out.println(" -> Move: " + move);
        }
        System.out.println("=========================");
    }




    public void drawBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);


        // Determine column order based on perspective.
        char startCol = (playerColor.equals("BLACK")) ? (char)('a' + BOARD_SIZE - 1) : 'a';
        char endCol   = (playerColor.equals("BLACK")) ? 'a': (char)('a' + BOARD_SIZE - 1);
        int colStep   = (playerColor.equals("BLACK")) ? -1 : 1;


        // Determine row order.
        int startRow, endRow, rowStep;
        if (playerColor.equals("BLACK")) {
            startRow = BOARD_SIZE - 1;  // bottom of board in array corresponds to row 1
            endRow   = 0;               // top of board corresponds to row 8
            rowStep  = -1;
        } else {
            startRow = 0;
            endRow   = BOARD_SIZE - 1;
            rowStep  = 1;
        }


        // Draw column labels at the top.
        out.println();
        out.print("   ");
        for (char c = startCol; (colStep > 0 ? c <= endCol : c >= endCol); c += colStep) {
            out.printf("  %c  ", c);
        }
        out.println();


        // Draw each row.
        for (int row = startRow; (rowStep > 0 ? row <= endRow : row >= endRow); row += rowStep) {


            // Calculate the row label
            int rowLabel = 8 - row;
            out.printf(" %d ", rowLabel);


            // Draw each square in the row.
            for (int col = (colStep > 0 ? 0 : BOARD_SIZE - 1); (colStep > 0 ? col < BOARD_SIZE : col >= 0); col += colStep) {


                ChessPiece piece = board.squares[row][col];
                String display;


                ChessPosition currentPos;


//                if (playerColor.equals("BLACK")) {
//                    // For black,
//                    currentPos = new ChessPosition(8 - row, BOARD_SIZE - col);
//                } else {
//                    // For white and observe,
//                    currentPos = new ChessPosition(8 - row, col + 1);
//                }


                currentPos = new ChessPosition(8 - row, col + 1);








                // Set background color for the square.
                String squareBgColor;
                // Highlight the selected square in green.
                if (currentPos.equals(selectedSquare)) {
                    squareBgColor = SET_BG_COLOR_GREEN;
                }
                // Highlight valid move destination squares in yellow.
                else if (validMovePositions != null && validMovePositions.contains(currentPos)) {
                    squareBgColor = SET_BG_COLOR_YELLOW;
                }
                // Otherwise, use the standard chessboard square colors.
                else {
                    squareBgColor = ((row + col) % 2 == 0) ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;
                }
                out.print(squareBgColor);


                if (piece == null) {
                    display = "     "; // Empty square
                    out.print(SET_TEXT_COLOR_BLACK);
                } else {
                    // Set text color based on the team.
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        out.print(SET_TEXT_COLOR_WHITE);
                    } else {
                        out.print(SET_TEXT_COLOR_BLACK);
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
        System.out.println(" >>> ");
        out.print(RESET);


        // Debug printout after board is drawn
        System.out.println("=== DEBUG DRAW END ===");
        System.out.println("Selected Square: " + selectedSquare);
        System.out.println("Valid Move Positions:");
        if (validMovePositions != null) {
            for (ChessPosition pos : validMovePositions) {
                System.out.println(" - " + pos);
            }
        } else {
            System.out.println(" (none)");
        }
        System.out.println("Player color: " + playerColor);
        System.out.println("=======================");
    }
}

