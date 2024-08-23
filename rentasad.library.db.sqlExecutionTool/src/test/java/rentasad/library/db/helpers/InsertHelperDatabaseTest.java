package rentasad.library.db.helpers;

import org.junit.jupiter.api.AfterEach;
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

public class InsertHelperDatabaseTest
{

	private static String CONFIG_FILE_NAME = "resources/config/TestDatabaseConfig.ini";
	private static String CONFIG_FILE_SECTION = "MYSQL_DATABASE_CONNECTION";
	private Connection mySqlConnection = null;

	@BeforeEach
	public void init() throws IOException, ConfigFileToolException, SQLException
	{
		if (this.mySqlConnection == null)
		{
			Map<String, String> configMap = ConfigFileTool.readConfiguration(CONFIG_FILE_NAME, CONFIG_FILE_SECTION);
			mySqlConnection = MYSQLConnection.dbConnect(configMap);
		}

	}

	@AfterEach
	void tearDown() throws SQLException
	{
		deleteTestItem(22L);
	}

	private void deleteTestItem(long id) throws SQLException
	{
		String query = "DELETE FROM test_objects WHERE order_number=10000100443520";
		try (Statement stmt = this.mySqlConnection.createStatement())
		{
			stmt.executeUpdate(query);
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

	@Test
	void fromResultSet() throws SQLException
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
				assertEquals(518505328L, testObject.getVs4InvoiceNumber());
				assertEquals(1100000016L, testObject.getVs4CustomerNumber());
				assertEquals(319123874L, testObject.getVs4OrderNumber());
				assertEquals(22L, testObject.getId());
				assertEquals(StatusEnum.NEW, testObject.getStatus());

			}
		}

	}

	@Test
	public void insertIntoDatabaseTest() throws SQLException
	{
		TestObject testObject = getTestObject();
		new InsertHelper<>(testObject).insertIntoDatabase(this.mySqlConnection);
		// CHECK INSERT RESULT
		String query = "SELECT * FROM test_objects WHERE id=23";
		try (Statement stmt = this.mySqlConnection.createStatement())
		{
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
			{
				assertEquals(testObject.getOrderNumber(), rs.getLong("order_number"));
				assertEquals(testObject.getManuallyProvided(), rs.getBoolean("manually_provided"));
				assertEquals(testObject.getVs4InvoiceNumber(), rs.getLong("vs4_invoice_number"));
				assertEquals(testObject.getVs4CustomerNumber(), rs.getLong("vs4_customer_number"));
				assertEquals(testObject.getVs4OrderNumber(), rs.getLong("vs4_order_number"));
				assertEquals(testObject.getId(), rs.getLong("id"));
				assertEquals(testObject.getStatus()
									   .name(), rs.getString("status"));

			}
		}

	}

	/**
	 * @return
	 */
	private static TestObject getTestObject()
	{
		TestObject to = new TestObject();
		to.setId(23L);
		to.setOrderNumber(10000100443520L);
		to.setStatus(StatusEnum.OLD);
		to.setManuallyProvided(true);
		to.setVs4CustomerNumber(1100000017L);
		to.setVs4InvoiceNumber(518505329L);
		to.setVs4OrderNumber(319123875L);
		return to;
	}
}
