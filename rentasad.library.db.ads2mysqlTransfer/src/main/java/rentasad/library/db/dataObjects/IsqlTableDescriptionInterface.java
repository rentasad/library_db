package rentasad.library.db.dataObjects;

import java.util.Date;

public interface IsqlTableDescriptionInterface
{

	
	/**
	 * Den Namen der Tabelle.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Den Namen der Tabelle
	 * 
	 * @param name
	 *            the name to set
	 */
	public abstract void setName(String name);

	/**
	 * Die Speicher-Engine der Tabelle. Siehe auch Kapitel 14, Speicher-Engines
	 * und Tabellentypen. Vor MySQL 4.1.2 hiess dieser Wert Type.
	 * 
	 * @return the engine
	 */
	public abstract String getEngine();

	/**
	 * Die Speicher-Engine der Tabelle. Siehe auch Kapitel 14, Speicher-Engines
	 * und Tabellentypen. Vor MySQL 4.1.2 hiess dieser Wert Type.
	 * 
	 * @param engine
	 *            the engine to set
	 */
	public abstract void setEngine(String engine);

	/**
	 * Die Versionsnummer der .frm-Datei der Tabelle.
	 * 
	 * @return the version
	 */
	public abstract int getVersion();

	/**
	 * Die Versionsnummer der .frm-Datei der Tabelle.
	 * 
	 * @param version
	 *            the version to set
	 */
	public abstract void setVersion(int version);

	/**
	 * Das Format fuer die Datensatzspeicherung (Fixed, Dynamic, Compressed,
	 * Redundant, Compact). Das Format von InnoDB-Tabellen wird als Redundant
	 * oder Compact gemeldet.
	 * 
	 * @return the rowFormat
	 */
	public abstract String getRowFormat();

	/**
	 * Das Format fuer die Datensatzspeicherung (Fixed, Dynamic, Compressed,
	 * Redundant, Compact). Das Format von InnoDB-Tabellen wird als Redundant
	 * oder Compact gemeldet.
	 * 
	 * @param rowFormat
	 *            the rowFormat to set
	 */
	public abstract void setRowFormat(String rowFormat);

	/**
	 * Anzahl der Datensaetze. Einige Speicher-Engines wie etwa MyISAM speichern
	 * die exakte Anzahl.
	 * 
	 * Bei anderen Engines wie InnoDB hingegen ist dies ein Schaetzwert, der vom
	 * tatsaechlichen Wert um bis zu 40 oder 50 Prozent abweichen kann. In
	 * solchen Faellen sollten Sie mit SELECT COUNT(*) eine exakte Anzahl
	 * ermitteln.
	 * 
	 * Der Rows-Wert ist NULL bei Tabellen in der Datenbank INFORMATION_SCHEMA.
	 * 
	 * @return the rows
	 */
	public abstract double getRows();

	/**
	 * Anzahl der Datensaetze. Einige Speicher-Engines wie etwa MyISAM speichern
	 * die exakte Anzahl.
	 * 
	 * Bei anderen Engines wie InnoDB hingegen ist dies ein Schaetzwert, der vom
	 * tatsaechlichen Wert um bis zu 40 oder 50 Prozent abweichen kann. In
	 * solchen Faellen sollten Sie mit SELECT COUNT(*) eine exakte Anzahl
	 * ermitteln.
	 * 
	 * Der Rows-Wert ist NULL bei Tabellen in der Datenbank INFORMATION_SCHEMA.
	 * 
	 * @param rows
	 *            the rows to set
	 */
	public abstract void setRows(double rows);

	/**
	 * Durchschnittliche Laenge eines Datensatzes.
	 * 
	 * @return the avgRowLength
	 */
	public abstract double getAvgRowLength();

	/**
	 * Durchschnittliche Laenge eines Datensatzes.
	 * 
	 * @param avgRowLength
	 *            the avgRowLength to set
	 */
	public abstract void setAvgRowLength(double avgRowLength);

	/**
	 * Die Laenge der Datendatei.
	 * 
	 * @return the data_length
	 */
	public abstract int getData_length();

	/**
	 * Die Laenge der Datendatei.
	 * 
	 * @param data_length
	 *            the data_length to set
	 */
	public abstract void setData_length(int data_length);

	/**
	 * Die maximale Laenge der Datendatei. Dies ist die Gesamtzahl der Bytes, die
	 * in der Tabelle gespeichert werden koennen. Sie wird auf der Basis der
	 * verwendeten Zeigergroesse berechnet.
	 * 
	 * @return the maxDataLength
	 */
	public abstract int getMaxDataLength();

	/**
	 * Die maximale Laenge der Datendatei. Dies ist die Gesamtzahl der Bytes, die
	 * in der Tabelle gespeichert werden koennen. Sie wird auf der Basis der
	 * verwendeten Zeigergroesse berechnet.
	 * 
	 * @param maxDataLength
	 *            the maxDataLength to set
	 */
	public abstract void setMaxDataLength(int maxDataLength);

