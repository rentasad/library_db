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
}




