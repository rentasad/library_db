package rentasad.library.db.sqlExecutionTool;

import rentasad.library.basicTools.dateTool.DateTools;
import rentasad.library.db.QueryFunctions;
import rentasad.library.db.dataObjects.PreparedDataTypesEnum;
import rentasad.library.db.sqlExecutionTool.objects.QueryTypEnum;
import rentasad.library.db.sqlExecutionTool.objects.SqlExecutionObject;
import rentasad.library.tools.fileOperator.FileOperator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gustini GmbH (2017)
 * Creation: 30.05.2017
 * Library
 * gustini.library.db.sqlExecutionTool
 *
 * @author Matthias Staud
 * <p>
 * <p>
 * Description:
 */
public class SqlFileExecutionTool
{
	private final Map<String, Map<String, String>> configMapMap;
	private Map<String, String> configMapGeneralSettings;
	private List<SqlExecutionObject> sqlExecutionObjectList;
	private List<String> sectionNameList;
	private Map<String, SqlExecutionObject> sqlExecutionObjectMap;
	public final static String CONFIG_PARAM_SQL_FILENAME = "SQL_FILENAME";
	public final static String CONFIG_PARAM_QUERY_TYP = "QUERY_TYP";
	public final static String CONFIG_PARAM_MULTIPLE_STATEMENTS = "MULTIPLE_STATEMENTS";
	public final static String CONFIG_PARAM_IS_PREPARED_STATEMENT = "IS_PREPARED_STATEMENT";

	private final String[] paramsToCheck = { CONFIG_PARAM_SQL_FILENAME, CONFIG_PARAM_QUERY_TYP, CONFIG_PARAM_MULTIPLE_STATEMENTS, CONFIG_PARAM_IS_PREPARED_STATEMENT
	};

	// /**
	// *
	// * @param configFile
	// * @throws ConfigFileToolException
	// */
	// public SqlFileExecutionTool(
	// File configFile)
	// throws ConfigFileToolException
	// {
	// this.configMapMap = ConfigFileTool.readIniFileWithAllSections(configFile.getAbsolutePath());
	// initConfig();
	// }

	/**
	 * @param configMapMap NEEDED GENERAL_SETTINGS;
	 * @throws SqlExecutionToolException sql failure
	 */
	public SqlFileExecutionTool(Map<String, Map<String, String>> configMapMap) throws SqlExecutionToolException
	{
		this.configMapMap = configMapMap;
		initConfig();
	}

	private void initConfig() throws SqlExecutionToolException
	{
		this.configMapGeneralSettings = this.configMapMap.get("GENERAL_SETTINGS");
		this.sectionNameList = getSqlExecutionConfigIniSectionsFromConfig();
		this.sqlExecutionObjectMap = getSqlExecutionObjectMap(sectionNameList);
		this.sqlExecutionObjectList = new ArrayList<>(this.sqlExecutionObjectMap.values());

	}

	/**
	 * Description:
	 *
	 * @param seo
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void executeExecutionQuery(SqlExecutionObject seo, Connection con) throws IOException
	{
		/**
		 * Check if SqlExecutionObject are valid for execution
		 */
		if (seo.isPreparedStatement())
		{
			throw new IllegalArgumentException("Can't execute Prepeared Statement in this context!");
		}
		else if (seo.getQueryTypEnum() == QueryTypEnum.QUERY)
		{
			throw new IllegalArgumentException("Can't execute ResultSetQuery in this context!");
		}

