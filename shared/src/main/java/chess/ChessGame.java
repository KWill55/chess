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
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board,startPosition);

        //filter valid moves from pieceMoves
        for (ChessMove move : pieceMoves){

            //gain info about move
            ChessPosition movePosition = move.getStartPosition();
            ChessPosition newMovePosition = move.getEndPosition();
            ChessPiece movePiece = board.getPiece(movePosition);

            //skip move if in check and move leaves king in check
            if (isInCheck(currentTeamTurn)){
                if (doesMoveLeaveKingInCheck(move, board, currentTeamTurn)){
                    continue;
                }
            }

            //skip move if move puts king in check
            if (doesMoveLeaveKingInCheck(move, board, currentTeamTurn)){
                continue;
            }

            //skip move if it moves kings too close together
//            if (isKingTooCloseToEnemyKing(move, board)){
//                continue;
//            }


            //skip moves if stalemate/checkmate? maybe not
            //TODO

            //Its made it this far, so its a valid move
            validMoves.add(move);
        }

        System.out.println("Valid moves for " + startPosition + ": " + validMoves);
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

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
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

                //if currentPiece is an enemy, add their moves to the enemyEndPositions array
                if (currentPiece.getTeamColor() == enemyTeam) {
                    System.out.println("Piece to find moves for: " + currentPiece);
                    Collection<ChessMove> enemyPieceMoves = currentPiece.pieceMoves(board, position);
                    for (ChessMove enemyPieceMove : enemyPieceMoves) {
                        enemyEndPositions.add(enemyPieceMove.getEndPosition());
                    }
                }

                //find our teamColor's King and get his endPosition
                if ((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && (currentPiece.getTeamColor() == teamColor)) {
                    ChessPosition internalKingPosition = new ChessPosition(row,col);
                    kingPosition = board.toChessFormat(internalKingPosition);
                    System.out.println(teamColor + " king Position: " + kingPosition);
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

    private boolean doesMoveLeaveKingInCheck(ChessMove move, ChessBoard board, TeamColor teamColor) {
        //create temporary board to see how move affects check
        ChessGame tempGame = createTempGame(board);

        System.out.println("\nTesting move: " + move);
        System.out.println("Board after move");
        tempGame.board.drawBoard();

        //no out of bounds error when both checks are calculated with enemyTeam
//        boolean isEnemyKingInCheck = tempGame.isInCheck(enemyTeam, tempGame.board);
//        boolean isKingInCheck = tempGame.isInCheck(enemyTeam, tempGame.board);

        //out of bounds when i use temGame.currentTeamTurn and enemyTeam
//        boolean isKingInCheck = tempGame.isInCheck(tempGame.currentTeamTurn, tempGame.board);
//        boolean isEnemyKingInCheck = tempGame.isInCheck(enemyTeam, tempGame.board);

        //not out of bounds when i use tempGame.currentTeamTurn for both
//        boolean isKingInCheck = tempGame.isInCheck(tempGame.currentTeamTurn, tempGame.board);
//        boolean isEnemyKingInCheck = tempGame.isInCheck(tempGame.currentTeamTurn, tempGame.board);

        //out of bounds error when i try to use this.currentTeamTurn and enemyTeam
//        boolean isKingInCheck = tempGame.isInCheck(this.currentTeamTurn, tempGame.board);
//        boolean isEnemyKingInCheck = tempGame.isInCheck(enemyTeam, tempGame.board);

        TeamColor enemyTeam = teamColor;
        TeamColor friendlyTeam = (enemyTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        //TODO this is the problem. enemyTeam is making everything not work

        boolean isKingInCheck = tempGame.isInCheck(friendlyTeam, tempGame.board);
        boolean isEnemyKingInCheck = tempGame.isInCheck(enemyTeam, tempGame.board);

        System.out.println("Is King in check after move? " + isKingInCheck);
        System.out.println("Is enemy king in check after move? " + isEnemyKingInCheck);

        //return whether one of the kings is in check or not
        return isKingInCheck || isEnemyKingInCheck;
    }


    private boolean isKingTooCloseToEnemyKing(ChessMove move, ChessBoard board) {
        // Get the enemy king's position
        ChessPosition enemyKingPosition = null;
        ChessPosition kingPosition = null;

        //simulate the move on a temporary game and board
        ChessGame tempGame = simulateMove(move, board);

        //iterate through temp board to find king locations
        kingPosition = findKing(tempGame.board, currentTeamTurn);
        TeamColor enemyTeam = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        enemyKingPosition = findKing(tempGame.board,  enemyTeam);

        // make sure our variable contains enemy king
        if (enemyKingPosition == null || kingPosition == null) {
            return false;
        }

        //calculate distance between kings
        int rowDistanceBetweenKings = Math.abs(kingPosition.getRow() - enemyKingPosition.getRow());
        int columnDistanceBetweenKings = Math.abs(kingPosition.getColumn() - enemyKingPosition.getColumn());

        if (rowDistanceBetweenKings == 1 && columnDistanceBetweenKings == 1) {
            return true;
        }
        return false;
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




    //creates temp chess board and game and simulates a move
    public ChessGame simulateMove(ChessMove move, ChessBoard board){

        // Create a temporary board and simulate move in question
        ChessBoard tempBoard = board.copy();

        //organize move details
        ChessPosition position = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());

        if (piece == null) {
            return null; // No piece to move (should never happen in valid moves)
        }

        // Ensure the move is valid by checking pieceMoves()
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
        if (!pieceMoves.contains(move)) {
            return null; // Move is invalid, return null (or throw an exception)
        }

        //make move on temporary board
        tempBoard.addPiece(position, null); // remove old piece location
        tempBoard.addPiece(newPosition, piece); // add new piece location

        // create a temporary game to simulate the move
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