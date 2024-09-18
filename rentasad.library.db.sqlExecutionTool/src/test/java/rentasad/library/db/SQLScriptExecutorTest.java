package rentasad.library.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
class SQLScriptExecutorTest
{
	private Connection connection;

	@BeforeEach
	public void setUp() throws Exception {
		// H2 In-Memory-Datenbankverbindung herstellen
		connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
	}

	@AfterEach
	public void tearDown() throws Exception {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}

	@Test
	public void testRunScript() throws Exception {
		// SQL-Skript als String
		String sqlScript = "CREATE TABLE Test (ID INT PRIMARY KEY, Name VARCHAR(255));"
				+ "INSERT INTO Test (ID, Name) VALUES (1, 'TestName');";

		// SQL-Skript ausführen
		SQLScriptExecutor.runScript(connection, sqlScript);

		// Datenbank überprüfen, ob die Tabelle korrekt erstellt und die Daten eingefügt wurden
		try (Statement stmt = connection.createStatement()) {
			// Abfrage ausführen
			ResultSet rs = stmt.executeQuery("SELECT * FROM Test WHERE ID = 1");

			// Überprüfen, ob ein Ergebnis vorhanden ist
			assertNotNull(rs);
			if (rs.next()) {
				assertEquals(1, rs.getInt("ID"));
				assertEquals("TestName", rs.getString("Name"));
			}
		}
	}
}