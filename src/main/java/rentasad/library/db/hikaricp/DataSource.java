package rentasad.library.db.hikaricp;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource
{

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static
    {
        config.setJdbcUrl("jdbc:extendedsystems:advantage://192.168.111.30:6262/vs4/VS/DG/VC2/;LockType=proprietary;CharType=OEM;TableType=cdx");
        // config.setUsername( "database_username" );
        // config.setPassword( "database_password" );
        config.addDataSourceProperty("dataSourceClassName", "com.extendedsystems.jdbc.advantage.ADSDriver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private DataSource()
    {
    }

    public static Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }
    
    public static Connection getAdsConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
    {
        Properties props = new Properties();
        Driver driver =  (Driver) Class.forName("com.extendedsystems.jdbc.advantage.ADSDriver").newInstance();
        props.setProperty( "dataSourceClassName" , "com.extendedsystems.jdbc.advantage.ADSDriver" );
        HikariConfig hikariConfig = new HikariConfig(props);
        hikariConfig.setDriverClassName("com.extendedsystems.jdbc.advantage.ADSDriver");
        hikariConfig.setJdbcUrl("jdbc:extendedsystems:advantage://192.168.111.30:6262/vs4/VS/DG/VC2/;LockType=proprietary;CharType=OEM;TableType=cdx");
        ds = new HikariDataSource(hikariConfig);
        return ds.getConnection();
    }
}
