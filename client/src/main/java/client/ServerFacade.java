package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    // ---------- Prelogin Methods ----------

    //quit and help don't interact with the server, so are therefore only in repl

    public RegisterResponse register(String username, String password, String email) throws ResponseException {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, RegisterResponse.class, null);
    }

    public LoginResponse login(String username, String password) throws ResponseException {
        var request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", request, LoginResponse.class, null);
    }

    // ---------- Postlogin Methods ----------

    //quit and help don't interact with the server, so are therefore only in repl

    public LogoutResponse logout(String authToken) throws ResponseException {
        LogoutResponse response = makeRequest("DELETE", "/session", null, LogoutResponse.class, authToken);
        // If the logout response is empty, create a new empty LogoutResponse.
        if (response == null) {
            return new LogoutResponse();
        }
        return response;
    }


    public CreateGameResponse createGame(String authToken, String gameName) throws ResponseException {
        var request = new CreateGameRequest(gameName); // ONLY the game name
        return makeRequest("POST", "/game", request, CreateGameResponse.class, authToken);
    }


    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var request = new ListGamesRequest(authToken);
        return makeRequest("GET", "/game", request, ListGamesResponse.class, authToken);
    }

    //play game
    public JoinGameResponse joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var request = new JoinGameRequest(playerColor, gameID);
        return makeRequest("PUT", "/game", request, JoinGameResponse.class, authToken);
    }

    //observe game
    public void observeGame(String authToken, int gameID) throws ResponseException {
        var request = new JoinGameRequest(null, gameID);
//        return makeRequest("PUT", "/game", request, JoinGameResponse.class, authToken);
    }

//    public void observeGame(int gameID, String authToken) throws Exception {
//        var body = Map.of(
//                "gameID", gameID,
//                "playerColor", null
//        );
//
//        var response = this.makePostRequest("/game/join", body, authToken);
//
//        if (response.statusCode() != 200) {
//            throw new Exception("Error: " + response.body());
//        }
//    }



    // ---------- Clear Method ----------

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    // ---------- Request Handling ----------

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            // Set the auth token header BEFORE writing the body.
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            // Only enable output for methods that send a body (POST, PUT, DELETE)
            if (!method.equalsIgnoreCase("GET") && request != null) {
                http.setDoOutput(true);
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);

            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException, ResponseException {
        if (http.getResponseCode() >= 400) {
            try (InputStream errorStream = http.getErrorStream()) {
                InputStreamReader reader = new InputStreamReader(errorStream);
                var map = new Gson().fromJson(reader, Map.class);
                String message = (String) map.get("message");
                throw new ResponseException(http.getResponseCode(), message != null ? message : "Unknown error");
            }
        }

        if (http.getResponseCode() == 204 || http.getContentLength() == 0) {
            return null;
        }

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (responseClass != null) {
                return new Gson().fromJson(reader, responseClass);
            } else {
                return null;
            }
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

