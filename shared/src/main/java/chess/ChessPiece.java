package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        ChessPiece piece = board.getPiece(myPosition);

        switch(piece.getPieceType()){
            case QUEEN:
                calculator = new QueenMovesCalculator();
                break;
            case ROOK:
                calculator = new RookMovesCalculator();
                break;
            case BISHOP:
                calculator = new BishopMovesCalculator();
                break;
            case KNIGHT:
                calculator = new KnightMovesCalculator();
                break;
            case KING:
                calculator = new KingMovesCalculator();
                break;
            case PAWN:
                calculator = new PawnMovesCalculator();
                break;
            default:
                throw new RuntimeException("Not a valid piece type");
        }

        Collection<ChessMove> validMoves = calculator.calculateMoves(myPosition, board);


        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "Color=" + pieceColor +
                ", type=" + type +
                '}';
    }
}