		if (seo.isFileused())
		{

			String sqlFileName = seo.getSqlFileName();
			if (seo.isMultiple_Statements())
			{
				String[] queries = getQueriesFromSqlExecutionObject(seo);
				for (String query : queries)
				{
					QueryFunctions.executeUpdateQuery(con, query);
				}
			}
			else
			{
				String query = getQueryFromSqlExecutionObject(seo);
				QueryFunctions.executeUpdateQuery(con, query);
			}
		}
		else
		{
			if (seo.isMultiple_Statements())
			{
				for (String query : seo.getMultipleQuery())
				{
					QueryFunctions.executeUpdateQuery(con, query);
				}
			}
			else
			{
				QueryFunctions.executeUpdateQuery(con, seo.getSingleQuery());
			}
		}

	}

	/**
	 * Returns Queries of SqlExecutionObject considering whether SQLFile (JAR) is stored in Resources or local file system.
	 *
	 * @param seo
	 * @return
	 * @throws IOException
	 */
	private static String[] getQueriesFromSqlExecutionObject(final SqlExecutionObject seo) throws IOException
	{
		String[] queries;
		String sqlFileName = seo.getSqlFileName();
		if (seo.isFileused())
		{
			if (seo.isFileStoredInResources())
			{
				queries = getSqlQueriesFromSqlFileInResources(sqlFileName);
			}
			else
			{
				queries = getSqlQueriesFromSqlFile(sqlFileName);
			}
		}
		else
		{
			queries = seo.getMultipleQuery();
		}
		return queries;
	}

	/**
	 * Returns Query of SqlExecutionObject considering whether SQLFile (JAR) is stored in Resources or local file system.
	 *
	 * @param seo
	 * @return
	 * @throws IOException
	 */
	private static String getQueryFromSqlExecutionObject(final SqlExecutionObject seo) throws IOException
	{
		String query;
		String sqlFileName = seo.getSqlFileName();
		if (seo.isFileused())
		{
			if (seo.isFileStoredInResources())
			{
				query = getQueryFromSqlFileInResources(sqlFileName);
			}
			else
			{
				query = getQueryFromSqlFile(sqlFileName);
			}
		}
		else
		{
			query = seo.getSingleQuery();
		}

		return query;
	}

	/**
	 * Description:
	 *
	 * @param seo
	 * @return Creation: 30.05.2017 by mst
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void executeExecutionQueryWithStringReplace(SqlExecutionObject seo, Connection con, String replaceRegex, String replaceValue) throws SQLException, IOException
	{
		/**
		 * Check if SqlExecutionObject are valid for execution
		 */
		if (seo.isPreparedStatement())
		{
			throw new IllegalArgumentException("Can't execute PrepearedStatement in this context!");
		}
		else if (seo.getQueryTypEnum() == QueryTypEnum.QUERY)
		{
			throw new IllegalArgumentException("Can't execute ResultSetQuery in this context!");
		}
		if (seo.isFileused())
		{
			if (seo.isMultiple_Statements())
			{
				String[] queries = getQueriesFromSqlExecutionObject(seo);
				for (String query : queries)
				{
					String replacedQuery;
					replacedQuery = query.replaceAll(replaceRegex, replaceValue);

					QueryFunctions.executeUpdateQuery(con, replacedQuery);
				}
			}
			else
			{
				String query = getQueryFromSqlExecutionObject(seo);
				String replacedQuery = query.replaceAll(replaceRegex, replaceValue);
				QueryFunctions.executeUpdateQuery(con, replacedQuery);
			}
		}
		else
		{
			if (seo.isMultiple_Statements())
			{
				String[] queries = seo.getMultipleQuery();
				for (String query : queries)
				{
					String replacedQuery;
					replacedQuery = query.replaceAll(replaceRegex, replaceValue);
					QueryFunctions.executeUpdateQuery(con, replacedQuery);
				}
			}
			else
			{
				String query = seo.getSingleQuery();
				String replacedQuery = query.replaceAll(replaceRegex, replaceValue);
				QueryFunctions.executeUpdateQuery(con, replacedQuery);
			}
		}

	}

	/**
	 * Description: generate List with SqlExecutionObject from ConfigMapMap incl. validation
	 *
	 * @param sqlConfigIniSectionsList
	 * @return
	 * @throws SqlExecutionToolException
	 */
	private Map<String, SqlExecutionObject> getSqlExecutionObjectMap(List<String> sqlConfigIniSectionsList) throws SqlExecutionToolException
	{

		Map<String, SqlExecutionObject> map = new HashMap<>();
		for (String sectionName : sqlConfigIniSectionsList)
		{
			if (isSqlExecConfigMapValid(sectionName))
			{
				Map<String, String> sqlMap = this.configMapMap.get(sectionName);

				String sqlFileName = sqlMap.get(CONFIG_PARAM_SQL_FILENAME);
				QueryTypEnum queryTypEnum = QueryTypEnum.valueOf(sqlMap.get(CONFIG_PARAM_QUERY_TYP));
				boolean multiple_Statements = Boolean.parseBoolean(sqlMap.get(CONFIG_PARAM_MULTIPLE_STATEMENTS));
				boolean isPreparedStatement = Boolean.parseBoolean(sqlMap.get(CONFIG_PARAM_IS_PREPARED_STATEMENT));
				SqlExecutionObject executionObject = new SqlExecutionObject(sqlFileName, queryTypEnum, multiple_Statements, isPreparedStatement);
				executionObject.setSectionName(sectionName);
				map.put(sectionName, executionObject);
			}
		}
		return map;
	}

	/**
	 * Description:
	 *
	 * @param sqlConfigIniSectionsList
	 * @return
	 * @throws SqlExecutionToolException
	 */
	protected List<SqlExecutionObject> getSqlExecutionObjectList(List<String> sqlConfigIniSectionsList) throws SqlExecutionToolException
	{
		Map<String, SqlExecutionObject> map = getSqlExecutionObjectMap(sqlConfigIniSectionsList);
		List<SqlExecutionObject> list = new ArrayList<>(map.values());
		return list;

	}

	/**
	 * Description: Check Validity of Given SQLConfigSections
	 *
	 * @return Creation: 30.05.2017 by mst
	 */
	private boolean isSqlExecConfigMapValid(String sectionName) throws SqlExecutionToolException
	{
		boolean isValid = true;
		if (this.configMapMap.containsKey(sectionName))
		{
			Map<String, String> sqlMap = this.configMapMap.get(sectionName);
			for (String paramName : this.paramsToCheck)
			{
				if (!sqlMap.containsKey(paramName))
				{
					isValid = false;
					throw new SqlExecutionToolException("PARAM not found: " + paramName + " in " + sectionName);
				}
			}
			File sqlFile = new File(sqlMap.get(CONFIG_PARAM_SQL_FILENAME));
			if (!sqlFile.exists())
			{
				throw new SqlExecutionToolException("SQL File not found: " + sqlFile.getAbsolutePath());
			}

		}
		else
		{
			throw new SqlExecutionToolException("Section not found: " + sectionName);
		}

		return isValid;
	}

	/**
	 * Description:Read SQLConfigSections-Param-Value from config map
	 *
	 * @throws SqlExecutionToolException
	 */
	private List<String> getSqlExecutionConfigIniSectionsFromConfig() throws SqlExecutionToolException
	{
		List<String> list = new ArrayList<>();
		if (configMapGeneralSettings.containsKey("SQLConfigSections"))
		{
			String[] sqlConfigSections = configMapGeneralSettings.get("SQLConfigSections").split(",");
			for (String sqlConfigSection : sqlConfigSections)
			{
				list.add(sqlConfigSection.trim());
			}

			return list;

		}
		else
		{
			throw new SqlExecutionToolException("Missed Parameter:SQLConfigSections ");
		}
	}

	/**
	 * @return the sqlExecutionObjectList
	 */
	public List<SqlExecutionObject> getSqlExecutionObjectList()
	{
		return sqlExecutionObjectList;
	}

	/**
	 * Description: get single SQL-Query from File
	 * <p>
	 * if more than one sql statement are in file, the execution will fail
	 *
	 * @param sqlFileName
	 * @return
	 * @throws IOException Creation: 09.05.2017 by mst
	 */
	public static String getQueryFromSqlFile(String sqlFileName) throws IOException
	{
		return FileOperator.readFile(sqlFileName);
	}

	/**
	 * Description: get single SQL-Query from File in Resources
	 * <p>
	 * if more than one sql statement are in file, the execution will fail
	 *
	 * @param sqlFileName
	 * @return
	 * @throws IOException Creation: 09.05.2017 by mst
	 */
	public static String getQueryFromSqlFileInResources(String sqlFileName) throws IOException
	{
		InputStream inputStream = SqlFileExecutionTool.class.getClassLoader().getResourceAsStream(sqlFileName);
		if (inputStream == null)
		{
			throw new IllegalArgumentException(sqlFileName + " is not found");
		}
		return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")).trim();
	}

	/**
	 * Description: Get a StringArray with more then one SQL Queries from a sql file
	 *
	 * @param sqlFileName
	 * @return
	 * @throws IOException Creation: 17.05.2017 by mst
	 */
	public static String[] getSqlQueriesFromSqlFileInResources(String sqlFileName) throws IOException
	{
		String sqlQueryValue = getQueryFromSqlFileInResources(sqlFileName);
		/*
		 * Aufsplitten der SQL-Statements in mehrere Strings
		 */
		String[] queries = getSqlQueriesFromSqlString(sqlQueryValue);
		return queries;
	}

	/**
	 * Description: Get a StringArray with more then one SQL Queries from a sql file
	 *
	 * @param sqlFileName
	 * @return
	 * @throws IOException Creation: 17.05.2017 by mst
	 */
	public static String[] getSqlQueriesFromSqlFile(String sqlFileName) throws IOException
	{
		String sqlQueryValue = FileOperator.readFile(sqlFileName);
		/*
		 * Aufsplitten der SQL-Statements in mehrere Strings
		 */
		String[] queries = getSqlQueriesFromSqlString(sqlQueryValue);
		return queries;
	}

	/**
	 * Remove the last element of a string array
	 *
	 * @param stringArray
	 * @return
	 */
	private static String[] removeLastElementFromStringArray(final String[] stringArray)
	{
		return Arrays.copyOf(stringArray, stringArray.length - 1);
	}

	/**
	 * Description:Splittet einen SQL-String in einzelne SQL-Strings durch den Trenner ";"
	 *
	 * @param multipleSqlString
	 * @return Creation: 11.09.2019 by mst
	 */
	public static String[] getSqlQueriesFromSqlString(String multipleSqlString)
	{
		/*
		 * Aufsplitten der SQL-Statements in mehrere Strings
		 */
		final String DEFAULT_DELIMITER = ";";
		String[] queries = multipleSqlString.split(DEFAULT_DELIMITER);
		// last Element is to short for an valid sql query
		String lastElementFromStringArray = queries[queries.length - 1];
		if (lastElementFromStringArray.length() <= 8)
		{
			queries = removeLastElementFromStringArray(queries);
		}
		return queries;

	}

	/**
	 * @return the configMapGeneralSettings
	 */
	public Map<String, String> getConfigMapGeneralSettings()
	{
		return configMapGeneralSettings;
	}

	/**
	 * Description:
	 *
	 * @param seo
	 * @param con
	 * @param preparedArguments
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public ResultSet getResultSetQueryWithPreparedStatements(
			SqlExecutionObject seo, Connection con, Object[] preparedArguments) throws IOException, SQLException
	{
		PreparedStatement ps = getFilledPreparedStatement(seo, con, preparedArguments);
		return ps.executeQuery();

	}

	/**
	 * Description: return ResultSet from Single Query from SqlExecutionObject
	 *
	 * @param seo SqlExecutionObject must have the follogwin Properties:
	 *            isPreparedStatement = false;
	 *            isMultiple_Statements = false;
	 *            QueryTypEnum = QUERIES
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws IOException  Creation: 30.05.2017 by mst
	 */
	public ResultSet getResultSetFromSqlExecutionObject(SqlExecutionObject seo, Connection con) throws SQLException, IOException
	{
		/**
		 * Check if SqlExecutionObject are valid for execution
		 */
		if (seo.isPreparedStatement())
		{
			throw new IllegalArgumentException("Can't execute Prepeared Statement in this context!");
		}
		else if (seo.getQueryTypEnum() == QueryTypEnum.EXECUTION)
		{
			throw new IllegalArgumentException("Can't execute EXECUTION-Query in this context!");
		}
		if (!seo.isMultiple_Statements())
		{
			String query = getQueryFromSqlExecutionObject(seo);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			// stmt.close();
			return rs;
		}
		else
		{

			throw new IllegalArgumentException("This method need an SqlExecutionObject with Single Query - multiple Queries can't receive a result Set!");
		}

	}

	/**
	 * Description: Execute Query without ResultSet
	 * <p>
	 * Creation: 30.05.2017 by mst
	 */
	public static boolean executeQueryWithPreparedArguments(
			SqlExecutionObject seo, Connection con, Object[] preparedArguments) throws IOException, SQLException
	{
		PreparedStatement ps = getFilledPreparedStatement(seo, con, preparedArguments);

		boolean hasResult = ps.execute();
		return !hasResult;
	}

	/**
	 * Description: Execute Query without ResultSet
	 * <p>
	 * Creation: 30.05.2017 by mst
	 */
	public static boolean executeQueryWithPreparedArguments(
			SqlExecutionObject seo, String sqlQuery, Connection con, Object[] preparedArguments) throws IOException, SQLException
	{
		PreparedStatement ps = getFilledPreparedStatement(seo, sqlQuery, con, preparedArguments);

		boolean hasResult = ps.execute();
		return !hasResult;
	}

	/**
	 * Description:
	 *
	 * @param seo
	 * @param con
	 * @param preparedArguments
	 * @return
	 * @throws UnknownEnumException
	 * @throws IOException
	 * @throws SQLException         Creation: 19.06.2017 by mst
	 */
	public static PreparedStatement getFilledPreparedStatement(
			SqlExecutionObject seo, Connection con, Object[] preparedArguments) throws IOException, SQLException
	{
		if (seo.isPreparedStatement())
		{
			if (!seo.isMultiple_Statements())
			{
				String sqlQuery;
				if (seo.isFileused())
				{
					sqlQuery = SqlFileExecutionTool.getQueryFromSqlExecutionObject(seo);
				}
				else
				{
					sqlQuery = seo.getSingleQuery();
				}
				PreparedStatement ps = con.prepareStatement(sqlQuery);
				// pi = ParameterIndex
				int pi = 1;
				for (Object pa : preparedArguments)
				{
					PreparedDataTypesEnum psTyp = QueryFunctions.getPreparedDataTypFromObject(pa);
					switch (psTyp)
					{
					case STRING:
						ps.setString(pi++, (String) pa);
						break;
					case DATE_SQL:
						ps.setDate(pi++, (Date) pa);
						break;
					case DATE_UTIL:
						Date sqlDate = DateTools.getSQLDateFromDate((java.util.Date) pa);
						ps.setDate(pi++, sqlDate);
						break;
					case DOUBLE:
						ps.setDouble(pi++, (Double) pa);
						break;
					case FLOAT:
						ps.setFloat(pi++, (Float) pa);
						break;
					case INTEGER:
						ps.setInt(pi++, (Integer) pa);
						break;
					case LONG:
						ps.setLong(pi++, (Long) pa);
						break;
					default:
						throw new IllegalArgumentException(psTyp.name());
					}
				}
				return ps;

			}
			else
			{
				throw new IllegalArgumentException("SqlExecutionObject darf kein MultipleQuery sein wenn es Prepared ist! ");
			}
		}
		else
		{
			throw new IllegalArgumentException("SqlExecutionObject muss Prepared sein, damit es richtig verarbeitet werden kann! ");
		}
	}

	/**
	 * Description:
	 *
	 * @param seo
	 * @param sqlQuery
	 * @param con
	 * @param preparedArguments
	 * @return
	 * @throws IOException
	 * @throws SQLException         Creation: 19.06.2017 by mst
	 */
	public static PreparedStatement getFilledPreparedStatement(
			SqlExecutionObject seo, String sqlQuery, Connection con, Object[] preparedArguments) throws IOException, SQLException
	{
		if (seo.isPreparedStatement())
		{
			if (!seo.isMultiple_Statements())
			{
				PreparedStatement ps = con.prepareStatement(sqlQuery);
				// pi = ParameterIndex
				int pi = 1;
				for (Object pa : preparedArguments)
				{
					PreparedDataTypesEnum psTyp = QueryFunctions.getPreparedDataTypFromObject(pa);
					switch (psTyp)
					{
					case STRING:
						ps.setString(pi++, (String) pa);
						break;
					case DATE_SQL:
						ps.setDate(pi++, (Date) pa);
						break;
					case DATE_UTIL:
						Date sqlDate = DateTools.getSQLDateFromDate((java.util.Date) pa);
						ps.setDate(pi++, sqlDate);
						break;
					case DOUBLE:
						ps.setDouble(pi++, (Double) pa);
						break;
					case FLOAT:
						ps.setFloat(pi++, (Float) pa);
						break;
					case INTEGER:
						ps.setInt(pi++, (Integer) pa);
						break;
					case LONG:
						ps.setLong(pi++, (Long) pa);
						break;
					default:
						throw new IllegalArgumentException(psTyp.name());
					}
				}
				return ps;

			}
			else
			{
				throw new IllegalArgumentException("SqlExecutionObject darf kein MultipleQuery sein wenn es Prepared ist! ");
			}
		}
		else
		{
			throw new IllegalArgumentException("SqlExecutionObject muss Prepared sein, damit es richtig verarbeitet werden kann! ");
		}
	}

	/**
	 * @return the sqlExecutionObjectMap
	 */
	public Map<String, SqlExecutionObject> getSqlExecutionObjectMap()
	{
		return sqlExecutionObjectMap;
	}

	/**
	 * @return the sectionNameList
	 */
	public List<String> getSectionNameList()
	{
		return sectionNameList;
	}

	/**
	 * @param sectionNameList the sectionNameList to set
	 */
	public void setSectionNameList(List<String> sectionNameList)
	{
		this.sectionNameList = sectionNameList;
	}

}
