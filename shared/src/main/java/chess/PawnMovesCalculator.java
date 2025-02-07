package chess;

import java.util.Collection;
import java.util.ArrayList;


public class PawnMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board){
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);

        int[][] forwardMoves;
        int[][] captureMoves;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            forwardMoves = new int[][]{
                    {-1,0}
            };
            captureMoves = new int[][]{
                    {-1,-1},
                    {-1,1}
            };
        }
        else{
            forwardMoves = new int[][]{
                    {1,0}
            };
            captureMoves = new int[][]{
                    {1,-1},
                    {1,1}
            };
        }

        addForwardMoves(position, board, validMoves, forwardMoves);
        addCaptureMoves(position, board, validMoves, captureMoves);


        return validMoves;
    }

    public void addForwardMoves(ChessPosition position, ChessBoard board, Collection<ChessMove> validMoves, int[][] forwardMoves){

        //2 move
        if (isStartingRow(position, board)){
            ChessPiece piece = board.getPiece(position);
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            internalRow += forwardMoves[0][0];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);
            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            if (newPiece == null){
                validMoves.add(move);
                internalRow += forwardMoves[0][0];

                newInternalPosition = new ChessPosition(internalRow, internalCol);
                newPosition = board.toChessFormat(newInternalPosition);
                newPiece = board.getPiece(newPosition);
                move = new ChessMove(position, newPosition, null);

                if(newPiece == null){
                    validMoves.add(move);
                }
            }
        }
        else{
            ChessPiece piece = board.getPiece(position);
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            internalRow += forwardMoves[0][0];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);
            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            if (newPiece == null) {
                if (isPromotionRow(position,board)){
                    addPromotionMoves(position,newPosition,board,validMoves);
                }
                else{
                    validMoves.add(move);
                }

            }
        }
    }

    public void addCaptureMoves(ChessPosition position, ChessBoard board, Collection<ChessMove> validMoves, int[][] captureMoves){
        //regular move(possibility for promotion)
        ChessPiece piece = board.getPiece(position);
        ChessPosition internalPosition = board.fromChessFormat(position);

        for (int[]direction : captureMoves) {
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            internalRow += direction[0];
            internalCol += direction[1];

            ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
            ChessPosition newPosition = board.toChessFormat(newInternalPosition);

            if(!board.isWithinBounds(newPosition)){
                continue;
            }

            ChessPiece newPiece = board.getPiece(newPosition);
            ChessMove move = new ChessMove(position, newPosition, null);

            if (newPiece != null) {
                if (piece.getTeamColor() != newPiece.getTeamColor()){
                    if (isPromotionRow(position,board)){
                        addPromotionMoves(position, newPosition, board, validMoves);
                    }
                    else{
                        validMoves.add(move);
                    }
                    continue;
                }
            }
        }
    }

    public void addPromotionMoves(ChessPosition position, ChessPosition newPosition, ChessBoard board, Collection<ChessMove> validMoves){
        validMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.KNIGHT));

    }

    public boolean isStartingRow(ChessPosition position, ChessBoard board){
        int row = position.getRow();
        ChessPiece piece = board.getPiece(position);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2){
            return true;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7){
            return true;
        }
        return false;
    }

    public boolean isPromotionRow(ChessPosition position, ChessBoard board){
        int row = position.getRow();
        ChessPiece piece = board.getPiece(position);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7){
            return true;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2){
            return true;
        }
        return false;
    }




}