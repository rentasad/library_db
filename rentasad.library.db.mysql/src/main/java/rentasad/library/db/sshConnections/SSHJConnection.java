package rentasad.library.db.sshConnections;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import rentasad.library.db.ConnectionInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The `SSHJConnection` class is an implementation of the `SSHConnection` interface,
 * representing a connection to a MySQL database over SSH using the SSHJ library
 * and HikariCP connection pooling.
 * <p>
 * It provides methods for establishing a connection, retrieving a database connection,
 * and closing the connection.
 * <p>
 * Example usage:
 * <p>
 * ```java
 * ConnectionInfo connectionInfo = ConnectionInfo.builder()
 * .sshHost("localhost")
 * .sshPort(22)
 * .sshUser("user")
 * .sshKeyFilePath("/path/to/keyfile")
 * .remoteHost("remotehost")
 * .remotePort(3306)
 * .dbUser("dbuser")
 * .dbPassword("dbpassword")
 * .dbName("dbname")
 * .build();
 * <p>
 * SSHJConnection sshConnection = new SSHJConnection();
 * sshConnection.connect(connectionInfo);
 * <p>
 * Connection connection = sshConnection.getConnection();
 * // Use the connection for database operations...
 * <p>
 * sshConnection.close();
 * ```
 * <p>
 * Note: The `ConnectionInfo` class is a separate class used to store the connection
 * parameters. See the documentation for the `ConnectionInfo` class for more information.
 *
 * @see SSHConnection
 */
public class SSHJConnection implements SSHConnection
{

	private SSHClient sshClient;
	private HikariDataSource dataSource;
	private ServerSocket serverSocket;

	@Override
	public void connect(ConnectionInfo connectionInfo) throws IOException, SQLException
	{
		sshClient = new SSHClient();
		sshClient.addHostKeyVerifier(new PromiscuousVerifier());
		sshClient.connect(connectionInfo.getSshHost(), connectionInfo.getSshPort());
		sshClient.authPublickey(connectionInfo.getSshUser(), connectionInfo.getSshKeyFilePath());

		// Set up local port forwarding
		final Parameters params = new Parameters("localhost", 0, connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(params.getLocalHost(), params.getLocalPort()));

		// Start the port forwarder
		LocalPortForwarder forwarder = sshClient.newLocalPortForwarder(params, serverSocket);
		new Thread(() -> {
			try
			{
				forwarder.listen();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();

		int assignedPort = serverSocket.getLocalPort(); // Get the actual assigned local port

		dataSource = HikariDataSourceHelper.getDatasource(assignedPort, connectionInfo);
	}

	@Override
	public Connection getDatabaseConnection() throws SQLException
	{
		if (dataSource == null)
		{
			throw new SQLException("Connection pool is not initialized.");
		}
		return dataSource.getConnection();
	}

	@SneakyThrows
	@Override
	public void close()
	{
		if (dataSource != null)
		{
			dataSource.close();
		}
		if (sshClient != null)
		{
			sshClient.disconnect();
		}
		if (serverSocket != null && !serverSocket.isClosed())
		{
			serverSocket.close();
		}
	}
}