package org.gustini.library.db.sqlExecutionTool.objects;

/**
 * 
 * Gustini GmbH (2017)
 * Creation: 30.05.2017
 * Library
 * gustini.library.db.sqlExecutionTool.objects
 * 
 * @author Matthias Staud
 *
 *
 * Description:
 *
 */
public class SqlExecutionObject
{
    /**
     * @param sqlFileName Dateiname der auszuführenden SQL-Datei
     * @param queryTypEnum EXECUTION, QUERY (Mit Resultset)
     * @param multiple_Statements TRUE/FALSE - Gibt Auskunft darüber, ob in dem SQL-File mehrere oder nur ein SQL-Statement stehen
     */
    public SqlExecutionObject(
                              String sqlFileName,
                              QueryTypEnum queryTypEnum,
                              boolean multiple_Statements, 
                              boolean isPreparedStatement)
    {
        super();
        this.sqlFileName = sqlFileName;
        this.queryTypEnum = queryTypEnum; 
        this.multiple_Statements = multiple_Statements;
        this.isPreparedStatement = isPreparedStatement;
    }
    private final String sqlFileName;
    private final QueryTypEnum queryTypEnum;
    private final boolean multiple_Statements;
    private final boolean isPreparedStatement;
    private String sectionName;
    
    /**
     * @return the sqlFileName
     */
    public String getSqlFileName()
    {
        return sqlFileName;
    }
    /**
     * @return the queryTypEnum
     */
    public QueryTypEnum getQueryTypEnum()
    {
        return queryTypEnum;
    }
    /**
     * @return the multiple_Statements
     */
    public boolean isMultiple_Statements()
    {
        return multiple_Statements;
    }
    /**
     * @return the isPreparedStatement
     */
    public boolean isPreparedStatement()
    {
        return isPreparedStatement;
    }
    /**
     * @return the sectionName
     */
    public String getSectionName()
    {
        return sectionName;
    }
    /**
     * @param sectionName the sectionName to set
     */
    public void setSectionName(String sectionName)
    {
        this.sectionName = sectionName;
    }
    
    



}
