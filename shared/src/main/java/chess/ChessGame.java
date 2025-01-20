package chess;

import java.util.Collection;
import java.util.Scanner;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {

    private ChessBoard board; //reference to chessBoard (pointer)

    public ChessGame() {
        board = new ChessBoard(); //creates board object in memory
        board.resetBoard(); //initialize game board
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets which team's turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        //if black's turn:
            //change to white. and vice versa
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start); //get piece type

        board.addPiece(end,piece); //adds move to the board
        board.addPiece(start,null); //removes piece from old position
        board.drawBoard(); //update board for the user
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //account for the turn and then update board?
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
    * TODO fix this later
     */
    public static int rowToArrayForm (char letter){
        letter = Character.toUpperCase(letter); //make sure row is upper case
        //TODO convert board labels to array labels. for now user will enter matrix labels to make it easier
        return 6;
    }


    public static void main(String[] args) throws InvalidMoveException {
        // Initialize the game
        ChessGame game = new ChessGame();  // Creates and initializes a new ChessGame object; ChessGame constructor is called
        System.out.println("Game is initializing...");// Starting message for user
        game.board.drawBoard(); // Display the initial board setup

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the starting position (row column):");
        int startRow = scanner.nextInt();
        int startCol = scanner.nextInt();
        //TODO: use below to change these to chess labels
//        String startInput = scanner.nextLine();
//        char start_row = startInput.charAt(0); //A-G
//        char start_col = startInput.charAt(1); //1-8

        System.out.println("Enter the ending position (row column):");
        int endRow = scanner.nextInt();
        int endCol = scanner.nextInt();

        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(start, end, null);

        //just make sure pieces can actually move
        game.makeMove(move);


        //TODO: loop to contain player turns
//        while (game_is_over == false){
//            System.out.println("Current turn: " + currentColor);
//            game.makeMove(TODO);
//
//            //switch turn
//            if (currentColor == BLACK){
//                setTeamTurn(WHITE);
//            }
//            else {
//                setTeamTurn(BLACK)
//            }
//        }
        //setTeamTurn(WHITE);
        //ChessMove(

        //setTeamTurn(BLACK);


    }
}
