package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;

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
        return makeRequest("DELETE", "/session", null, LogoutResponse.class, authToken);
    }

    public CreateGameResponse createGame(String authToken, String gameName) throws ResponseException {
        var request = new CreateGameRequest(authToken, gameName); // Don't include authToken in the request body
        return makeRequest("POST", "/game", request, CreateGameResponse.class, authToken);
    }


    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var request = new ListGamesRequest(authToken);
        return makeRequest("GET", "/game", request, ListGamesResponse.class, authToken);
    }

    //play game
    public JoinGameResponse joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var request = new JoinGameRequest(authToken, playerColor, gameID);
        return makeRequest("PUT", "/game", request, JoinGameResponse.class, authToken);
    }

    //observe game
    public JoinGameResponse observeGame(String authToken, int gameID) throws ResponseException {
        var request = new JoinGameRequest(authToken,null, gameID); // null color = observe
        return makeRequest("PUT", "/game", request, JoinGameResponse.class, authToken);
    }

    // ---------- Clear Method ----------

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    // ---------- Request Handling ----------

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            System.out.println("[DEBUG] Sending request to: " + url);
            System.out.println("[DEBUG] Method: " + method);
            System.out.println("[DEBUG] AuthToken: " + authToken);
            System.out.println("[DEBUG] Request body: " + new Gson().toJson(request));

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            // Only enable output for POST/PUT/DELETE requests that send a body
            if (!method.equalsIgnoreCase("GET") && request != null) {
                http.setDoOutput(true);
                writeBody(request, http);
            }

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
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

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null || http.getContentLength() == 0) return null;

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            return new Gson().fromJson(reader, responseClass);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}

