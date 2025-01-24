package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines possible moves a rook at a certain position is allowed to take
 */
public class RookMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //possible directions for a rook piece
        int[][] directions = {
                {1, 0},   // Right
                {0, -1},  // Up
                {-1, 0},  // Left
                {0, 1}  // Down
        };

        //get starting position in internal format
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        ChessPiece rookPiece = board.getPiece(position);

        //iterate through possible rook directions
        for (int[] direction : directions){
            //initial location of the rook
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            //travel in direction until invalid move
            while (true){
                //calculate and store new location of rook
                internalRow += direction[0];
                internalCol += direction[1];
                ChessPosition newInternalPosition = new ChessPosition(internalRow,internalCol); //internal format
                ChessPosition newChessPosition = ChessBoard.toChessFormat(newInternalPosition);

                //check for out of bounds
                if (!board.isWithinBounds(internalRow,internalCol)){
                    break;
                }

                //get piece type of target location in internal format
                ChessPiece targetChessPiece = board.getPiece(newChessPosition);

                //check for enemy and ally pieces
                if (targetChessPiece != null){
                    //capture enemy
                    if (targetChessPiece.getTeamColor() != rookPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, newChessPosition, null));
                        break;
                    }
                    //stop moving in that direction if its an ally
                    break;
                }

                //empty space so we can add it
                moves.add(new ChessMove(position, newChessPosition, null));
            }
        }

        // Return possible moves for a bishop
        return moves;
    }
}