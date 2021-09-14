package rentasad.library.db.dataObjects;

import java.sql.Time;
import java.util.Date;

/**
 * 
 * @author mst
 *         Datenobjekt, welches bei folgender SQL-Abfrage zurueckkommt:
 * 
 *         show full fields from v2la1001
 * 
 *         Mit diesem Datenmodell wird die Struktur einer MySQL-Tabelle
 *         ausgegeben und beschrieben.
 *         Fuer jedes Feld einer Tabelle wird der Datentyp zurueckgegeben, welcher
 *         dieses Feld beschreibt.
 */
public class MySQLTableColumnDescription implements ISQLTableColumnsDescriptionInterface
{
	
	
	
	String field;
	int type;
	String collation;
	int length = LENGTH_IS_NOT_DEFINED;
	boolean canBeNull;
	String key;
	String extra;
	String privileges;
	String comment;
	int defaultInt;
	double defaultDouble;
	String defaultString;
	Date defaultDate;
	Time defaultTime;
	boolean defaultBoolean;
	boolean LengthDefined;

	/**
	 * MYSQL hat sehr viele verschiedene Datentypen. Diese werden auf bestimmte
	 * Java Datentypen umgesetzt. Die Entscheidung, welcher Datentyp wie in Java
	 * umgesetzt wird, geschieht in dieser Funktion
	 * 
	 * @param typ
	 *            ISQLTableColumnsDescriptionInterface.DATATYPE_[...]
	 * 
	 * @return ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_[...]
	 */
	public int getJavaDatatypeFromISQLTyp(int typ)
	{
		int dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_UNDEFINED;

		if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_STRING_VARCHAR
					|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_STRING_LONGTEXT)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_STRING;
		} else if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_DATE_DATE
					|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_DATE_DATETIME
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_DATE_TIME
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_DATE_TIMESTAMP)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DATE;

		} else if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_BIGINT
					|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_BIT
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_DECIMAL
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_INT
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_MEDIUMINT
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_SMALLINT
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_TINYINT
						|| typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_INT)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_INT;
		} else if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_DOUBLE)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_DOUBLE;
		} else if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_FLOAT)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_FLOAT;
		} else if (typ == ISQLTableColumnsDescriptionInterface.DATATYPE_NUM_BOOL)
		{
			dataType = ISQLTableColumnsDescriptionInterface.OBJECT_DATA_TYPE_BOOLEAN;
		}

		return dataType;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getField()
	 */
	public String getField()
	{
		return field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setField(java.lang.String)
	 */
	public void setField(String field)
	{
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getType()
	 */
	public int getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setType(int)
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getCollation()
	 */
	public String getCollation()
	{
		return collation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setCollation(java.lang.String)
	 */
	public void setCollation(String collation)
	{
		this.collation = collation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getLength()
	 */
	public int getLength()
	{
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setLength(int)
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #isCanBeNull()
	 */
	public boolean isCanBeNull()
	{
		return canBeNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setCanBeNull(boolean)
	 */
	public void setCanBeNull(boolean canBeNull)
	{
		this.canBeNull = canBeNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getKey()
	 */
	public String getKey()
	{
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setKey(java.lang.String)
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getExtra()
	 */
	public String getExtra()
	{
		return extra;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setExtra(java.lang.String)
	 */
	public void setExtra(String extra)
	{
		this.extra = extra;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getPrivileges()
	 */
	public String getPrivileges()
	{
		return privileges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setPrivileges(java.lang.String)
	 */
	public void setPrivileges(String privileges)
	{
		this.privileges = privileges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getComment()
	 */
	public String getComment()
	{
		return comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setComment(java.lang.String)
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getDefaultInt()
	 */
	public int getDefaultInt()
	{
		return defaultInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setDefaultInt(int)
	 */
	public void setDefaultInt(int defaultInt)
	{
		this.defaultInt = defaultInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getDefaultDouble()
	 */
	public double getDefaultDouble()
	{
		return defaultDouble;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setDefaultDouble(double)
	 */
	public void setDefaultDouble(double defaultDouble)
	{
		this.defaultDouble = defaultDouble;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getDefaultString()
	 */
	public String getDefaultString()
	{
		return defaultString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setDefaultString(java.lang.String)
	 */
	public void setDefaultString(String defaultString)
	{
		this.defaultString = defaultString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getDefaultDate()
	 */
	public Date getDefaultDate()
	{
		return defaultDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setDefaultDate(java.util.Date)
	 */
	public void setDefaultDate(Date defaultDate)
	{
		this.defaultDate = defaultDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #getDefaultTime()
	 */
	public Time getDefaultTime()
	{
		return defaultTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface
	 * #setDefaultTime(java.sql.Time)
	 */
	public void setDefaultTime(Time defaultTime)
	{
		this.defaultTime = defaultTime;
	}

	/**
	 * @return the defaultBoolean
	 */
	public boolean isDefaultBoolean()
	{
		return defaultBoolean;
	}

	/**
	 * @param defaultBoolean
	 *            the defaultBoolean to set
	 */
	public void setDefaultBoolean(boolean defaultBoolean)
	{
		this.defaultBoolean = defaultBoolean;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.ISQLTableColumnsDescriptionInterface#isLengthDefined()
	 */
	public boolean isLengthDefined()
	{
		if (this.length == LENGTH_IS_NOT_DEFINED)
		{
			return false;
		}else
		{
			return true;
		}
		
	}

}
