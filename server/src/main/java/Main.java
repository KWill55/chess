import chess.*;
import server.Server;
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

        // Initialize Services
        UserService userService = new UserService(userDAO, authDAO);
        AuthService authService = new AuthService(authDAO, userDAO);

        // Create and start the server on port 8080
        Server server = new Server(userService, authService);
        server.run(8080);
    }
}