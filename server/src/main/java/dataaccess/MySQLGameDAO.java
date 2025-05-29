package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import chess.ChessGame;


public class MySQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM game")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game", e);
        }
    }

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)")) {
                statement.setInt(1, game.gameID());
                statement.setString(2, game.whiteUsername());
                statement.setString(3, game.blackUsername());
                statement.setString(4, game.gameName());
                statement.setString(5, new Gson().toJson(game.game()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Game already exists: " + game.gameName());
        }
    }

    @Override
    public int getNextGameID() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT MAX(gameID) AS maxID FROM game");
             var result = statement.executeQuery()) {
            if (result.next()) {
                return result.getInt("maxID") + 1;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate gameID", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>(16);

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game");
             var results = statement.executeQuery()) {

            while (results.next()) {
                int gameID = results.getInt("gameID");
                String whiteUsername = results.getString("whiteUsername");
                String blackUsername = results.getString("blackUsername");
                String gameName = results.getString("gameName");
                String gameJson = results.getString("chessGame");

                ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
        return games;
    }


    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?")) {
                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, new Gson().toJson(game.game()));
                statement.setInt(5, game.gameID());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) throw new DataAccessException("Failed: Update didn't work");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?")) {

            statement.setInt(1, gameID);
            var result = statement.executeQuery();

            if (result.next()) {
                int id = result.getInt("gameID");
                String whiteUsername = result.getString("whiteUsername");
                String blackUsername = result.getString("blackUsername");
                String gameName = result.getString("gameName");
                String gameJson = result.getString("game");

                ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);

                return new GameData(id, whiteUsername, blackUsername, gameName, chessGame);

            } else {
                throw new DataAccessException("Game not found: " + gameID);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game: " + gameID, e);
        }
    }
}
