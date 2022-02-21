package rentasad.library.db;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsConnectionTest
{

	@Test public void testDbConnection() throws Exception
	{
		Connection con = AdsConnection.dbConnect();
		con.close();

	}

	@Test public void testDbConnectionWithParameters() throws Exception
	{

		String host = "//192.168.111.30";
		String socket = "6262";
		String databaseDictionaty = "/vs4/VS/DG/VC2/";
		String lockType = "proprietary";
		String charType = "OEM";
		String tableType = "cdx";
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_HOST, host);
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_SOCKET, socket);
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_DATABASE_DICTIONARY, databaseDictionaty);
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_LOCK_TYPE, lockType);
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_CHAR_TYPE, charType);
		configMap.put(AdsConnection.PARAMETER_NAME_ADS_TABLE_TYPE, tableType);

		Connection con = AdsConnection.dbConnect(configMap);
		con.close();

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test public void encodingCheck() throws Exception
	{
		String query = "SELECT NUMMER, ART_UEBERS FROM F01\\ARTIKEL\\V4AR1009 WHERE NUMMER = '0070022'";
		Connection con = AdsConnection.dbConnect();

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery(query);
		if (rs.next())
		{
			final String expected = "Grissini mit Nativem Oliven�l Extra Italien Amor di pane";
			final String fieldName = "ART_UEBERS";
			byte[] textBytes = rs.getString(fieldName).getBytes("Cp1252");
			String actualText = new String(textBytes, "Cp1252");
			assertEquals(actualText, expected);
			System.out.println(actualText);
		}
	}

	@Test public void encodingCheckQuarterAddress() throws Exception
	{
		String query = "SELECT ORTSTEIL FROM F99\\ADRESSEN\\V2AD1001 WHERE NUMMER = '1102717070'";
		Connection con = AdsConnection.dbConnect();

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery(query);
		if (rs.next())
		{
			final String expected = "M�ckern";
			final String fieldName = "ORTSTEIL";
			String normalFieldValue = rs.getString(fieldName);
			byte[] textBytes = rs.getString(fieldName).getBytes("Cp1252");
			String actualText = new String(textBytes, "Cp1252");
			assertEquals(actualText, expected);
			assertEquals(normalFieldValue, expected);
			System.out.println(actualText);
		}
	}
}
