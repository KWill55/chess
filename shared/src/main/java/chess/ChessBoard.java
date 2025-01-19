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
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
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
        for (int col = 0; col <= 7; col++){
            squares[1][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

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
        for (int col = 0; col <= 7; col++){
            squares[6][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        // Leave other squares null to represent empty spaces
    }

    /**
     * draws the current chess board
     */
    public void drawBoard(){
        for (int row = 0; row<squares.length; row++){ //go through each row
            for (int col = 0; col < squares.length; col ++){ //go through each col
                //print a space if no piece at that spot on board
                if (squares[row][col] == null){
                    System.out.print("_ ");
                }
                //print first letter of the piece present at that coordinate
                else {
                    //abbreviate knights to N since K is reserved for King
                    if (squares[row][col].getPieceType() == ChessPiece.PieceType.KNIGHT){
                        System.out.print("N ");
                    }
                    //print the first letter of all the pieces (besides the knight)
                    else{
                        System.out.print(squares[row][col].getPieceType().name().charAt(0) + " ");
                    }
                }
            }
            System.out.println(); //go to next row
        }


    }
}
