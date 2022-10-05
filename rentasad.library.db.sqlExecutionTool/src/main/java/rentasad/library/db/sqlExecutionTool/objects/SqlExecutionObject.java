package rentasad.library.db.sqlExecutionTool.objects;

import lombok.Data;

/**
 * Gustini GmbH (2017)
 * Creation: 30.05.2017
 * Library
 * gustini.library.db.sqlExecutionTool.objects
 *
 * @author Matthias Staud
 * <p>
 * <p>
 * Description:
 */
@Data
public class SqlExecutionObject
{

	/**
	 * Is used to toggle if Query/Queries is given as file or as parameter
	 */
	private boolean isFileused = true;
	private String sqlFileName;
	private final QueryTypEnum queryTypEnum;
	private final boolean multiple_Statements;
	private final boolean isPreparedStatement;
	private String sectionName;
	/**
	 * If the file is stored in "Resources", the file access must be different than in the normal file system
	 */
	private boolean fileStoredInResources;

	private String singleQuery;
	private String[] multipleQuery;


	/**
	 *
	 * @param sqlFileName Dateiname der auszuführenden SQL-Datei
	 * @param queryTypEnum EXECUTION (UPDATE or INSERT), QUERY (Mit Resultset)
	 * @param multiple_Statements  TRUE/FALSE - Gibt Auskunft darüber, ob in dem SQL-File mehrere oder nur ein SQL-Statement stehen
	 * @param isPreparedStatement
	 */
	public SqlExecutionObject(
			final String sqlFileName, final QueryTypEnum queryTypEnum, final boolean multiple_Statements, final boolean isPreparedStatement)
	{
		super();
		this.sqlFileName = sqlFileName;
		this.queryTypEnum = queryTypEnum;
		this.multiple_Statements = multiple_Statements;
		this.isPreparedStatement = isPreparedStatement;
		this.fileStoredInResources = false;
	}

	public SqlExecutionObject(String singleQuery, QueryTypEnum queryTypEnum, boolean isPreparedStatement)
	{
		this.singleQuery = singleQuery;
		this.queryTypEnum = queryTypEnum;
		this.multiple_Statements = false;
		this.isPreparedStatement = isPreparedStatement;
		this.isFileused = false;
	}

	public SqlExecutionObject(String[] multipleQuery, QueryTypEnum queryTypEnum, boolean isPreparedStatement)
	{
		this.multipleQuery = multipleQuery;
		this.queryTypEnum = queryTypEnum;
		this.multiple_Statements = true;
		this.isPreparedStatement = isPreparedStatement;
		this.isFileused = false;
	}
}
