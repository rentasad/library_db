package rentasad.library.db.sshConnections;

import rentasad.library.db.ConnectionInfo;

import java.sql.Connection;
import java.sql.SQLException;

public interface SSHConnection extends AutoCloseable {

	/**
	 * Establishes an SSH connection using the provided connection information.
	 *
	 * @param connectionInfo The details required to establish the SSH connection.
	 * @throws Exception If unable to establish the connection.
	 */
	void connect(ConnectionInfo connectionInfo) throws Exception;

	/**
	 * Retrieves a connection to the database through the established SSH tunnel.
	 *
	 * @return The database connection.
	 * @throws SQLException If the connection pool is not initialized or any SQL error occurs.
	 */
	Connection getDatabaseConnection() throws SQLException;

	/**
	 * Closes the SSH connection and any associated resources.
	 *
	 */
	@Override
	void close() ;
}
