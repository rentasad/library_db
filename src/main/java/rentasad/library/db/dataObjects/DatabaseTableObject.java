package rentasad.library.db.dataObjects;

import java.util.HashMap;
/**
 * DatabaseTableObject
 * 
 * Enthaelt einen Datensatz einer Tabelle mit allen zugehoerigen Feldern und Feldinhalten - setzt allerdings String voraus.
 * @author mtr
 *
 */
public class DatabaseTableObject
{
	/**
	 * HashMap<String Feldname, String Feldinhalt>
	 */
	private HashMap<String, String> databaseTableHashMap;

	/**
	 * @param databaseTableHashMap
	 */
	public DatabaseTableObject(HashMap<String, String> databaseTableHashMap)
	{
		super();
		this.databaseTableHashMap = databaseTableHashMap;
	}

	public HashMap<String, String> getDatabaseTableHashMap()
	{
		return databaseTableHashMap;
	}

	public void setDatabaseTableHashMap(HashMap<String, String> databaseTableHashMap)
	{
		this.databaseTableHashMap = databaseTableHashMap;
	}
}
