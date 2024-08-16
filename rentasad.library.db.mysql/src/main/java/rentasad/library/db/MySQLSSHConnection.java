package rentasad.library.db;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mysql.cj.log.Slf4JLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Properties;

/**
 * Represents a connection to a MySQL database over SSH using HikariCP connection pooling.
 */
@Slf4j
public class MySQLSSHConnection implements AutoCloseable
{
	private  Session sshSession;
	private  HikariDataSource dataSource;

	public MySQLSSHConnection (ConnectionInfo connectionInfo)
	{
		try
		{
			connectWithSSH(connectionInfo);
		} catch (JSchException | SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	//	private static final Slf4JLogger log = LoggerFactory.getLogger(MySQLSSHConnection.class);

	/**
	 * Establishes an SSH session and connects to a remote MySQL database using HikariCP connection pooling.
	 *
	 * @param connectionInfo the ConnectionInfo object containing the SSH and database connection details
	 * @throws JSchException if an SSH session could not be established
	 * @throws SQLException if a database connection could not be established
	 */
	private void connectWithSSH(ConnectionInfo connectionInfo) throws JSchException, SQLException
	{
		if (sshSession == null || !sshSession.isConnected())
		{
			JSch jsch = new JSch();
			jsch.addIdentity(connectionInfo.getSshKeyFilePath());

			log.info("Establishing SSH session to {}:{}", connectionInfo.getSshHost(), connectionInfo.getSshPort());
			sshSession = jsch.getSession(connectionInfo.getSshUser(), connectionInfo.getSshHost(), connectionInfo.getSshPort());
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(config);
			sshSession.connect();

			int assignedPort = sshSession.setPortForwardingL(0, connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
			log.info("SSH session established. Port forwarding set from localhost:{} to {}:{}", assignedPort, connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());

			String jdbcUrl = String.format("jdbc:mysql://localhost:%d/%s", assignedPort, connectionInfo.getDbName());
			log.info("JDBC-URL: {}", jdbcUrl);
			HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setJdbcUrl(jdbcUrl);
			hikariConfig.setUsername(connectionInfo.getDbUser());
			hikariConfig.setPassword(connectionInfo.getDbPassword());
			hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
			hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
			hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

			dataSource = new HikariDataSource(hikariConfig);
			log.info("HikariCP connection pool created.");
		}
	}

	/**
	 * Retrieves a connection from the initialized connection pool.
	 *
	 * @return a {@link Connection} object representing a connection from the pool
	 * @throws SQLException if the connection pool is not initialized
	 */
	public Connection getConnection() throws SQLException
	{
		if (dataSource == null)
		{
			throw new SQLException("Connection pool is not initialized.");
		}
		return dataSource.getConnection();
	}

	/**
	 * Closes the data source and SSH session (if connected).
	 *
	 * If the data source is not null, it is closed by calling the 'close' method of the data source.
	 * The method then logs a message indicating that the HikariCP connection pool has been closed.
	 *
	 * If the SSH session is not null and is currently connected, it is disconnected by calling the 'disconnect' method of the SSH session.
	 * The method then logs a message indicating that the SSH session has been disconnected.
	 */
	public void close()
	{
		if (dataSource != null)
		{
			dataSource.close();
			log.info("HikariCP connection pool closed.");
		}
		if (sshSession != null && sshSession.isConnected())
		{
			sshSession.disconnect();
			log.info("SSH session disconnected.");
		}
	}
}

//	public static void main(String[] args)
//	{
//		try
//		{
//			ConnectionInfo connectionInfo = ConnectionInfo.builder()
//														  .sshHost("your.ssh.host")
//														  .sshPort(22)
//														  .sshUser("sshUser")
//														  .sshKeyFilePath("path/to/private/key")
//														  .remoteHost("127.0.0.1")
//														  .remotePort(3306)
//														  .dbUser("dbUser")
//														  .dbPassword("dbPassword")
//														  .dbName("dbName")
//														  .build();
//
//			connectWithSSH(connectionInfo);
//
//			Connection conn = getConnection();
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("select * from gustini_negativ LIMIT 1");
//			// Use the connection here
//
//		} catch (Exception e)
//		{
//			log.error("An error occurred", e);
//		} finally
//		{
//			try
//			{
//				close();
//			} catch (SQLException e)
//			{
//				log.error("An error occurred while closing the connection", e);
//			}
//		}
//	}
//}