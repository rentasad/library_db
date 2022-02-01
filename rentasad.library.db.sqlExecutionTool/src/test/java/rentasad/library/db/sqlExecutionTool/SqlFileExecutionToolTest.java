package rentasad.library.db.sqlExecutionTool;

import org.junit.jupiter.api.Test;

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
}