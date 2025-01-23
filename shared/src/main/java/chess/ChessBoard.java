package chess;


import java.sql.SQLOutput;

/**
 * Board
 * [0][0] [0][1] [0][2] [0][3] [0][4] [0][5] [0][6] [0][7]
 * [1][0] [1][1] [1][2] [1][3] [1][4] [1][5] [1][6] [1][7]
 * [2][0] [2][1] [2][2] [2][3] [2][4] [2][5] [2][6] [2][7]
 * [3][0] [3][1] [3][2] [3][3] [3][4] [3][5] [3][6] [3][7]
 * [4][0] [4][1] [4][2] [4][3] [4][4] [4][5] [4][6] [4][7]
 * [5][0] [5][1] [5][2] [5][3] [5][4] [5][5] [5][6] [5][7]
 * [6][0] [6][1] [6][2] [6][3] [6][4] [6][5] [6][6] [6][7]
 * [7][0] [7][1] [7][2] [7][3] [7][4] [7][5] [7][6] [7][7]
 * .
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8]; //creates squares object

    public ChessBoard() {
    }


    /**
     * converts ChessPosition from internal array to chess (Chess) format
     */
    public static ChessPosition toChessFormat(ChessPosition internalPosition) {
        int chessRow = 8 - internalPosition.getRow() ;  // Converts top-down 0-based row to bottom-up 1-based row
        int chessCol = internalPosition.getColumn() + 1; // Converts 0-based column to 1-based column

        // Debug: Print the conversion
        System.out.println("Converting from internal format: " + internalPosition + " to chesss format: (" + chessRow + ", " + chessCol + ")");
//        Thread.dumpStack();
        return new ChessPosition(chessRow, chessCol);
    }


    /**
     * converts position from Chess format to internal array format
     */
    public static ChessPosition fromChessFormat(ChessPosition chessPosition) {
        int internalRow = 8 - chessPosition.getRow(); // Chess row 1 → Internal row 7
        int internalCol = chessPosition.getColumn() - 1; // Chess col 1 → Internal col 0

        System.out.println("Converting from Chess format: " + chessPosition + " to internal format: (" + internalRow + ", " + internalCol + ")");
        return new ChessPosition(internalRow, internalCol);
    }


    /**
     * tells whether a row and col in Chess format are within the 8x8 grid
     */
    public boolean isWithinBounds(int row, int col) {
        //in Chess format
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        return true;


        //in internal format?
//        ChessPosition chessPosition = new ChessPosition(row,col);
//        ChessPosition internalChessPosition = ChessBoard.fromChessFormat(chessPosition);
//        return internalChessPosition.getRow >= 0 && row <= 8 && col >= 0 && col <= 8;
        }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        squares[internalPosition.getRow()][internalPosition.getColumn()] = piece;
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        System.out.println("Accessing chess position: " + position + " array position" + internalPosition);

        return squares[internalPosition.getRow()][internalPosition.getColumn()];
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Set up Black pieces (top of the board)
        // Top left corner rook
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        // Top left knight
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        // Top left bishop
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        // Top queen
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        // Top king
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        // Top right bishop
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        // Top right knight
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        // Top right corner rook
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        // Place Black pawns
//        for (int col = 0; col <= 7; col++){
//            squares[1][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
//        }

        // Set up White pieces (bottom of the board)
        // Bottom left corner rook
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        // Bottom left knight
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        // Bottom left bishop
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        // Bottom queen
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        // Bottom king
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        // Bottom right bishop
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        // Bottom right knight
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        // Bottom right corner rook
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        //place white pawns
//        for (int col = 0; col <= 7; col++){
//            squares[6][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        }

        // Leave other squares null to represent empty spaces
        System.out.println("Board after reset:");
        drawBoard();
    }


    /**
     * draws the current chess board
     */
    public void drawBoard() {
        // Iterate from top to bottom of the chessboard (internal array)
        for (int row = 0; row < squares.length; row++) {
            // Convert internal row to chess notation (1-based, bottom row = 1)
            System.out.print((squares.length - row) + " "); // Flip internal row to Chess row

            for (int col = 0; col < squares[row].length; col++) {
                // Check if the square is empty
                if (squares[row][col] == null) {
                    System.out.print("_ ");
                } else {
                    // Get the piece and determine its display
                    ChessPiece piece = squares[row][col];
                    char pieceChar = piece.getPieceType().name().charAt(0);

                    // Abbreviate Knight to 'N'
                    if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        pieceChar = 'N';
                    }

                    // Use uppercase for white pieces, lowercase for black pieces
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        System.out.print(Character.toUpperCase(pieceChar) + " ");
                    } else {
                        System.out.print(Character.toLowerCase(pieceChar) + " ");
                    }
                }
            }
            System.out.println(); // Move to the next line after each row
        }

        // Print column headers
        System.out.println("  1 2 3 4 5 6 7 8");
    }




}
