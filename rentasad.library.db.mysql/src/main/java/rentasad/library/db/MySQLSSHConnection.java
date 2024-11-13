package rentasad.library.db;

import lombok.extern.slf4j.Slf4j;
import rentasad.library.db.enums.SSHLibraryEnum;
import rentasad.library.db.sshConnections.JSchSSHConnection;
import rentasad.library.db.sshConnections.SSHConnection;
import rentasad.library.db.sshConnections.SSHJConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a connection to a MySQL database over SSH using HikariCP connection pooling.
 */
@Slf4j
public class MySQLSSHConnection implements AutoCloseable {
	private SSHLibraryEnum selectedLibrary = SSHLibraryEnum.DEFAULT;
	private final SSHConnection sshConnection;

	/**
	 * Represents a connection to a MySQL database over SSH using HikariCP connection pooling.
	 */
	public MySQLSSHConnection(ConnectionInfo connectionInfo, SSHLibraryEnum library) {
		try {
			setSSHLibrary(library);
			sshConnection = initializeSSHConnection();
			sshConnection.connect(connectionInfo);
		} catch (Exception e) {
			throw new RuntimeException("Failed to set up SSH connection", e);
		}
	}

	/**
	 * Represents a connection to a MySQL database over SSH.
	 *
	 * This class is responsible for establishing and managing an SSH connection
	 * to a MySQL database using HikariCP connection pooling. It provides methods
	 * to retrieve a database connection through the established SSH tunnel and
	 * to close the SSH connection and associated resources.
	 *
	 * @param connectionInfo The details required to establish the SSH connection.
	 *                       This includes SSH host, port, user, key file path,
	 *                       remote host, port, database user, password, and name.
	 */
	public MySQLSSHConnection(ConnectionInfo connectionInfo) throws SQLException
	{
		try {
			sshConnection = initializeSSHConnection();
			sshConnection.connect(connectionInfo);
		} catch (Exception e) {
			throw new SQLException("Failed to set up SSH connection", e);
		}
	}

	/**
	 * Initializes and returns an instance of SSHConnection based on the selected SSH library.
	 *
	 * @return The initialized SSHConnection instance.
	 * @throws IllegalStateException If the selected library is not supported.
	 */
	private SSHConnection initializeSSHConnection() {
		return switch (selectedLibrary)
		{
			case JSCH -> new JSchSSHConnection();
			case SSHJ -> new SSHJConnection();
			default -> throw new IllegalStateException("Unexpected value: " + selectedLibrary);
		};
	}

	/**
	 * Sets the SSH library to be used for establishing an SSH connection.
	 *
	 * @param library The SSH library to be used. Must be one of the values in the SSHLibraryEnum enumeration.
	 */
	public void setSSHLibrary(SSHLibraryEnum library) {
		selectedLibrary = library;
	}

	/**
	 * Retrieves a connection to the database through the established SSH tunnel.
	 *
	 * @return The database connection.
	 * @throws SQLException If the connection pool is not initialized or any SQL error occurs.
	 */
	public Connection getConnection() throws SQLException
	{
		Connection connection = sshConnection.getDatabaseConnection();
		return connection;
	}

	/**
	 * Closes the SSH connection and any associated resources.
	 *
	 * @throws IOException If an I/O error occurs while closing the connection.
	 */
	@Override
	public void close() throws IOException
	{
		if (sshConnection != null) {
			try
			{
				boolean closed = sshConnection.getDatabaseConnection()
											  .isClosed();
				sshConnection.close();
			} catch (SQLException e)
			{
				throw new RuntimeException(e);
			}

		}
	}
}





