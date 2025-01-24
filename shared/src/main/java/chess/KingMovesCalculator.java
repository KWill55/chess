package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines possible moves a king at a certain position is allowed to take
 */
public class KingMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //possible directions for a king piece
        int[][] directions = {
                {1, 1},   // Down right
                {1, -1},  // Down left
                {-1, 1},  // up right
                {-1, -1},  // up left
                {1, 0},   // Right
                {0, -1},  // Up
                {-1, 0},  // Left
                {0, 1}  // Down
        };

        //get starting position in internal format
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        ChessPiece kingPiece = board.getPiece(position);

        //loop through possible king moves
        for (int[] direction : directions){

            //set King's initial starting row and column
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();


            //go for the length of a king's possible move
            for (int i = 0; i<1; i++){

                //move king to current directions
                internalRow += direction[0];
                internalCol += direction[1];

                //save new location (possible valid movement)
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
                    if (targetChessPiece.getTeamColor() != kingPiece.getTeamColor()) {
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

        // Return possible moves for a king
        return moves;
    }
}