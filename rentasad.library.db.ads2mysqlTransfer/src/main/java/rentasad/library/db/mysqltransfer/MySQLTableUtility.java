package rentasad.library.db.mysqltransfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import rentasad.library.basicTools.StringTool;
import rentasad.library.basicTools.dateTool.DateTools;
import rentasad.library.db.dataObjects.ISQLTableColumnsDescriptionInterface;
import rentasad.library.db.dataObjects.IsqlTableDescriptionInterface;
import rentasad.library.db.dataObjects.MySQLTableColumnDescription;
import rentasad.library.db.dataObjects.MySQLTableDescription;
import rentasad.library.tools.exceptions.WrongDataTypeException;

/**
 * Diese Klasse liest eine SQL Tabelle aus und erzeugt aus der Struktur ein
 * Datenobjekt, welches alle relevanten Datenbankinformationen enthaelt ACHTUNG:
 * Fuer richtiges Encoding muss im MySQL-Connection-String folgende Variable
 * angegeben werden: ?characterEncoding=cp1250
 *
 * @author mst
 *
 */
public class MySQLTableUtility
{

	private Map<String, String> configMap;

	public MySQLTableUtility(Map<String, String> configMap)
	{
		super();
		this.configMap = configMap;
	}

	/**
	 * Gibt die Anzahl der verfuegbaren Eintraege einer Tabelle zurueck. Z.B.
	 * verwendbar fuer Progressbars und aehnliche Fortschrittsanzeigen.
	 *
	 * @param adsConnection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public int getNumberOfEntriesFromAdsTableName(final Connection adsConnection, String tableName) throws SQLException
	{
		int entriesCount = 0;
		String adsTabellenPfad = "";
		String selectQuery = "SELECT COUNT(*) as anzahl FROM ";

		tableName = tableName.toUpperCase();
		if (configMap.containsKey(tableName))
		{
			adsTabellenPfad = this.configMap.get(tableName) + "\\";
		}
		selectQuery += adsTabellenPfad + tableName;
		Statement adsSelectStatement = adsConnection.createStatement();
		ResultSet adsSelectResultSet = adsSelectStatement.executeQuery(selectQuery);
		if (adsSelectResultSet.next())
		{
			entriesCount = adsSelectResultSet.getInt("anzahl");
		}
		return entriesCount;
	}

	/**
	 * Diese Methode kopiert von ads-Datenbank die angegebenen Tabellen in
	 * MySQL-Connection
	 *
	 * ACHTUNG: Fuer richtiges Encoding muss im MySQL-Connection-String folgende
	 * Variable angegeben werden: ?characterEncoding=cp1250
	 *
	 * @param mySQLConnection
	 * @param adsConnection
	 * @param mySQLTableDescription
	 * @return Anzahl importierter Zeilen
	 * @throws WrongDataTypeException
	 * @throws SQLException
	 * @throws Exception
	 */
	public int transferTableFromAdsToMySQL(final Connection mySQLConnection, final Connection adsConnection,
			final IsqlTableDescriptionInterface mySQLTableDescription) throws SQLException, WrongDataTypeException
	{

		boolean debug = false;
		String tableName = mySQLTableDescription.getName();
		ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions = MySQLTableUtility
				.getMySQLTableColumnDescription(mySQLConnection, tableName);
		if (debug)
			System.out.println("Anzahl Spalten: " + tableColumnDescriptions.length);
		String truncateQuery = "truncate " + tableName;

		/*
		 * Erstellen des SELECT- und INSERT-Querys
		 */

		String selectQuery = "SELECT ";
		String columnListString = "";
		String mySQLColumnListString = "";
		String columnPlaceHolder = "(";
		for (int i = 0; i < tableColumnDescriptions.length; i++)
		{
			// Liste der Spalten, wird fuer SELCT und INSERT verwendet
			columnListString += tableColumnDescriptions[i].getField();
			mySQLColumnListString += "`" + tableColumnDescriptions[i].getField() + "`";
			// Wird fuer PreparedStatement verwendet. Anzahl Spalten entspricht
			// Anzahl der Platzhalter
			columnPlaceHolder += "\n ?";
			if (i < (tableColumnDescriptions.length - 1))
			{
				columnListString += ",";
				mySQLColumnListString += ",";
				columnPlaceHolder += ",";
			}
			// columnList += "\n";
		}
		String adsTabellenPfad = "";
		// tableName = tableName.toLowerCase();
		// if (tableName.equalsIgnoreCase("v4haupt"))
		// {
		// System.out.println("STOP");
		// }

		if (this.configMap.containsKey(tableName))
		{
			adsTabellenPfad = this.configMap.get(tableName) + "\\";
		}
		columnPlaceHolder += ")";
		selectQuery += columnListString + "\n FROM " + adsTabellenPfad + tableName;
		if (debug)
			selectQuery += "\n WHERE NUMMER > '70000'";

		// System.out.println(selectQuery);

		/*
		 * Erzeugen und Ausfuehren der SQL-Statements
		 */
		// Leeren der mySQL-Tabelle vor Import
		Statement truncateStatement = mySQLConnection.createStatement();
		truncateStatement.execute(truncateQuery);
		truncateStatement.close();

		Statement adsSelectStatement = adsConnection.createStatement();
		ResultSet adsSelectResultSet = adsSelectStatement.executeQuery(selectQuery);

		int zeile = 0;

		if (debug)
		{// Im DEBUG-Modus werden die INSERT-Queries von hand erzeugt, um Fehler
			// in der Erzeugung zu erkennen.

			while (adsSelectResultSet.next())
			{
				String insertQueryString = String.format("INSERT INTO %s \n(%s) \n VALUES \n ", tableName,
						mySQLColumnListString);
				Statement insertStatement = mySQLConnection.createStatement();
				String insertValueRow = "(";
				for (int i = 0; i < tableColumnDescriptions.length; i++)
				{
					String columnString = "";
					int colNumber = i + 1;
					ISQLTableColumnsDescriptionInterface column = tableColumnDescriptions[i];
					int objektTypInt = column.getJavaDatatypeFromISQLTyp(column.getType());
					switch (objektTypInt)
					{
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN:
						columnString = Boolean.valueOf(adsSelectResultSet.getBoolean(column.getField())).toString();
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE:

						Date date = adsSelectResultSet.getDate(column.getField());
						if (date == null)
						{
							columnString = "null";
						} else
						{
							columnString = "'" + DateTools.getSQLTimeStampFromDate(date) + "'";
						}

						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE:
						columnString = Double.valueOf(adsSelectResultSet.getDouble(column.getField())).toString();
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT:
						columnString = Float.valueOf(adsSelectResultSet.getFloat(column.getField())).toString();
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT:
						columnString = Integer.valueOf(adsSelectResultSet.getInt(column.getField())).toString();
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING:
						columnString = "'" + adsSelectResultSet.getString(column.getField()) + "'";
						break;

					default:
						throw new WrongDataTypeException("Unbekannter nicht definierter Datentyp: " + objektTypInt);
					}
					if (colNumber > 1)
						insertValueRow += ",";
					insertValueRow += "\n" + columnString + "/*" + column.getField() + "*/";

				}
				insertValueRow += ")";
				insertQueryString = insertQueryString + insertValueRow;
				System.out.println("insertQueryString: " + insertQueryString);
				insertStatement.execute(insertQueryString);
				insertStatement.close();
			}
		} else
		{
			// im NICHT-DEBUG-Modus wird mit Prepared Statements gearbeitet.
			String preparedInsertQueryString = String.format("INSERT INTO %s \n(%s) \n VALUES \n %s ", tableName,
					mySQLColumnListString, columnPlaceHolder);
			PreparedStatement insertPreparedStatement = mySQLConnection.prepareStatement(preparedInsertQueryString);
			// insertPreparedStatement.set
			while (adsSelectResultSet.next())
			{

				for (int i = 0; i < tableColumnDescriptions.length; i++)
				{
					int colNumber = i + 1;
					ISQLTableColumnsDescriptionInterface column = tableColumnDescriptions[i];
					int objektTypInt = column.getJavaDatatypeFromISQLTyp(column.getType());
					/*
					 * Die Auskommentierten Zeilen dienen dem DEBUG, wenn der Transfer fehlschlaegt.
					 * Meistens liegt die Ursache im Encoding
					 */
//                    String field = column.getField();
//                     if (field.equals("ART_UEBERS"))
//                     {
//                     System.out.println("ART_UEBERS : " + adsSelectResultSet.getString(column.getField()));
//                     }
					switch (objektTypInt)
					{
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN:
						insertPreparedStatement.setBoolean(colNumber, adsSelectResultSet.getBoolean(column.getField()));
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE:
						insertPreparedStatement.setDate(colNumber, adsSelectResultSet.getDate(column.getField()));
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE:
						insertPreparedStatement.setDouble(colNumber, adsSelectResultSet.getDouble(column.getField()));
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT:
						insertPreparedStatement.setFloat(colNumber, adsSelectResultSet.getFloat(column.getField()));
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT:
						insertPreparedStatement.setInt(colNumber, adsSelectResultSet.getInt(column.getField()));
						break;
					case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING:
						insertPreparedStatement.setString(colNumber, adsSelectResultSet.getString(column.getField()));
						break;

					default:
						throw new WrongDataTypeException("Unbekannter nicht definierter Datentyp: " + objektTypInt);
					}
				}

//				insertPreparedStatement.executeUpdate();
				insertPreparedStatement.addBatch();
				//
				zeile++;
			} // Ende der Datensätze
			insertPreparedStatement.executeBatch();
			insertPreparedStatement.close();
		}

		return zeile;
	}

