package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessPosition> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessPosition> moves = new ArrayList<>();

        //possible directions for a bishop piece
        int[][] directions = {
            {1, 1},   // Down-right
            {1, -1},  // Down-left
            {-1, 1},  // Up-right
            {-1, -1}  // Up-left
        };

        //get starting position in internal format
        ChessPosition internalPosition = ChessBoard.fromChessFormat(position);
        ChessPiece bishopPiece = board.getPiece(internalPosition);

        //go through each of four directions to Chess
        for (int[] direction : directions){

            //current position of the bishop
            int row = internalPosition.getRow();
            int col = internalPosition.getColumn();

            while (true){
                row += direction[0];
                col += direction[1];

                //check for out of bounds
                if (!board.isWithinBounds(row,col)){
                    break;
                }

                //get piece type of target location in internal format
                ChessPosition newInternalPosition = new ChessPosition(row,col); //internal format
               ChessPosition newPosition = ChessBoard.toChessFormat(newInternalPosition);

                ChessPiece targetPiece = board.getPiece(newInternalPosition);


                //check for friendly pieces in the way
                if (targetPiece != null){
                    if (targetPiece.getTeamColor() != bishopPiece.getTeamColor()) {
                        moves.add(newPosition); // Chess format
                    }
                    break; // Stop moving further in this direction
                }

                //The current row and col are valid, so add it to possible moves
                moves.add(newPosition);
            }
        }

        System.out.println("Calculating moves for Bishop at: " + position);
        for (ChessPosition move : moves) {
            System.out.println("Valid move: " + move);
        }


        // Return possible moves for a bishop
        return moves;
    }
}