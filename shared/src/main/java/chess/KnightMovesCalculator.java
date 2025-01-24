package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines possible moves a knight at a certain position is allowed to take
 */
public class KnightMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //possible directions for a knight piece
        int[][] directions = {
                {2, 1},   // Down right
                {2, -1},  // Down left
                {-2, 1},  // up right
                {-2, -1},  // up left
                {1, 2},   // right down
                {1, -2},  // right up
                {-1, 2},  // left down
                {-1, -2}  // left up
        };

        //get starting position in internal format
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        ChessPiece knightPiece = board.getPiece(position);

        //iterate through possible knight moves
        for (int[] direction : directions){

            //initial location of knight
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            //allow the knight one turn
            for (int i = 0; i<1; i++){
                internalRow += direction[0];
                internalCol += direction[1];

                // new location of knight (possibly valid)
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
                    if (targetChessPiece.getTeamColor() != knightPiece.getTeamColor()) {
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

        // Return possible moves for a knight
        return moves;
    }
}