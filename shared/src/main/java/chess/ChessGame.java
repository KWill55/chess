package chess;

import java.util.Collection;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {
    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board; //reference to chessBoard (pointer)

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
        ChessPosition internalStartPosition = ChessBoard.fromChessFormat(startPosition);
        System.out.println("chess Start in validMoves: " + startPosition);
        System.out.println("internal start in validmoves: " + internalStartPosition);
        ChessPiece piece = board.getPiece(startPosition);

        PieceMovesCalculator calculator;


        System.out.println("Calculating moves for piece at " + startPosition + ": " + piece);

        if (piece == null){
            throw new RuntimeException("No piece at the specified position.");
        }

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

        // Get valid positions for this piece
        Collection<ChessPosition> validPositions = calculator.calculateMoves(board, startPosition);

        System.out.println("Valid positions:");
        for (ChessPosition position : validPositions) {
            System.out.println(" - " + position);
        }

        // Convert positions into ChessMove objects
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessPosition endPosition : validPositions) {
            validMoves.add(new ChessMove(startPosition, endPosition, null));
        }

        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition chessStart = move.getStartPosition();
        ChessPosition chessEnd = move.getEndPosition();

        // Debug: Log start and end positions
        System.out.println("Attempting to move from " + chessStart + " to " + chessEnd);

        ChessPosition internalStart = ChessBoard.fromChessFormat(chessStart);
        ChessPosition internalEnd = ChessBoard.fromChessFormat(chessEnd);

        // Debug: Log converted positions
        System.out.println("Internal start: " + internalStart + ", Internal end: " + internalEnd);

        ChessPiece piece = board.getPiece(chessStart); //get piece type

        // Debug: Log the piece type
        if (piece == null) {
            System.out.println("No piece at chess form " + chessStart);
            System.out.println("No piece at array form " + internalStart);
            throw new InvalidMoveException("No piece at the starting position!");
        }
        System.out.println("Piece to move: " + piece);

        // Get the valid moves for the piece
        System.out.println("chessStart before validMoves: " + chessStart);
        Collection<ChessMove> validMoves = validMoves(chessStart);

        // Check if the desired move is valid


        System.out.println("Valid moves for piece at " + chessStart + ":");
        for (ChessMove validMove : validMoves) {
            System.out.println(" - " + validMove.getEndPosition());
        }

        boolean isValid = false;
        for (ChessMove validMove :validMoves){
            System.out.println("Comparing: " + validMove.getEndPosition() + " with " + chessEnd);
            if (validMove.getEndPosition().equals(chessEnd)){
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            System.out.println("Move to " + chessEnd + " is invalid!");
            throw new InvalidMoveException("Invalid move for the piece at " + chessStart);
        }

        System.out.println("Move is valid! Moving piece.");

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

    public static void main(String[] args) throws InvalidMoveException {
        // Initialize the game
        boolean gameIsOver = false;
        ChessGame game = new ChessGame();  // Creates and initializes a new ChessGame object; ChessGame constructor is called
        System.out.println("Game is initializing...");// Starting message for user
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
            ChessPosition internalStart = ChessBoard.fromChessFormat(chessStart);//convert to array format

            // Get the ending position from the user
            System.out.println("Where do you want to move? (row and column format, e.g., 6 5):");
            int endRow = scanner.nextInt(); // Input row (e.g., 6)
            int endCol = scanner.nextInt(); // Input column (e.g., 5)
            ChessPosition chessEnd = new ChessPosition(endRow, endCol); //stored in Chess format
            ChessPosition internalEnd = ChessBoard.fromChessFormat(chessEnd);//convert to array format

            // Store chess positions to prepare to make the move
            ChessMove move = new ChessMove(chessStart, chessEnd, null);
            game.makeMove(move); //accepts chess format positions
            //doesnt get to this point
            if (game.currentTurn == TeamColor.WHITE){
                game.setTeamTurn(TeamColor.BLACK);
            }
            else{
                game.setTeamTurn(TeamColor.WHITE);
            }


            // Check game-over conditions
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
