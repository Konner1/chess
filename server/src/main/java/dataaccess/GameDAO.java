package dataaccess;
import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;
    void insertGame(GameData game) throws DataAccessException;
    int getNextGameID();
}