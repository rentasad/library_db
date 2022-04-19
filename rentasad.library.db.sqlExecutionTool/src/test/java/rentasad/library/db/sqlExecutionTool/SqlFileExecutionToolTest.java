package rentasad.library.db.sqlExecutionTool;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SqlFileExecutionToolTest
{

	@Test void getSqlQueriesFromSqlFileTwoQueriesTwoSemikolons()
	{
		String testSql = "SELECT * FROM HALLO;\r\n" + "UPDATE TABELLE SET FELD=5; \r\n";
		String[] queries = SqlFileExecutionTool.getSqlQueriesFromSqlString(testSql);
		assertEquals(2, queries.length);
	}

	@Test void getSqlQueriesFromSqlFileTwoQueriesOneSemikolons()
	{
		String testSql = "SELECT * FROM HALLO;\r\n" + "UPDATE TABELLE SET FELD=5  \r\n";
		String[] queries = SqlFileExecutionTool.getSqlQueriesFromSqlString(testSql);
		assertEquals(2, queries.length);
	}

	@Test void getQueryFromSqlFileInResources() throws IOException
	{
		String actualQuery = SqlFileExecutionTool.getQueryFromSqlFileInResources("sql/query.sql");
		String expected = "SELECT * FROM TABLE;";
		assertEquals(actualQuery, expected);
	}
}