import chess.*;
import dataaccess.GameDAO;
import server.Server;
import service.GameService;
import service.UserService;
import service.AuthService;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        // Initialize DAOs
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        // Initialize Services
        UserService userService = new UserService(userDAO);
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDAO);

        // Create and start the server on port 8080
        Server server = new Server(userService, authService, gameService);
        server.run(8080);
    }
}