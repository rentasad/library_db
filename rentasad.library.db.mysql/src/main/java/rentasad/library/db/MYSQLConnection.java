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

public class MYSQLConnection
{
	public static final String MYSQL_HOST = "MYSQL_HOST";
	public static final String MYSQL_USER = "MYSQL_USER";
	public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
	public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
	public static final String MYSQL_PORT = "MYSQL_PORT";
	public static final String MYSQL_DEFAULT_PORT_VALUE = "3306";
	public static final int MYSQL_DEFAULT_PORT_VALUE_INT = 3306;
	private static final boolean debug = false;
	public static final String DRIVER_CLASS_MYSQL_CONNECTOR_8 = "com.mysql.cj.jdbc.Driver";

	private static MYSQLConnection instance = null;
	@Getter
	@Setter
	private Map<String, String> connectionParametersMap;
	private HikariDataSource dataSource;

	private MYSQLConnection()
	{
	}

	public static void initInstance(Map<String, String> connectionParametersMap) throws SQLException
	{
		if (instance == null)
		{
			instance = new MYSQLConnection();
			instance.setConnectionParametersMap(connectionParametersMap);
			instance.initializeDataSource(connectionParametersMap);
		}
		else
		{
			if (debug)
			{
				System.err.println("Instance wurde bereits initialisiert");
			}
		}
	}

	public static MYSQLConnection getInstance() throws SQLException
	{
		if (instance == null)
		{
			throw new SQLException("MySqlConnection Instance not initialized");
		}
		return instance;
	}

	private void initializeDataSource(Map<String, String> connectionParametersMap)
	{
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

		this.dataSource = new HikariDataSource(hikariConfig);
	}

	public Connection getConnection() throws SQLException
	{
		if (dataSource == null)
		{
			throw new SQLException("Connection pool is not initialized.");
		}
		return dataSource.getConnection();
	}

	public static Connection dbConnect(Map<String, String> connectionParametersMap) throws SQLException
	{
		initInstance(connectionParametersMap);
		return getInstance().getConnection();
	}

	public static Connection dbConnect(String dbHost, String databaseName, String dbUserid, String dbPassword) throws SQLException
	{
		Map<String, String> connectionParametersMap = new HashMap<>();
		connectionParametersMap.put(MYSQL_HOST, dbHost);
		connectionParametersMap.put(MYSQL_DATABASE, databaseName);
		connectionParametersMap.put(MYSQL_USER, dbUserid);
		connectionParametersMap.put(MYSQL_PASSWORD, dbPassword);
		initInstance(connectionParametersMap);
		return getInstance().getConnection();
	}

	public static Connection dbConnect(String dbHost, Integer connectionPort, String databaseName, String dbUserid, String dbPassword) throws SQLException
	{
		Map<String, String> connectionParametersMap = new HashMap<>();
		connectionParametersMap.put(MYSQL_HOST, dbHost);
		connectionParametersMap.put(MYSQL_DATABASE, databaseName);
		connectionParametersMap.put(MYSQL_USER, dbUserid);
		connectionParametersMap.put(MYSQL_PASSWORD, dbPassword);
		connectionParametersMap.put(MYSQL_PORT, connectionPort.toString());
		initInstance(connectionParametersMap);
		return getInstance().getConnection();
	}

	public void close()
	{
		if (dataSource != null)
		{
			dataSource.close();
			System.out.println("HikariCP connection pool closed.");
		}
	}

	public static Map<String, String> getDefaultConnectionPropertiesMap()
	{
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

	public static String getParamStringFromConnectionPropertiesMap(final Map<String, String> connectionPropertiesMap)
	{
		StringBuilder parameterString = new StringBuilder();
		Set<String> keySet = connectionPropertiesMap.keySet();
		int i = 0;
		for (String param : keySet)
		{
			i++;
			String value = connectionPropertiesMap.get(param);
			parameterString.append(String.format("%s=%s", param, value));
			if (i < keySet.size())
			{
				parameterString.append("&");
			}
		}

		return parameterString.toString();
	}

	public static Connection dbConnectWithTimeZoneUTC(Map<String, String> mySqlConfigMap) throws SQLException
	{
		if (instance == null)
		{
			instance = new MYSQLConnection();
			instance.setConnectionParametersMap(mySqlConfigMap);
			instance.initializeDataSourceWithTimeZoneUTC(mySqlConfigMap);
		}
		return instance.getConnection();
	}

	private void initializeDataSourceWithTimeZoneUTC(Map<String, String> connectionParametersMap)
	{
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
		hikariConfig.addDataSourceProperty("serverTimezone", "UTC");

		hikariConfig.addDataSourceProperty("characterEncoding", connectionParametersMap.getOrDefault("MYSQL_ENCODING", "cp1250"));

		this.dataSource = new HikariDataSource(hikariConfig);
	}

}
