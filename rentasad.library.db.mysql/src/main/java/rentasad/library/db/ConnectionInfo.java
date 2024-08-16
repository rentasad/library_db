package rentasad.library.db;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ConnectionInfo
{
	private String sshHost;
	private int sshPort;
	private String sshUser;
	private String sshKeyFilePath;
	private String remoteHost;
	private int remotePort;
	private String dbUser;
	private String dbPassword;
	private String dbName;

	public static ConnectionInfo getConnectionInfoFromConfigMap(Map<String, String> connectionParametersMap)
	{
		ConnectionInfoBuilder builder = ConnectionInfo.builder();
		for (ConnectionInfoParameter param : ConnectionInfoParameter.values())
		{
			String value = connectionParametersMap.get(param.configParameterString);

			if (value != null)
			{
				switch (param)
				{
				case SSH_HOST:
					builder.sshHost(value);
					break;
				case SSH_PORT:
					builder.sshPort(Integer.parseInt(value));
					break;
				case SSH_USER:
					builder.sshUser(value);
					break;
				case SSH_KEY_FILE_PATH:
					builder.sshKeyFilePath(value);
					break;
				case REMOTE_HOST:
					builder.remoteHost(value);
					break;
				case REMOTE_PORT:
					builder.remotePort(Integer.parseInt(value));
					break;
				case DB_USER:
					builder.dbUser(value);
					break;
				case DB_PASSWORD:
					builder.dbPassword(value);
					break;
				case DB_NAME:
					builder.dbName(value);
				}
			}
		}

		return builder.build();

	}

	/**
	 * Retrieves a ConnectionInfo object from a map of connection parameters.
	 *
	 * This method takes a map of connection parameters and a SSH key file path as input.
	 * It adds the SSH key file path to the connection parameters map and then calls
	 * the getConnectionInfoFromConfigMap method to retrieve a ConnectionInfo object.
	 *
	 * @param connectionParametersMap A map of connection parameters.
	 * @param sshKeyFilePath The SSH key file path to be added to the connection parameters map.
	 * @return A ConnectionInfo object containing the connection information.
	 */
	public static ConnectionInfo getConnectionInfoFromConfigMap(Map<String, String> connectionParametersMap, final String sshKeyFilePath)
	{
		connectionParametersMap.put(ConnectionInfoParameter.SSH_KEY_FILE_PATH.configParameterString,  sshKeyFilePath);
		return getConnectionInfoFromConfigMap(connectionParametersMap);

	}
}




