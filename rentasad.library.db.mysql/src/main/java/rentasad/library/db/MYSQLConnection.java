package rentasad.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Gustini GmbH (2017) Creation: 03.03.2017 UPDATE 15.08.2018 Switch to MySql
 * Connector 8 Library gustini.library.db
 * 
 * @author Matthias Staud
 *
 *
 *         Description: ENCODING TABLE: MySQL Character Set Name Java-Style
 *         Character Encoding Name ascii US-ASCII big5 Big5 gbk GBK sjis SJIS
 *         (or Cp932 or MS932 for MySQL Server < 4.1.11) cp932 Cp932 or MS932
 *         (MySQL Server > 4.1.11) gb2312 EUC_CN ujis EUC_JP euckr EUC_KR latin1
 *         Cp1252 latin2 ISO8859_2 greek ISO8859_7 hebrew ISO8859_8 cp866 Cp866
 *         tis620 TIS620 cp1250 Cp1250 cp1251 Cp1251 cp1257 Cp1257 macroman
 *         MacRoman macce MacCentralEurope utf8 UTF-8 ucs2 UnicodeBig
 *
 */
public class MYSQLConnection
{
	public static final String MYSQL_HOST = "MYSQL_HOST";
	public static final String MYSQL_USER = "MYSQL_USER";
	public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
	public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
	public static final String MYSQL_PORT = "MYSQL_PORT";
	public static final String MYSQL_DEFAULT_PORT_VALUE = "3306";
	public static final int MYSQL_DEFAULT_PORT_VALUE_INT = 3306;

	public static final String DRIVER_CLASS_MYSQL_CONNECTOR_5 = "com.mysql.jdbc.Driver";
	public static final String DRIVER_CLASS_MYSQL_CONNECTOR_8 = "com.mysql.cj.jdbc.Driver";

	private static MYSQLConnection instance = null;
	// private static Driver driver = null;
	public static boolean debug = false;
	private Map<String, String> connectionParametersMap;
	private Connection connection;
	private static boolean driverWasInitStatic = false;
	private boolean driverWasInit = false;

