package rentasad.library.db;

import org.junit.jupiter.api.Test;
import rentasad.library.configFileTool.ConfigFileTool;
import rentasad.library.configFileTool.ConfigFileToolException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class is used to test the functionality of the {@link ConnectionInfo} class.
 * It contains a single test method, which tests the behavior of the {@code getConnectionInfoFromConfigMap} method.
 * The test method creates a mock configuration map with various connection parameters,
 * invokes the {@code getConnectionInfoFromConfigMap} method, and then verifies that
 * the returned {@link ConnectionInfo} object has the expected values for each parameter.
 * Assertions are used to verify the values of the individual properties of the {@link ConnectionInfo} object.
 * Additional assertions can be added if needed to verify other fields.
 */
class ConnectionInfoTest
{

	/**
	 * This method tests the behavior of the {@code getConnectionInfoFromConfigMap} method.
	 * It creates a mock configuration map with test connection parameters and invokes the {@code getConnectionInfoFromConfigMap} method.
	 * The method retrieves the values from the map and sets them in a ConnectionInfoBuilder object.
	 * Finally, it asserts that the returned ConnectionInfo object has the expected values for each parameter.
	 *
	 * @throws AssertionError if any assertion fails
	 */
	@Test
	public void testGetConnectionInfoFromConfigMap()
	{
		Map<String, String> mockConfigMap = Map.of("SSH_HOST", "example.com", "SSH_PORT", "22", "SSH_USER", "sshUser", "SSH_KEY_FILE_PATH", "/path/to/key", "MYSQL_HOST", "db.example.com",
												   "MYSQL_PORT", "3306", "MYSQL_USER", "dbUser", "MYSQL_PASSWORD", "dbPassword");

		ConnectionInfo connectionInfo = ConnectionInfo.getConnectionInfoFromConfigMap(mockConfigMap);

		assertNotNull(connectionInfo);
		assertEquals("example.com", connectionInfo.getSshHost());
		assertEquals(22, connectionInfo.getSshPort());
		assertEquals("sshUser", connectionInfo.getSshUser());
		assertEquals("/path/to/key", connectionInfo.getSshKeyFilePath());
		assertEquals("db.example.com", connectionInfo.getRemoteHost());
		assertEquals(3306, connectionInfo.getRemotePort());
		assertEquals("dbUser", connectionInfo.getDbUser());
		assertEquals("dbPassword", connectionInfo.getDbPassword());
		// Add more assertions if needed for other fields
	}

	/**
	 * This method tests the behavior of the {@code getConnectionInfoFromConfigMap} method.
	 * It reads a test configuration map from an INI file, invokes the {@code getConnectionInfoFromConfigMap} method,
	 * and then verifies that the returned {@link ConnectionInfo} object has the expected values for each parameter.
	 * Assertions are used to verify the values of the individual properties of the {@link ConnectionInfo} object.
	 *
	 * @throws IOException              if an I/O error occurs while reading the configuration file
	 * @throws ConfigFileToolException  if there is an error processing the configuration file
	 */
	@Test
	void testGetConnectionInfoFromConfigMapFromIniFile() throws IOException, ConfigFileToolException
	{
		Map<String, String> testConfig = ConfigFileTool.readConfigurationFromResources("testConfig.ini", "TESTCONFIG");
		ConnectionInfo connectionInfo = ConnectionInfo.getConnectionInfoFromConfigMap(testConfig);
		assertEquals(testConfig.get("MYSQL_DATABASE"), connectionInfo.getDbName());
		assertEquals(testConfig.get("SSH_HOST"), connectionInfo.getSshHost());
		assertEquals(Integer.valueOf(testConfig.get("SSH_PORT")), connectionInfo.getSshPort());
		assertEquals(testConfig.get("SSH_USER"), connectionInfo.getSshUser());
		assertEquals(testConfig.get("SSH_KEY_FILE_PATH"), connectionInfo.getSshKeyFilePath());
		assertEquals(testConfig.get("MYSQL_HOST"), connectionInfo.getRemoteHost());
		assertEquals(Integer.valueOf(testConfig.get("MYSQL_PORT")), connectionInfo.getRemotePort());
		assertEquals(testConfig.get("MYSQL_USER"), connectionInfo.getDbUser());
		assertEquals(testConfig.get("MYSQL_PASSWORD"), connectionInfo.getDbPassword());


	}
}