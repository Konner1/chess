package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;


public class MySQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)")) {

                statement.setString(1, game.username());
                statement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                statement.setString(3, user.email());
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            throw new DataAccessException("Game already exists: " + game.gameName());
        }
    }

    @Override
    public int getNextGameID() {
        return 0;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }
}
