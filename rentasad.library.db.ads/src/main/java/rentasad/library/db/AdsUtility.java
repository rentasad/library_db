package rentasad.library.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * Gustini GmbH (2016)
 * Creation: 23.02.2016
 * Library
 * gustini.library.db
 *
 * @author Matthias Staud
 *
 *
 *         Description:Werkzeuge zur effizienten Arbeit mit Sybase ADS
 *
 */
public class AdsUtility
{
    public static final String VS4_ADVANTAGE_DATABASE_DEFAULT_PATH = "/vs4/VS/DG/VC2/";

//    /**
//     * 
//     * Description:
//     * 
//     * @param tableName
//     * @return
//     * @throws SQLException
//     *             Creation: 07.02.2018 by mst
//     * 
//     *             IMPORTANT: IT WILL USE HER OWN CONNECTION on DEFAULT PATH
//     *             //192.168.111.30:6262/vs4/VS/DG/VC2/
//     */
//    @Deprecated
//    public static boolean existTable(final String tableName) throws SQLException
//    {
//        Connection con = AdsConnection.dbConnect();
//        String query = "Select TOP 1 * FROM %s";
//        java.sql.Statement stmt = con.createStatement();
//        query = String.format(query, tableName);
//        try
//        {
//
//            ResultSet rs = stmt.executeQuery(query);
//            rs.close();
//            stmt.close();
//            con.close();
//            return true;
//        } catch (SQLException e)
//        {
//            con.close();
//            String message = e.getMessage();
//            if (message.startsWith("[iAnywhere Solutions][Advantage SQL][ASA] Error 7041:"))
//                return false;
//            else
//                throw e;
//        }
//    }

    /**
     * 
     * Description:
     * 
     * @param tablePath
     *            path of table --> should be start AFTER "/vs4/VS/DG/VC2/"
     * @param tableName
     * @return
     *         Creation: 01.03.2018 by mst
     * @throws SQLException
     */
    public static boolean existTable(final String tablePathAfterVC2, final String tableName) throws SQLException
    {
        Connection con = AdsConnection.dbConnectToDictionary(tablePathAfterVC2);
        return existTable(con, tableName);
    }
    
/**
 * 
 * @param con
 * @param tableName
 * @return
 * @throws SQLException
 */
    public static boolean existTable(final Connection con, final String tableName) throws SQLException
    {
        String existQuery = String.format("SELECT count(TABLE_NAME) anzahl FROM (\r\n" + "EXECUTE PROCEDURE sp_GetTables( NULL, NULL, NULL, 'TABLE')\r\n" + ")  as  tables WHERE TABLE_NAME = '%s.dbf'", tableName);
        try (Statement stmt = con.createStatement())
		{
			ResultSet rs = stmt.executeQuery(existQuery);
			int anzahl = 0;
			if (rs.next())
			{
			    anzahl = rs.getInt("anzahl");
			}
			return anzahl > 0;
		}
    }
    

    // /**
    // *
    // * Description: Check if Table with SELECT on Table and capture the SQL Exception if not exist
    // *
    // * Not a good way -_> Connection should be close
    // *
    // * @param con
    // * @param tableName
    // * @return
    // * @throws SQLException
    // * Creation: 01.03.2018 by mst
    // */
    // public static boolean existTable(Connection con, final String tableName) throws SQLException
    // {
    // String query = "Select TOP 1 * FROM %s";
    // java.sql.Statement stmt = con.createStatement();
    // query = String.format(query, tableName);
    // try
    // {
    //
    // ResultSet rs = stmt.executeQuery(query);
    // rs.close();
    // stmt.close();
    // return true;
    // } catch (SQLException e)
    // {
    // con.close();
    // String message = e.getMessage();
    // if (message.startsWith("[iAnywhere Solutions][Advantage SQL][ASA] Error 7041:"))
    // return false;
    // else throw e;
    // }
    // }
}
