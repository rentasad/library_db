package rentasad.library.db;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import rentasad.library.db.dataObjects.PreparedDataTypesEnum;

public class QueryFunctionsTest
{


    @Test
    public void testGetPreparedDataTypFromObject() throws Exception
    {
        String testString = "Hallo";
        Integer i1 = Integer.valueOf(1);
        int i2 = 2;
        double d1 = 1.0;
        float f1 = 1f;
        long l1 = 10000l;
//        Date sqlDate = new Date(new java.util.Date().getTime());
        java.util.Date utilDate = new java.util.Date();
        
        assertEquals(PreparedDataTypesEnum.STRING, QueryFunctions.getPreparedDataTypFromObject(testString));
        assertEquals(PreparedDataTypesEnum.INTEGER, QueryFunctions.getPreparedDataTypFromObject(i1));
        assertEquals(PreparedDataTypesEnum.INTEGER, QueryFunctions.getPreparedDataTypFromObject(i2));
        assertEquals(PreparedDataTypesEnum.DOUBLE, QueryFunctions.getPreparedDataTypFromObject(d1));
        assertEquals(PreparedDataTypesEnum.FLOAT, QueryFunctions.getPreparedDataTypFromObject(f1));
        assertEquals(PreparedDataTypesEnum.LONG, QueryFunctions.getPreparedDataTypFromObject(l1));
//        assertEquals(PreparedDataTypesEnum.DATE_SQL, QueryFunctions.getPreparedDataTypFromObject(sqlDate));
        assertEquals(PreparedDataTypesEnum.DATE_UTIL, QueryFunctions.getPreparedDataTypFromObject(utilDate));
        
        
        
        
    }

}
