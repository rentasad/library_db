package org.gustini.library.db;

import java.sql.Connection;

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

}