	/**
	 * Gibt einen den statischen MySQL-Datentyp (int) zurueck. Wenn der Datentyp
	 * unbekannt ist, wird der Datentyp
	 * ISQLTableColumnsDescriptionInterface.DATATYPE_UNDEFINED (999) zurueckgegeben.
	 *
	 * @param typString
	 * @return
	 */
	public static int getTypeIntFromTypString(final String typString)
	{
		String modifiedTypString = typString.toUpperCase();
		int dataType = ISQLTableColumnsDescriptionInterface.DATATYPE_UNDEFINED;
		for (int i = 0; i < ISQLTableColumnsDescriptionInterface.DATATYP_NAME_ARRAY.length; i++)
		{
			String startString = ISQLTableColumnsDescriptionInterface.DATATYP_NAME_ARRAY[i];
			if (modifiedTypString.startsWith(startString))
			{// Datentyp wurde zugeordnet und wird der Variable zugewiesen.
				dataType = i;
			}

		}
		return dataType;

	}

	/**
	 *
	 * @param adsConnection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 * @throws WrongDataTypeException
	 * @throws Exception
	 */
	public static ISQLTableColumnsDescriptionInterface[] getMySQLTableColumnDescription(Connection connection,
			String tableName) throws SQLException, WrongDataTypeException
	{
		ArrayList<ISQLTableColumnsDescriptionInterface> mySQLTableDescriptions = new ArrayList<ISQLTableColumnsDescriptionInterface>();
		String showQuery = "show full fields from " + tableName;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(showQuery);
		while (resultSet.next())
		{
			ISQLTableColumnsDescriptionInterface columnDescription = new MySQLTableColumnDescription();

			columnDescription.setField(resultSet.getString("Field"));

			String fieldName = resultSet.getString("Field");
			columnDescription.setCollation(resultSet.getString("Collation"));
			// columnDescription.setLength(resultSet.getInt("Length"));
			String canBeNullString = resultSet.getString("Null");
			boolean canBeNullBoolean = parseBooleanYesNoString(canBeNullString);
			columnDescription.setCanBeNull(canBeNullBoolean);
			columnDescription.setKey(resultSet.getString("Key"));
			columnDescription.setExtra(resultSet.getString("Extra"));
			columnDescription.setPrivileges(resultSet.getString("Privileges"));
			columnDescription.setComment(resultSet.getString("Comment"));

			String typeString = resultSet.getString("Type");
			String[] werteInKlammernString = StringTool.getStringZwischen(typeString, "\\(", "\\)");
			if (werteInKlammernString.length == 1)
			{
				columnDescription.setLength(Integer.valueOf(werteInKlammernString[0]).intValue());
			}

			int typInt = getTypeIntFromTypString(typeString);
			if (typInt != ISQLTableColumnsDescriptionInterface.DATATYPE_UNDEFINED)
			{// Datentyp ist NICHT unbekannt
				columnDescription.setType(typInt);
				int javaObjektTypInt = columnDescription.getJavaDatatypeFromISQLTyp(typInt);
				if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN)
				{

					columnDescription.setDefaultBoolean(resultSet.getBoolean("Default"));
				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE)
				{
					columnDescription.setDefaultDate(resultSet.getDate("Default"));
				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE)
				{
					columnDescription.setDefaultDouble(resultSet.getDouble("Default"));
				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT)
				{

				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT)
				{
					columnDescription.setDefaultInt(resultSet.getInt("Default"));
				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING)
				{
					columnDescription.setDefaultString(resultSet.getString("Default"));
				} else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_TEXT)
				{
					columnDescription.setDefaultString(resultSet.getString("Default"));
				} else

				if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_UNDEFINED)
				{
					throw new WrongDataTypeException(
							"Undefinierter Datentyp in Feld \"Default\" der Tabellenspaltenbeschreibung, javaObjektTypInt:"
									+ javaObjektTypInt + ", typInt:" + typInt + " in Feld"
									+ columnDescription.getField());
				}

