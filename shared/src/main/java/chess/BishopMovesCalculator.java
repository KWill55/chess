package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines possible moves a bishop at a certain position is allowed to take
 */
public class BishopMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //possible directions for a bishop piece
        int[][] directions = {
            {1, 1},   // Down right
            {1, -1},  // Down left
            {-1, 1},  // up right
            {-1, -1}  // up left
        };

        //get starting position of bishop in internal format
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        ChessPiece bishopPiece = board.getPiece(position);

        //go through each of the possible directions a bishop can travel
        for (int[] direction : directions){

            //reset to starting piece between iterations
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            //move in current direction until something causes a break and transition to next direction
            while (true){
                internalRow += direction[0];
                internalCol += direction[1];

                //translate internal format to chess format
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
                    if (targetChessPiece.getTeamColor() != bishopPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, newChessPosition, null));
                        break;
                    }
                    //stop moving in that direction if it's an ally
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