package rentasad.library.db;

import java.io.StringReader;
import java.sql.Connection;
import org.apache.ibatis.jdbc.ScriptRunner;

public class SQLScriptExecutor {

	// Statische Methode zum Ausführen eines SQL-Skripts als String
	public static void runScript(Connection connection, String sqlScript) {
		try {
			// ScriptRunner verwenden, um das SQL-Skript auszuführen
			ScriptRunner scriptRunner = new ScriptRunner(connection);

//			// Optional: Logging deaktivieren, falls es nicht benötigt wird
//			scriptRunner.setLogWriter(null); // Deaktiviert den Log-Ausgabe
//			scriptRunner.setErrorLogWriter(null); // Deaktiviert den Fehlerlog

			// Das SQL-Skript aus einem StringReader ausführen
			try (StringReader reader = new StringReader(sqlScript)) {
				scriptRunner.runScript(reader);
				System.out.println("SQL script executed successfully!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error executing SQL script", e);
		}
	}
}
