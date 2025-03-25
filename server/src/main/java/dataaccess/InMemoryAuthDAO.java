//package dataaccess;
//
//import model.AuthData;
//import java.util.HashMap;
//import java.util.Map;
//
//public class InMemoryAuthDAO implements AuthDAO {  // Now implements an interface
//    private final Map<String, AuthData> authMap = new HashMap<>();
//
//    @Override
//    public void createAuth(AuthData auth) throws DataAccessException {
//        if (authMap.containsKey(auth.authToken())) {
//            throw new DataAccessException("Error: Auth token already exists");
//        }
//        authMap.put(auth.authToken(), auth);
//    }
//
//    @Override
//    public AuthData getAuth(String authToken) throws DataAccessException {
//        return authMap.get(authToken);
//    }
//
//    @Override
//    public void deleteAuth(String authToken) throws DataAccessException {
//        authMap.remove(authToken);
//    }
//
//    @Override
//    public void clear() throws DataAccessException {
//        authMap.clear();
//    }
//}
