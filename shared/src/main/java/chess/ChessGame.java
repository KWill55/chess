package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
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
        PieceMovesCalculator calculator;
        ChessPiece piece = board.getPiece(startPosition);

        switch(piece.getPieceType()){
            case QUEEN:
                calculator = new QueenMovesCalculator();
                break;
            case ROOK:
                calculator = new RookMovesCalculator();
                break;
            case BISHOP:
                calculator = new BishopMovesCalculator();
                break;
            case KNIGHT:
                calculator = new KnightMovesCalculator();
                break;
            case KING:
                calculator = new KingMovesCalculator();
                break;
            case PAWN:
                calculator = new PawnMovesCalculator();
                break;
            default:
                throw new RuntimeException("Not a valid piece type");
        }

        Collection<ChessMove> validMoves = calculator.calculateMoves(startPosition, board);
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition position = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(position);

        if (piece == null){
            throw new InvalidMoveException("Invalid piece");
        }

        Collection<ChessMove> validMoves = validMoves(position);

        System.out.println(validMoves);

        for (ChessMove validMove : validMoves){
            System.out.println(validMove.getEndPosition());
            System.out.println(newPosition);;
            if (validMove.getEndPosition().equals (newPosition)){
                board.addPiece(position, null);
                board.addPiece(newPosition, piece);
            }
        }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }

    public static void main(String[] args) throws InvalidMoveException{
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);


        while (true){
            game.board.drawBoard();

            System.out.println("Pick starting piece");
            int startRow = scanner.nextInt();
            int startCol = scanner.nextInt();
            ChessPosition position = new ChessPosition(startRow, startCol);


            System.out.println("Pick ending location");
            int endRow = scanner.nextInt();
            int endCol = scanner.nextInt();
            ChessPosition newPosition = new ChessPosition(endRow,endCol);

            ChessMove move = new ChessMove(position, newPosition, null);

            game.makeMove(move);
        }



    }
}