				if (fieldName.equals("GUTS_VER"))
				{
					System.out.println("STOP");
				}
			} else
			{
				throw new WrongDataTypeException(
						"Datentyp ist unbekannt oder noch nicht behandelt. Feld: " + columnDescription.getField());
			}

			mySQLTableDescriptions.add(columnDescription);
		}
		return mySQLTableDescriptions.toArray(new MySQLTableColumnDescription[0]);
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param yesNoString
	 * @return Creation: 15.08.2018 by mst
	 */
	private static boolean parseBooleanYesNoString(String yesNoString)
	{
		if (yesNoString.toLowerCase().equals("yes"))
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * Holt aus der angegebenen SQL-Verbindung (MySQL) alle verfuegbaren Tabellen
	 * der angebenenen Datenbank und gibt sie als TableDescription-Objekt zurueck.
	 *
	 * @param adsConnection
	 * @param dbName
	 * @return
	 * @throws SQLException
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescriptions(Connection connection, String dbName)
			throws SQLException
	{
		ArrayList<IsqlTableDescriptionInterface> mySQLTableDescriptions = new ArrayList<IsqlTableDescriptionInterface>();
		String showQuery = "Show table status from " + dbName;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(showQuery);
		while (resultSet.next())
		{
			MySQLTableDescription tableDescription = new MySQLTableDescription();
			tableDescription.setName(resultSet.getString("Name"));
			tableDescription.setEngine(resultSet.getString("Engine"));
			tableDescription.setVersion(resultSet.getInt("Version"));
			tableDescription.setRowFormat(resultSet.getString("Row_Format"));
			tableDescription.setRows(resultSet.getDouble("Rows"));
			tableDescription.setAvgRowLength(resultSet.getDouble("Avg_row_length"));
			tableDescription.setData_length(resultSet.getInt("Data_length"));
			// tableDescription.setMaxDataLength(resultSet.getInt("Max_data_length"));
			tableDescription.setIndexLength(resultSet.getInt("Index_length"));
			tableDescription.setDataFree(resultSet.getInt("Data_free"));
			tableDescription.setAutoIncrement(resultSet.getLong("Auto_increment"));
			tableDescription.setCreateTime(resultSet.getDate("Create_time"));
			tableDescription.setUpdateTime(resultSet.getDate("Update_time"));
			tableDescription.setCheckTime(resultSet.getDate("Check_time"));
			tableDescription.setCollation(resultSet.getString("Collation"));
			tableDescription.setChecksum(resultSet.getString("Checksum"));
			tableDescription.setCreateOptions(resultSet.getString("Create_options"));
			tableDescription.setComment(resultSet.getString("Comment"));

			mySQLTableDescriptions.add(tableDescription);
		}

		return mySQLTableDescriptions.toArray(new IsqlTableDescriptionInterface[0]);
	}

	/**
	 * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle der
	 * angebenenen Datenbank und gibt sie als TableDescription-Objekt zurueck.
	 *
	 * @param adsConnection
	 * @param dbName
	 * @return null wenn Tabelle nicht gefunden und sonst ein
	 *         MySQLTableDescription-Objekt
	 * @throws SQLException
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescription(Connection connection, String dbName,
			String tableName) throws SQLException
	{
		// String showQuery = "Show table status from " + dbName +
		// " where name like '" + tableName + "'";
		return getMySQLTableDescriptionArray(connection, tableName);
	}

	/**
	 * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle der
	 * aktuell verbundenen Datenbank und gibt sie als TableDescription-Objekt
	 * zurueck.
	 *
	 * @param adsConnection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescriptionArray(Connection connection, String tableName)
			throws SQLException
	{
		ArrayList<IsqlTableDescriptionInterface> mySQLTableDescriptions = new ArrayList<IsqlTableDescriptionInterface>();
		String showQuery = "Show table status where name like '" + tableName + "'";
		// + "'";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(showQuery);
		if (resultSet.next())
		{
			MySQLTableDescription tableDescription = new MySQLTableDescription();
			tableDescription.setName(resultSet.getString("Name"));
			tableDescription.setEngine(resultSet.getString("Engine"));
			tableDescription.setVersion(resultSet.getInt("Version"));
			tableDescription.setRowFormat(resultSet.getString("Row_Format"));
			tableDescription.setRows(resultSet.getDouble("Rows"));
			tableDescription.setAvgRowLength(resultSet.getDouble("Avg_row_length"));
			tableDescription.setData_length(resultSet.getInt("Data_length"));
			// tableDescription.setMaxDataLength(resultSet.getInt("Max_data_length"));
			tableDescription.setIndexLength(resultSet.getInt("Index_length"));
			tableDescription.setDataFree(resultSet.getInt("Data_free"));
			tableDescription.setAutoIncrement(resultSet.getLong("Auto_increment"));
			tableDescription.setCreateTime(resultSet.getDate("Create_time"));
			tableDescription.setUpdateTime(resultSet.getDate("Update_time"));
			tableDescription.setCheckTime(resultSet.getDate("Check_time"));
			tableDescription.setCollation(resultSet.getString("Collation"));
			tableDescription.setChecksum(resultSet.getString("Checksum"));
			tableDescription.setCreateOptions(resultSet.getString("Create_options"));
			tableDescription.setComment(resultSet.getString("Comment"));

			mySQLTableDescriptions.add(tableDescription);
		}

		return mySQLTableDescriptions.toArray(new IsqlTableDescriptionInterface[0]);
	}

	/**
	 * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle der
	 * aktuell verbundenen Datenbank und gibt sie als TableDescription-Objekt
	 * zurueck.
	 *
	 * @param adsConnection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static IsqlTableDescriptionInterface getMySQLTableDescription(Connection connection, String tableName)
			throws SQLException
	{
		String showQuery = "Show table status where name like '" + tableName + "'";
		// + "'";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(showQuery);
		MySQLTableDescription tableDescription = null;
		if (resultSet.next())
		{
			tableDescription = new MySQLTableDescription();
			tableDescription.setName(resultSet.getString("Name"));
			tableDescription.setEngine(resultSet.getString("Engine"));
			tableDescription.setVersion(resultSet.getInt("Version"));
			tableDescription.setRowFormat(resultSet.getString("Row_Format"));
			tableDescription.setRows(resultSet.getDouble("Rows"));
			tableDescription.setAvgRowLength(resultSet.getDouble("Avg_row_length"));
			tableDescription.setData_length(resultSet.getInt("Data_length"));
			// tableDescription.setMaxDataLength(resultSet.getInt("Max_data_length"));
			tableDescription.setIndexLength(resultSet.getInt("Index_length"));
			tableDescription.setDataFree(resultSet.getInt("Data_free"));
			tableDescription.setAutoIncrement(resultSet.getLong("Auto_increment"));
			tableDescription.setCreateTime(resultSet.getDate("Create_time"));
			tableDescription.setUpdateTime(resultSet.getDate("Update_time"));
			tableDescription.setCheckTime(resultSet.getDate("Check_time"));
			tableDescription.setCollation(resultSet.getString("Collation"));
			tableDescription.setChecksum(resultSet.getString("Checksum"));
			tableDescription.setCreateOptions(resultSet.getString("Create_options"));
			tableDescription.setComment(resultSet.getString("Comment"));

		}

		return tableDescription;
	}

}
