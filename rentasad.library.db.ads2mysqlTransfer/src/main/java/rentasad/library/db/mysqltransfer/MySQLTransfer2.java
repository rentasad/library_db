package rentasad.library.db.mysqltransfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import rentasad.library.basicTools.StringTool;
import rentasad.library.db.dataObjects.ISQLTableColumnsDescriptionInterface;
import rentasad.library.db.dataObjects.IsqlTableDescriptionInterface;
import rentasad.library.db.dataObjects.MySQLTableColumnDescription;
import rentasad.library.db.dataObjects.MySQLTableDescription;
import rentasad.library.tools.exceptions.WrongDataTypeException;

/**
 * 
 * Gustini GmbH (2017)
 * Creation: 19.06.2017
 * Library
 * gustini.library.db
 * 
 * @author Matthias Staud
 *
 *
 *         Description:
 *         Diese Klasse liest eine SQL Tabelle aus und erzeugt aus der Struktur ein
 *         Datenobjekt, welches alle relevanten Datenbankinformationen enthaelt
 *         ACHTUNG: Fuer richtiges Encoding muss im MySQL-Connection-String folgende Variable angegeben werden:
 *         ?characterEncoding=cp1250
 *
 */
public class MySQLTransfer2
{

    public MySQLTransfer2()
    {
        super();
    }

    /**
     * Gibt die Anzahl der verfuegbaren Eintraege einer Tabelle zurueck.
     * Z.B. verwendbar fuer Progressbars und aehnliche Fortschrittsanzeigen.
     *
     * @param adsConnection
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getNumberOfEntriesFromAdsTableName(final Connection adsConnection, String tableName, String adsTabellenPfad) throws SQLException
    {
        int entriesCount = 0;
        String selectQuery = "SELECT COUNT(*) as anzahl FROM ";

        tableName = tableName.toUpperCase();
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
     * Diese Methode kopiert von ads-Datenbank die angegebenen Tabellen in MySQL-Connection
     *
     * ACHTUNG: Fuer richtiges Encoding muss im MySQL-Connection-String folgende Variable angegeben werden:
     * ?characterEncoding=cp1250
     *
     * @param mySQLConnection
     * @param adsConnection
     * @param mySQLTableDescription
     * @return Anzahl importierter Zeilen
     * @throws WrongDataTypeException
     * @throws SQLException
     * @throws Exception
     */
    public int transferTableFromAdsToMySQL(final Connection mySQLConnection, final Connection adsConnection, final IsqlTableDescriptionInterface mySQLTableDescription, String adsTabellenPfad, boolean deleteBeforeInsert)
                throws SQLException, WrongDataTypeException
    {

        String tableName = mySQLTableDescription.getName();

        /*
         * Erstellen des SELECT- und INSERT-Querys
         */
        SelectQueryObject selectQueryObject = getSelectQueryFromMySQlDescription(mySQLConnection, adsConnection, mySQLTableDescription, adsTabellenPfad);
        /*
         * Erzeugen und Ausfuehren der SQL-Statements
         */
        // Leeren der mySQL-Tabelle vor Import
        if (deleteBeforeInsert)
        {
            String truncateQuery = "truncate " + tableName;
            Statement truncateStatement = mySQLConnection.createStatement();
            truncateStatement.execute(truncateQuery);
            truncateStatement.close();
        }

        int zeilenCount = insertDatasFromSelectQueryObject(adsConnection, mySQLConnection, selectQueryObject, mySQLTableDescription);
        return zeilenCount;
    }

