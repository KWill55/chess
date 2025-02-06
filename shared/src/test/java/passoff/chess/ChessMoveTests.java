package passoff.chess;

import chess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChessMoveTests {
    private ChessMove original;
    private ChessMove equal;
    private ChessMove startDifferent;
    private ChessMove endDifferent;
    private ChessMove promoteDifferent;
    @BeforeEach
    public void setUp() {
        original = new ChessMove(new ChessPosition(2, 6), new ChessPosition(1, 5), null);
        equal = new ChessMove(new ChessPosition(2, 6), new ChessPosition(1, 5), null);
        startDifferent = new ChessMove(new ChessPosition(2, 4), new ChessPosition(1, 5), null);
        endDifferent = new ChessMove(new ChessPosition(2, 6), new ChessPosition(5, 3), null);
        promoteDifferent = new ChessMove(new ChessPosition(2, 6), new ChessPosition(1, 5),
                ChessPiece.PieceType.QUEEN);
    }

    @Test
    @DisplayName("Equals Testing")
    public void equalsTest() {
        Assertions.assertEquals(original, equal, "equals returned false for equal moves");
        Assertions.assertNotEquals(original, startDifferent, "equals returned true for different moves");
        Assertions.assertNotEquals(original, endDifferent, "equals returned true for different moves");
        Assertions.assertNotEquals(original, promoteDifferent, "equals returned true for different moves");
    }

    @Test
    @DisplayName("HashCode Testing")
    public void hashTest() {
        Assertions.assertEquals(original.hashCode(), equal.hashCode(),
                "hashCode returned different values for equal moves");
        Assertions.assertNotEquals(original.hashCode(), startDifferent.hashCode(),
                "hashCode returned the same value for different moves");
        Assertions.assertNotEquals(original.hashCode(), endDifferent.hashCode(),
                "hashCode returned the same value for different moves");
        Assertions.assertNotEquals(original.hashCode(), promoteDifferent.hashCode(),
                "hashCode returned the same value for different moves");
    }

    @Test
    @DisplayName("Combined Testing")
    public void hashSetTest() {
        Set<ChessMove> set = new HashSet<>();
        set.add(original);

        Assertions.assertTrue(set.contains(original));
        Assertions.assertTrue(set.contains(equal));
        Assertions.assertEquals(1, set.size());
        set.add(equal);
        Assertions.assertEquals(1, set.size());

        Assertions.assertFalse(set.contains(startDifferent));
        set.add(startDifferent);
        Assertions.assertEquals(2, set.size());

        Assertions.assertFalse(set.contains(endDifferent));
        set.add(endDifferent);
        Assertions.assertEquals(3, set.size());

        Assertions.assertFalse(set.contains(promoteDifferent));
        set.add(promoteDifferent);
        Assertions.assertEquals(4, set.size());

    }

    public static class FullGameTest {
        @Test
        @DisplayName("Full Game Checkmate")
        public void scholarsMate() throws InvalidMoveException {
            var game = new ChessGame();
            game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
            /*
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p|p|p|p|p|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K|B|N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
            /*
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p| |p|p|p|
                    | | | | | | | | |
                    | | | | |p| | | |
                    | | | | |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K|B|N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(1, 6), new ChessPosition(4, 3), null));
            /*
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p| |p|p|p|
                    | | | | | | | | |
                    | | | | |p| | | |
                    | | |B| |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K| |N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(8, 7), new ChessPosition(6, 6), null));
            /*
                    |r|n|b|q|k|b| |r|
                    |p|p|p|p| |p|p|p|
                    | | | | | |n| | |
                    | | | | |p| | | |
                    | | |B| |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B|Q|K| |N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(1, 4), new ChessPosition(5, 8), null));
            /*
                    |r|n|b|q|k|b| |r|
                    |p|p|p|p| |p|p|p|
                    | | | | | |n| | |
                    | | | | |p| | |Q|
                    | | |B| |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B| |K| |N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(8, 2), new ChessPosition(6, 3), null));
            /*
                    |r| |b|q|k|b| |r|
                    |p|p|p|p| |p|p|p|
                    | | |n| | |n| | |
                    | | | | |p| | |Q|
                    | | |B| |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B| |K| |N|R|
             */
            game.makeMove(new ChessMove(new ChessPosition(5, 8), new ChessPosition(7, 6), null));
            /*
                    |r| |b|q|k|b| |r|
                    |p|p|p|p| |Q|p|p|
                    | | |n| | |n| | |
                    | | | | |p| | | |
                    | | |B| |P| | | |
                    | | | | | | | | |
                    |P|P|P|P| |P|P|P|
                    |R|N|B| |K| |N|R|
             */
            Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.BLACK), GameStatusTests.INCORRECT_BLACK_CHECK);
            Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.WHITE), GameStatusTests.INCORRECT_WHITE_CHECK);
            Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.BLACK), GameStatusTests.MISSING_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), GameStatusTests.INCORRECT_WHITE_CHECKMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), GameStatusTests.INCORRECT_BLACK_STALEMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), GameStatusTests.INCORRECT_WHITE_STALEMATE);
        }
    }

    public static class GameStatusTests {
        static final String INCORRECT_BLACK_CHECK = "Black is not in check but isInCheck returned true";
        static final String INCORRECT_WHITE_CHECK = "White is not in check but isInCheck returned true";
        static final String INCORRECT_BLACK_CHECKMATE = "Black is not in checkmate but isInCheckmate returned true";
        static final String INCORRECT_WHITE_CHECKMATE = "White is not in checkmate but isInCheckmate returned true";
        static final String INCORRECT_BLACK_STALEMATE = "Black is not in stalemate but isInStalemate returned true";
        static final String INCORRECT_WHITE_STALEMATE = "White is not in stalemate but isInStalemate returned true";
        static final String MISSING_BLACK_CHECK = "White is in check but isInCheck returned false";
        static final String MISSING_BLACK_CHECKMATE = "Black is in checkmate but isInCheckmate returned false";
        static final String MISSING_WHITE_CHECKMATE = "White is in checkmate but isInCheckmate returned false";
        static final String MISSING_WHITE_STALEMATE = "White is in stalemate but isInStalemate returned false";

        @Test
        @DisplayName("New Game Default Values")
        public void newGame() {
            var game = new ChessGame();
            var expectedBoard = TestUtilities.defaultBoard();
            Assertions.assertEquals(expectedBoard, game.getBoard(), "Incorrect starting board");
            Assertions.assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn(), "Incorrect starting team turn");
        }

        @Test
        @DisplayName("Default Board No Statuses")
        public void noGameStatuses() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.defaultBoard());
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECK);
            Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECK);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_STALEMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_STALEMATE);
        }


        @Test
        @DisplayName("White in Check")
        public void whiteCheck() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | | |k|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | |K| | | |r| | |
                    | | | | | | | | |
                    | | | | | | | | |
                    """));

            Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), MISSING_BLACK_CHECK);
            Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECK);
        }


        @Test
        @DisplayName("Black in Check")
        public void blackCheck() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | |K| | | | |
                    | | | | | | | | |
                    | | | |k| | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |B| | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    """));

            Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.BLACK),
                    "Black is in check but isInCheck returned false");
            Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECK);
        }


        @Test
        @DisplayName("White in Checkmate")
        public void whiteTeamCheckmate() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    | | |b|q| | | | |
                    | | | | | | | | |
                    | | | |p| | | |k|
                    | | | | | |K| | |
                    | | |r| | | | | |
                    | | | | |n| | | |
                    | | | | | | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.WHITE), MISSING_WHITE_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECKMATE);
        }


        @Test
        @DisplayName("Black in Checkmate by Pawns")
        public void blackTeamPawnCheckmate() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | |k| | | | |
                    | | | |P|P| | | |
                    | |P| | |P|P| | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |K| | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.BLACK), MISSING_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);

        }

        @Test
        @DisplayName("Black can escape Check by capturing")
        public void escapeCheckByCapturingThreateningPiece() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | |r|k| |
                    | | | | | |P| |p|
                    | | | |N| | | | |
                    | | | | |B| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |n| | | |
                    |K| | | | | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);
        }


        @Test
        @DisplayName("Black CANNOT escape Check by capturing")
        public void cannotEscapeCheckByCapturingThreateningPiece() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | |r|k| |
                    | | | | | |P| |p|
                    | | | |N| | | | |
                    | | | | |B| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |n| | | |
                    |K| | | | | |R| |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.BLACK), MISSING_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);
        }


        @Test
        @DisplayName("Checkmate, where blocking a threat reveals a new threat")
        public void checkmateWhereBlockingThreateningPieceOpensNewThreat() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | |r|k|
                    | | |R| | | | | |
                    | | | | | | | | |
                    | | | | |r| | | |
                    | | | | | | | | |
                    | | |B| | | | | |
                    | | | | | | | | |
                    |K| | | | | | |R|
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.BLACK), MISSING_BLACK_CHECKMATE);
            Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_CHECKMATE);
        }


        @Test
        @DisplayName("Pinned King Causes Stalemate")
        public void stalemate() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    |k| | | | | | | |
                    | | | | | | | |r|
                    | | | | | | | | |
                    | | | | |q| | | |
                    | | | |n| | |K| |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |b| | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            Assertions.assertTrue(game.isInStalemate(ChessGame.TeamColor.WHITE), MISSING_WHITE_STALEMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_STALEMATE);
        }

        @Test
        @DisplayName("Stalemate Requires not in Check")
        public void checkmateNotStalemate() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                    |k| | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |P| | | |
                    | | | | | | | |r|
                    |K| | | | | |r| |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), INCORRECT_WHITE_STALEMATE);
            Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), INCORRECT_BLACK_STALEMATE);
        }
    }

    public static class MakeMoveTests {
        private static final String WRONG_BOARD = "Board not correct after move made";
        private ChessGame game;

        @BeforeEach
        public void setUp() {
            game = new ChessGame();
            game.setTeamTurn(ChessGame.TeamColor.WHITE);
            game.setBoard(TestUtilities.defaultBoard());
        }

        @Test
        @DisplayName("Make Valid King Move")
        public void makeValidKingMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    |p| | | | | | |k|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | |K| | | | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            var kingStartPosition = new ChessPosition(1, 2);
            var kingEndPosition = new ChessPosition(1, 1);
            game.makeMove(new ChessMove(kingStartPosition, kingEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    |p| | | | | | |k|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |K| | | | | | | |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Valid Queen Move")
        public void makeValidQueenMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | |q| |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |K| |k| | | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            var queenStartPosition = new ChessPosition(6, 7);
            var queenEndPosition = new ChessPosition(1, 2);
            game.makeMove(new ChessMove(queenStartPosition, queenEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |K|q|k| | | | | |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Valid Rook Move")
        public void makeValidRookMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | |R|
                    | | | | | | | | |
                    |K| | | | | | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            var rookStartPosition = new ChessPosition(3, 8);
            var rookEndPosition = new ChessPosition(7, 8);
            game.makeMove(new ChessMove(rookStartPosition, rookEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    | | | | | | | |R|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |K| | | | | | | |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Valid Knight Move")
        public void makeValidKnightMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | |n| | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | |P|
                    | | | | |K| | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            var knightStartPosition = new ChessPosition(6, 3);
            var knightEndPosition = new ChessPosition(4, 4);
            game.makeMove(new ChessMove(knightStartPosition, knightEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | |n| | | | |
                    | | | | | | | | |
                    | | | | | | | |P|
                    | | | | |K| | | |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Valid Bishop Move")
        public void makeValidBishopMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    |p| | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | |B| |K| | | |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.WHITE);

            var bishopStartPosition = new ChessPosition(1, 3);
            var bishopEndPosition = new ChessPosition(6, 8);
            game.makeMove(new ChessMove(bishopStartPosition, bishopEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    |p| | | | | | | |
                    | | | | | | | |B|
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |K| | | |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Valid Pawn Move")
        public void makeValidPawnMove() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    | |k| | | | | | |
                    | |p| | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | |P| |
                    | | | | | | |K| |
                    """));
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            var pawnStartPosition = new ChessPosition(7, 2);
            var pawnEndPosition = new ChessPosition(6, 2);
            game.makeMove(new ChessMove(pawnStartPosition, pawnEndPosition, null));

            Assertions.assertEquals(TestUtilities.loadBoard("""
                    | |k| | | | | | |
                    | | | | | | | | |
                    | |p| | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | |P| |
                    | | | | | | |K| |
                    """), game.getBoard(), WRONG_BOARD);
        }

        @Test
        @DisplayName("Make Move Changes Team Turn")
        public void makeMoveChangesTurn() throws InvalidMoveException {
            String failureMessage = "Team color not changed after move made";

            game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
            Assertions.assertEquals(ChessGame.TeamColor.BLACK, game.getTeamTurn(), failureMessage);

            game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
            Assertions.assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn(), failureMessage);
        }

        @Test
        @DisplayName("Invalid Make Move Too Far")
        public void invalidMakeMoveTooFar() {
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(5, 1), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Pawn Diagonal No Capture")
        public void invalidMakeMovePawnDiagonalNoCapture() {
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 2), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Out Of Turn")
        public void invalidMakeMoveOutOfTurn() {
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(6, 5), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Through Piece")
        public void invalidMakeMoveThroughPiece() {
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(1, 1), new ChessPosition(4, 1), null)));
        }

        @Test
        @DisplayName("Invalid Make Move No Piece")
        public void invalidMakeMoveNoPiece() {
            //starting position does not have a piece
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(4, 4), new ChessPosition(4, 5), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Invalid Move")
        public void invalidMakeMoveInvalidMove() {
            //not a move the piece can ever take
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(8, 7), new ChessPosition(5, 5), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Take Own Piece")
        public void invalidMakeMoveTakeOwnPiece() {
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(1, 3), new ChessPosition(2, 4), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Captured Piece")
        public void invalidMakeMoveCapturedPiece() throws InvalidMoveException {
            game.setBoard(TestUtilities.loadBoard("""
                    |r|n|b|q|k|b|n|r|
                    |p|p|p|p| |p|p|p|
                    | | | | | | | | |
                    | | | | |p| | | |
                    | | | | | | | | |
                    | | | | | |N| | |
                    |P|P|P|P|P|P|P|P|
                    |R|N|B|Q|K|B| |R|
                    """));

            game.makeMove(new ChessMove(new ChessPosition(3, 6), new ChessPosition(5, 5), null));
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(5, 5), new ChessPosition(4, 5), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Jump Enemy")
        public void invalidMakeMoveJumpEnemy() {
            game.setBoard(TestUtilities.loadBoard("""
                    | | | | |k| | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |R| |r| | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | |K| | | |
                    """));
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(5, 1), new ChessPosition(5, 5), null)));
        }

        @Test
        @DisplayName("Invalid Make Move In Check")
        public void invalidMakeMoveInCheck() {
            game.setBoard(TestUtilities.loadBoard("""
                    |r|n| |q|k|b| |r|
                    |p| |p|p|p|p|p|p|
                    |b|p| | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    |P| | |B| |n| | |
                    |R|P|P| | |P|P|P|
                    | |N|B|Q|K| |R| |
                    """));
            //try to make an otherwise valid move that doesn't remove check
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(1, 7), new ChessPosition(1, 8), null)));
        }

        @Test
        @DisplayName("Invalid Make Move Double Move Moved Pawn")
        public void invalidMakeMoveDoubleMoveMovedPawn() {
            game.setBoard(TestUtilities.loadBoard("""
                    |r|n|b|q|k|b|n|r|
                    |p| |p|p|p|p|p|p|
                    | | | | | | | | |
                    | |p| | | | | | |
                    | | | | | | | | |
                    | | | | | | |P| |
                    |P|P|P|P|P|P| |P|
                    |R|N|B|Q|K|B|N|R|
                    """));
            Assertions.assertThrows(InvalidMoveException.class,
                    () -> game.makeMove(new ChessMove(new ChessPosition(3, 7), new ChessPosition(5, 7), null)));
        }


        @ParameterizedTest
        @EnumSource(value = ChessPiece.PieceType.class, names = {"QUEEN", "ROOK", "KNIGHT", "BISHOP"})
        @DisplayName("Pawn Promotion")
        public void promotionMoves(ChessPiece.PieceType promotionType) throws InvalidMoveException {
            String pieceAtStart = "After move, a piece is still present in the start position";
            String noPieceAtEnd = "After move, no piece found at the end position";
            String incorrectType = "Found piece at end position is not the correct piece type";
            String incorrectColor = "Found piece at end position is the wrong team color";

            game.setBoard(TestUtilities.loadBoard("""
                    | | | | | | | | |
                    | | |P| | | | | |
                    | | | | | | |k| |
                    | | | | | | | | |
                    | | | | | | | | |
                    | | | | | | | | |
                    | |K| | |p| | | |
                    | | | | | |Q| | |
                    """));

            //White promotion
            ChessMove whitePromotion = new ChessMove(new ChessPosition(7, 3), new ChessPosition(8, 3), promotionType);
            game.makeMove(whitePromotion);

            Assertions.assertNull(game.getBoard().getPiece(whitePromotion.getStartPosition()), pieceAtStart);
            ChessPiece whiteEndPiece = game.getBoard().getPiece(whitePromotion.getEndPosition());
            Assertions.assertNotNull(whiteEndPiece, noPieceAtEnd);
            Assertions.assertEquals(promotionType, whiteEndPiece.getPieceType(), incorrectType);
            Assertions.assertEquals(ChessGame.TeamColor.WHITE, whiteEndPiece.getTeamColor(), incorrectColor);


            //Black take + promotion
            game.setTeamTurn(ChessGame.TeamColor.BLACK);
            ChessMove blackPromotion = new ChessMove(new ChessPosition(2, 5), new ChessPosition(1, 6), promotionType);
            game.makeMove(blackPromotion);

            Assertions.assertNull(game.getBoard().getPiece(blackPromotion.getStartPosition()), pieceAtStart);
            ChessPiece blackEndPiece = game.getBoard().getPiece(blackPromotion.getEndPosition());
            Assertions.assertNotNull(blackEndPiece, noPieceAtEnd);
            Assertions.assertEquals(promotionType, blackEndPiece.getPieceType(), incorrectType);
            Assertions.assertEquals(ChessGame.TeamColor.BLACK, blackEndPiece.getTeamColor(), incorrectColor);
        }
    }

    public static class ValidMovesTests {
        private static final String TRAPPED_PIECE_MOVES = "ChessGame validMoves returned valid moves for a trapped piece";

        @Test
        @DisplayName("Check Forces Movement")
        public void forcedMove() {

            var game = new ChessGame();
            game.setTeamTurn(ChessGame.TeamColor.BLACK);
            game.setBoard(TestUtilities.loadBoard("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | |B| | | | | | |
                        | | | | | |K| | |
                        | | |n| | | | | |
                        | | | | | | | | |
                        | | | |q| |k| | |
                        | | | | | | | | |
                        """));

            // Knight moves
            ChessPosition knightPosition = new ChessPosition(4, 3);
            var validMoves = TestUtilities.loadMoves(knightPosition, new int[][]{{3, 5}, {6, 2}});
            assertMoves(game, validMoves, knightPosition);

            // Queen Moves
            ChessPosition queenPosition = new ChessPosition(2, 4);
            validMoves = TestUtilities.loadMoves(queenPosition, new int[][]{{3, 5}, {4, 4}});
            assertMoves(game, validMoves, queenPosition);
        }


        @Test
        @DisplayName("Piece Partially Trapped")
        public void moveIntoCheck() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |k|r| | | |R| |K|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """));

            ChessPosition rookPosition = new ChessPosition(5, 6);
            var validMoves = TestUtilities.loadMoves(rookPosition, new int[][]{
                    {5, 7}, {5, 5}, {5, 4}, {5, 3}, {5, 2}
            });

            assertMoves(game, validMoves, rookPosition);
        }

        @Test
        @DisplayName("Piece Completely Trapped")
        public void rookPinnedToKing() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                        |K| | | | | | |Q|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |r| | | | |
                        | | | | | | | | |
                        | |k| | | | | | |
                        | | | | | | | | |
                        """));

            ChessPosition position = new ChessPosition(4, 4);
            Assertions.assertTrue(game.validMoves(position).isEmpty(), TRAPPED_PIECE_MOVES);
        }


        @Test
        @DisplayName("Pieces Cannot Eliminate Check")
        public void kingInDanger() {

            var game = new ChessGame();
            game.setTeamTurn(ChessGame.TeamColor.BLACK);
            game.setBoard(TestUtilities.loadBoard("""
                        |R| | | | | | | |
                        | | | |k| | | |b|
                        | | | | |P| | | |
                        |K| |Q|n| | | | |
                        | | | | | | | | |
                        | | | | | | | |r|
                        | | | | | |p| | |
                        | |q| | | | | | |
                        """));

            //get positions
            ChessPosition kingPosition = new ChessPosition(7, 4);
            ChessPosition pawnPosition = new ChessPosition(2, 6);
            ChessPosition bishopPosition = new ChessPosition(7, 8);
            ChessPosition queenPosition = new ChessPosition(1, 2);
            ChessPosition knightPosition = new ChessPosition(5, 4);
            ChessPosition rookPosition = new ChessPosition(3, 8);


            var validMoves = TestUtilities.loadMoves(kingPosition, new int[][]{{6, 5}});

            assertMoves(game, validMoves, kingPosition);

            //make sure teams other pieces are not allowed to move
            Assertions.assertTrue(game.validMoves(pawnPosition).isEmpty(), TRAPPED_PIECE_MOVES);
            Assertions.assertTrue(game.validMoves(bishopPosition).isEmpty(), TRAPPED_PIECE_MOVES);
            Assertions.assertTrue(game.validMoves(queenPosition).isEmpty(), TRAPPED_PIECE_MOVES);
            Assertions.assertTrue(game.validMoves(knightPosition).isEmpty(), TRAPPED_PIECE_MOVES);
            Assertions.assertTrue(game.validMoves(rookPosition).isEmpty(), TRAPPED_PIECE_MOVES);
        }


        @Test
        @DisplayName("King Cannot Move Into Check")
        public void noPutSelfInDanger() {

            var game = new ChessGame();
            game.setBoard(TestUtilities.loadBoard("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | |k| | |
                        | | | | | | | | |
                        | | | | | |K| | |
                        | | | | | | | | |
                        """));

            ChessPosition position = new ChessPosition(2, 6);
            var validMoves = TestUtilities.loadMoves(position, new int[][]{
                    {1, 5}, {1, 6}, {1, 7}, {2, 5}, {2, 7},
            });
            assertMoves(game, validMoves, position);
        }

        @Test
        @DisplayName("Valid Moves Independent of Team Turn")
        public void validMovesOtherTeam() {
            var game = new ChessGame();
            game.setBoard(TestUtilities.defaultBoard());
            game.setTeamTurn(ChessGame.TeamColor.BLACK);

            ChessPosition position = new ChessPosition(2, 5);
            var validMoves = TestUtilities.loadMoves(position, new int[][]{
                    {3, 5}, {4, 5}
            });
            assertMoves(game, validMoves, position);
        }

        private static void assertMoves(ChessGame game, List<ChessMove> validMoves, ChessPosition position) {
            var generatedMoves = game.validMoves(position);
            var actualMoves = new ArrayList<>(generatedMoves);
            TestUtilities.validateMoves(validMoves, actualMoves);
        }
    }
}