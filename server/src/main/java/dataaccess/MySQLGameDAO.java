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
                    "DELETE FROM games")) {
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
                    "INSERT INTO games (id, white_username, black_username, name, game_data) VALUES(?, ?, ?, ?, ?)")) {
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
             var statement = conn.prepareStatement("SELECT MAX(id) AS maxID FROM games");
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
                     "SELECT id, white_username, black_username, name, game_data FROM games");
             var results = statement.executeQuery()) {

            while (results.next()) {
                int gameID = results.getInt("id");
                String whiteUsername = results.getString("white_username");
                String blackUsername = results.getString("black_username");
                String gameName = results.getString("name");
                String gameJson = results.getString("game_data");

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
            try (var statement = conn.prepareStatement(
                    "UPDATE games SET white_username=?, black_username=?, name=?, game_data=? WHERE id=?")) {

                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, new Gson().toJson(game.game())); // serialized ChessGame
                statement.setInt(5, game.gameID());

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Failed: Update didn't affect any rows");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SQL error during update: " + e.getMessage(), e);
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "SELECT id, white_username, black_username, name, game_data FROM games WHERE id = ?")) {

            statement.setInt(1, gameID);
            var result = statement.executeQuery();

            if (result.next()) {
                int id = result.getInt("id");
                String whiteUsername = result.getString("white_username");
                String blackUsername = result.getString("black_username");
                String gameName = result.getString("name");
                String gameJson = result.getString("game_data");

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
