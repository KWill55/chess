import chess.*;
import dataaccess.*;
import server.Server;
import service.GameService;
import service.UserService;
import service.AuthService;



public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        boolean useMySQL = true;  // Change this to false for in-memory storage

        AuthDAO authDAO = useMySQL ? new SQLAuthDAO() : new InMemoryAuthDAO();
        UserDAO userDAO = useMySQL ? new SQLUserDAO() : new InMemoryUserDAO();
        GameDAO gameDAO = useMySQL ? new SQLGameDAO() : new InMemoryGameDAO();

        UserService userService = new UserService(userDAO);
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDAO);

        Server server = new Server(userService, authService, gameService);
        server.run(8080);
    }
}