package dataaccess;
import model.GameData;
import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    void insertGame(GameData game) throws DataAccessException;

    int getNextGameID();

    List<GameData> listGames() throws DataAccessException;
}