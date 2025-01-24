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
    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard(); //creates board object in memory
        board.resetBoard(); //initialize game board
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets which team's turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
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
        //declare piece and calculator
        ChessPiece piece = board.getPiece(startPosition);
        PieceMovesCalculator calculator;

        //make sure startPosition holds a chess piece
        if (piece == null){
            throw new RuntimeException("No piece at the specified position.");
        }

        //switch to determine which PieceMovesCalculator to use
        switch (piece.getPieceType()) {
            case BISHOP:
                System.out.println("Using BishopMovesCalculator");
                calculator = new BishopMovesCalculator();
                break;
            case ROOK:
                System.out.println("Using RookMovesCalculator");
                calculator = new RookMovesCalculator(); // Add this case
                break;
            case KNIGHT:
                System.out.println("Using KnightMovesCalculator");
                calculator = new KnightMovesCalculator(); // Add this case
                break;
            case QUEEN:
                System.out.println("Using QueenMovesCalculator");
                calculator = new QueenMovesCalculator(); // Add this case
                break;
            case KING:
                System.out.println("Using KingMovesCalculator");
                calculator = new KingMovesCalculator(); // Add this case
                break;
            case PAWN:
                System.out.println("Using PawnMovesCalculator");
                calculator = new PawnMovesCalculator(); // Add this case
                break;
            default:
                System.out.println("No moves calculator found for: " + piece.getPieceType());
                throw new RuntimeException("No moves calculator for piece type: " + piece.getPieceType());
        }

        // Get valid positions for this piece and return them
        Collection<ChessMove> validMoves = calculator.calculateMoves(board, startPosition);
        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //define start, end, and piece
        ChessPosition chessStart = move.getStartPosition();
        ChessPosition chessEnd = move.getEndPosition();
        ChessPosition internalStart = ChessBoard.fromChessFormat(chessStart);
        ChessPiece piece = board.getPiece(chessStart); //get piece type

        //make sure there is a piece at the starting position
        if (piece == null) {
            throw new InvalidMoveException("No piece at the starting position!");
        }

        // Get the valid moves for the piece
        Collection<ChessMove> validMoves = validMoves(chessStart);

        //set isValid for valid moves
        boolean isValid = false;
        for (ChessMove validMove :validMoves){
            if (validMove.getEndPosition().equals(chessEnd)){
                isValid = true;
                break;
            }
        }
        // throw and exception if not valid
        if (!isValid) {
            throw new InvalidMoveException("Invalid move for the piece at " + chessStart);
        }

        board.addPiece(chessEnd,piece); //adds move to the board
        board.addPiece(chessStart,null); //removes piece from old position
        board.drawBoard(); //update board for the user
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return false; //TODO
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false; //TODO
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false; //TODO
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.drawBoard();
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
     * Main method for running the chess game.
     * Players take turns selecting and moving pieces until checkmate or stalemate.
     * Handles input, turn switching, and game-over conditions.
     */
    public static void main(String[] args) throws InvalidMoveException {
        // Initialize the game
        boolean gameIsOver = false;
        ChessGame game = new ChessGame();  // Creates and initializes a new ChessGame object; ChessGame constructor is called
        game.getBoard().drawBoard(); // Display the initial board setup

        Scanner scanner = new Scanner(System.in);

        //player turns
        while (gameIsOver == false){
            System.out.println("Current turn: " + game.currentTurn);

            // Get the starting position from the user
            System.out.println("Which piece would you like to move? (row and column format, e.g., 5 4):");
            int startRow = scanner.nextInt(); // Input row (e.g., 5)
            int startCol = scanner.nextInt(); // Input column (e.g., 4)
            ChessPosition chessStart = new ChessPosition(startRow, startCol); //stored in Chess format

            // Get the ending position from the user
            System.out.println("Where do you want to move? (row and column format, e.g., 6 5):");
            int endRow = scanner.nextInt(); // Input row (e.g., 6)
            int endCol = scanner.nextInt(); // Input column (e.g., 5)
            ChessPosition chessEnd = new ChessPosition(endRow, endCol); //stored in Chess format

            // Store chess positions to prepare to make the move
            ChessMove move = new ChessMove(chessStart, chessEnd, null);
            game.makeMove(move); //accepts chess format positions

            //change player turns
            if (game.currentTurn == TeamColor.WHITE){
                game.setTeamTurn(TeamColor.BLACK);
            }
            else{
                game.setTeamTurn(TeamColor.WHITE);
            }

            // Check game-over conditions (not needed for phase 0)
            if (game.isInCheckmate(game.currentTurn)) {
                System.out.println("Checkmate! " + game.currentTurn + " loses.");
                gameIsOver = true;
            } else if (game.isInStalemate(game.currentTurn)) {
                System.out.println("Stalemate! The game is a draw.");
                gameIsOver = true;
            }
        } //end player turn section
    } // end my main section
} // end chessGame class
