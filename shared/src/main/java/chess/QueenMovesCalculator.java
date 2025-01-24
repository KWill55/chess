package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

//        System.out.println("Calculating Queen moves from: " + position);

        //possible directions for a Queen piece
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
        ChessPiece queenPiece = board.getPiece(position);
//        System.out.println("queenPiece = " + queenPiece);

        //go through each of four directions to Chess
        for (int[] direction : directions){
//            System.out.println("direction[0] = " + direction[0]);
//            System.out.println("direction[1] = " + direction[1]);
            //different versions of the starting position of the piece
            int chessRow = position.getRow();
            int chessCol = position.getColumn();
            int internalRow = internalPosition.getRow();
            int internalCol = internalPosition.getColumn();

            while (true){
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
                    if (targetChessPiece.getTeamColor() != queenPiece.getTeamColor()) {
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

//        System.out.println("Valid moves for Queen at: " + position);
        for (ChessMove move : moves) {
//            System.out.println("Valid move: " + move);
        }


        // Return possible moves for a queen
        return moves;
    }
}