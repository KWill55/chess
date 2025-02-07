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

        // No piece or not the team's turn
        if (piece == null || piece.getTeamColor() != currentTeamTurn) {
            return validMoves;
        }

        // Get possible moves for the piece
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board,startPosition);

        //filter valid moves from pieceMoves
        for (ChessMove move : pieceMoves){

            //gain info about move
            ChessPosition movePosition = move.getStartPosition();
            ChessPosition newMovePosition = move.getEndPosition();
            ChessPiece movePiece = board.getPiece(movePosition);

            //skip move if start piece is an enemy
            if (movePiece.getTeamColor() != currentTeamTurn){
                continue;
            }

            //skip move if in check and move leaves king in check
            if (isInCheck(currentTeamTurn)){
                if (doesMoveLeaveKingInCheck(move, board)){
                    continue;
                }
            }

            //skip move if move puts king in check
            if (doesMoveLeaveKingInCheck(move, board)){
                continue;
            }

            //skip move if it moves kings too close together
            if (isKingTooCloseToEnemyKing(move, board)){
                continue;
            }

            //skip moves if stalemate/checkmate? maybe not
            //TODO

            //Its made it this far, so its a valid move
            validMoves.add(move);
        }
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
        if (!validMoves.contains(move)){
            throw new InvalidMoveException("Invalid Move, please try again");
        }


        for (ChessMove validMove : validMoves){
            if (validMove.getEndPosition().equals (newPosition)){
                board.addPiece(position, null);
                board.addPiece(newPosition, piece);
            }
        }
        currentTeamTurn = changeTeamTurn(currentTeamTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        //collection to store all the current valid enemy moves
        Collection<ChessPosition> enemyEndPositions = new ArrayList<>();

        // Determine which team is the enemy team
        TeamColor enemyTeam;
        if (teamColor == ChessGame.TeamColor.BLACK) {
            enemyTeam = ChessGame.TeamColor.WHITE;
        } else {
            enemyTeam = ChessGame.TeamColor.BLACK;
        }

        //iterate through the board, stop at enemy piece, and add their validMoves to enemyMoves array
        for (int row = 0; row<8; row++) {
            for (int col = 0; col<8; col++) {

                //info for current piece iteration
                ChessPiece currentPiece = board.squares[row][col];
                ChessPosition internalPosition = new ChessPosition(row, col);
                ChessPosition position = board.toChessFormat(internalPosition);

                // Skip empty squares
                if (currentPiece == null) {
                    continue;
                }

                //if currentPiece is an enemy, add their moves
                if (currentPiece.getTeamColor() == enemyTeam) {
                    Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(board, position);

                    //add each valid endPosition to the enemyEndPositions array
                    for (ChessMove enemyMove : enemyMoves) {
                        enemyEndPositions.add(enemyMove.getEndPosition());
                    }
                }

                //find our teamColor's King and get his endPosition
                if ((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && (currentPiece.getTeamColor() == teamColor)) {
                    ChessPosition internalKingPosition = new ChessPosition(row,col);
                    kingPosition = board.toChessFormat(internalKingPosition);
                }
            }
        }

        //if an enemy can capture teamColor's KING, return true (king is in check)
        if (kingPosition != null){
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
    public TeamColor changeTeamTurn(TeamColor currentTeamTurn){
        //change team turn
        if (currentTeamTurn == ChessGame.TeamColor.BLACK){
            currentTeamTurn = ChessGame.TeamColor.WHITE;
        }
        else{
            currentTeamTurn = ChessGame.TeamColor.BLACK;
        }

        return currentTeamTurn;
    }

    private boolean doesMoveLeaveKingInCheck(ChessMove move, ChessBoard board) {
        //simulate the move on a temporary game and board
        ChessGame tempGame = simulateMove(move, board);

        // Check if the move results in check
        if (tempGame.isInCheck(currentTeamTurn)) {
            return true; // Move is invalid
        }
        return false; // Move is valid
    }


    private boolean isKingTooCloseToEnemyKing(ChessMove move, ChessBoard board) {
        // Get the enemy king's position
        ChessPosition enemyKingPosition = null;
        ChessPosition kingPosition = null;

        //simulate the move on a temporary game and board
        ChessGame tempGame = simulateMove(move, board);

        //iterate through temp board to find king locations
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = tempGame.board.squares[row][col];

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() != currentTeamTurn) {
                    enemyKingPosition = new ChessPosition(row, col);
                }
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == currentTeamTurn) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }

        // make sure our variable contains enemy king
        if (enemyKingPosition == null || kingPosition == null) {
            return false;
        }

        //calculate distance between kings
        int rowDistanceBetweenKings = Math.abs(kingPosition.getRow() - enemyKingPosition.getRow());
        int columnDistanceBetweenKings = Math.abs(kingPosition.getColumn() - enemyKingPosition.getColumn());

//        System.out.println("row distance between kings: " + rowDistanceBetweenKings);
//        System.out.println("col distance between kings: " + columnDistanceBetweenKings);

        if (rowDistanceBetweenKings == 1 && columnDistanceBetweenKings == 1) {
            return true;
        }
        return false;
    }

    //creates temp chess board and game and simulates a move
    public ChessGame simulateMove(ChessMove move, ChessBoard board){

        // Create a temporary board and simulate move in question
        ChessBoard tempBoard = board.copy();

        //organize move details
        ChessPosition position = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());

        //make move on temporary board
        tempBoard.addPiece(position, null); // remove old piece location
        tempBoard.addPiece(newPosition, piece); // add new piece location

        // create a temporary game to simulate the move
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(tempBoard);
        tempGame.setTeamTurn(currentTeamTurn); // Preserve turn

        return tempGame;
    }

    //TODO
    //public ChessPosition findKings()

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