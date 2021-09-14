package rentasad.library.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * 
 * Gustini GmbH (2015)
 * Creation: 18.03.2015
 * Rentasad Library
 * rentasad.lib.db
 * 
 * @author Matthias Staud
 *
 *
 *         Description:
 *         Klasse zum Herstellen einer Verbindung zu einem MSSQL-Server
 */
public class MsSQLConnection
{
    public static final String MSSQL_DATASOURCE = "MSSQL_DATASOURCE";
    public static final String MSSQL_USER = "MSSQL_USER";
    public static final String MSSQL_DATABASE = "MSSQL_DATABASE";
    public static final String MSSQL_PASSWORD = "MSSQL_PASSWORD";
    private static boolean debug = false;
    private static MsSQLConnection instance = null;
    private Connection connection;
    private Map<String, String> connectionParametersMap;

    /**
     * 
     * Description:
     * 
     * @param connectionParametersMap
     * @return
     *         Creation: 15.12.2015 by mst
     */
    public static Connection dbConnect(Map<String, String> connectionParametersMap)
    {
        String msSqlServerUrl = connectionParametersMap.get(MSSQL_DATASOURCE);
        String msSqlDatabaseName = connectionParametersMap.get(MSSQL_DATABASE);
        String msSqlDbUserid = connectionParametersMap.get(MSSQL_USER);
        String msSqlDbPassword = connectionParametersMap.get(MSSQL_PASSWORD);
        return MsSQLConnection.dbConnect(msSqlServerUrl, msSqlDatabaseName, msSqlDbUserid, msSqlDbPassword);
    }

    /**
     * 
     * Description:
     * 
     * @param connectionParametersMap
     * @throws SQLException
     *             Creation: 14.02.2018 by mst
     */
    public static void initInstance(Map<String, String> connectionParametersMap) throws SQLException
    {

        if (instance == null)
        {
            instance = new MsSQLConnection();
            instance.setConnection(MsSQLConnection.dbConnect(connectionParametersMap));
            instance.setConnectionParametersMap(connectionParametersMap);
        } else
        {
            if (debug)
            {
                System.err.println("Instance wurde bereits initialisiert");
            }
        }
    }

    /**
     * 
     * Description: GetInstance Class for Managing Connection (and Reconnection after Close and Timeout
     * 
     * @return
     * @throws SQLException
     *             Creation: 14.02.2018 by mst
     */
    public static MsSQLConnection getInstance() throws SQLException
    {
        if (instance == null)
        {
            throw new SQLException("MsSqlConnection Instance not initialized");
        } else
        {
            return instance;
        }

    }

    /**
     * 
     * Description:
     * 
     * @param connectionParametersMap
     *            Creation: 14.02.2018 by mst
     */
    private void setConnectionParametersMap(Map<String, String> connectionParametersMap)
    {
        this.connectionParametersMap = connectionParametersMap;

    }

    private void setConnection(Connection msSqlConnection)
    {
        this.connection = msSqlConnection;
    }