    /**
     *
     * Description: Generiert und fuehrt INSERT-Query fuer Transfer aus
     *
     * @param conSource
     * @param conTarget
     * @param selectQueryObject
     * @param mySQLTableDescription
     * @return
     * @throws SQLException
     *             Creation: 07.06.2016 by mst
     * @throws WrongDataTypeException
     */
    private int insertDatasFromSelectQueryObject(Connection conSource, Connection conTarget, SelectQueryObject selectQueryObject, final IsqlTableDescriptionInterface mySQLTableDescription)
                throws SQLException, WrongDataTypeException
    {
        String tableName = "`" + mySQLTableDescription.getName() + "`";

        String selectQuery = selectQueryObject.getSelectQuery();
        String mySQLColumnList = selectQueryObject.getMySQLColumnList();
        String columnPlaceHolder = selectQueryObject.getColumnPlaceHolder();
        ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions = selectQueryObject.getTableColumnDescriptions();

        int zeile = 0;
        String preparedInsertQueryString = String.format("INSERT INTO %s \n(%s) \n VALUES \n %s ", tableName, mySQLColumnList, columnPlaceHolder);
        PreparedStatement insertPreparedStatement = conTarget.prepareStatement(preparedInsertQueryString);

        Statement sourceSelectStatement = conSource.createStatement();
        ResultSet sourceSelectResultSet;
        try
        {

            sourceSelectResultSet = sourceSelectStatement.executeQuery(selectQuery);
        } catch (SQLException e)
        {
            System.err.println(selectQuery);
            throw e;
        }

        while (sourceSelectResultSet.next())
        {

            for (int i = 0; i < tableColumnDescriptions.length; i++)
            {
                int colNumber = i + 1;
                ISQLTableColumnsDescriptionInterface column = tableColumnDescriptions[i];
                int objektTypInt = column.getJavaDatatypeFromISQLTyp(column.getType());
                switch (objektTypInt)
                {
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN:
                        insertPreparedStatement.setBoolean(colNumber, sourceSelectResultSet.getBoolean(column.getField()));
                        break;
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE:
                        insertPreparedStatement.setDate(colNumber, sourceSelectResultSet.getDate(column.getField()));
                        break;
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE:
                        insertPreparedStatement.setDouble(colNumber, sourceSelectResultSet.getDouble(column.getField()));
                        break;
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT:
                        insertPreparedStatement.setFloat(colNumber, sourceSelectResultSet.getFloat(column.getField()));
                        break;
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT:
                        insertPreparedStatement.setInt(colNumber, sourceSelectResultSet.getInt(column.getField()));
                        break;
                    case ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING:
                        String value = sourceSelectResultSet.getString(column.getField());
                        if (column.getField().equalsIgnoreCase("TEASER") && !value.equals(""))
                        {
                            System.out.println(value);
                        }

                        insertPreparedStatement.setString(colNumber, value);
                        break;

                    default:
                        throw new WrongDataTypeException("Unbekannter nicht definierter Datentyp: " + objektTypInt);
                }
            }

            insertPreparedStatement.executeUpdate();
            // insertPreparedStatement.executeBatch();
            zeile++;
        }
        insertPreparedStatement.close();

        return zeile;
    }

    /**
     *
     * Gustini GmbH (2016)
     * Creation: 07.06.2016
     * Library
     * gustini.library.db
     *
     * @author Matthias Staud
     *
     *
     *         Description: Fasst alle notwendigen Transferinformationen fuer den automatisierten INSERT in einem SELECT-Objekt zusammen.
     *
     */
    private class SelectQueryObject
    {
        private final String selectQuery;
        private final String mySQLColumnList;
        private final String columnPlaceHolder;
        private final ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions;

        /**
         * @param selectQuery
         * @param selectQueryMySql
         * @param mySQLColumnList
         * @param columnPlaceHolder
         * @param tableColumnDescriptions
         */
        public SelectQueryObject(
                                 String selectQuery,
                                 String mySQLColumnList,
                                 String columnPlaceHolder,
                                 ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions)
        {
            super();
            this.selectQuery = selectQuery;
            this.mySQLColumnList = mySQLColumnList;
            this.columnPlaceHolder = columnPlaceHolder;
            this.tableColumnDescriptions = tableColumnDescriptions;
        }

        /**
         * @return the mySQLColumnList
         */
        public String getMySQLColumnList()
        {
            return mySQLColumnList;
        }

        /**
         * @return the columnPlaceHolder
         */
        public String getColumnPlaceHolder()
        {
            return columnPlaceHolder;
        }

        /**
         * @return the tableColumnDescriptions
         */
        public ISQLTableColumnsDescriptionInterface[] getTableColumnDescriptions()
        {
            return tableColumnDescriptions;
        }

        /**
         * @return the selectQueryAds
         */
        public String getSelectQuery()
        {
            return selectQuery;
        }

    }

    /**
     *
     * Description:
     *
     * @param conSource
     * @param conTarget
     * @param mySQLTableDescription
     * @param fromTablePath
     * @return
     * @throws SQLException
     * @throws WrongDataTypeException
     *             Creation: 08.06.2016 by mst
     */
    private SelectQueryObject getSelectQueryFromMySQlDescription(final Connection conSource, final Connection conTarget, final IsqlTableDescriptionInterface mySQLTableDescription, final String fromTablePath)
                throws SQLException, WrongDataTypeException
    {
        String tableName;
        if (conTarget.getMetaData().getDatabaseProductName().equalsIgnoreCase("Advantage Database Server"))
        {
            tableName = mySQLTableDescription.getName();
        } else
        {
            tableName = "`" + mySQLTableDescription.getName() + "`";
        }
        ISQLTableColumnsDescriptionInterface[] tableColumnDescriptions = MySQLTableUtility.getMySQLTableColumnDescription(conSource, tableName);

        String selectQueryAds = "SELECT ";
        String selectQueryMySql = "SELECT ";
        String columnList = "";
        String mySQLColumnList = "";
        String columnPlaceHolder = "(";
        for (int i = 0; i < tableColumnDescriptions.length; i++)
        {
            // Liste der Spalten, wird fuer SELECT und INSERT verwendet
            columnList += tableColumnDescriptions[i].getField();
            mySQLColumnList += "`" + tableColumnDescriptions[i].getField() + "`";
            // Wird fuer PreparedStatement verwendet. Anzahl Spalten entspricht
            // Anzahl der Platzhalter
            columnPlaceHolder += "\n?";
            if (i < (tableColumnDescriptions.length - 1))
            {
                columnList += ",";
                mySQLColumnList += ",";
                columnPlaceHolder += ",";
            }
        }

        columnPlaceHolder += ")";
        selectQueryAds += columnList + "\n FROM " + fromTablePath + tableName;
        selectQueryMySql += mySQLColumnList + "\n FROM " + fromTablePath + tableName;

        String selectQuery;
        if (conTarget.getMetaData().getDatabaseProductName().equalsIgnoreCase("Advantage Database Server"))
        {
            selectQuery = selectQueryAds;
        } else
        {
            selectQuery = selectQueryMySql;
        }
        return new SelectQueryObject(selectQuery, mySQLColumnList, columnPlaceHolder, tableColumnDescriptions);
    }

