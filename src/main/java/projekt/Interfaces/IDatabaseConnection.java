package projekt.Interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseConnection {

  IDatabaseConnection openConnection() throws SQLException;

  void createAllTables();

  void truncateAllTables();

  void removeAllTables();

}
