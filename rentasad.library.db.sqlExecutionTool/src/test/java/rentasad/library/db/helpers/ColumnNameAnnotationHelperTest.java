package rentasad.library.db.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rentasad.library.configFileTool.ConfigFileTool;
import rentasad.library.configFileTool.ConfigFileToolException;
import rentasad.library.db.MYSQLConnection;
import rentasad.library.db.helpers.testClasses.ColumnNameAnnotationHelperTestObject;
import rentasad.library.db.helpers.testClasses.StatusEnum;
import rentasad.library.db.helpers.testClasses.TestObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnNameAnnotationHelperTest
{

private static String CONFIG_FILE_NAME = "resources/config/TestDatabaseConfig.ini";
private static String CONFIG_FILE_SECTION = "MYSQL_DATABASE_CONNECTION";
private Connection mySqlConnection = null;

@BeforeEach
	public void beforeEach() throws IOException, ConfigFileToolException, SQLException
{
		if (this.mySqlConnection == null)
		{
			Map<String, String> configMap = ConfigFileTool.readConfiguration(CONFIG_FILE_NAME, CONFIG_FILE_SECTION);
			mySqlConnection = MYSQLConnection.dbConnect(configMap);
		}

	}

	@Test
	public void testGetOrderColumnNameFromOrdersAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field columNameActual = ColumnNameAnnotationHelperTestObject.class.getDeclaredField("lastName");
		String actualColumName = ColumnNameAnnotationHelper.getAnnotationFromSingleField(columNameActual);
		String expectedColumnName = "last_name";
		assertEquals(expectedColumnName, actualColumName);
	}

	@Test void fromResultSet() throws SQLException
	{
		String query = "SELECT * FROM test_objects WHERE id=22";
		try (Statement stmt = this.mySqlConnection.createStatement())
		{
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
			{
				TestObject testObject = ColumnNameAnnotationHelper.fromResultSet(TestObject.class, rs);
				assertEquals(10000100443519L, testObject.getOrderNumber());
				assertEquals(false, testObject.getManuallyProvided());
				assertEquals(518505328, testObject.getVs4InvoiceNumber());
				assertEquals(1100000016, testObject.getVs4CustomerNumber());
				assertEquals(319123874, testObject.getVs4OrderNumber());
				assertEquals(22, testObject.getId());
				assertEquals(StatusEnum.NEW, testObject.getStatus());

			}
		}

	}
}
