package rentasad.library.db.dataObjects;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class ODBCConnection
{

	private static ODBCConnection odbcConnection;
	private String dsnString;
	private Connection connection;
	private String username = null;
	private String password = null;
		

	/**
	 * @param dsnString
	 * @throws SQLException 
	 */
	public ODBCConnection(String dsnString) throws SQLException
	{
		super();
		this.dsnString = dsnString;
		// this.aktivateDriverManagerLogWriterToSystemOut();
		this.connect();
	}
	public static ODBCConnection createInstance(String dsnString) throws SQLException
	{
		if (ODBCConnection.odbcConnection == null)
		{
			ODBCConnection.odbcConnection = new ODBCConnection(dsnString);
//			ODBCConnection.odbcConnection.connect();
		}
		return ODBCConnection.odbcConnection;
	}
	
	public static ODBCConnection getInstance()
	{
		return ODBCConnection.odbcConnection;
	}
	public ODBCConnection(String dsnString, String username, String password) throws SQLException
	{
		super();
		this.dsnString = dsnString;
		this.username = username;
		this.password = password;

//		 this.aktivateDriverManagerLogWriterToSystemOut();
		this.connect();
	}

	public void connect() throws SQLException
	{


			// DriverManager.setLogWriter( new PrintWriter(System.out) );
			if (this.password != null || this.username != null)
			{
				this.connection = DriverManager.getConnection(this.dsnString,
						this.username, this.password);
			} else
			{
				this.connection = DriverManager.getConnection(this.dsnString);
			}

	}

	public String getDsnString()
	{
		return dsnString;
	}

	public void setDsnString(String dsnString)
	{
		this.dsnString = dsnString;
	}


	public void listInstalledJDBCDriver()
	{
		/**
		 * Auflisten der Installierten Datenbank-Treiber
		 */
		for (Enumeration<Driver> e = DriverManager.getDrivers(); e
				.hasMoreElements();)
		{
			System.out.println(e.nextElement().getClass().getName());
		}
	}

	public void testJDBCDriverName(String driverNameString) throws ClassNotFoundException
	{
			// Example: "sun.jdbc.odbc.JdbcOdbcDriver"
			Class.forName(driverNameString);
	}
	public void aktivateDriverManagerLogWriterToSystemOut()
	{
		DriverManager.setLogWriter(new PrintWriter(System.out));
	}
	
	
//	public ResultSet runSQLStatement(String sqlStatement) throws SQLException
//	{
//		ResultSet rs = null;
//
//			Statement stmt = this.connection.createStatement();
//			rs = stmt.executeQuery(sqlStatement);
//
//			while (rs.next())
//			{
//				System.out.println(rs.getString("Nummer"));
//			}
//			stmt.close();
//
//
//		return (ResultSet) rs;
//	}
	public void disconnect() throws SQLException
	{

			this.connection.close();

	}
	/**
	 * 
	 * @return java.sql.Connection;
	 */
	public Connection getConnection()
	{

		return connection;
	}

	public boolean isConnected() throws SQLException
	{
		boolean isconnected =this.connection.isClosed();
		
		return !isconnected;
	}
	
}
