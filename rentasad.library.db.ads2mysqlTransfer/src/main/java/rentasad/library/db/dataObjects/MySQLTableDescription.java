package rentasad.library.db.dataObjects;

import java.util.Date;
/**
 * 
 * @author mst
 *
 *
 * Die unten beschriebenen Felder werden durch folgende Abfrage ausgegeben:
 * 
 * show table status  from [datenbankname]
 *
 */
public class MySQLTableDescription implements IsqlTableDescriptionInterface
{
	String name;
	String engine;
	int version;
	String rowFormat;
	double rows;
	double avgRowLength;
	int data_length;
	int maxDataLength;
	int indexLength;
	int dataFree;
	long autoIncrement;
	Date createTime;
	Date updateTime;
	Date checkTime;
	String collation;
	String checksum;
	String createOptions;
	String comment;

	String[] HeaderNames =
	{
			"Name",
			"Engine",
			"Version",
			"Row_format",
			"Rows",
			"Avg_row_length",
			"Data_length",
			"Max_data_length",
			"Index_length",
			"Data_free",
			"Auto_increment",
			"Create_time",
			"Update_time",
			"Check_time",
			"Collation",
			"Checksum",
			"Create_options",
			"Comment" };

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getEngine()
	 */
	public String getEngine()
	{
		return engine;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setEngine(java.lang.String)
	 */
	public void setEngine(String engine)
	{
		this.engine = engine;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getVersion()
	 */
	public int getVersion()
	{
		return version;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setVersion(int)
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getRowFormat()
	 */
	public String getRowFormat()
	{
		return rowFormat;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setRowFormat(java.lang.String)
	 */
	public void setRowFormat(String rowFormat)
	{
		this.rowFormat = rowFormat;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getRows()
	 */
	public double getRows()
	{
		return rows;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setRows(double)
	 */
	public void setRows(double rows)
	{
		this.rows = rows;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getAvgRowLength()
	 */
	public double getAvgRowLength()
	{
		return avgRowLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setAvgRowLength(double)
	 */
	public void setAvgRowLength(double avgRowLength)
	{
		this.avgRowLength = avgRowLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getData_length()
	 */
	public int getData_length()
	{
		return data_length;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setData_length(int)
	 */
	public void setData_length(int data_length)
	{
		this.data_length = data_length;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getMaxDataLength()
	 */
	public int getMaxDataLength()
	{
		return maxDataLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setMaxDataLength(int)
	 */
	public void setMaxDataLength(int maxDataLength)
	{
		this.maxDataLength = maxDataLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getIndexLength()
	 */
	public int getIndexLength()
	{
		return indexLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setIndexLength(int)
	 */
	public void setIndexLength(int indexLength)
	{
		this.indexLength = indexLength;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getDataFree()
	 */
	public int getDataFree()
	{
		return dataFree;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setDataFree(int)
	 */
	public void setDataFree(int dataFree)
	{
		this.dataFree = dataFree;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getAutoIncrement()
	 */
	public long getAutoIncrement()
	{
		return autoIncrement;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setAutoIncrement(long)
	 */
	public void setAutoIncrement(long autoIncrement)
	{
		this.autoIncrement = autoIncrement;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getCreateTime()
	 */
	public Date getCreateTime()
	{
		return createTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setCreateTime(java.util.Date)
	 */
	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getUpdateTime()
	 */
	public Date getUpdateTime()
	{
		return updateTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setUpdateTime(java.util.Date)
	 */
	public void setUpdateTime(Date updateTime)
	{
		this.updateTime = updateTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getCheckTime()
	 */
	public Date getCheckTime()
	{
		return checkTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setCheckTime(java.util.Date)
	 */
	public void setCheckTime(Date checkTime)
	{
		this.checkTime = checkTime;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getCollation()
	 */
	public String getCollation()
	{
		return collation;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setCollation(java.lang.String)
	 */
	public void setCollation(String collation)
	{
		this.collation = collation;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getChecksum()
	 */
	public String getChecksum()
	{
		return checksum;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setChecksum(java.lang.String)
	 */
	public void setChecksum(String checksum)
	{
		this.checksum = checksum;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getCreateOptions()
	 */
	public String getCreateOptions()
	{
		return createOptions;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setCreateOptions(java.lang.String)
	 */
	public void setCreateOptions(String createOptions)
	{
		this.createOptions = createOptions;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#getComment()
	 */
	public String getComment()
	{
		return comment;
	}

	/* (non-Javadoc)
	 * @see rentasad.lib.dbtransfer2.dataobjects.IsqlTableDescriptionInterface#setComment(java.lang.String)
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

}
