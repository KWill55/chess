package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessPosition> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessPosition> moves = new ArrayList<>();

//        System.out.println("Calculating knight moves from: " + position);

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
//        System.out.println("knightPiece = " + knightPiece);

        //go through each of four directions to Chess
        for (int[] direction : directions){
//            System.out.println("direction[0] = " + direction[0]);
//            System.out.println("direction[1] = " + direction[1]);
            //different versions of the starting position of the piece
            int chessRow = position.getRow();
            int chessCol = position.getColumn();
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();



            for (int i = 0; i<1; i++){
                internalRow += direction[0];
                internalCol += direction[1];


                ChessPosition newInternalPosition = new ChessPosition(internalRow,internalCol); //internal format
                ChessPosition newChessPosition = ChessBoard.toChessFormat(newInternalPosition);
//                System.out.println("Considering position: " + newChessPosition);

                //check for out of bounds
                if (!board.isWithinBounds(internalRow,internalCol)){
//                    System.out.println("Out of bounds: " + newChessPosition);
                    break;
                }

                //get piece type of target location in internal format
                ChessPiece targetChessPiece = board.getPiece(newChessPosition);

                //check for enemy and ally pieces
                if (targetChessPiece != null){
                    //capture enemy
                    if (targetChessPiece.getTeamColor() != knightPiece.getTeamColor()) {
                        moves.add(newChessPosition);
                        break;
                    }
                    //stop moving in that direction if its an ally
                    break;
                }

                //empty space so we can add it
                moves.add(newChessPosition);
            }
        }

//        System.out.println("Valid moves for knight at: " + position);
        for (ChessPosition move : moves) {
//            System.out.println("Valid move: " + move);
        }


        // Return possible moves for a knight
        return moves;
    }
}