package rentasad.library.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rentasad.library.db.enums.SSHLibraryEnum;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MySQLSSHConnectionTest
{
	private ConnectionInfo connectionInfo;
	private MySQLSSHConnection mysqlSSHConnection;

	@BeforeEach
	public void setUp() throws SQLException
	{
		connectionInfo = mock(ConnectionInfo.class);
		when(connectionInfo.getSshHost()).thenReturn("localhost");
		when(connectionInfo.getSshPort()).thenReturn(2222);
		when(connectionInfo.getSshUser()).thenReturn("root");
		when(connectionInfo.getSshKeyFilePath()).thenReturn(".ssh/id_rsa_ssh_server");
		when(connectionInfo.getRemoteHost()).thenReturn("mysql_db");
		when(connectionInfo.getRemotePort()).thenReturn(3306);
		when(connectionInfo.getDbName()).thenReturn("testdb");
		when(connectionInfo.getDbUser()).thenReturn("dbUser");
		when(connectionInfo.getDbPassword()).thenReturn("dbPassword");
		mysqlSSHConnection = new MySQLSSHConnection(connectionInfo);

	}

	@Test
	public void testConnectionSuccessful()
	{
		try
		{

			Connection connection = mysqlSSHConnection.getConnection();
			assertNotNull(connection, "The connection should not be null");
			assertFalse(connection.isClosed(), "The connection should be open");
		} catch (SQLException e)
		{
			fail("Exception should not be thrown: " + e.getMessage());
		}
	}

	@Test
	public void testConnectionFailureDueInvalidHost() throws SQLException, IOException
	{
		when(connectionInfo.getSshHost()).thenReturn("invalidhost");
		//		when(connectionInfo.getDbPassword()).thenReturn("invalidPW");
		when(connectionInfo.getSshKeyFilePath()).thenReturn(".ssh/id_rsaadssdd_ssh_server");

//		mysqlSSHConnection.setSSHLibrary(SSHLibraryEnum.SSHJ);

		assertThrows(SQLException.class, () -> {
			mysqlSSHConnection = new MySQLSSHConnection(connectionInfo);

		}, "Expected SqlException due to invalid SSH host");
	}

	@Test
	public void testConnectionFailureDueInvalidDbPassword() throws SQLException, IOException
	{
		when(connectionInfo.getDbPassword()).thenReturn("invalidPW");


		assertThrows(SQLException.class, () -> {
			mysqlSSHConnection = new MySQLSSHConnection(connectionInfo);

		}, "Expected SqlException due to invalid SSH Password");
	}

	@AfterEach
	public void tearDown() throws IOException
	{
		mysqlSSHConnection.close();
	}
}
