package rentasad.library.db.mysqltransfer;

import lombok.Setter;
import rentasad.library.basicTools.StringTool;
import rentasad.library.basicTools.dateTool.DateTools;
import rentasad.library.db.ProgressDisplay;
import rentasad.library.db.dataObjects.*;
import rentasad.library.tools.exceptions.WrongDataTypeException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/*
 * Diese Klasse liest eine SQL Tabelle aus und erzeugt aus der Struktur ein
 * Datenobjekt, welches alle relevanten Datenbankinformationen enthaelt ACHTUNG:
 * Fuer richtiges Encoding muss im MySQL-Connection-String folgende Variable
 * angegeben werden: ?characterEncoding=cp1250
 *
 * @author mst
 *
 */

/**
 * The MySQLTableUtility class provides utility methods for working with MySQL tables.
 * Fuer richtiges Encoding muss im MySQL-Connection-String folgende Variable
 * angegeben werden: ?characterEncoding=cp1250
 */
public class MySQLTableUtility
{
	private static final String COLUMN_QUOTE = "`";
	private static final String COLUMN_PLACEHOLDER = " ? ";
	private Map<String, String> configMap;
	private final int BATCH_SIZE = 500;
	private final ProgressDisplay progressDisplay;
	@Setter private final boolean debug;

