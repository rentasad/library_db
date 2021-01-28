package rentasad.library.db;

import java.sql.Connection;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AdsConnectionTest
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

}
