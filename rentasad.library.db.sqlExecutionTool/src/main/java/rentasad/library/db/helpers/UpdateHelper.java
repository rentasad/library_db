package rentasad.library.db.helpers;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateHelper {
	public static void update(Connection connection, Object dataObject) throws SQLException, IllegalAccessException {
		Class<?> clazz = dataObject.getClass();
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(clazz.getSimpleName()).append(" SET ");

		List<Object> values = new ArrayList<>();
		String whereClause = null;
		Object primaryKeyValue = null;

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				whereClause = field.getName() + " = ?";
				primaryKeyValue = field.get(dataObject);
			} else if (field.isAnnotationPresent(DBPersisted.class)) {
				sql.append(field.getName()).append(" = ?, ");
				values.add(field.get(dataObject));
			}
		}

		if (whereClause == null) {
			throw new IllegalArgumentException("No primary key field found in class " + clazz.getName());
		}

		// Remove the trailing comma and space
		sql.setLength(sql.length() - 2);
		sql.append(" WHERE ").append(whereClause);

		try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
			int index = 1;
			for (Object value : values) {
				statement.setObject(index++, value);
			}
			statement.setObject(index, primaryKeyValue);

			statement.executeUpdate();
		}
	}
}


//// Example usage:
//class DBPersisted {
//	@PrimaryKey
//	private int id;
//	private String name;
//	private int age;
//
//	// Getters and setters...
//}

//class Main {
//	public static void main(String[] args) throws SQLException, IllegalAccessException {
//		Connection connection = null; // Assume this is initialized
//		DBPersisted data = new DBPersisted();
//		data.setId(1);
//		data.setName("John Doe");
//		data.setAge(30);
//
//		UpdateHelper.update(connection, data);
//	}
//}

