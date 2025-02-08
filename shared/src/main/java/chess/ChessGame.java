package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    private TeamColor currentTeamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        this.currentTeamTurn = ChessGame.TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
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
        //array to store valid Moves
        Collection<ChessMove> validMoves = new ArrayList<>();

        //Get valid moves for the given piece, not including specific chess rules
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return validMoves;


        // Get possible moves for the piece
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);

        //filter valid moves from pieceMoves
        for (ChessMove move : pieceMoves) {

            //gain info about move
            ChessPosition movePosition = move.getStartPosition();
            ChessPosition newMovePosition = move.getEndPosition();
            ChessPiece movePiece = board.getPiece(movePosition);


            TeamColor notCurrentTeamTurn = getOtherTeamColor(currentTeamTurn);

            //skip move if move puts currentTeamTurn king in check
            System.out.println(currentTeamTurn);
            if (doesMoveLeaveKingInCheck(move, board, currentTeamTurn)) {
                continue;
            }

//            if (isPiecePinned(move.getStartPosition(),board)){
//                continue;
//            }

            //Its made it this far, so it's a valid move
            validMoves.add(move);
        }

//        System.out.println("Valid moves for " + startPosition + ": " + validMoves);
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

        // Ensure a piece exists at the start position
        if (piece == null || piece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException("No valid piece to move.");
        }

        Collection<ChessMove> validMoves = validMoves(position);

        //throw Exception if move is not valid
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move, please try again");
        }

        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition().equals(newPosition)) {
                board.addPiece(position, null);
                board.addPiece(newPosition, piece);
            }
        }
        currentTeamTurn = getOtherTeamColor(currentTeamTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPosition = null;

        TeamColor enemyTeamColor = getOtherTeamColor(teamColor);

        //collection to store all the current valid enemy moves
        Collection<ChessPosition> enemyEndPositions = new ArrayList<>();

        //iterate through the board, stop at enemy piece, and add their validMoves to enemyMoves array
        for (int row = 0; row < board.squares.length; row++) {
            for (int col = 0; col < board.squares.length; col++) {

                //info for current piece iteration
                ChessPiece currentPiece = board.squares[row][col];
                ChessPosition internalPosition = new ChessPosition(row, col);
                ChessPosition position = board.toChessFormat(internalPosition);

                // Skip empty squares
                if (currentPiece == null) {
                    continue;
                }

                //if currentPiece is an enemy, add their moves
                if (currentPiece.getTeamColor() == enemyTeamColor) {
                    Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(board, position);

                    //add each valid endPosition to the enemyEndPositions array
                    for (ChessMove enemyMove : enemyMoves) {
                        enemyEndPositions.add(enemyMove.getEndPosition());
                    }
                }

                //find our teamColor's King and get his endPosition
                if ((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && (currentPiece.getTeamColor() == teamColor)) {
                    ChessPosition internalKingPosition = new ChessPosition(row, col);
                    kingPosition = board.toChessFormat(internalKingPosition);
                }
            }
        }

        //king is in check
        if (kingPosition != null) {
            if (enemyEndPositions.contains(kingPosition)) {
                return true;
            }
        }

        //king is not in check
        return false;
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = null;
        TeamColor enemyTeamColor = getOtherTeamColor(teamColor);

        //collection to store all the current valid enemy moves
        Collection<ChessPosition> enemyEndPositions = new ArrayList<>();

        //iterate through the board, stop at enemy piece, and add their validMoves to enemyMoves array
        for (int row = 0; row < board.squares.length; row++) {
            for (int col = 0; col < board.squares.length; col++) {

                //info for current piece iteration
                ChessPiece currentPiece = board.squares[row][col];
                ChessPosition internalPosition = new ChessPosition(row, col);
                ChessPosition position = board.toChessFormat(internalPosition);

                // Skip empty squares
                if (currentPiece == null) {
                    continue;
                }

                //if currentPiece is an enemy, add their moves
                if (currentPiece.getTeamColor() == enemyTeamColor) {
                    Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(board, position);

                    //add each valid endPosition to the enemyEndPositions array
                    for (ChessMove enemyMove : enemyMoves) {
                        enemyEndPositions.add(enemyMove.getEndPosition());
                    }
                }

                //find our teamColor's King and get his endPosition
                if ((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && (currentPiece.getTeamColor() == teamColor)) {
                    ChessPosition internalKingPosition = new ChessPosition(row, col);
                    kingPosition = board.toChessFormat(internalKingPosition);
                }
            }
        }

        //king is in check
        if (kingPosition != null) {
            if (enemyEndPositions.contains(kingPosition)) {
                return true;
            }
        }

        //king is not in check
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If the team is NOT in check, it's not checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Get all the pieces of the current team and try every possible move
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Skip empty squares and enemy pieces
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                // Get all valid moves for this piece
                Collection<ChessMove> moves = validMoves(position);

                // Check if any move removes the check
                for (ChessMove move : moves) {
                    ChessGame tempGame = createTempGame(board);
                    try {
                        tempGame.makeMove(move);
                        // If the move removes check, it's NOT checkmate
                        if (!tempGame.isInCheck(teamColor)) {
                            return false;
                        }
                    } catch (InvalidMoveException e) {
                        // Ignore invalid moves
                    }
                }
            }
        }

        // If no move removes check, it's checkmate
        return true;
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
        this.board = board;
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
     * takes in @currentTeamTurn and
     * changes it
     */
    public TeamColor getOtherTeamColor(TeamColor currentTeamTurn){
        //change team turn
        return (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /*
    determines whether a move results in a king being in check by simulating move on a temporary board
     */
    private boolean doesMoveLeaveKingInCheck(ChessMove move, ChessBoard board, TeamColor teamColor) {
        //create temporary board to test move in
        ChessGame tempGame = createTempGame(board);

        //temporary comments
        System.out.println("\nTesting move: " + move);

        //organize move details
        ChessPosition position = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ChessPiece piece = tempGame.board.getPiece(move.getStartPosition());

        //make move on the temporary board
        tempGame.board.addPiece(position, null); // remove old piece location
        tempGame.board.addPiece(newPosition, piece); // add new piece location

        //change team turn

        TeamColor newTeamColor = getOtherTeamColor(teamColor);

        System.out.println("Board after move");
        tempGame.board.drawBoard();

        boolean isKingInCheck = tempGame.isInCheck(teamColor, tempGame.board);

        System.out.println(teamColor + " is in check? " + isKingInCheck);

        //return whether one of the kings is in check or not
        return isKingInCheck;
    }

    public ChessGame createTempGame(ChessBoard board) {
        // Create a temporary board copy
        ChessBoard tempBoard = board.copy();

        // Create a temporary game with the copied board
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(tempBoard);
        tempGame.setTeamTurn(currentTeamTurn); // Preserve turn

        return tempGame;
    }

    public ChessPosition findKing(ChessBoard board, TeamColor teamColor){
        ChessPosition kingPosition = null;

        //iterate through the board to find the king location
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.squares[row][col];

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }
        return kingPosition;
    }

    //maybe get rid of this
    private boolean isPiecePinned(ChessPosition position, ChessBoard board){
        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            return false;
        }

        ChessPosition kingPosition = findKing(board, piece.getTeamColor());

        if (kingPosition == null) {
            return false;
        }

        //simulate board and remove piece
        ChessBoard tempBoard = board.copy();
        tempBoard.addPiece(position, null); // Remove the piece

        // If the king is now in check, the piece was pinned
        return isInCheck(piece.getTeamColor());
    }

    public static void main(String[] args) throws InvalidMoveException{
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);

        while (true){
            game.board.drawBoard();
            System.out.println("Current Team Turn: " + game.currentTeamTurn);

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