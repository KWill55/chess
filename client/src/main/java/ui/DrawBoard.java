package ui;

import chess.*;

public class DrawBoard {

    // 8x8 board array to hold ChessPiece objects.
    private ChessPiece[][] squares = new ChessPiece[8][8];

    // Constructor: optionally initialize board pieces here.
    public DrawBoard() {
        // Example: placing a knight at position (0,1) if desired
        // squares[0][1] = new ChessPiece(ChessPiece.PieceType.KNIGHT, ChessPiece.Color.WHITE);
        // Populate other pieces as needed.
    }

    // Method to print the chessboard to the console.
    public void drawBoard() {
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                ChessPiece piece = squares[row][col];
            }
        }
    }




//                if (piece == null) {
//                    System.out.print("_ ");
//                } else {
//                    if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
//                        System.out.print("N ");
//                    } else {
//                        // Print the first letter of the piece type.
//                        System.out.print(piece.getPieceType().name().charAt(0) + " ");
//                    }
//                }
//            }
//            System.out.println();
//        }
//    }


    //        // Columns are labeled a-h.
//        String columns = "abcdefgh";
//
//
//
//
//
//
//        // Determine the row order based on perspective.
//        int startRow = whitePerspective ? 1 : 8;
//        int endRow = whitePerspective ? 8 : 1;
//        int rowStep = whitePerspective ? 1 : -1;
//
//        // Print each row.
//        for (int row = startRow; whitePerspective ? row <= endRow : row >= endRow; row += rowStep) {
//            // Print the row number on the left.
//            System.out.print(row + " ");
//            // For each column:
//            for (int col = 0; col < 8; col++) {
//                // For black perspective, reverse the column index.
//                int colIndex = whitePerspective ? col : 7 - col;
//                // Calculate whether this square is light or dark.
//                // A common rule: if row + colIndex is even, it's light; if odd, it's dark.
//                boolean isLight = ((row + colIndex) % 2 == 0);
//                // Choose a symbol (you can change these symbols as desired).
//                String square = isLight ? "□" : "■";
//                System.out.print(square + " ");
//            }
//            System.out.println();
//        }
//
//        // Print the column letters at the bottom.
//        System.out.print("  ");
//        if (whitePerspective) {
//            for (int col = 0; col < 8; col++) {
//                System.out.print(columns.charAt(col) + "  ");
//            }
//        } else {
//            for (int col = 7; col >= 0; col--) {
//                System.out.print(columns.charAt(col) + "  ");
//            }
//        }
//        System.out.println();
//    }
//
    public static void main(String[] args) {
        DrawBoard board = new DrawBoard();
        board.drawBoard();
    }


}
