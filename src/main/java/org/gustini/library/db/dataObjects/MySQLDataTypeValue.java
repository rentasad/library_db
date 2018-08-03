package org.gustini.library.db.dataObjects;

public class MySQLDataTypeValue
{
	/*
	 * Umsetzungen verschiedener Begriffe auf Datentypen
	 * Datenbankenuebergreifend:
	 * BOOL TINYINT
	 * BOOLEAN TINYINT
	 * CHARACTER VARYING(M) VARCHAR(M)
	 * FIXED DECIMAL
	 * FLOAT4 FLOAT
	 * FLOAT8 DOUBLE
	 * INT1 TINYINT
	 * INT2 SMALLINT
	 * INT3 MEDIUMINT
	 * INT4 INT
	 * INT8 BIGINT
	 * LONG VARBINARY MEDIUMBLOB
	 * LONG VARCHAR MEDIUMTEXT
	 * LONG MEDIUMTEXT
	 * MIDDLEINT MEDIUMINT
	 * NUMERIC DECIMAL
	 */
	
	public final static int TYPE_INT_DATE = 1;
	public final static int TYPE_INT_DATE_TIME = 2;
	public final static int TYPE_INT_TIMESTAMP = 3;
	public final static int TYPE_INT_TIME = 4;
	public final static int TYPE_INT_YEAR = 5;
	public final static int TYPE_INT_CHAR = 6;
	public final static int TYPE_INT_VARCHAR = 7;
	public final static int TYPE_INT_BINARY = 8;
	public final static int TYPE_INT_BLOB = 9;
	public final static int TYPE_INT_ENUM = 10;
	public final static int TYPE_INT_SET = 11;
	public final static int TYPE_INT_TINY_INT = 12;
	public final static int TYPE_INT_SMALL_INT = 13;
	public final static int TYPE_INT_MEDIUM_INT = 14;
	public final static int TYPE_INT_INT = 15;
	public final static int TYPE_INT_BIG_INT = 16;
	public final static int TYPE_INT_DECMAL = 17;
	public final static int TYPE_INT_NUMERIC = 18;
	public final static int TYPE_INT_FLOAT = 19;
	public final static int TYPE_INT_DOUBLE = 20;
	public final static int TYPE_INT_BIT = 21;

	
	/*
	 * Numerische Datentpyen:
	 */
	public final static String TYPE_NAME_TINY_INT = "TINYINT";
	public final static String TYPE_NAME_SMALL_INT = "SMALLINT";
	public final static String TYPE_NAME_MEDIUM_INT ="MEDIUMINT"; 
	public final static String TYPE_NAME_INT = "INT";
	public final static String TYPE_NAME_BIG_INT = "BIGINT";
	public final static String TYPE_NAME_DECIMAL = "DECIMAL";
	public final static String TYPE_NAME_NUMERIC ="NUMERIC"; 
	public final static String TYPE_NAME_FLOAT = "FLOAT";
	public final static String TYPE_NAME_DOUBE ="DOUBLE"; 
	public final static String TYPE_NAME_BIT = "BIT";
	
	/*
	 * Date and Time Types
	 */

	public final static String TYPE_NAME_DATE = "DATE";
	public final static String TYPE_NAME_DATE_TIME = "DATETIME";
	public final static String TYPE_NAME_TIME_STAMP = "TIMESTAMP";
	public final static String TYPE_NAME_TIME = "TIME";
	public final static String TYPE_NAME_YEAR = "YEAR";

	/*
	 * String Types
	 */
	public final static String TYPE_NAME_CHAR = "CHAR";
	public final static String TYPE_NAME_VARCHAR = "VARCHAR";
	public final static String TYPE_NAME_BINARY = "BINARY";
	public final static String TYPE_NAME_BLOB = "BLOB";
	public final static String TYPE_NAME_ENUM = "ENUM";
	public final static String TYPE_NAME_SET = "SET";

	
	
	
	
	
}
