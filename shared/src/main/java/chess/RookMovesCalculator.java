package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessPosition position, ChessBoard board){
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {1,0},
                {-1,0},
                {0,1},
                {0,-1},
        };

        for (int[] direction : directions){
            ChessPosition internalPosition = board.fromChessFormat(position);
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            while (true){
                internalRow += direction[0];
                internalCol += direction[1];

                ChessPosition newInternalPosition = new ChessPosition(internalRow, internalCol);
                ChessPosition newPosition = board.toChessFormat(newInternalPosition);


                if (!board.isWithinBounds(newPosition)){
                    break;
                }

                ChessPiece piece = board.getPiece(position);
                ChessPiece newPiece = board.getPiece(newPosition);
                ChessMove move = new ChessMove(position, newPosition,null);

                if (newPiece != null){
                    if (piece.getTeamColor() != newPiece.getTeamColor()){
                        validMoves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }

                validMoves.add(move);
            }
        }

        return validMoves;
    }
}