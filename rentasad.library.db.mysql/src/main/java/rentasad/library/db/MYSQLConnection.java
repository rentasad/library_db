package rentasad.library.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The MYSQLConnection class represents a connection to a MySQL database. It provides methods for creating and managing database connections.
 *
 * This class has the following properties:
 * - MYSQL_HOST: A constant that represents the host name of the MySQL server.
 * - MYSQL_USER: A constant that represents the user name for database login.
 * - MYSQL_DATABASE: A constant that represents the name of the database.
 * - MYSQL_PASSWORD: A constant that represents the password for database login.
 * - MYSQL_PORT: A constant that represents the port of the MySQL server.
 * - MYSQL_DEFAULT_PORT_VALUE: A constant that represents the default port of the MySQL server.
 * - MYSQL_DEFAULT_PORT_VALUE_INT: A constant that represents the default port of the MySQL server as an integer.
 * - DRIVER_CLASS_MYSQL_CONNECTOR_8: A constant that represents the fully qualified class name of the MySQL Connector/J driver.
 * - connectionParametersMap: A map that contains the connection parameters passed to the getInstance method.
 * - dataSource: A HikariDataSource object that represents the connection pool.
 * - instances: A map that stores the instances of MYSQLConnection created with different connection parameters.
 *
 * The MYSQLConnection class has the following methods:
 * - getInstance: A static method that returns an instance of MYSQLConnection based on the specified connection parameters.
 * - initializeDataSource: A private method that initializes the HikariDataSource object with the specified connection parameters.
 * - getConnection: A method that returns a Connection object from the connection pool.
 * - close: A method that closes the connection pool.
 * - getDefaultConnectionPropertiesMap: A static method that returns a map of default connection properties.
 * - getParamStringFromConnectionPropertiesMap: A static method that returns a formatted string of connection properties.
 * - dbConnectWithTimeZoneUTC: A static method that returns a Connection object with the UTC time zone.
 * - dbConnect: A static method that returns a Connection object with the specified connection parameters.
 */
public class MYSQLConnection {

	public static final String MYSQL_HOST = "MYSQL_HOST";
	public static final String MYSQL_USER = "MYSQL_USER";
	public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
	public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
	public static final String MYSQL_PORT = "MYSQL_PORT";
	public static final String MYSQL_DEFAULT_PORT_VALUE = "3306";
	public static final int MYSQL_DEFAULT_PORT_VALUE_INT = 3306;
	private static final boolean debug = false;
	public static final String DRIVER_CLASS_MYSQL_CONNECTOR_8 = "com.mysql.cj.jdbc.Driver";

	private static final Map<String, MYSQLConnection> instances = new HashMap<>();
	@Getter
	@Setter
	private Map<String, String> connectionParametersMap;
	private HikariDataSource dataSource;

	private MYSQLConnection() {
	}

	public static MYSQLConnection getInstance(Map<String, String> connectionParametersMap) throws SQLException {
		String host = connectionParametersMap.get(MYSQL_HOST);
		String dbName = connectionParametersMap.get(MYSQL_DATABASE);
		String port = connectionParametersMap.getOrDefault(MYSQL_PORT, MYSQL_DEFAULT_PORT_VALUE);
		String instanceKey = host + "-" + port + "-" + dbName;

		if (!instances.containsKey(instanceKey)) {
			MYSQLConnection instance = new MYSQLConnection();
			instance.setConnectionParametersMap(connectionParametersMap);
			instance.initializeDataSource(connectionParametersMap, instanceKey);
			instances.put(instanceKey, instance);
		}
		return instances.get(instanceKey);
	}

	private void initializeDataSource(Map<String, String> connectionParametersMap, String poolName) {
		HikariConfig hikariConfig = new HikariConfig();
		String mySqlServerHostname = connectionParametersMap.get(MYSQL_HOST);
		String mySqlDatabaseName = connectionParametersMap.get(MYSQL_DATABASE);
		String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
		String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);
		String mySqlDbPort = connectionParametersMap.getOrDefault(MYSQL_PORT, MYSQL_DEFAULT_PORT_VALUE);
		String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", mySqlServerHostname, mySqlDbPort, mySqlDatabaseName);

		hikariConfig.setJdbcUrl(jdbcUrl);
		hikariConfig.setUsername(mySqlDbUserid);
		hikariConfig.setPassword(mySqlDbPassword);
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
		hikariConfig.setPoolName("HikariPool-" + poolName);
		hikariConfig.setMaximumPoolSize(2);

		this.dataSource = new HikariDataSource(hikariConfig);
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
			throw new SQLException("Connection pool is not initialized.");
		}
		return dataSource.getConnection();
	}

	public void close() {
		if (dataSource != null) {
			dataSource.close();
			System.out.println("HikariCP connection pool closed.");
		}
	}

	public static Map<String, String> getDefaultConnectionPropertiesMap() {
		Map<String, String> connectionPropertiesMap = new HashMap<>();
		connectionPropertiesMap.put("allowMultiQueries", "true");
		connectionPropertiesMap.put("rewriteBatchedStatements", "true");
		connectionPropertiesMap.put("zeroDateTimeBehavior", "convertToNull");
		connectionPropertiesMap.put("useUnicode", "true");
		connectionPropertiesMap.put("useSSL", "false");
		connectionPropertiesMap.put("useJDBCCompliantTimezoneShift", "true");
		connectionPropertiesMap.put("useLegacyDatetimeCode", "false");
		connectionPropertiesMap.put("serverTimezone", "Europe/Berlin");

		return connectionPropertiesMap;
	}

	public static String getParamStringFromConnectionPropertiesMap(final Map<String, String> connectionPropertiesMap) {
		StringBuilder parameterString = new StringBuilder();
		Set<String> keySet = connectionPropertiesMap.keySet();
		int i = 0;
		for (String param : keySet) {
			i++;
			String value = connectionPropertiesMap.get(param);
			parameterString.append(String.format("%s=%s", param, value));
			if (i < keySet.size()) {
				parameterString.append("&");
			}
		}

		return parameterString.toString();
	}

	// Abwärtskompatible Methode für die Verbindung mit UTC-Zeitzone
	public static Connection dbConnectWithTimeZoneUTC(Map<String, String> mySqlConfigMap) throws SQLException {
		mySqlConfigMap.put("serverTimezone", "UTC");
		MYSQLConnection instance = getInstance(mySqlConfigMap);
		return instance.getConnection();
	}

	// Abwärtskompatible Methode für die Standard-DB-Verbindung
	public static Connection dbConnect(Map<String, String> connectionParametersMap) throws SQLException {
		MYSQLConnection instance = getInstance(connectionParametersMap);
		return instance.getConnection();
	}
}