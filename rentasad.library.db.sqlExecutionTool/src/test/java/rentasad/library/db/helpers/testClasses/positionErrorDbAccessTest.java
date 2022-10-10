package rentasad.library.db.helpers.testClasses;

import org.junit.jupiter.api.*;
import rentasad.library.configFileTool.ConfigFileTool;
import rentasad.library.configFileTool.ConfigFileToolException;
import rentasad.library.db.MYSQLConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionErrorDbAccessTest
{

	private Connection dbConnection = null;
	private PositionErrorDbAccess positionErrorDbAccess;
	private static String CONFIG_FILE_NAME = "resources/config/TestDatabaseConfig.ini";
	private static String CONFIG_FILE_SECTION = "MYSQL_DATABASE_CONNECTION";

	@BeforeEach
	public void beforeEach() throws IOException, ConfigFileToolException, SQLException
	{
		if (this.dbConnection ==
			null)
		{
			Map<String, String> configMap = ConfigFileTool.readConfiguration(CONFIG_FILE_NAME, CONFIG_FILE_SECTION);
			this.dbConnection = MYSQLConnection.dbConnect(configMap);
			positionErrorDbAccess = new PositionErrorDbAccess(dbConnection);
		}
		setupDeletion();

	}



	@AfterEach
	public void cleanUp() throws SQLException
	{
		if (dbConnection != null)
		{
			dbConnection.close();
			dbConnection = null;
		}
	}


	public void setupDeletion() throws SQLException
	{
		String query = "DELETE FROM position_errors;";
		try (PreparedStatement ps = this.dbConnection.prepareStatement(query))
		{
			ps.executeUpdate();
		}
	}

	@AfterEach
	public void tearDown() throws SQLException
	{
		String query = "DELETE FROM position_errors;";
		try (PreparedStatement ps = dbConnection.prepareStatement(query))
		{
			ps.executeUpdate();
		}
	}



	@Test
	public void addOne() throws SQLException
	{
		PositionError originalPositionError = new PositionError();
		originalPositionError.setPositionId(3L);
		originalPositionError.setOrderId(-2047L);
		originalPositionError.setErrorEnum(PositionErrorTestEnum.WRONG_VS4_SKU);

		positionErrorDbAccess.add(originalPositionError);
		System.out.println("STOP");
//		List<PositionError> positionErrors = positionErrorDbAccess.getClass();
//		assertEquals(1, positionErrors.size());
//		PositionError actualPositionError = positionErrors.get(0);
//		assertEquals(originalPositionError, actualPositionError);
	}


}