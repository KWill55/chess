package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

/**
 * BaseHandler is an abstract class that provides common functionality for all request handlers.
 * It processes incoming requests, handles errors, and gets authTokens.
 *
 * @param <T> The type of request data expected by the handler (e.g., LoginRequest, RegisterRequest).
 */
public abstract class BaseHandler<T> implements Route {

    // Gson instance for converting JSON data to/from Java objects
    protected final Gson gson = new Gson();

    /**
     * Handles incoming HTTP requests. This method is automatically called by Spark.
     * It parses the request, calls the specific handler logic, and handles any errors.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @return The response object (typically a JSON string).
     */
    @Override
    public Object handle(Request request, Response response) {
        try {
            // Convert the request body into the expected Java object (T)
            T requestData = parseRequest(request);

            // Call the specific request handler method implemented by subclasses
            return handleRequest(requestData, request, response);
        } catch (Exception e) {
            // If any error occurs, return a 500 Internal Server Error response
            response.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Parses the request body and converts it into the appropriate Java object.
     * Subclasses must implement this method to define how to parse their specific request type.
     *
     * @param request The HTTP request.
     * @return The parsed request data object.
     */
    protected abstract T parseRequest(Request request);

    /**
     * Processes the request and returns a response.
     * This method must be implemented by each specific handler to define request handling logic.
     *
     * @param requestData The parsed request data.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @return The response object (usually a JSON string).
     */
    protected abstract Object handleRequest(T requestData, Request request, Response response);

    /**
     * Gets authToken from request headers
     *
     * @param request The HTTP request.
     * @return The auth token as a string (or null if not present).
     */
    protected String getAuthToken(Request request) {
        return request.headers("Authorization");
    }
}
