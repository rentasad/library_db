package rentasad.library.db.hikaricp;

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import rentasad.library.db.AdsConnection;

public class DataSourceTest
{


    @Test
    public void testGetConnection() throws Exception
    {
        Connection con = AdsConnection.dbConnect();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GUSTINI\\LKW_PLAN");
        rs.close();
    }

    @Test
    public void testGetAdsConnection() throws Exception
    {
    	Connection con = AdsConnection.dbConnect();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GUSTINI\\LKW_PLAN");
        rs.close();
    }

}
