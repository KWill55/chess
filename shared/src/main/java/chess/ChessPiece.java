package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Responsibility: represent data of a chess piece (color, type)
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private PieceType type; //creates reference variable for pieceType enum (blueprint)
    private ChessGame.TeamColor pieceColor; //creates reference variable for TeamColor enum (blueprint)

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor; //assign parameter pieceColor to value of reference variable pieceColor
        this.type = type; //assign parameter type to value of reference variable type
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator;

        switch (type) {
//            case ROOK:
//                calculator = new RookMovesCalculator();
//                break;
            case BISHOP:
                calculator = new BishopMovesCalculator();
                break;
//            case KNIGHT:
//                calculator = new KnightMovesCalculator();
//                break;
//            case PAWN:
//                calculator = new PawnMovesCalculator();
//                break;
//            case QUEEN:
//                calculator = new QueenMovesCalculator();
//                break;
//            case KING:
//                calculator = new KingMovesCalculator();
//                break;
            default:
                throw new IllegalArgumentException("Unknown piece type");
        }

        // Calculate moves using the specific calculator
        Collection<ChessPosition> validPositions = calculator.calculateMoves(board, myPosition);

        return validPositions.stream()
                .map(pos -> new ChessMove(myPosition, pos, null))
                .collect(Collectors.toList());

    }
}
