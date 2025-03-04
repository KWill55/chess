package chess;

import java.util.ArrayList;
import java.util.Collection;

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

        if (piece == null){
            return validMoves;
        }

        //don't accept moves for a fully pinned piece
        if (isPieceCompletelyPinned(startPosition, board)) {
            return validMoves; // Return empty move set
        }

        // Get possible moves for the piece
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);

        //filter valid moves from pieceMoves
        for (ChessMove move : pieceMoves) {
            //skip move if move puts currentTeamTurn king in check
            if (doesMoveLeaveKingInCheck(move, board, currentTeamTurn)) {
                continue;
            }
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

        // Make sure a piece exists at the start position
        if (piece == null || piece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException("No valid piece to move.");
        }

        Collection<ChessMove> validMoves = validMoves(position);

        //Confirm that move is contained in validMoves
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move, please try again");
        }

        //go through all validMoves that the piece can take
        for (ChessMove validMove : validMoves) {
            //find the valid move that matches the move parameter
            if (validMove.getEndPosition().equals(newPosition)) {
                //move and promote pawn if move is a promotion piece
                if (move.getPromotionPiece() != null) {
                    ChessPiece promotedPiece = new ChessPiece(currentTeamTurn, move.getPromotionPiece());
                    board.addPiece(position, null); // Remove the pawn
                    board.addPiece(newPosition, promotedPiece); // Place the new piece
                }
                //move piece (normal, no promotion)
                else {
                    board.addPiece(position, null);
                    board.addPiece(newPosition, piece);
                }
            }
        }
        //change player turns
        currentTeamTurn = getOtherTeamColor(currentTeamTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //define variables
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
     * Determines if the given team is in check
     * overload method version of isInCheck with one parameter
     * @param teamColor which team to check for check
     * @param board current board
     * @return True if the specified team is in check
     */
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
        // If the team is not in check, it's not checkmate
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
        // If the king is in check, it's NOT stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        // Iterate through all pieces of the current team
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

                // Check if any move is actually legal
                for (ChessMove move : moves) {
                    if (!doesMoveLeaveKingInCheck(move, board, teamColor)) {
                        return false; // Found a valid move, so it's NOT stalemate
                    }
                }
            }
        }

        // If no valid move exists, it's stalemate
        return true;
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
     * Input a team and get the other team back
     * @param currentTeamTurn current team turn
     * @return other team
     */
    public TeamColor getOtherTeamColor(TeamColor currentTeamTurn){
        //change team turn
        return (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if king is left in check after a move is performed
     * @param move chess move in question
     * @param board 8x8 grid of chess pieces
     * @param teamColor current team
     * @return boolean
     */
    private boolean doesMoveLeaveKingInCheck(ChessMove move, ChessBoard board, TeamColor teamColor) {
        System.out.println("\nTesting move: " + move);

        ChessBoard tempBoard = board.copy();
        ChessPosition position = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());

        // Simulate the move on the temporary board
        tempBoard.addPiece(position, null); // remove old piece location
        tempBoard.addPiece(newPosition, piece); // add new piece location

        System.out.println("Board after move");
        System.out.println(teamColor + " is in check? " + isInCheck(teamColor, tempBoard));

        return isInCheck(teamColor, tempBoard);
    }

    /**
     *
     * @param board 8x8 array of chess pieces
     * @return a copy of the chess board
     */
    public ChessGame createTempGame(ChessBoard board) {
        // Create a temporary board copy
        ChessBoard tempBoard = board.copy();

        // Create a temporary game with the copied board
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(tempBoard);
        tempGame.setTeamTurn(currentTeamTurn);

        return tempGame;
    }

    /**
     * Method to find a King within a given 8x8 chessboard
     * @param board 8x8 array of chess pieces
     * @param teamColor which team's King to find
     * @return the King's position
     */
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

    /**
     * determines whether a piece is completely pinned and cannot move
     * @param position starting position of piece
     * @param board current ChessBoard
     * @return a boolean value
     */
    private boolean isPieceCompletelyPinned(ChessPosition position, ChessBoard board) {
        ChessPiece piece = board.getPiece(position);
        ChessPosition kingPosition = findKing(board, piece.getTeamColor());

        // Get all possible moves for this piece
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);

        // Check if any move does not leave the king in check
        for (ChessMove move : possibleMoves) {
            // Found at least one valid move, so it's not completely pinned
            if (!doesMoveLeaveKingInCheck(move, board, piece.getTeamColor())) {
                return false;
            }
        }

        // If no valid moves exist, the piece is completely pinned
        return true;
    }
}