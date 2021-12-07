package rentasad.library.db;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdsUtilityTest
{


	private static Connection adsCon;

	@BeforeAll
	protected static void setUp() throws Exception
	{
		adsCon = AdsConnection.dbConnect();
		String createTableQuery = "CREATE TABLE GUSTINI\\Lala (ID Numeric( 13 ,0 ));";
		Statement stmt = adsCon.createStatement();
		stmt.executeUpdate(createTableQuery);
	}

	@AfterAll
	protected static  void tearDown() throws Exception
	{
		adsCon = AdsConnection.dbConnect();
		String createTableQuery = "DROP TABLE GUSTINI\\Lala;";
		Statement stmt = adsCon.createStatement();
		stmt.executeUpdate(createTableQuery);
		adsCon.close();
	}

	@Test
	public void testExistTable() throws SQLException
	{
		assertTrue(AdsUtility.existTable("GUSTINI", "Lala"));
		assertTrue(AdsUtility.existTable("GUSTINI", "LALA")); 
		assertTrue(AdsUtility.existTable("GUSTINI", "lala")); 
		assertTrue(AdsUtility.existTable("GUSTINI", "LaLa"));
		assertFalse(AdsUtility.existTable("GUSTINI", "LaLa2"));
		assertFalse(AdsUtility.existTable("GUSTINI", "LaL"));
	}

}