    /**
     * Stellt eine MSSQL-Verbindung her mit expliziter Authentifizierung
     * 
     * @param db_connect_string
     * @param dbUserid
     * @param dbPassword
     */
    public static Connection dbConnect(String serverUrl, String databaseName, String dbUserid, String dbPassword)
    {
        Connection connection = null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setIntegratedSecurity(false);
            ds.setServerName(serverUrl);
            ds.setPortNumber(1433);
            ds.setDatabaseName(databaseName);
            ds.setUser(dbUserid);
            ds.setPassword(dbPassword);
            connection = ds.getConnection();

            System.out.println("connected");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * <br>
     * Stellt eine MSSQL-Verbindung her mit integrativer (Kerberos-)Authentifizierung
     * <br>
     * <br>
     * 
     * @param serverUrl
     *            <br>
     * @param databaseName
     *            <br>
     * @param db_userid
     *            <br>
     * @param db_password
     *            <br>
     * @return
     *         <br>
     *         <br>
     * @description
     *              <br>
     *              Unter Herstellen von Verbindungen mit SQL Server mit der integrierten Kerberos-Authentifizierung finden Sie die Beschreibung einer in Microsoft JDBC Driver fuer SQL Server
     *              <br>
     *              eingefuehrten
     *              <br>
     *              Funktion, durch die Anwendungen mithilfe der integrierten Authentifizierung Verbindungen mit einer Datenbank herstellen koennen.
     *              <br>
     *              <br>
     *              Der JDBC-Treiber unterstuetzt ueber die integratedSecurity-Verbindungszeichenfolgeneigenschaft die Verwendung der integrierten Authentifizierung vom Typ 2 auf
     *              <br>
     *              Windows-Betriebssystemen.
     *              <br>
     *              Wenn Sie die integrierte Authentifizierung verwenden moechten, muessen Sie die Datei sqljdbc_auth.dll in ein Verzeichnis im Windows-Systempfad des Computers kopieren, auf dem der
     *              <br>
     *              JDBC-Treiber installiert ist.
     *              <br>
     *              <br>
     *              Die sqljdbc_auth.dll-Dateien werden im folgenden Pfad installiert:
     *              <br>
     *              <br>
     *              <installation directory>\sqljdbc_<version>\<language>\auth\
     *              <br>
     *              <br>
     *              Wenn Sie eine 32-Bit-JVM (Java Virtual Machine) ausfuehren, verwenden Sie die Datei sqljdbc_auth.dll im Ordner x86, auch wenn es sich bei dem Betriebssystem um die x64-Version
     *              <br>
     *              handelt.
     *              <br>
     *              Wenn Sie eine 64-Bit-JVM mit einem x64-Prozessor ausfuehren, verwenden Sie die Datei sqljdbc_auth.dll im Ordner x64.
     *              <br>
     *              <br>
     *              Alternativ koennen Sie mit der java.libary.path-Systemeigenschaft das Verzeichnis von sqljdbc_auth.dll angeben. Wenn der JDBC-Treiber beispielsweise im Standardverzeichnis
     *              <br>
     *              installiert
     *              <br>
     *              ist, koennen Sie den Speicherort der DLL beim Start der Java-Anwendung mit dem folgenden VM-Argument (Virtual Machine) angeben:
     *              <br>
     *              <br>
     *              -Djava.library.path=C:\Microsoft JDBC Driver 4.0 for SQL Server\sqljdbc_<version>\enu\auth\x86
     *              <br>
     *              <br>
     *              Der JDBC-Treiber unterstuetzt die Verwendung der integrierten Authentifizierung nicht, wenn der Treiber nicht unter einem Windows-Betriebssystem ausgefuehrt wird. Er bietet beim
     *              <br>
     *              Herstellen einer Verbindung mit SQL Server von einem Betriebssystem, das kein Windows-Betriebssystem ist, auch keine Funktionen zur Bereitstellung von Anmeldeinformationen fuer die
     *              <br>
     *              Windows-Authentifizierung wie Benutzername und Kennwort. In diesen Faellen muessen Anwendungen stattdessen die SQL Server-Authentifizierung verwenden.
     *              <br>
     *              <br>
     *              Ab Microsoft JDBC Driver 4.0 fuer SQL Server koennen Anwendungen mithilfe der authenticationScheme-Verbindungseigenschaft angeben, dass eine Verbindung mit einer Datenbank unter
     *              <br>
     *              Verwendung der integrierten Kerberos-Authentifizierung Typ 4 hergestellt werden soll. Weitere Informationen zu Verbindungseigenschaften finden Sie unter Festlegen von
     *              <br>
     *              Verbindungseigenschaften. Weitere Informationen zu Kerberos finden Sie unter Technischer Anhang zu Kerberos fuer Windows und Microsoft Kerberos.
     *              <br>
     *              <br>
     *              Bei Verwendung der integrierten Authentifizierung mit dem Java-Krb5LoginModule koennen Sie das Modul mithilfe der Krb5LoginModule-Klasse konfigurieren.
     *              <br>
     *              <br>
     *              Microsoft JDBC Driver fuer SQL Server legt fuer Java-VMs von IBM die folgenden Eigenschaften fest:
     *              <br>
     *              <br>
     *              useDefaultCcache = true
     *              <br>
     *              <br>
     *              moduleBanner = false
     *              <br>
     *              <br>
     *              Microsoft JDBC Driver fuer SQL Server legt fuer alle anderen Java-VMs die folgenden Eigenschaften fest:
     *              <br>
     *              <br>
     *              useTicketCache = true
     *              <br>
     *              <br>
     *              doNotPrompt = true
     *              <br>
     *              <br>
     *              Hinweise
     *              <br>
     *              <br>
     *              Vor Microsoft JDBC Driver 4.0 fuer SQL Server konnten Anwendungen die integrierte Authentifizierung (mit Kerberos oder NTLM, je nach Verfuegbarkeit) mithilfe der
     *              <br>
     *              integratedSecurity-Verbindungseigenschaft und durch Verweisen auf sqljdbc_auth.dll angeben, entsprechend der Beschreibung in Erstellen der Verbindungs-URL.
     *              <br>
     *              <br>
     *              Ab Microsoft JDBC Driver 4.0 fuer SQL Server koennen Anwendungen mit der authenticationScheme-Verbindungseigenschaft angeben, dass eine Verbindung mit einer Datenbank mithilfe der
     *              <br>
     *              integrierten Kerberos-Authentifizierung hergestellt werden soll, unter Verwendung der reinen Java-Kerberos-Implementierung:
     *              <br>
     *              <br>
     *              Wenn die integrierte Authentifizierung mit dem Krb5LoginModule ausgefuehrt werden soll, muessen Sie dennoch die integratedSecurity=true-Verbindungseigenschaft angeben. In diesem
     *              Fall
     *              <br>
     *              geben Sie auch die authenticationScheme=JavaKerberos-Verbindungseigenschaft an.
     *              <br>
     *              <br>
     *              Wenn weiterhin die integrierte Authentifizierung mit sqljdbc_auth.dll verwendet werden soll, geben Sie einfach die integratedSecurity=true-Verbindungseigenschaft (und optional
     *              <br>
     *              authenticationScheme=NativeAuthentication) an.
     *              <br>
     *              <br>
     *              Wenn Sie authenticationScheme=JavaKerberos angeben, jedoch nicht gleichzeitig integratedSecurity=true angeben, ignoriert der Treiber die
     *              <br>
     *              authenticationScheme-Verbindungseigenschaft, und es wird davon ausgegangen, dass Benutzername und Kennwort fuer die Anmeldeinformationen in der Verbindungszeichenfolge enthalten
     *              <br>
     *              sind.
     *              <br>
     *              <br>
     *              Werden Verbindungen mit einer Datenquelle erstellt, koennen Sie das Authentifizierungsschema programmgesteuert mit setAuthenticationScheme festlegen.
     *              <br>
     *              <br>
     *              Zur Unterstuetzung der Kerberos-Authentifizierung wurde eine neue Protokollierung eingefuehrt: com.microsoft.sqlserver.jdbc.internals.KerbAuthentication. Weitere Informationen
     *              finden
     *              <br>
     *              Sie unter Ablaufverfolgung fuer Treibervorgaenge.
     *              <br>
     *              <br>
     *              Befolgen Sie beim Konfigurieren von Kerberos die folgenden Richtlinien:
     *              <br>
     *              <br>
     *              Legen Sie AllowTgtSessionKey in der Registrierung fuer Windows auf 1 fest. Weitere Informationen finden Sie unter KDC-Konfigurationsschluessel in Windows Server 2003 und
     *              <br>
     *              Kerberos-Protokoll-Registrierungseintraege.
     *              <br>
     *              <br>
     *              Stellen Sie sicher, dass die Kerberos-Konfiguration (krb5.conf in UNIX-Umgebungen) auf den richtigen Bereich und KDC fuer Ihre Umgebung zeigt.
     *              <br>
     *              <br>
     *              Initialisieren Sie den TGT-Cache mit "kinit" oder indem Sie sich bei der Domaene anmelden.
     *              <br>
     *              <br>
     *              Bei Ausfuehrung einer Anwendung, die authenticationScheme=JavaKerberos verwendet, unter dem Betriebssystem Windows Vista bzw. Windows 7 muss ein Standardbenutzerkonto verwendet
     *              <br>
     *              werden. Wenn Sie jedoch die Anwendung unter einem Administratorkonto ausfuehren, muss sie mit Administratorberechtigungen ausgefuehrt werden.
     *              <br>
     *              <br>
     *              Dienstprinzipalnamen
     *              <br>
     *              <br>
     *              Anhand eines Dienstprinzipalnamens (Service Principal Name, SPN) identifiziert ein Client eine Instanz eines Dienstes eindeutig. Weitere Informationen zu Dienstprinzipalnamen
     *              <br>
     *              (SPNs) finden Sie in folgendem Thema:
     *              <br>
     *              <br>
     *              Verwenden der Kerberos-Authentifizierung in SQL Server
     *              <br>
     *              <br>
     *              Verwenden von Kerberos mit SQL Server
     *              <br>
     *              <br>
     *              Erstellen einer Anmeldemodul-Konfigurationsdatei
     *              <br>
     *              <br>
     *              Sie koennen optional eine Kerberos-Konfigurationsdatei angeben. Wenn keine Konfigurationsdatei angegeben wird, gelten die folgenden Einstellungen:
     *              <br>
     *              <br>
     *              Sun-JVM
     *              <br>
     *              <br>
     *              com.sun.security.auth.module.Krb5LoginModule required useTicketCache=true doNotPrompt=true;
     *              <br>
     *              IBM-JVM
     *              <br>
     *              <br>
     *              com.ibm.security.auth.module.Krb5LoginModule required useDefaultCcache = true moduleBanner = false;
     *              <br>
     *              <br>
     *              Wenn Sie selbststaendig eine Anmeldemodul-Konfigurationsdatei erstellen, muss die Datei das folgende Format aufweisen:
     *              <br>
     *              <br>
     *              <name> {
     *              <br>
     *              <LoginModule> <flag> <LoginModule options>;
     *              <br>
     *              <optional_additional_LoginModules, flags_and_options>;
     *              <br>
     *              };
     *              <br>
     *              <br>
     *              Eine Anmeldemodul-Konfigurationsdatei enthaelt einen oder mehrere Eintraege, die jeweils angeben, welche zugrunde liegende Authentifizierungstechnologie fuer eine bestimmte
     *              Anwendung
     *              <br>
     *              bzw. fuer bestimmte Anwendungen verwendet werden soll. Beispiel:
     *              <br>
     *              <br>
     *              SQLJDBCDriver {
     *              <br>
     *              com.sun.security.auth.module.Krb5LoginModule required useTicketCache=true doNotPrompt=true;
     *              <br>
     *              };
     *              <br>
     *              <br>
     *              Jeder Eintrag der Anmeldemodul-Konfigurationsdatei besteht aus einem Namen, auf den ein oder mehrere LoginModule-spezifische Eintraege folgen. Jeder LoginModule-spezifische Eintrag
     *              <br>
     *              wird jeweils durch ein Semikolon abgeschlossen, und die gesamte Gruppe von LoginModule-spezifischen Eintraegen wird von geschweiften Klammern umschlossen. Jeder
     *              <br>
     *              Konfigurationsdateieintrag wird durch ein Semikolon abgeschlossen.
     *              <br>
     *              <br>
     *              Fuer den Treiber kann nicht nur festgelegt werden, dass Kerberos-Anmeldeinformationen mit den Einstellungen in der Anmeldemodul-Konfigurationsdatei erhalten werden koennen.
     *              <br>
     *              Stattdessen kann der Treiber auch vorhandene Anmeldeinformationen verwenden. Dies kann hilfreich sein, wenn die Anwendung Verbindungen mit den Anmeldeinformationen mehrerer
     *              <br>
     *              Benutzer herstellen muss.
     *              <br>
     *              <br>
     *              Der Treiber versucht zunaechst vorhandene Anmeldeinformationen zu verwenden (sofern diese verfuegbar sind), bevor er die Anmeldung mit dem angegebenen Anmeldemodul ausfuehrt. Bei
     *              <br>
     *              Ausfuehrung von Code unter einem bestimmten Kontext mithilfe der Subject.doAs-Methode wird daher eine Verbindung mit den Anmeldeinformationen erstellt, die an den Aufruf von
     *              <br>
     *              Subject.doAs uebergeben werden.
     *              <br>
     *              <br>
     *              Weitere Informationen finden Sie unter JAAS Login Configuration File und Class Krb5LoginModule.
     *              <br>
     *              Erstellen einer Kerberos-Konfigurationsdatei
     *              <br>
     *              <br>
     *              Weitere Informationen zu Kerberos-Konfigurationsdateien finden Sie unter Kerberos Requirements.
     *              <br>
     *              <br>
     *              Dies ist eine Konfiguration fuer eine Beispieldomaene. YYYY und ZZZZ sind durch die Domaenennamen an Ihrem Standort zu ersetzen.
     *              <br>
     *              <br>
     *              [libdefaults]
     *              <br>
     *              default_realm = YYYY.CORP.CONTOSO.COM
     *              <br>
     *              dns_lookup_realm = false
     *              <br>
     *              dns_lookup_kdc = true
     *              <br>
     *              ticket_lifetime = 24h
     *              <br>
     *              forwardable = yes
     *              <br>
     *              <br>
     *              [domain_realm]
     *              <br>
     *              .yyyy.corp.contoso.com = YYYY.CORP.CONTOSO.COM
     *              <br>
     *              .zzzz.corp.contoso.com = ZZZZ.CORP.CONTOSO.COM
     *              <br>
     *              <br>
     *              [realms]
     *              <br>
     *              YYYY.CORP.CONTOSO.COM = {
     *              <br>
     *              kdc = krbtgt/YYYY.CORP. CONTOSO.COM @ YYYY.CORP. CONTOSO.COM
     *              <br>
     *              default_domain = YYYY.CORP. CONTOSO.COM
     *              <br>
     *              }
     *              <br>
     *              <br>
     *              ZZZZ.CORP. CONTOSO.COM = {
     *              <br>
     *              kdc = krbtgt/ZZZZ.CORP. CONTOSO.COM @ ZZZZ.CORP. CONTOSO.COM
     *              <br>
     *              default_domain = ZZZZ.CORP. CONTOSO.COM
     *              <br>
     *              }
     *              <br>
     *              <br>
     *              Aktivieren der Domaenenkonfigurationsdatei und der Anmeldemodul-Konfigurationsdatei
     *              <br>
     *              <br>
     *              Sie koennen eine Domaenenkonfigurationsdatei mit -Djava.security.krb5.conf aktivieren. Sie koennen eine Anmeldemodul-Konfigurationsdatei mit -Djava.security.auth.login.config
     *              <br>
     *              aktivieren.
     *              <br>
     *              <br>
     *              Sie koennen beispielsweise beim Starten der Anwendung Folgendes an der Befehlszeile eingeben:
     *              <br>
     *              <br>
     *              Java.exe -Djava.security.auth.login.config=SQLJDBCDriver.conf -Djava.security.krb5.conf=krb5.ini <APPLICATION_NAME>
     *              <br>
     *              <br>
     *              ueberpruefen des Zugriffs auf SQL Server ueber Kerberos
     *              <br>
     *              <br>
     *              Fuehren Sie in SQL Server Management Studio die folgende Abfrage aus:
     *              <br>
     *              <br>
     *              select auth_scheme from sys.dm_exec_connections where session_id=@@spid
     *              <br>
     *              <br>
     *              Vergewissern Sie sich, dass Sie ueber die erforderliche Berechtigung zum Ausfuehren dieser Abfrage verfuegen.
     */
    public static Connection dbConnect(String serverUrl, String databaseName)
    {
        Connection connection = null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setIntegratedSecurity(true);
            ds.setServerName(serverUrl);
            ds.setPortNumber(1433);
            ds.setDatabaseName(databaseName);
            connection = ds.getConnection();

            System.out.println("connected");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * @return the connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException
    {
        if (this.connection.isValid(4))
        {
            return this.connection;
        } else
        {
            this.connection = MsSQLConnection.dbConnect(this.connectionParametersMap);
            return this.connection;
        }
    }

    public static boolean isInit() throws SQLException
    {
        if (instance != null)
        {
            if (instance.getConnection() != null)
            {
                return true;
            }else
            {
                return false;
            }
        }

        else
            return false;
    }
}
