package rentasad.library.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gustini GmbH (2016)
 * Creation: 23.02.2016
 * Library
 * gustini.library.db
 *
 * @author Matthias Staud
 * <p>
 * <p>
 * Description:Werkzeuge zur effizienten Arbeit mit Sybase ADS
 */
public class AdsUtility
{
	public static final String VS4_ADVANTAGE_DATABASE_DEFAULT_PATH = "/vs4/VS/DG/VC2/";

	/**
	 * Description:
	 *
	 * @param tablePathAfterVC2 path of table --> should be start AFTER "/vs4/VS/DG/VC2/"
	 * @param tableName
	 * @return Creation: 01.03.2018 by mst
	 * @throws SQLException
	 */
	public static boolean existTable(final String tablePathAfterVC2, final String tableName) throws SQLException
	{
		Connection con = AdsConnection.dbConnectToDictionary(tablePathAfterVC2);
		return existTable(con, tableName);
	}

	/**
	 * @param con
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static boolean existTable(final Connection con, final String tableName) throws SQLException
	{
		String existQuery = String.format("SELECT count(TABLE_NAME) anzahl FROM (\r\n" + "EXECUTE PROCEDURE sp_GetTables( NULL, NULL, NULL, 'TABLE')\r\n" + ")  as  tables WHERE TABLE_NAME = '%s.dbf'",
										  tableName);
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

}
