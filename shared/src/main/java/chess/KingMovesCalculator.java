package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

//        System.out.println("Calculating King moves from: " + position);

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
//        System.out.println("kingPiece = " + kingPiece);

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

//        System.out.println("Valid moves for king at: " + position);
        for (ChessMove move : moves) {
//            System.out.println("Valid move: " + move);
        }


        // Return possible moves for a king
        return moves;
    }
}