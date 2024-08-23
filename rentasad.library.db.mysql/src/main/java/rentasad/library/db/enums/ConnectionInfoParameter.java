package rentasad.library.db.enums;

/**
 * Enum class for defining connection information parameters.
 *
 * This enum class represents the parameters required for establishing a connection,
 * such as SSH host, SSH port, SSH user, SSH key file path, remote host, remote port,
 * database user, and database password.
 *
 * Each enum constant represents a specific parameter and has a corresponding
 * configuration parameter string associated with it. This configuration parameter
 * string can be used to retrieve the parameter value from a map of connection parameters.
 *
 * The enum also provides a builder method to create a ConnectionInfo object using
 * the configuration parameters stored in a map. The builder method iterates over
 * the enum constants to retrieve the parameter values from the map and sets them
 * in the builder. Once all the parameters are set, the builder builds and returns
 * a ConnectionInfo object.
 */
public enum ConnectionInfoParameter
{
	SSH_HOST("SSH_HOST"),
	SSH_PORT("SSH_PORT"),
	SSH_USER("SSH_USER"),
	SSH_KEY_FILE_PATH("SSH_KEY_FILE_PATH"),
	REMOTE_HOST("MYSQL_HOST"),
	REMOTE_PORT("MYSQL_PORT"),
	DB_USER("MYSQL_USER"),
	DB_PASSWORD("MYSQL_PASSWORD"),
	DB_NAME("MYSQL_DATABASE");

	public final String configParameterString;

	private ConnectionInfoParameter(String configParameterString)
	{
		this.configParameterString = configParameterString;
	}
}
