package rentasad.library.db.sshConnections;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zaxxer.hikari.HikariDataSource;
import rentasad.library.db.ConnectionInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Represents a SSH connection using the JSch library.
 * Implements the SSHConnection interface.
 * Provides methods to connect to a remote server via SSH and establish a forwarding connection.
 * Also provides methods to get a database connection and close the SSH connection.
 */
public class JSchSSHConnection implements SSHConnection
{
	private Session sshSession;
	private HikariDataSource dataSource;

	@Override
	public void connect(ConnectionInfo connectionInfo) throws JSchException, SQLException
	{
		JSch jsch = new JSch();
		jsch.addIdentity(connectionInfo.getSshKeyFilePath());

		sshSession = jsch.getSession(connectionInfo.getSshUser(), connectionInfo.getSshHost(), connectionInfo.getSshPort());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(config);
		sshSession.connect();

		int assignedPort = sshSession.setPortForwardingL(0, connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());

		dataSource = HikariDataSourceHelper.getDatasource(assignedPort, connectionInfo);
	}

	@Override
	public Connection getDatabaseConnection() throws SQLException
	{
		if (dataSource == null) {
			throw new SQLException("Connection pool is not initialized.");
		}

		try {
			// Die Verbindung wird hier aufgebaut (SSH und MySQL).

			Connection sshConnection = dataSource.getConnection();
			if (sshConnection.isValid(1000)) {
				return sshConnection;
			}else
			{
				throw new SQLException("SSH connection failed: ");
			}

		} catch (SQLException e) {
			// Wenn der Fehler mit SSH zu tun hat, kannst du eine spezifischere Exception werfen.
			if (e.getMessage().contains("SSH") || e.getMessage().contains("Could not connect")) {
				throw new SQLException("SSH connection failed: " + e.getMessage(), e);
			}
			// Andernfalls die ursprüngliche SQLException weitergeben.
			throw e;
		}
	}

	@Override
	public void close() {
		if (dataSource != null) {
			if (dataSource.isRunning())
				dataSource.close();
		}
		if (sshSession != null && sshSession.isConnected()) {
			sshSession.disconnect();
		}
	}
}

