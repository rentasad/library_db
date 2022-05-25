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
	 * @param sqlFileName         Dateiname der auszuführenden SQL-Datei
	 * @param queryTypEnum        EXECUTION, QUERY (Mit Resultset)
	 * @param multiple_Statements TRUE/FALSE - Gibt Auskunft darüber, ob in dem SQL-File mehrere oder nur ein SQL-Statement stehen
	 */

	public SqlExecutionObject(
			String sqlFileName, QueryTypEnum queryTypEnum, boolean multiple_Statements, boolean isPreparedStatement)
	{
		super();
		this.sqlFileName = sqlFileName;
		this.queryTypEnum = queryTypEnum;
		this.multiple_Statements = multiple_Statements;
		this.isPreparedStatement = isPreparedStatement;
		this.fileStoredInResources = false;
	}

	public SqlExecutionObject(String sqlFileName, QueryTypEnum queryTypEnum, boolean multiple_Statements, boolean isPreparedStatement, boolean fileStoredInResources)
	{
		this.sqlFileName = sqlFileName;
		this.queryTypEnum = queryTypEnum;
		this.multiple_Statements = multiple_Statements;
		this.isPreparedStatement = isPreparedStatement;
		this.fileStoredInResources = fileStoredInResources;
	}

	private final String sqlFileName;
	private final QueryTypEnum queryTypEnum;
	private final boolean multiple_Statements;
	private final boolean isPreparedStatement;
	private String sectionName;
	/**
	 * If the file is stored in "Resources", the file access must be different than in the normal file system
	 */
	private boolean fileStoredInResources;

}