    /**
     *
     * Description: Transfer Datas from Source MySql-Table to Target MySqlTable on different Databases
     *
     * @param conSource
     * @param conTarget
     * @param mySQLTableDescription
     * @return
     * @throws SQLException
     * @throws WrongDataTypeException
     *             Creation: 08.06.2016 by mst
     */
    public int transferMySqlTableToMySqlTable(final Connection conSource, final Connection conTarget, final IsqlTableDescriptionInterface mySQLTableDescription) throws SQLException, WrongDataTypeException
    {
        String tableName = "`" + mySQLTableDescription.getName() + "`";
        SelectQueryObject selectQueryObject = getSelectQueryFromMySQlDescription(conSource, conTarget, mySQLTableDescription, "");

        // Leeren der mySQL-Tabelle vor Import
        String truncateQuery = "truncate " + tableName;
        Statement truncateStatement = conTarget.createStatement();
        truncateStatement.execute(truncateQuery);
        truncateStatement.close();

        // Insert durchfuehren
        insertDatasFromSelectQueryObject(conSource, conTarget, selectQueryObject, mySQLTableDescription);

        return 0;
    }

    /**
     * Gibt einen den statischen MySQL-Datentyp (int) zurueck.
     * Wenn der Datentyp unbekannt ist, wird der Datentyp
     * ISQLTableColumnsDescriptionInterface.DATATYPE_UNDEFINED (999)
     * zurueckgegeben.
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
    public static ISQLTableColumnsDescriptionInterface[] getMySQLTableColumnDescription(Connection connection, String tableName) throws SQLException, WrongDataTypeException
    {
        ArrayList<ISQLTableColumnsDescriptionInterface> mySQLTableDescriptions = new ArrayList<ISQLTableColumnsDescriptionInterface>();
        String showQuery = "show full fields from " + tableName;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(showQuery);
        while (resultSet.next())
        {
            ISQLTableColumnsDescriptionInterface columnDescription = new MySQLTableColumnDescription();

            columnDescription.setField(resultSet.getString("Field"));

            columnDescription.setCollation(resultSet.getString("Collation"));
            // columnDescription.setLength(resultSet.getInt("Length"));
            String canBeNullString = resultSet.getString("Null");
            Boolean canBeNullBoolean;
            if (canBeNullString.equalsIgnoreCase("YES"))
            {
                canBeNullBoolean = true;
            } else
            {
                canBeNullBoolean = false;
            }
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
                                "Undefinierter Datentyp in Feld \"Default\" der Tabellenspaltenbeschreibung, javaObjektTypInt:" + javaObjektTypInt + ", typInt:" + typInt + " in Feld" + columnDescription.getField());
                }

            } else
            {
                throw new WrongDataTypeException("Datentyp ist unbekannt oder noch nicht behandelt. Feld: " + columnDescription.getField());
            }

            mySQLTableDescriptions.add(columnDescription);
        }
        return mySQLTableDescriptions.toArray(new MySQLTableColumnDescription[0]);
    }

    /**
     * Holt aus der angegebenen SQL-Verbindung (MySQL) alle verfuegbaren Tabellen
     * der angebenenen Datenbank und gibt sie als TableDescription-Objekt
     * zurueck.
     *
     * @param adsConnection
     * @param dbName
     * @return
     * @throws SQLException
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
     * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle
     * der angebenenen Datenbank und gibt sie als TableDescription-Objekt
     * zurueck.
     *
     * @param adsConnection
     * @param dbName
     * @return null wenn Tabelle nicht gefunden und sonst ein
     *         MySQLTableDescription-Objekt
     * @throws SQLException
     */
    public static IsqlTableDescriptionInterface[] getMySQLTableDescription(Connection connection, String dbName, String tableName) throws SQLException
    {
        // String showQuery = "Show table status from " + dbName +
        // " where name like '" + tableName + "'";
        return getMySQLTableDescriptionArray(connection, tableName);
    }

    /**
     * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle
     * der aktuell verbundenen Datenbank und gibt sie als
     * TableDescription-Objekt
     * zurueck.
     *
     * @param adsConnection
     * @param tableName
     * @return
     * @throws SQLException
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
     * Holt aus der angegebenen SQL-Verbindung (MySQL) eine bestimmte Tabelle
     * der aktuell verbundenen Datenbank und gibt sie als
     * TableDescription-Objekt
     * zurueck.
     *
     * @param adsConnection
     * @param tableName
     * @return
     * @throws SQLException
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
