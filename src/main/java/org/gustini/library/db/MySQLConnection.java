package org.gustini.library.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * 
 * Gustini GmbH (2017)
 * Creation: 03.03.2017
 * Library
 * gustini.library.db
 * 
 * @author Matthias Staud
 *
 *
 *         Description:
 *         ENCODING TABLE:
 *         MySQL Character Set Name Java-Style Character Encoding Name
 *         ascii US-ASCII
 *         big5 Big5
 *         gbk GBK
 *         sjis SJIS (or Cp932 or MS932 for MySQL Server < 4.1.11)
 *         cp932 Cp932 or MS932 (MySQL Server > 4.1.11)
 *         gb2312 EUC_CN
 *         ujis EUC_JP
 *         euckr EUC_KR
 *         latin1 Cp1252
 *         latin2 ISO8859_2
 *         greek ISO8859_7
 *         hebrew ISO8859_8
 *         cp866 Cp866
 *         tis620 TIS620
 *         cp1250 Cp1250
 *         cp1251 Cp1251
 *         cp1257 Cp1257
 *         macroman MacRoman
 *         macce MacCentralEurope
 *         utf8 UTF-8
 *         ucs2 UnicodeBig
 *
 */
public class MySQLConnection
{
    public static final String MYSQL_HOST = "MYSQL_HOST";
    public static final String MYSQL_USER = "MYSQL_USER";
    public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
    public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
    private static MySQLConnection instance = null;
    private static Driver driver = null;
    public static boolean debug = false;
    private Map<String, String> connectionParametersMap;
    private Connection connection;
    
    /**
     * 
     * Description: 
     * 
     * @return
     * Creation: 14.02.2018 by mst
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
            instance = new MySQLConnection();
            instance.setConnection(MySQLConnection.dbConnect(connectionParametersMap));
            instance.setConnectionParametersMap(connectionParametersMap);
        }else
        {
            if (debug)
            {
                System.err.println("Instance wurde bereits initialisiert");
            }
        }
    }
//    public static void initInstance(String mySqlDatabaseName, Map<String, String> connectionParametersMap) throws SQLException
//    {
//        if (instance == null)
//        {
//            instance = new MySQLConnection();
//            instance.setConnection(MySQLConnection.dbConnect(mySqlDatabaseName, connectionParametersMap)); 
//        }else
//        {
//            if (debug)
//            {
//                System.err.println("Instance wurde bereits initialisiert");
//            }
//        }
//    }
//
//    public static void initInstance(String dbHost, String databaseName, String dbUserid, String dbPassword) throws SQLException
//    {
//        if (instance == null)
//        {
//            instance = new MySQLConnection();
//            instance.setConnection(MySQLConnection.dbConnect(dbHost, databaseName, dbUserid, dbPassword)); 
//        }else
//        {
//            if (debug)
//            {
//                System.err.println("Instance wurde bereits initialisiert");
//            }
//        }
//    }
//    
//    public static void initInstance(String dbHost, String databaseName, String dbUserid, String dbPassword, String encoding) throws SQLException
//    {
//        if (instance == null)
//        {
//            instance = new MySQLConnection();
//            instance.setConnection(MySQLConnection.dbConnect(dbHost, databaseName, dbUserid, dbPassword, encoding)); 
//        }else
//        {
//            if (debug)
//            {
//                System.err.println("Instance wurde bereits initialisiert");
//            }
//        }
//    }
//    
//    public static void initInstanceWithoutEncoding(final Map<String, String> connectionParametersMap) throws SQLException
//    {
//        if (instance == null)
//        {
//            instance = new MySQLConnection();
//            instance.setConnection(MySQLConnection.dbConnectWithoutEncoding(connectionParametersMap)); 
//        }else
//        {
//            if (debug)
//            {
//                System.err.println("Instance wurde bereits initialisiert");
//            }
//        }
//    }
//    public static void initInstanceWithoutEncoding(String dbHost, String databaseName, String db_userid, String db_password) throws SQLException
//    {
//        if (instance == null)
//        {
//            instance = new MySQLConnection();
//            instance.setConnection(MySQLConnection.dbConnectWithoutEncoding(dbHost, databaseName, db_userid, db_password)); 
//        }else
//        {
//            if (debug)
//            {
//                System.err.println("Instance wurde bereits initialisiert");
//            }
//        }
//    }
    
    
    /**
     * 
     * Description: GetInstance Class for Managing Connection (and Reconnection after Close and Timeout
     * 
     * @return
     * @throws SQLException
     * Creation: 14.02.2018 by mst
     */
    public static MySQLConnection getInstance() throws SQLException
    {
        if (instance == null)
        {
            throw new SQLException("MySqlConnection Instance not initialized");
        }else
        {
            return instance;
        }
        
        
    }
    /**
     *
     * Description:
     *
     * @param connectionParametersMap
     *            Map<String, String> mit folgenden Parametern:
     *            <br>
     *            MYSQL_HOST
     *            <br>
     *            MYSQL_DATABASE
     *            <br>
     *            MYSQL_USER
     *            <br>
     *            MYSQL_PASSWORD
     *
     * @return
     * @throws DbConnectionException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     *             Creation: 15.12.2015 by mst
     */
    public static Connection dbConnect(Map<String, String> connectionParametersMap) throws SQLException
    {
        String mySqlServerUrl = connectionParametersMap.get(MYSQL_HOST);
        String mySqlDatabaseName = connectionParametersMap.get(MYSQL_DATABASE);
        String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
        String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);

