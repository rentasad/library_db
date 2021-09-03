package rentasad.library.db.mysqltransfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rentasad.library.db.AdsConnection;


public class MySQLTableUtilityTest
{
    Connection conSource;
    Connection conTarget;
    @SuppressWarnings("unused")
    @BeforeEach
    public void setUp() throws Exception
    {
//        String MYSQL_SOURCE_HOST = "intranet.gustini.local";
//        String MYSQL_SOURCE_DATABASE = "euverordnung";
//        String MYSQL_SOURCE_USER = "rpo";
//        String MYSQL_SOURCE_PASSWORD = "Welcome2012";
//        String MYSQL_SOURCE_ENCODING = "utf8";
//        System.out.println("CONNECTION zu SOURCE" + MYSQL_SOURCE_HOST + " herstellen");
//        conSource = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false&characterEncoding=utf8&useUnicode=true&clobCharacterEncoding=utf8", MYSQL_SOURCE_HOST, MYSQL_SOURCE_DATABASE, MYSQL_SOURCE_USER, MYSQL_SOURCE_PASSWORD));

        String MYSQL_TARGET_HOST = "gstvmdbs3.gustini.local";
        String MYSQL_TARGET_DATABASE = "euv";
        String MYSQL_TARGET_USER = "euverordnung";
        String MYSQL_TARGET_PASSWORD = "fdJ7SDBF";
        String MYSQL_TARGET_ENCODING = "utf8";
        System.out.println("CONNECTION zu TARGET" + MYSQL_TARGET_HOST + " herstellen");
        conTarget = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false&characterEncoding=utf8&useUnicode=true&clobCharacterEncoding=utf8", MYSQL_TARGET_HOST, MYSQL_TARGET_DATABASE, MYSQL_TARGET_USER, MYSQL_TARGET_PASSWORD));


    }

    @AfterEach
    public void tearDown() throws Exception
    {
    }

    
//    public void testTransferMySqlTableToMySqlTable() throws Exception
//    {
//        String[] tablesToImport = {
//                "v4ar1009"
//        };
//        for (String tableName : tablesToImport)
//        {
//            System.out.println("TRANSFER Tabelle: " + tableName);
//            IsqlTableDescriptionInterface tableDescriptionArray = MySQLTableUtility.getMySQLTableDescription(conTarget, tableName);
//            MySQLTableUtility mySQLTableUtility = new MySQLTableUtility(null);
//            mySQLTableUtility.transferMySqlTableToMySqlTable(conSource, conTarget, tableDescriptionArray);
//
//        }
//    }
    
    @Test
    public void encodingCheck()  throws Exception
    {
        String query = "SELECT NUMMER, ART_UEBERS FROM F01\\ARTIKEL\\V4AR1009 WHERE NUMMER = '0070022'";
        Connection con = AdsConnection.dbConnect();
        
        
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(query);
        if (rs.next())
        {
            String text = rs.getString("ART_UEBERS");
            System.out.println(text);
        }
    }

}
