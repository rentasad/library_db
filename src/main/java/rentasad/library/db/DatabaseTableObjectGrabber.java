package rentasad.library.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;

import rentasad.library.db.dataObjects.DatabaseTableObject;
/**
 * 
 * Gustini GmbH (2015)
 * Creation: 28.05.2015
 * Rentasad Library
 * rentasad.lib.db
 * 
 * @author Matthias Staud
 *
 *
 * Description:
 *
 */
public class DatabaseTableObjectGrabber
{
	private Connection connection;
	/**
	 * @param adsConnection
	 * @param tableNameString
	 */
	public DatabaseTableObjectGrabber(Connection connection)
	{
		super();
		this.connection = connection;
	}
/**
 * Diese Funktion gibt ein Hashmap mit einzelnen Hashmaps zurueck. Jedes Hashmap in dem Hashmap entspricht einem Entry aus einer DB-Abfrage.
 * Der Feldname entspricht dem Key, das Value dem Wert des Feldes des Datenbankeintrages.
 * Funktioniert nicht mit ADS-Datenbank!!
 * 
 * @param tableNameString  - Der Name der Tabelle, aus der die Daten geholt werden
 * @param dataObjectQuery - die genaue Abfrage, mit der die Daten aus der Datenbank geholt werden sollen
 * @param keyName - der Feldname aus der Datenbanktabelle, der als referenzierter Keys in der HashMap als Zuordnung verwendet wird
 * @return gHashMap<String, DatabaseTableObject> 
 * @throws SQLException
 */
	public HashMap<String, DatabaseTableObject> getDatabaseTableObjectHashMapFromDB(String tableNameString, String dataObjectQuery, String keyName) throws SQLException
	{
		/**
		 * Die databaseTableObjectHashMap enthaelt die einzelnen Datensaetze der Tabelle
		 */
		HashMap<String, DatabaseTableObject> databaseTableObjectsHashMap = new HashMap<String, DatabaseTableObject>();
		
		/**
		 * HashMap<Integer, String> fieldNameHashMap 
		 * enthaelt eine Hashmap mit allen Feldnamen und der Position in der Tabelle
		 */
		HashMap<Integer, String> fieldNameHashMap = this.getFieldnamesFromDatabaseTable(tableNameString);
		Set<Integer> keySet = fieldNameHashMap.keySet();
		Integer[] keyStringArray = keySet.toArray(new Integer[0]);
		
		Statement dataObjectStatement = this.connection.createStatement();;
		ResultSet dataObjectResultSet =  dataObjectStatement.executeQuery(dataObjectQuery);
		
		while(dataObjectResultSet.next())
		{
			HashMap<String, String> databaseTableObjectEntry = new HashMap<String, String>();
			for (int i = 0; i < keyStringArray.length; i++)
			{
				String fieldName = fieldNameHashMap.get(keyStringArray[i]);
				String fieldValue = dataObjectResultSet.getString(fieldName);
				databaseTableObjectEntry.put(fieldName, fieldValue);
			}
			
			String entryKey = dataObjectResultSet.getString(keyName);
			
			DatabaseTableObject databaseTableObject = new DatabaseTableObject(databaseTableObjectEntry);
			databaseTableObjectsHashMap.put(entryKey,databaseTableObject) ;
//			System.out.println("faege ein: " + entryKey);
		}
		return databaseTableObjectsHashMap;
	}
/**
 * Funktioniert nicht mit ADS-Datenbank!!
 * 
 * 
 * @param tableNameString
 * @return hashMap
 * @throws SQLException
 */
	private HashMap<Integer, String> getFieldnamesFromDatabaseTable(String tableNameString) throws SQLException
	{
		String fieldNameQuery = "SHOW fields FROM " + tableNameString;
		ResultSet fieldNamesResultSet = null;
		Statement fieldNamesStatement;
		fieldNamesStatement = this.connection.createStatement();
		fieldNamesResultSet = fieldNamesStatement.executeQuery(fieldNameQuery);

//		String[] fieldNameStringArray = new String[fieldNamesResultSet.getFetchSize()];
		int i = 0;
		HashMap<Integer, String> fieldNameHashMap = new HashMap<Integer, String>();
		while (fieldNamesResultSet.next())
		{
			fieldNameHashMap.put( Integer.valueOf(i), fieldNamesResultSet.getString(1));
			i++;
		}
		return fieldNameHashMap;
	}
	
	
}
