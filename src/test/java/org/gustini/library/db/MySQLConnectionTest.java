package org.gustini.library.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        assertEquals(MySQLConnection.getParamStringFromConnectionPropertiesMap(connectionPropertiesMap), "useUnicode=true?allowMultiQueries=true?zeroDateTimeBehavior=CONVERT_TO_NULL");
    }


}
