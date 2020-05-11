package rentasad.library.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rentasad.library.db.MySQLConnection;

public class MySQLConnectionTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testGetParamStringFromConnectionPropertiesMap() throws Exception
    {
        Map<String, String> connectionPropertiesMap = MySQLConnection.getDefaultConnectionPropertiesMap();
        System.out.println(MySQLConnection.getParamStringFromConnectionPropertiesMap(connectionPropertiesMap));
        assertEquals(MySQLConnection.getParamStringFromConnectionPropertiesMap(connectionPropertiesMap), "useUnicode=true&useJDBCCompliantTimezoneShift=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useLegacyDatetimeCode=false");
    }

    @Test
    public void testDbConnectWithTimeZoneUTC() throws Exception
    {
        final String MYSQL_HOST = "gstvmdbs3.gustini.local";
        final String MYSQL_DATABASE = "Endkontrolle_DEV1";
        final String MYSQL_USER = "greetingcardDev";
        final String MYSQL_PASSWORD = "greetingcardDev";
        final String MYSQL_ENCODING = "utf8";
        HashMap<String, String> mySqlConfigMap = new HashMap<>();
        mySqlConfigMap.put("MYSQL_HOST", MYSQL_HOST);
        mySqlConfigMap.put("MYSQL_DATABASE", MYSQL_DATABASE);
        mySqlConfigMap.put("MYSQL_USER", MYSQL_USER);
        mySqlConfigMap.put("MYSQL_PASSWORD", MYSQL_PASSWORD);
        mySqlConfigMap.put("MYSQL_ENCODING", MYSQL_ENCODING);
        Connection con = MySQLConnection.dbConnectWithTimeZoneUTC(mySqlConfigMap);
        con.close();
    }
    @Test
    public void testDbConnectWithMap() throws Exception
    {
        final String MYSQL_HOST = "gstvmdbs3.gustini.local";
        final String MYSQL_DATABASE = "Endkontrolle_DEV1";
        final String MYSQL_USER = "greetingcardDev";
        final String MYSQL_PASSWORD = "greetingcardDev";
        final String MYSQL_ENCODING = "utf8";
        HashMap<String, String> mySqlConfigMap = new HashMap<>();
        mySqlConfigMap.put("MYSQL_HOST", MYSQL_HOST);
        mySqlConfigMap.put("MYSQL_DATABASE", MYSQL_DATABASE);
        mySqlConfigMap.put("MYSQL_USER", MYSQL_USER);
        mySqlConfigMap.put("MYSQL_PASSWORD", MYSQL_PASSWORD);
        mySqlConfigMap.put("MYSQL_ENCODING", MYSQL_ENCODING);
        Connection con = MySQLConnection.dbConnect(mySqlConfigMap);
        con.close();
    }
    
    

}