	/**
	 * 
	 * Description: init MySQl Driver on static access if not loaded
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException Creation: 20.08.2018 by mst
	 */
	private static void loadDriverStatic() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (!driverWasInitStatic)
		{
			Class.forName(DRIVER_CLASS_MYSQL_CONNECTOR_8);
			driverWasInitStatic = true;
		}
	}

	/**
	 * 
	 * Description: init MySQl Driver on insctance access if not loaded
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException Creation: 20.08.2018 by mst
	 */
	private void loadDriverInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (!driverWasInit)
		{
			Class.forName(DRIVER_CLASS_MYSQL_CONNECTOR_8);
			driverWasInit = true;
		}
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return Creation: 14.02.2018 by mst
	 */
	public static boolean isInit()
	{
		if (instance == null)
			return false;
		else
			return true;
	}

	public static void initInstance(Map<String, String> connectionParametersMap) throws SQLException
	{
		if (instance == null)
		{
			instance = new MYSQLConnection();
			if (!instance.driverWasInit)
			{
				try
				{
					instance.loadDriverInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
				{
					throw new SQLException(e);
				}
			}
			instance.setConnection(MYSQLConnection.dbConnect(connectionParametersMap));
			instance.setConnectionParametersMap(connectionParametersMap);
		} else
		{
			if (debug)
			{
				System.err.println("Instance wurde bereits initialisiert");
			}
		}
	}

	/**
	 * 
	 * Description: GetInstance Class for Managing Connection (and Reconnection
	 * after Close and Timeout
	 * 
	 * @return
	 * @throws SQLException Creation: 14.02.2018 by mst
	 */
	public static MYSQLConnection getInstance() throws SQLException
	{
		if (instance == null)
		{
			throw new SQLException("MySqlConnection Instance not initialized");
		} else
		{
			return instance;
		}

	}

	/**
	 *
	 * Description:
	 *
	 * @param connectionParametersMap Map<String, String> mit folgenden Parametern:
	 *                                <br>
	 *                                MYSQL_HOST <br>
	 *                                MYSQL_DATABASE <br>
	 *                                MYSQL_USER <br>
	 *                                MYSQL_PASSWORD
	 *
	 * @return
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException           Creation: 15.12.2015 by mst
	 */
	public static Connection dbConnect(Map<String, String> connectionParametersMap) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		String mySqlServerHostname = connectionParametersMap.get(MYSQL_HOST);
		String mySqlDatabaseName = connectionParametersMap.get(MYSQL_DATABASE);
		String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
		String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);

		/*
		 * Add Connecting TCP Port
		 */
		Integer mySqlDbPort;
		if (connectionParametersMap.containsKey(MYSQL_PORT))
		{
			mySqlDbPort = Integer.valueOf(connectionParametersMap.get(MYSQL_PORT));
		} else
		{
			mySqlDbPort = MYSQL_DEFAULT_PORT_VALUE_INT;
		}

		if (connectionParametersMap.containsKey("MYSQL_ENCODING"))
		{
			String mySqlEncoding = connectionParametersMap.get("MYSQL_ENCODING");
			return MYSQLConnection.dbConnect(mySqlServerHostname, mySqlDbPort, mySqlDatabaseName, mySqlDbUserid,
					mySqlDbPassword, mySqlEncoding);
		} else
		{

			return MYSQLConnection.dbConnect(mySqlServerHostname, mySqlDbPort, mySqlDatabaseName, mySqlDbUserid,
					mySqlDbPassword);
		}

	}

	/**
	 * 
	 * Description:Connect with parameter serverTimezone=UTC
	 * 
	 * @param mySqlConfigMap
	 * @return
	 * @throws SQLException Creation: 20.09.2018 by mst
	 */
	public static Connection dbConnectWithTimeZoneUTC(Map<String, String> mySqlConfigMap) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		String mySqlServerUrl = mySqlConfigMap.get(MYSQL_HOST);
		String mySqlDatabaseName = mySqlConfigMap.get(MYSQL_DATABASE);
		String mySqlDbUserid = mySqlConfigMap.get(MYSQL_USER);
		String mySqlDbPassword = mySqlConfigMap.get(MYSQL_PASSWORD);

		String characterEncoding = "cp1250";
		Map<String, String> connectionProperties = getDefaultConnectionPropertiesMap();
		connectionProperties.put("serverTimezone", "UTC");
		connectionProperties.put("characterEncoding", characterEncoding);

		/*
		 * Encoding hinzufügen wenn in Config angegeben
		 */
		if (mySqlConfigMap.containsKey("MYSQL_ENCODING"))
		{
			String mySqlEncoding = mySqlConfigMap.get("MYSQL_ENCODING");
			connectionProperties.put("characterEncoding", mySqlEncoding);
		}

		/*
		 * Add Connecting TCP Port
		 */
		String mySqlDbPort;
		if (mySqlConfigMap.containsKey(MYSQL_PORT))
		{
			mySqlDbPort = mySqlConfigMap.get(MYSQL_PORT);
			connectionProperties.put("port", mySqlDbPort);
		} else
		{
			mySqlDbPort = MYSQL_DEFAULT_PORT_VALUE;
			connectionProperties.put("port", mySqlDbPort);
		}

		String paramString = getParamStringFromConnectionPropertiesMap(connectionProperties);
		String connectionString = String.format("jdbc:mysql://%s/%s?%s", mySqlServerUrl, mySqlDatabaseName,
				paramString);
		return DriverManager.getConnection(connectionString, mySqlDbUserid, mySqlDbPassword);

	}

	/**
	 * 
	 * Description:
	 * 
	 * @param mySqlDatabaseName
	 * @param connectionParametersMap
	 * @return
	 * @throws SQLException Creation: 14.02.2018 by mst
	 */
	public static Connection dbConnect(String mySqlDatabaseName, Map<String, String> connectionParametersMap)
			throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		String mySqlServerUrl = connectionParametersMap.get(MYSQL_HOST);
		String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
		String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);
		return MYSQLConnection.dbConnect(mySqlServerUrl, mySqlDatabaseName, mySqlDbUserid, mySqlDbPassword);
	}

	/**
	 * Stellt eine Datenbankverbindung zu einer MySQL-Datenbank her und gibt das
	 * Connection-Objekt zurueck. ENCODING im Connection-String:
	 * characterEncoding=cp1250
	 *
	 * @param dbHost
	 * @param databaseName
	 * @param dbUserid
	 * @param dbPassword
	 * @return
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Connection dbConnect(String dbHost, String databaseName, String dbUserid, String dbPassword)
			throws SQLException
	{

		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		String characterEncoding = "cp1250";
		Map<String, String> connectionProperties = getDefaultConnectionPropertiesMap();
		connectionProperties.put("characterEncoding", characterEncoding);
		String paramString = getParamStringFromConnectionPropertiesMap(connectionProperties);
		String connectionString = String.format("jdbc:mysql://%s/%s?%s", dbHost, databaseName, paramString);
		return DriverManager.getConnection(connectionString, dbUserid, dbPassword);
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param dbHost
	 * @param databaseName
	 * @param dbUserid
	 * @param dbPassword
	 * @param encoding
	 * @return
	 * @throws SQLException Creation: 14.02.2015 by mst
	 */
	public static Connection dbConnect(String dbHost, String databaseName, String dbUserid, String dbPassword,
			String encoding) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		Map<String, String> connectionProperties = getDefaultConnectionPropertiesMap();
		connectionProperties.put("characterEncoding", encoding);
		String paramString = getParamStringFromConnectionPropertiesMap(connectionProperties);
		String connectionString = String.format("jdbc:mysql://%s/%s?%s", dbHost, databaseName, paramString);
		return DriverManager.getConnection(connectionString, dbUserid, dbPassword);
	}

	/**
	 * 
	 * @param dbHost
	 * @param connectionPort (default = 3306)
	 * @param databaseName
	 * @param dbUserid
	 * @param dbPassword
	 * @param encoding
	 * @return
	 * @throws SQLException
	 */
	public static Connection dbConnect(String dbHost, Integer connectionPort, String databaseName, String dbUserid,
			String dbPassword) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		Map<String, String> connectionProperties = getDefaultConnectionPropertiesMap();
		String paramString = getParamStringFromConnectionPropertiesMap(connectionProperties);
		String connectionString = String.format("jdbc:mysql://%s:%s/%s?%s", dbHost, connectionPort.toString(),
				databaseName, paramString);
		return DriverManager.getConnection(connectionString, dbUserid, dbPassword);
	}

	public static Connection dbConnect(String dbHost, Integer connectionPort, String databaseName, String dbUserid,
			String dbPassword, String encoding) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		Map<String, String> connectionProperties = getDefaultConnectionPropertiesMap();
		connectionProperties.put("characterEncoding", encoding);
		String paramString = getParamStringFromConnectionPropertiesMap(connectionProperties);
		String connectionString = String.format("jdbc:mysql://%s:%s/%s?%s", dbHost, connectionPort.toString(),
				databaseName, paramString);
		return DriverManager.getConnection(connectionString, dbUserid, dbPassword);
	}

	/**
	 *
	 * Description: Default Connection without any additional Params
	 *
	 * @param dbHost
	 * @param databaseName
	 * @param db_userid
	 * @param db_password
	 * @return
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException Creation: 01.10.2015 by mst
	 */
	public static Connection dbConnectWithoutEncoding(String dbHost, String databaseName, String db_userid,
			String db_password) throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		return DriverManager.getConnection(
				String.format("jdbc:mysql://%s/%s?user=%s&password=%s", dbHost, databaseName, db_userid, db_password));
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param connectionParametersMap
	 * @return
	 * @throws SQLException Creation: 14.02.2015 by mst
	 */
	public static Connection dbConnectWithoutEncoding(final Map<String, String> connectionParametersMap)
			throws SQLException
	{
		try
		{
			loadDriverStatic();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
		String mySqlServerUrl = connectionParametersMap.get(MYSQL_HOST);
		String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
		String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);
		String mySqlDbName = connectionParametersMap.get(MYSQL_DATABASE);
		return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", mySqlServerUrl,
				mySqlDbName, mySqlDbUserid, mySqlDbPassword));
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return Creation: 14.02.2018 by mst
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException
	{
		if (this.connection.isValid(4))
		{
			return this.connection;
		} else
		{
			this.connection = MYSQLConnection.dbConnect(this.connectionParametersMap);
			return this.connection;
		}

	}

	/**
	 * 
	 * Description: Return default Connection Properties Map with hard coded
	 * Defaults
	 * 
	 * allowMultiQueries = true zeroDateTimeBehavior = CONVERT_TO_NULL useUnicode =
	 * true
	 * 
	 * @return Creation: 15.08.2018 by mst
	 */
	public static Map<String, String> getDefaultConnectionPropertiesMap()
	{
		Map<String, String> connectionPropertiesMap = new HashMap<String, String>();
		connectionPropertiesMap.put("allowMultiQueries", "true");
		connectionPropertiesMap.put("rewriteBatchedStatements", "true");

		// JDBC 8
		// connectionPropertiesMap.put("zeroDateTimeBehavior", "CONVERT_TO_NULL");
		connectionPropertiesMap.put("allowPublicKeyRetrieval", "true");
		// JDBC 5.x
		connectionPropertiesMap.put("zeroDateTimeBehavior", "convertToNull");
		connectionPropertiesMap.put("useUnicode", "true");
		connectionPropertiesMap.put("useSSL", "false");
		/*
		 * UPDATE 20.08.2018
		 * https://stackoverflow.com/questions/26515700/mysql-jdbc-driver-5-1-33-time-
		 * zone-issue After Error: The server time zone value 'CEST' is unrecognized or
		 * represents more than one time zone.
		 */
		connectionPropertiesMap.put("useJDBCCompliantTimezoneShift", "true");
		connectionPropertiesMap.put("useLegacyDatetimeCode", "false");
		connectionPropertiesMap.put("serverTimezone", "Europe/Berlin");

		/**
		 * Zeitzone UTC sorgte für das Einfügen des falschen Datums
		 */
//        connectionPropertiesMap.put("serverTimezone", "UTC");

		return connectionPropertiesMap;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param connectionPropertiesMap
	 * @return Creation: 15.08.2018 by mst
	 */
	public static String getParamStringFromConnectionPropertiesMap(final Map<String, String> connectionPropertiesMap)
	{
		String parameterString = "";
		Set<String> keySet = connectionPropertiesMap.keySet();
		int i = 0;
		for (String param : keySet)
		{
			i++;
			String value = connectionPropertiesMap.get(param);
			parameterString += String.format("%s=%s", param, value);
			if (i < keySet.size())
			{
				// Append ? if other param will follow
				parameterString += "&";
			}
		}

		return parameterString;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param connection Creation: 14.02.2018 by mst
	 */
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * @return the connectionParametersMap
	 */
	public Map<String, String> getConnectionParametersMap()
	{
		return connectionParametersMap;
	}

	/**
	 * @param connectionParametersMap the connectionParametersMap to set
	 */
	public void setConnectionParametersMap(Map<String, String> connectionParametersMap)
	{
		this.connectionParametersMap = connectionParametersMap;
	}

}
