package rentasad.library.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DublettenQueryTool
{

	public DublettenQueryTool()
	{
		// TODO Auto-generated constructor stub
	}

	public static void runQuery()
	{
//		String select = "SELECT NUMMER,NOTIZEN FROM F01\\EXPORT\\TEST";
//		Connection con = ADSConnection.dbConnect();
//		java.sql.Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery(select);
//		Map<String, String> dublettenMap = new HashMap<>();
//
//		while (rs.next())
//		{
//			String kn = rs.getString("NUMMER");
//			String notiz = rs.getString("NOTIZEN");
//			dublettenMap.put(kn, notiz);
//		}
//
//		for (String kn : dublettenMap.keySet())
//		{
//			String notiz = dublettenMap.get(kn);
//			String regexPattern1 = "\\*\\*\\* Folge-Dublette zu:\\s\\d* \\*\\*\\*";
//			String regexPattern2 = "\\d{10}";
//
//			Pattern pattern1 = Pattern.compile(regexPattern1);
//			Pattern pattern2 = Pattern.compile(regexPattern2);
//			Matcher matcher1 = pattern1.matcher(notiz);
//			while (matcher1.find())
//			{
//				// System.out.print("Start index: " + matcher1.start());
//				// System.out.print(" End index: " + matcher1.end() + " ");
//				String folgeDubletteText = matcher1.group();
//				Matcher matcher2 = pattern2.matcher(folgeDubletteText);
//				if (matcher2.find())
//				{
//					String knNew = matcher2.group();
//					System.out.println(String.format(
//							"UPDATE customer_entity_text SET value = '%s' WHERE entity_type_id = 1 AND  attribute_id = 180 AND value = '%s';",
//							knNew, kn));
//				}
//
//			}
//		}
	}

}
