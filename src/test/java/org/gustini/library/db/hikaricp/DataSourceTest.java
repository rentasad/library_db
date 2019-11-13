package org.gustini.library.db.hikaricp;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class DataSourceTest
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
    public void testGetConnection() throws Exception
    {
        BasicConfigurator.configure();
        Connection con = DataSource.getConnection();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM FROM GUSTINI\\LKW_PLAN");
        
    }

    @Test
    public void testGetAdsConnection() throws Exception
    {
        BasicConfigurator.configure();
        Connection con = DataSource.getAdsConnection();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM FROM GUSTINI\\LKW_PLAN");
    }

}