        if (connectionParametersMap.containsKey("MYSQL_ENCODING"))
        {
            String mySqlEncoding = connectionParametersMap.get("MYSQL_ENCODING");
            return MySQLConnection.dbConnect(mySqlServerUrl, mySqlDatabaseName, mySqlDbUserid, mySqlDbPassword, mySqlEncoding);
        } else
        {
            return MySQLConnection.dbConnect(mySqlServerUrl, mySqlDatabaseName, mySqlDbUserid, mySqlDbPassword);
        }

    }

    /**
     * 
     * Description: 
     * 
     * @param mySqlDatabaseName
     * @param connectionParametersMap
     * @return
     * @throws SQLException
     * Creation: 14.02.2018 by mst
     */
    public static Connection dbConnect(String mySqlDatabaseName, Map<String, String> connectionParametersMap) throws SQLException
    {
        String mySqlServerUrl = connectionParametersMap.get(MYSQL_HOST);
        String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
        String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);
        return MySQLConnection.dbConnect(mySqlServerUrl, mySqlDatabaseName, mySqlDbUserid, mySqlDbPassword);
    }

    /**
     * Stellt eine Datenbankverbindung zu einer MySQL-Datenbank her und gibt das Connection-Objekt zurueck.
     * ENCODING im Connection-String: characterEncoding=cp1250
     *
     * @param dbHost
     * @param databaseName
     * @param dbUserid
     * @param dbPassword
     * @return
     * @throws DbConnectionException
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Connection dbConnect(String dbHost, String databaseName, String dbUserid, String dbPassword) throws SQLException
    {
        try
        {
            initDriver();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull&characterEncoding=cp1250", dbHost, databaseName), dbUserid, dbPassword);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            throw new SQLException(e);
        }
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
     * @throws SQLException
     * Creation: 14.02.2015 by mst
     */
    public static Connection dbConnect(String dbHost, String databaseName, String dbUserid, String dbPassword, String encoding) throws SQLException 
    {
        try
        {
            initDriver();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(
                        String.format("jdbc:mysql://%s/%s?rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=%s", dbHost, databaseName, encoding), dbUserid, dbPassword);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            throw new SQLException(e);
        }
    }

    /**
     *
     * Description:
     *
     * @param dbHost
     * @param databaseName
     * @param db_userid
     * @param db_password
     * @return
     * @throws DbConnectionException
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     *             Creation: 01.10.2015 by mst
     */
    public static Connection dbConnectWithoutEncoding(String dbHost, String databaseName, String db_userid, String db_password) throws SQLException
    {
        try
        {
            initDriver();
            return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", dbHost, databaseName, db_userid, db_password));
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            throw new SQLException(e);
        }
    }

    /**
     * 
     * Description: 
     * 
     * @param connectionParametersMap
     * @return
     * @throws SQLException
     * Creation: 14.02.2015 by mst
     */
    public static Connection dbConnectWithoutEncoding(final Map<String, String> connectionParametersMap) throws SQLException
    {
        String mySqlServerUrl = connectionParametersMap.get(MYSQL_HOST);
        String mySqlDbUserid = connectionParametersMap.get(MYSQL_USER);
        String mySqlDbPassword = connectionParametersMap.get(MYSQL_PASSWORD);
        String mySqlDbName = connectionParametersMap.get(MYSQL_DATABASE);
        return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", mySqlServerUrl, mySqlDbName, mySqlDbUserid, mySqlDbPassword));
    }

    /**
     *
     * Description: Initialisiert Driver
     *
     * Creation: 15.12.2015 by mst
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void initDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        if (driver == null)
        {
            driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
    }
    /**
     * 
     * Description: 
     * 
     * @return
     * Creation: 14.02.2018 by mst
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException
    {
        if (this.connection.isValid(4))
        {
            return this.connection;    
        }else
        {
            this.connection = MySQLConnection.dbConnect(this.connectionParametersMap);
            return this.connection; 
        }
        
    }
    /**
     * 
     * Description: 
     * 
     * @param connection
     * Creation: 14.02.2018 by mst
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    
    /**
     * @param connectionParametersMap the connectionParametersMap to set
     */
    public void setConnectionParametersMap(Map<String, String> connectionParametersMap)
    {
        this.connectionParametersMap = connectionParametersMap;
    }
}