	/**
	 * Die Laenge der Indexdatei.
	 * 
	 * @return the indexLength
	 */
	public abstract int getIndexLength();

	/**
	 * Die Laenge der Indexdatei.
	 * 
	 * @param indexLength
	 *            the indexLength to set
	 */
	public abstract void setIndexLength(int indexLength);

	/**
	 * Anzahl der reservierten, aber nicht verwendeten Bytes.
	 * 
	 * @return the dataFree
	 */
	public abstract int getDataFree();

	/**
	 * Anzahl der reservierten, aber nicht verwendeten Bytes.
	 * 
	 * @param dataFree
	 *            the dataFree to set
	 */
	public abstract void setDataFree(int dataFree);

	/**
	 * Der naechste AUTO_INCREMENT-Wert.
	 * 
	 * @return the autoIncrement
	 */
	public abstract long getAutoIncrement();

	/**
	 * Der naechste AUTO_INCREMENT-Wert.
	 * 
	 * @param autoIncrement
	 *            the autoIncrement to set
	 */
	public abstract void setAutoIncrement(long autoIncrement);

	/**
	 * Zeitpunkt der Tabellenerstellung.
	 * 
	 * @return the createTime
	 */
	public abstract Date getCreateTime();

	/**
	 * Zeitpunkt der Tabellenerstellung.
	 * 
	 * @param createTime
	 *            the createTime to set
	 */
	public abstract void setCreateTime(Date createTime);

	/**
	 * Zeitpunkt der letzten Aktualisierung der Datendatei.
	 * 
	 * @return the updateTime
	 */
	public abstract Date getUpdateTime();

	/**
	 * Zeitpunkt der letzten Aktualisierung der Datendatei.
	 * 
	 * @param updateTime
	 *            the updateTime to set
	 */
	public abstract void setUpdateTime(Date updateTime);

	/**
	 * Zeitpunkt der letzten Tabellenueberpruefung. Nicht alle Speicher-Engines
	 * aktualisieren diese Zeitangabe (in solchen Faellen ist der Wert stets
	 * NULL).
	 * 
	 * @return the checkTime
	 */
	public abstract Date getCheckTime();

	/**
	 * Zeitpunkt der letzten Tabellenueberpruefung. Nicht alle Speicher-Engines
	 * aktualisieren diese Zeitangabe (in solchen Faellen ist der Wert stets
	 * NULL).
	 * 
	 * @param checkTime
	 *            the checkTime to set
	 */
	public abstract void setCheckTime(Date checkTime);

	/**
	 * Zeichensatz und Sortierung der Tabelle.
	 * 
	 * @return the collation
	 */
	public abstract String getCollation();

	/**
	 * Zeichensatz und Sortierung der Tabelle.
	 * 
	 * @param collation
	 *            the collation to set
	 */
	public abstract void setCollation(String collation);

	/**
	 * Wert der mitlaufenden Pruefsumme (sofern vorhanden).
	 * 
	 * @return the checksum
	 */
	public abstract String getChecksum();

	/**
	 * Wert der mitlaufenden Pruefsumme (sofern vorhanden).
	 * 
	 * @param checksum
	 *            the checksum to set
	 */
	public abstract void setChecksum(String checksum);

	/**
	 * @return the createOptions
	 */
	public abstract String getCreateOptions();

	/**
	 * Bei der Erstellung der Tabelle angegebener Kommentar (oder Angaben dazu,
	 * warum MySQL nicht auf die Tabellendaten zugreifen konnte).
	 * 
	 * Im Tabellenkommentar meldet InnoDB den freien Speicher des Tablespaces,
	 * zu dem die Tabelle gehoert. Bei einer Tabelle, die sich in einem
	 * gemeinsamen Tablespace befindet, ist dies der freie Speicher des
	 * gemeinsamen Tablespaces. Verwenden Sie mehrere Tablespaces und hat die
	 * Tabelle einen eigenen Tablespace, dann bezieht sich die Angabe des freien
	 * Speichers nur auf diese Tabelle.
	 * 
	 * @param createOptions
	 *            the createOptions to set
	 */
	public abstract void setCreateOptions(String createOptions);

	/**
	 * Bei der Erstellung der Tabelle angegebener Kommentar (oder Angaben dazu,
	 * warum MySQL nicht auf die Tabellendaten zugreifen konnte).
	 * 
	 * Im Tabellenkommentar meldet InnoDB den freien Speicher des Tablespaces,
	 * zu dem die Tabelle gehoert. Bei einer Tabelle, die sich in einem
	 * gemeinsamen Tablespace befindet, ist dies der freie Speicher des
	 * gemeinsamen Tablespaces. Verwenden Sie mehrere Tablespaces und hat die
	 * Tabelle einen eigenen Tablespace, dann bezieht sich die Angabe des freien
	 * Speichers nur auf diese Tabelle.
	 * 
	 * @return the comment
	 */
	public abstract String getComment();

	/**
	 * @param comment
	 *            the comment to set
	 */
	public abstract void setComment(String comment);

}
