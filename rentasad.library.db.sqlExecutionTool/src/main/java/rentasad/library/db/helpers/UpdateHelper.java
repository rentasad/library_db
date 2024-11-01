package rentasad.library.db.helpers;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateHelper
{
	/**
	 * Updates the record in the database corresponding to the given data object.
	 *
	 * @param connection the database connection to use for the update
	 * @param dataObject the object containing the data to be updated; fields marked with
	 *                   {@link DBPersisted} are included in the update,
	 *                   and the field marked with {@link PrimaryKey} is used as the identifier
	 * @throws SQLException if a database access error occurs or the SQL statement is invalid
	 * @throws IllegalAccessException if the data object's fields are not accessible
	 * @throws IllegalArgumentException if the data object does not contain a field marked with {@link PrimaryKey}
	 */
	public static void update(Connection connection, Object dataObject) throws SQLException, IllegalAccessException {
		Class<?> clazz = dataObject.getClass();
		String tableName;

		// Prüfen, ob die Klasse die Annotation @DBPersisted hat und den Tabellenname extrahieren
		if (clazz.isAnnotationPresent(DBPersisted.class)) {
			DBPersisted dbPersisted = clazz.getAnnotation(DBPersisted.class);
			tableName = dbPersisted.value().isEmpty() ? clazz.getSimpleName() : dbPersisted.value();
		} else {
			tableName = clazz.getSimpleName();
		}

		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName)
		   .append(" SET ");

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
				DBPersisted fieldAnnotation = field.getAnnotation(DBPersisted.class);
				String columnName = fieldAnnotation.value().isEmpty() ? field.getName() : fieldAnnotation.value();
				sql.append(columnName)
				   .append(" = ?, ");
				values.add(field.get(dataObject));
			}
		}

		if (whereClause == null) {
			throw new IllegalArgumentException("No primary key field found in class " + clazz.getName());
		}

		// Entfernen des letzten Kommas und Leerzeichens
		sql.setLength(sql.length() - 2);
		sql.append(" WHERE ")
		   .append(whereClause);

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

