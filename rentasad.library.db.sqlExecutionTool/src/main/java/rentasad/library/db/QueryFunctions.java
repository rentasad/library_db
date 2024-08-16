package rentasad.library.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import rentasad.library.db.dataObjects.PreparedDataTypesEnum;
import rentasad.library.tools.fileOperator.FileOperator;

public class QueryFunctions
{
    // private Connection adsConnection;
    //
    // /**
    // * @param adsConnection
    // */
    // public QueryFunctions(Connection adsConnection)
    // {
    // super();
    // this.connection = adsConnection;
    // }
    public static HashMap<Integer, String> getFieldnamesFromDatabaseTable(Connection connection, String tableNameString) throws SQLException
    {
        String fieldNameQuery = "SHOW fields FROM " + tableNameString;
        ResultSet fieldNamesResultSet = null;
        Statement fieldNamesStatement;
        fieldNamesStatement = connection.createStatement();
        fieldNamesResultSet = fieldNamesStatement.executeQuery(fieldNameQuery);

        @SuppressWarnings("unused")
        String[] fieldNameStringArray = new String[fieldNamesResultSet.getFetchSize()];
        int i = 0;
        HashMap<Integer, String> fieldNameHashMap = new HashMap<Integer, String>();
        while (fieldNamesResultSet.next())
        {
            fieldNameHashMap.put( Integer.valueOf(i), fieldNamesResultSet.getString(1));
            i++;
        }
        return fieldNameHashMap;
    }

    public static int getNumberOfEntriesInTable(Connection connection, String tableNameString) throws SQLException
    {
        String numberOfEntriesQuery = "SELECT COUNT(*) as anzahl FROM " + tableNameString;
        ResultSet numberOfEntriesResultSet = null;
        Statement numberOfEntriesStatement;
        numberOfEntriesStatement = connection.createStatement();
        numberOfEntriesResultSet = numberOfEntriesStatement.executeQuery(numberOfEntriesQuery);

        // String[] fieldNameStringArray = new String[numberOfEntriesResultSet.getFetchSize()];
        int numberOfEntries = 0;
        while (numberOfEntriesResultSet.next())
        {
            numberOfEntries = numberOfEntriesResultSet.getInt("anzahl");
        }
        return numberOfEntries;
    }

    /**
     * 
     * @param connection
     * @param tableNameString
     * @param whereClausel
     *            Zum Beispiel "WHERE ..."
     * @return
     * @throws SQLException
     */
    public static int getNumberOfEntriesInTableWithConditions(Connection connection, String tableNameString, String whereClausel) throws SQLException
    {
        String numberOfEntriesQuery = "SELECT COUNT(*) as anzahl FROM " + tableNameString + " " + whereClausel;
        ResultSet numberOfEntriesResultSet = null;
        Statement numberOfEntriesStatement;
        numberOfEntriesStatement = connection.createStatement();
        numberOfEntriesResultSet = numberOfEntriesStatement.executeQuery(numberOfEntriesQuery);

        // String[] fieldNameStringArray = new String[numberOfEntriesResultSet.getFetchSize()];
        int numberOfEntries = 0;
        while (numberOfEntriesResultSet.next())
        {
            numberOfEntries = numberOfEntriesResultSet.getInt("anzahl");
        }
        return numberOfEntries;
    }

    /**
     * Retrieves the names of all tables in a database.
     *
     * @param connection The database connection.
     * @return An array of table names.
     * @throws SQLException if a database access error occurs.
     */
    public static String[] getTableNamesFromDatabase(Connection connection) throws SQLException
    {
        String tableQuery = "SHOW TABLES";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(tableQuery);

        Vector<String> tableNameVecotr = new Vector<String>();
        while (resultSet.next())
        {
            tableNameVecotr.add(resultSet.getString(1));
        }
        return tableNameVecotr.toArray(new String[0]);
    }

