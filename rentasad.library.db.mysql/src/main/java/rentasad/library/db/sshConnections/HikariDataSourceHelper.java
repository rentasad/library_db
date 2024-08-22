package rentasad.library.db.sshConnections;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import rentasad.library.db.ConnectionInfo;

/**
 * The HikariDataSourceHelper class provides a static method to create a HikariDataSource object.
 *
 * <p>The {@code getDatasource} method takes an assigned port and a ConnectionInfo object as input.
 * It creates a JDBC URL using the assigned port and the database name from the ConnectionInfo object.
 * It then creates a HikariConfig object and sets the JDBC URL, username, and password from the
 * ConnectionInfo object. Finally, it returns a new HikariDataSource object.</p>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * int assignedPort = 3306;
 * ConnectionInfo connectionInfo = new ConnectionInfo.Builder()
 *                              .dbName("my_db")
 *                              .dbUser("my_user")
 *                              .dbPassword("my_password")
 *                              .build();
 * HikariDataSource dataSource = HikariDataSourceHelper.getDatasource(assignedPort, connectionInfo);
 * }</pre>
 *
 * @see ConnectionInfo
 */
public class HikariDataSourceHelper
{
	/**
	 * Returns a HikariDataSource object for a given assignedPort and connectionInfo.
	 * <p>
	 * This method takes an assigned port and a ConnectionInfo object as input and creates
	 * a JDBC URL using the assigned port and the database name from the ConnectionInfo object.
	 * It then creates a HikariConfig object and sets the JDBC URL, username, and password from
	 * the ConnectionInfo object. Finally, it returns a new HikariDataSource
	 */
	public static HikariDataSource getDatasource(final int port, final ConnectionInfo connectionInfo) {
		String jdbcUrl = createJdbcUrl(port, connectionInfo.getDbName());
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(connectionInfo.getDbUser());
		config.setPassword(connectionInfo.getDbPassword());
		return new HikariDataSource(config);
	}

	/**
	 * Creates a JDBC URL for connecting to a MySQL database.
	 *
	 * @param port The port number to connect to.
	 * @param dbName The name of the database to connect to.
	 * @return A JDBC URL string.
	 */
	private static String createJdbcUrl(int port, String dbName) {
		final String JDBC_URL_FORMAT = "jdbc:mysql://localhost:%d/%s";
		return String.format(JDBC_URL_FORMAT, port, dbName);
	}

}
