package rentasad.library.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsConnectionTest
{

    @Test
    public void testDbConnection() throws Exception
    {
        Connection con = AdsConnection.dbConnect();
        con.close();
        
    }
    
    @Test
    public void testDbConnectionWithParameters() throws Exception
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

    @Test
    public void encodingCheck()  throws Exception
    {
        String query = "SELECT NUMMER, ART_UEBERS FROM F01\\ARTIKEL\\V4AR1009 WHERE NUMMER = '0070022'";
        Connection con = AdsConnection.dbConnect();
        
        
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(query);
        if (rs.next())
        {
            String text = rs.getString("ART_UEBERS");
            assertEquals(text, "Grissini mit Nativem Oliven�l Extra Italien Amor di pane");
            System.out.println(text);
        }
    }
}