    /**
     * Returns the prepared insert placeholder string for an array of a given size.
     * The placeholder string is in the format "(?,?,...,?)" where the number of question marks corresponds to the array size.
     *
     * @param objectArray The array for which the placeholder string is needed.
     * @return The prepared insert placeholder string.
     */
    public static String getPreparedInsertPlaceholderStringFromArray(Object[] objectArray)
    {
        String placeHolderString = "(";

        for (int i = 0; i < objectArray.length; i++)
        {
            placeHolderString += "?,";
        }
        placeHolderString = placeHolderString.substring(0, placeHolderString.length() - 1);
        placeHolderString += ")";
        return placeHolderString;
    }

    /**
     * Returns the prepared insert placeholder string for an array of a given size.
     * The placeholder string is in the format "(?,?,...,?)" where the number of question marks corresponds to the array size.
     *
     * @param arraySize The size of the array for which the placeholder string is needed.
     * @return The prepared insert placeholder string.
     */
    public static String getPreparedInsertPlaceholderStringFromArray(int arraySize)
    {
        String placeHolderString = "(";

        for (int i = 0; i < arraySize; i++)
        {
            placeHolderString += "?,";
        }
        placeHolderString = placeHolderString.substring(0, placeHolderString.length() - 1);
        placeHolderString += ")";
        return placeHolderString;
    }

    /**
     * 
     * Description: convert an array to a string for a where clausel like "not in 'a','b'"
     * 
     * @param stringArray
     * @return String for example "'','',''"
     *         Creation: 03.05.2017 by mst
     */
    public static String getWhereStringFromArray(String[] stringArray)
    {
        String whereString = "";

        for (int i = 0; i < stringArray.length; i++)
        {

            whereString += "'" + stringArray[i].trim() + "',";
        }
        // Remove last destimel
        whereString = whereString.substring(0, whereString.length() - 1);
        return whereString;
    }

    /**
     * 
     * Description: get single SQL-Query from File
     * 
     * if more than one sql statement are in file, the execution will fail
     * 
     * @param sqlFileName
     * @return
     * @throws IOException
     *             Creation: 09.05.2017 by mst
     */
    public static String getQueryFromSqlFile(String sqlFileName) throws IOException
    {
        return FileOperator.readFile(sqlFileName);
    }

    /**
     * 
     * Description: Get a StringArray with more then one SQL Queries from a sql file
     * 
     * @param sqlFileName
     * @return
     * @throws IOException
     *             Creation: 17.05.2017 by mst
     */
    public static String[] getSqlQueriesFromSqlFile(String sqlFileName) throws IOException
    {
        final String DEFAULT_DELIMITER = ";";
        String sqlQueryValue = FileOperator.readFile(sqlFileName);
        /*
         * Aufsplitten der SQL-Statements in mehrere Strings
         */
        return sqlQueryValue.split(DEFAULT_DELIMITER);
    }

    /**
     * 
     * Description: Execute given Query without Resultset
     * 
     * @return true if query has no ResultSet and execution was successfull
     *         Creation: 30.05.2017 by mst
     * @throws SQLException
     */
    public static boolean executeUpdateQuery(Connection con, String query)
    {
        try
        {
            Statement stmt = con.createStatement();
            boolean isResultSet = stmt.execute(query);
            stmt.close();
            return !isResultSet;
        } catch (Exception e)
        {
            System.err.println("Query: " + query);
            throw new RuntimeException(e);
        } 
        
        
        

    }

    /**
     * 
     * Description: 
     * 
     * @param o
     * @return
     * Creation: 30.05.2017 by mst
     */
    public static PreparedDataTypesEnum getPreparedDataTypFromObject(Object o)
    {
        if (o instanceof String)
        {
            return PreparedDataTypesEnum.STRING;
        } else if (o instanceof Long)
        {
            return PreparedDataTypesEnum.LONG;
        } else if (o instanceof Double)
        {
            return PreparedDataTypesEnum.DOUBLE;
        } else if (o instanceof Float)
        {
            return PreparedDataTypesEnum.FLOAT;
        } else if (o instanceof Date )
        {
            return PreparedDataTypesEnum.DATE_UTIL;
        }else if (o instanceof java.sql.Date)
        {
            return PreparedDataTypesEnum.DATE_SQL;
         }else if (o instanceof Integer)
         {
             return PreparedDataTypesEnum.INTEGER;
          }else
         {
             throw new IllegalArgumentException("Unsupported Argument:" + o.getClass().getTypeName());
         }

    }

}
