package rentasad.library.db;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Properties;

public class MySQLSSHConnection {
	private static Session sshSession;
	private static HikariDataSource dataSource;
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);
	public static void connectWithSSH(ConnectionInfo connectionInfo) throws JSchException, SQLException {
		if (sshSession == null || !sshSession.isConnected()) {
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

	public static Connection getConnection() throws SQLException {
		if (dataSource == null) {
			throw new SQLException("Connection pool is not initialized.");
		}
		return dataSource.getConnection();
	}

	public static void close() throws SQLException {
		if (dataSource != null) {
			dataSource.close();
			log.info("HikariCP connection pool closed.");
		}
		if (sshSession != null && sshSession.isConnected()) {
			sshSession.disconnect();
			log.info("SSH session disconnected.");
		}
	}

	public static void main(String[] args) {
		try {
			ConnectionInfo connectionInfo = ConnectionInfo.builder()
														  .sshHost("your.ssh.host")
														  .sshPort(22)
														  .sshUser("sshUser")
														  .sshKeyFilePath("path/to/private/key")
														  .remoteHost("127.0.0.1")
														  .remotePort(3306)
														  .dbUser("dbUser")
														  .dbPassword("dbPassword")
														  .dbName("dbName")
														  .build();

			connectWithSSH(connectionInfo);

			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeQuery("select * from gustini_negativ LIMIT 1");
			// Use the connection here

		} catch (Exception e) {
			log.error("An error occurred", e);
		} finally {
			try {
				close();
			} catch (SQLException e) {
				log.error("An error occurred while closing the connection", e);
			}
		}
	}
}