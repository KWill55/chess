package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import model.GameData;
import ui.DrawBoard;

import java.util.Scanner;

public class GameRepl {
    private final ChessClient client;
    private final GameData game;
    private final String playerColor;
    private final Scanner scanner = new Scanner(System.in);

    public GameRepl(ChessClient client, GameData game, String playerColor) {
        this.client = client;
        this.game = game;
        this.playerColor = playerColor;
    }

    public void run() {
        System.out.println("\u2654 Entered Game '" + game.gameName() + "' as " + playerColor);
        System.out.println("Type 'help' to see available in-game commands.\n");

        String input;
        while (true) {
            System.out.print("[Game " + game.gameName() + "] >>> ");
            input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");

            if (tokens.length == 0) {
                continue;
            }

            switch (tokens[0].toLowerCase()) {

//                case "move" -> {
//                    if (tokens.length != 3) {
//                        System.out.println("Usage: move <from> <to>");
//                        break;
//                    }
//                    String from = tokens[1];
//                    String to = tokens[2];
//
//                    // Validate format: must be letter+number like "e2"
//                    if (!from.matches("^[a-h][1-8]$") || !to.matches("^[a-h][1-8]$")) {
//                        System.out.println("Invalid move format. Please use coordinates like 'e2' or 'g7'.");
//                        break;
//                    }
//
//                    client.makeMove(game.gameID(), from, to);
//                }
                case "move" -> {
                    if (tokens.length != 3) {
                        System.out.println("Usage: move <from> <to>");
                        break;
                    }
                    String from = tokens[1];
                    String to = tokens[2];

                    // Validate format: must be letter+number like "e2"
                    if (!from.matches("^[a-h][1-8]$") || !to.matches("^[a-h][1-8]$")) {
                        System.out.println("Invalid move format. Please use coordinates like 'e2' or 'g7'.");
                        break;
                    }

                    int col = from.charAt(0) - 'a' + 1;
                    int row = from.charAt(1) - '0';
                    ChessPosition fromPos = new ChessPosition(row, col);
                    ChessGame actualGame = game.game();
                    ChessPiece piece = actualGame.getBoard().getPiece(fromPos);

                    if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        int promotionRank = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
                        if (to.charAt(1) - '0' == promotionRank) {
                            System.out.println("Promote pawn to [Q]ueen, [R]ook, [B]ishop, or k[N]ight?");
                            String prom_input = scanner.nextLine().trim().toUpperCase();

                            ChessPiece.PieceType promotion = switch (prom_input) {
                                case "Q" -> ChessPiece.PieceType.QUEEN;
                                case "R" -> ChessPiece.PieceType.ROOK;
                                case "B" -> ChessPiece.PieceType.BISHOP;
                                case "N" -> ChessPiece.PieceType.KNIGHT;
                                default -> {
                                    System.out.println("Invalid input. Promoting to Queen by default.");
                                    yield ChessPiece.PieceType.QUEEN;
                                }
                            };

                            client.makeMove(game.gameID(), from, to, promotion);
                            break;
                        }
                    }

                    // Normal move (not promotion)
                    client.makeMove(game.gameID(), from, to);
                }


                case "redraw" -> client.redrawBoard();
                case "highlight" -> {
                    if (tokens.length != 2) {
                        System.out.println("Usage: highlight <pos>");
                        break;
                    }
                    String from = tokens[1];
                    client.highlightValidMoves(from);
                }
                case "resign" -> {
                    System.out.print("Are you sure you want to resign? (yes/no): ");
                    String confirmation = scanner.nextLine().trim().toLowerCase();
                    if (confirmation.equals("yes") || confirmation.equals("y")) {
                        client.resignGame(game.gameID());
                        System.out.println("You resigned the game.");
                    } else {
                        System.out.println("Resignation canceled.");
                    }
                }

                case "leave" -> {
                    client.leaveGame(game.gameID());
                    System.out.println("You left the game.");
                    return;
                }
                case "help" -> printHelp();
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }

            System.out.println();
        }
    }


    private void printHelp() {
        System.out.println("In-game commands:");
        System.out.println("- move <from> <to> (e.g. move e2 e4)");
        System.out.println("- resign (forfeit the game)");
        System.out.println("- leave (exit to lobby)");
        System.out.println("- redraw (reprint the board)");
        System.out.println("- highlight <pos> (highlight valid moves for piece)");
        System.out.println("- help (show this help)");
    }
}
