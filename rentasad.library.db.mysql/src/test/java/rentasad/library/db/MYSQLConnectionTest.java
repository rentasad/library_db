package rentasad.library.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class MYSQLConnectionTest
{

	@Test
	public void testGetParamStringFromConnectionPropertiesMap() throws Exception
	{
		Map<String, String> connectionPropertiesMap = MYSQLConnection.getDefaultConnectionPropertiesMap();

		final String expects = "rewriteBatchedStatements=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&serverTimezone=Europe/Berlin&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useLegacyDatetimeCode=false&useSSL=false";


		assertEquals(MYSQLConnection.getParamStringFromConnectionPropertiesMap(connectionPropertiesMap), expects);

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
		Connection con = MYSQLConnection.dbConnectWithTimeZoneUTC(mySqlConfigMap);
		con.close();
	}

	@Test
	public void testDbConnectWithTimeZoneUTCWithPort3307() throws Exception
	{
		final String MYSQL_HOST = "gstvmdbs3.gustini.local";
		final String MYSQL_DATABASE = "Endkontrolle_DEV1";
		final String MYSQL_USER = "greetingcardDev";
		final String MYSQL_PASSWORD = "greetingcardDev";
		final String MYSQL_ENCODING = "utf8";
		final String MYSQL_PORT = "3306";
		HashMap<String, String> mySqlConfigMap = new HashMap<>();
		mySqlConfigMap.put("MYSQL_HOST", MYSQL_HOST);
		mySqlConfigMap.put("MYSQL_DATABASE", MYSQL_DATABASE);
		mySqlConfigMap.put("MYSQL_USER", MYSQL_USER);
		mySqlConfigMap.put("MYSQL_PASSWORD", MYSQL_PASSWORD);
		mySqlConfigMap.put("MYSQL_ENCODING", MYSQL_ENCODING);
		mySqlConfigMap.put("MYSQL_PORT", MYSQL_PORT);
		Connection con = MYSQLConnection.dbConnectWithTimeZoneUTC(mySqlConfigMap);
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
		Connection con = MYSQLConnection.dbConnect(mySqlConfigMap);
		con.close();
	}

	@Test
	public void testDbConnectWithMapWithPort() throws Exception
	{
		final String MYSQL_HOST = "gstvmdbs3.gustini.local";
		final String MYSQL_DATABASE = "Endkontrolle_DEV1";
		final String MYSQL_USER = "greetingcardDev";
		final String MYSQL_PASSWORD = "greetingcardDev";
		final String MYSQL_ENCODING = "utf8";
		final String MYSQL_PORT = "3306";
		HashMap<String, String> mySqlConfigMap = new HashMap<>();
		mySqlConfigMap.put("MYSQL_HOST", MYSQL_HOST);
		mySqlConfigMap.put("MYSQL_DATABASE", MYSQL_DATABASE);
		mySqlConfigMap.put("MYSQL_USER", MYSQL_USER);
		mySqlConfigMap.put("MYSQL_PASSWORD", MYSQL_PASSWORD);
		mySqlConfigMap.put("MYSQL_ENCODING", MYSQL_ENCODING);
		mySqlConfigMap.put("MYSQL_PORT", MYSQL_PORT);
		Connection con = MYSQLConnection.dbConnect(mySqlConfigMap);
		con.close();
	}

}