	public MySQLTableUtility(Map<String, String> configMap)
	{
		super();
		this.configMap = configMap;
		this.debug = false;
		this.progressDisplay = new ProgressDisplay(60);
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
	 * Transfers data from an ADS table to a MySQL table.
	 * Fuer richtiges Encoding muss im MySQL-Connection-String folgende
	 * * Variable angegeben werden: ?characterEncoding=cp1250
	 *
	 * @param mySQLConnection       The connection to the MySQL database.
	 * @param adsConnection         The connection to the ADS database.
	 * @param mySQLTableDescription The description of the MySQL table.
	 * @return The number of rows transferred.
	 * @throws SQLException           If an error occurs while executing SQL statements.
	 * @throws WrongDataTypeException If an unknown or undefined data type is encountered.
	 */
	public int transferTableFromAdsToMySQL(
			final Connection mySQLConnection, final Connection adsConnection, final IsqlTableDescriptionInterface mySQLTableDescription) throws SQLException, WrongDataTypeException
	{
		progressDisplay.startProgress();
		String tableName = mySQLTableDescription.getName();
		ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions = MySQLTableUtility.getMySQLTableColumnDescription(mySQLConnection, tableName);
		if (debug)
		{
			System.out.println("Anzahl Spalten: " + tableColumnDescriptions.length);
		}
		/*
		 * Erstellen des SELECT- und INSERT-Querys
		 */
		TableSelectObject tableSelectObject = getTableSelectObject(tableColumnDescriptions, tableName);
		String selectQuery = tableSelectObject.getSelectString();
		/*
		 * Erzeugen und Ausfuehren der SQL-Statements
		 */
		// Leeren der mySQL-Tabelle vor Import
		truncateTable(tableName, mySQLConnection);
		Statement adsSelectStatement = adsConnection.createStatement();
		ResultSet adsSelectResultSet = adsSelectStatement.executeQuery(selectQuery);

		int zeile = 1;

		if (debug)
		{// Im DEBUG-Modus werden die INSERT-Queries von hand erzeugt, um Fehler
			// in der Erzeugung zu erkennen. (Ohne Prepared Statement)
			zeile = insertValuesWithoutPreparedStatements(adsSelectResultSet, adsConnection, mySQLConnection, tableColumnDescriptions, tableSelectObject, tableName);
		}
		else
		{
			zeile = insertValuesWithPreparedStatements(adsSelectResultSet, adsConnection, mySQLConnection, tableColumnDescriptions, tableSelectObject, tableName);
		}

		return zeile;
	}

	/**
	 * Inserts values from a given ResultSet into a MySQL table using prepared statements.
	 *
	 * @param adsSelectResultSet      The ResultSet holding the data to be inserted.
	 * @param adsConnection           The Connection object for the original database.
	 * @param mySQLConnection         The Connection object for the target MySQL database.
	 * @param tableColumnDescriptions Descriptions of the table columns to determine data types.
	 * @param tableSelectObject       An object containing metadata like column names and placeholders for the prepared statement.
	 * @param tableName               The name of the table into which data is to be inserted.
	 * @return The number of rows processed.
	 * @throws SQLException           If a database access error occurs or the SQL statement is incorrect.
	 * @throws WrongDataTypeException If an unknown or undefined data type is encountered.
	 */
	private int insertValuesWithPreparedStatements(
			ResultSet adsSelectResultSet, Connection adsConnection, Connection mySQLConnection, ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions, TableSelectObject tableSelectObject,
			String tableName) throws SQLException, WrongDataTypeException
	{
		int zeile = 1;
		// im NICHT-DEBUG-Modus wird mit Prepared Statements gearbeitet.
		String preparedInsertQueryString = String.format("INSERT INTO %s \n(%s) \n VALUES \n %s ", tableName, tableSelectObject.getColumnListString(), tableSelectObject.getColumnPlaceHolder());
		try (PreparedStatement insertPreparedStatement = mySQLConnection.prepareStatement(preparedInsertQueryString))
		{
			int rowNumbers = getNumberOfEntriesFromAdsTableName(adsConnection, tableName);
			// insertPreparedStatement.set
			progressDisplay.setMessage(tableName);
			while (adsSelectResultSet.next())
			{
				progressDisplay.displayProgressBarWithAbsolute(BigDecimal.valueOf(zeile), BigDecimal.valueOf(rowNumbers));
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

				insertPreparedStatement.addBatch();
				if (zeile % BATCH_SIZE == 0)
				{
					insertPreparedStatement.executeBatch();
				}
				//
				zeile++;
			} // Ende der Datensätze
			insertPreparedStatement.executeBatch();
		}
		return zeile;
	}

	/**
	 * Inserts values into a MySQL table without using prepared statements. This method generates and executes
	 * an SQL INSERT statement based on the data retrieved from an ADS database and the column descriptions.
	 * It's only used for debug cases to determine insert errors
	 *
	 * @param adsSelectResultSet      The ResultSet obtained from querying the ADS database, containing the data to be inserted.
	 * @param adsConnection           The connection to the ADS (Advantage Database Server) database.
	 * @param mySQLConnection         The connection to the MySQL database.
	 * @param tableColumnDescriptions An array of ISQLTableColumnsDescriptionInterface objects, representing the column descriptions of the target MySQL table.
	 * @param tableSelectObject       A TableSelectObject, containing various details such as the column list string used in the insertion.
	 * @param tableName               The name of the target MySQL table into which the data will be inserted.
	 * @throws SQLException           If an error occurs while executing SQL statements.
	 * @throws WrongDataTypeException If an unknown or undefined data type is encountered during the data insertion process.
	 */
	private int insertValuesWithoutPreparedStatements(
			ResultSet adsSelectResultSet, Connection adsConnection, Connection mySQLConnection, ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions, TableSelectObject tableSelectObject,
			String tableName) throws SQLException, WrongDataTypeException
	{
		int rowNumbers = getNumberOfEntriesFromAdsTableName(adsConnection, tableName);
		int zeile = 1;
		while (adsSelectResultSet.next())
		{
			progressDisplay.displayAbsoluteProgress(zeile, rowNumbers);
			String insertQueryString = String.format("INSERT INTO %s \n(%s) \n VALUES \n ", tableName, tableSelectObject.getColumnListString());
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
					columnString = Boolean.valueOf(adsSelectResultSet.getBoolean(column.getField()))
										  .toString();
					break;
				case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE:

					Date date = adsSelectResultSet.getDate(column.getField());
					if (date == null)
					{
						columnString = "null";
					}
					else
					{
						columnString = "'" + DateTools.getSQLTimeStampFromDate(date) + "'";
					}

					break;
				case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE:
					columnString = Double.valueOf(adsSelectResultSet.getDouble(column.getField()))
										 .toString();
					break;
				case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT:
					columnString = Float.valueOf(adsSelectResultSet.getFloat(column.getField()))
										.toString();
					break;
				case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT:
					columnString = Integer.valueOf(adsSelectResultSet.getInt(column.getField()))
										  .toString();
					break;
				case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING:
					columnString = "'" + adsSelectResultSet.getString(column.getField()) + "'";
					break;

				default:
					throw new WrongDataTypeException("Unbekannter nicht definierter Datentyp: " + objektTypInt);
				}
				if (colNumber > 1)
				{
					insertValueRow += ",";
				}
				insertValueRow += "\n" + columnString + "/*" + column.getField() + "*/";

			}
			insertValueRow += ")";
			insertQueryString = insertQueryString + insertValueRow;
			if (debug)
			{
				System.out.println("insertQueryString: " + insertQueryString);
			}
			insertStatement.execute(insertQueryString);
			insertStatement.close();
		}
		return zeile;
	}

	/**
	 * @param tableName
	 */
	private void truncateTable(String tableName, Connection mySQLConnection) throws SQLException
	{
		String truncateQuery = String.format("truncate %s", tableName);
		try (Statement truncateStatement = mySQLConnection.createStatement())
		{
			truncateStatement.execute(truncateQuery);
		}
	}

	/**
	 * Constructs a select SQL query string using the provided table column descriptions and table name.
	 *
	 * @param tableColumnDescriptions An array of ISQLTableColumnsDescriptionInterface representing the columns of the table.
	 * @param tableName               The name of the table for which the select query is constructed.
	 * @return A string representing the SELECT SQL query.
	 */
	private TableSelectObject getTableSelectObject(ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions, String tableName)
	{
		String selectQuery = "SELECT ";
		StringBuilder columnListString = new StringBuilder();
		StringBuilder quotedColumnListString = new StringBuilder();
		StringBuilder columnPlaceHolder = new StringBuilder("(");
		TableSelectObject tableSelectObject = TableSelectObject.builder()
															   .build();
		for (int i = 0; i < tableColumnDescriptions.length; i++)
		{
			String field = tableColumnDescriptions[i].getField();
			columnListString.append(field);
			quotedColumnListString.append(COLUMN_QUOTE)
								  .append(field)
								  .append(COLUMN_QUOTE);

			columnPlaceHolder.append(COLUMN_PLACEHOLDER);

			if (i < (tableColumnDescriptions.length - 1))
			{
				columnListString.append(",");
				quotedColumnListString.append(",");
				columnPlaceHolder.append(",");
			}
		}

		String adsTabellenPfad = "";
		if (this.configMap.containsKey(tableName))
		{
			adsTabellenPfad = this.configMap.get(tableName) + "\\";
		}

		columnPlaceHolder.append(")");
		selectQuery += columnListString + "\n FROM " + adsTabellenPfad + tableName;

		tableSelectObject.setColumnPlaceHolder(columnPlaceHolder.toString());
		tableSelectObject.setColumnListString(columnListString.toString());
		tableSelectObject.setQuotedColumnListString(quotedColumnListString.toString());
		tableSelectObject.setSelectString(selectQuery);
		return tableSelectObject;
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
	 * Retrieves the column descriptions of a MySQL table.
	 *
	 * @param connection The connection to the MySQL database.
	 * @param tableName  The name of the table.
	 * @return An array of ISQLTableColumnsDescriptionInterface containing the column descriptions.
	 * @throws SQLException           If an error occurs while executing SQL statements.
	 * @throws WrongDataTypeException If an unknown or undefined data type is encountered.
	 */
	public static ISQLTableColumnsDescriptionInterface[] getMySQLTableColumnDescription(
			Connection connection, String tableName) throws SQLException, WrongDataTypeException
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
				columnDescription.setLength(Integer.valueOf(werteInKlammernString[0])
												   .intValue());
			}

			int typInt = getTypeIntFromTypString(typeString);
			if (typInt != ISQLTableColumnsDescriptionInterface.DATATYPE_UNDEFINED)
			{// Datentyp ist NICHT unbekannt
				columnDescription.setType(typInt);
				int javaObjektTypInt = columnDescription.getJavaDatatypeFromISQLTyp(typInt);
				if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN)
				{

					columnDescription.setDefaultBoolean(resultSet.getBoolean("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE)
				{
					columnDescription.setDefaultDate(resultSet.getDate("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE)
				{
					columnDescription.setDefaultDouble(resultSet.getDouble("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT)
				{

				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT)
				{
					columnDescription.setDefaultInt(resultSet.getInt("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING)
				{
					columnDescription.setDefaultString(resultSet.getString("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_TEXT)
				{
					columnDescription.setDefaultString(resultSet.getString("Default"));
				}
				else if (javaObjektTypInt == ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_UNDEFINED)
				{
					throw new WrongDataTypeException(
							"Undefinierter Datentyp in Feld \"Default\" der Tabellenspaltenbeschreibung, javaObjektTypInt:" + javaObjektTypInt + ", typInt:" + typInt + " in Feld" + columnDescription.getField());
				}

				if (fieldName.equals("GUTS_VER"))
				{
					System.out.println("STOP");
				}
			}
			else
			{
				throw new WrongDataTypeException("Datentyp ist unbekannt oder noch nicht behandelt. Feld: " + columnDescription.getField());
			}

			mySQLTableDescriptions.add(columnDescription);
		}
		return mySQLTableDescriptions.toArray(new MySQLTableColumnDescription[0]);
	}

	/**
	 * Parses a string and returns a boolean value based on the string value.
	 * The string should be "yes" or "no".
	 *
	 * @param yesNoString the string to be parsed. Must be "yes" or "no".
	 * @return true if the string is "yes", false if the string is "no".
	 */
	private static boolean parseBooleanYesNoString(String yesNoString)
	{
		return yesNoString.equalsIgnoreCase("yes");
	}

	/**
	 * Retrieves the descriptions of all MySQL tables in the specified database.
	 *
	 * @param connection The connection to the MySQL database.
	 * @param dbName     The name of the database.
	 * @return An array of IsqlTableDescriptionInterface containing the table descriptions.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescriptions(Connection connection, String dbName) throws SQLException
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
	 * Retrieves the description of a specific MySQL table in the specified database.
	 *
	 * @param connection The connection to the MySQL database.
	 * @param dbName     The name of the database.
	 * @param tableName  The name of the table.
	 * @return An array of IsqlTableDescriptionInterface containing the table description.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescription(
			Connection connection, String dbName, String tableName) throws SQLException
	{
		// String showQuery = "Show table status from " + dbName +
		// " where name like '" + tableName + "'";
		return getMySQLTableDescriptionArray(connection, tableName);
	}

	/**
	 * Retrieves the description of a specific MySQL table and returns it as an array of IsqlTableDescriptionInterface objects.
	 * The method takes in a Connection object and the name of the table as parameters.
	 *
	 * @param connection The Connection object representing the connection to the MySQL database.
	 * @param tableName  The name of the table to retrieve the description for.
	 * @return An array of IsqlTableDescriptionInterface objects containing the table description.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static IsqlTableDescriptionInterface[] getMySQLTableDescriptionArray(Connection connection, String tableName) throws SQLException
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
	 * Retrieves the description of a specific MySQL table and returns it as an instance of IsqlTableDescriptionInterface.
	 *
	 * @param connection The Connection object representing the connection to the MySQL database.
	 * @param tableName  The name of the table to retrieve the description for.
	 * @return An instance of IsqlTableDescriptionInterface containing the table description, or null if the table does not exist.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static IsqlTableDescriptionInterface getMySQLTableDescription(Connection connection, String tableName) throws SQLException
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
