package rentasad.library.db.helpers;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
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
	 * @throws SQLException             if a database access error occurs or the SQL statement is invalid
	 * @throws IllegalAccessException   if the data object's fields are not accessible
	 * @throws IllegalArgumentException if the data object does not contain a field marked with {@link PrimaryKey}
	 */
	public static void update(Connection connection, Object dataObject) throws SQLException, IllegalAccessException {
		Class<?> clazz = dataObject.getClass();
		StringBuilder sql = new StringBuilder("UPDATE ");
		String tableName = clazz.isAnnotationPresent(DBPersisted.class) && !clazz.getAnnotation(DBPersisted.class).value().isEmpty() ? clazz.getAnnotation(DBPersisted.class).value() : clazz.getSimpleName();
		sql.append(tableName).append(" SET ");

		List<Object> values = new ArrayList<>();
		String whereClause = null;
		Object primaryKeyValue = null;

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				if (field.isAnnotationPresent(DBPersisted.class)) {
					DBPersisted dbPersisted = field.getAnnotation(DBPersisted.class);
					String columnName = dbPersisted.value().isEmpty() ? field.getName() : dbPersisted.value();
					whereClause = columnName + " = ?";
					primaryKeyValue = field.get(dataObject);
				} else {
					throw new IllegalArgumentException("Primary key field must be annotated with @DBPersisted to provide the column name.");
				}
			} else if (field.isAnnotationPresent(DBPersisted.class)) {
				DBPersisted dbPersisted = field.getAnnotation(DBPersisted.class);
				String columnName = dbPersisted.value().isEmpty() ? field.getName() : dbPersisted.value();
				sql.append(columnName).append(" = ?, ");
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
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(dataObject);
				if (field.isAnnotationPresent(DBPersisted.class) && !field.isAnnotationPresent(PrimaryKey.class)) {
					if (value == null) {
						statement.setNull(index++, getSqlType(field));
					} else {
						setValue(statement, index++, value);
					}
				}
			}
			if (primaryKeyValue == null) {
				statement.setNull(index, getSqlType(getPrimaryKeyField(fields)));
			} else {
				setValue(statement, index, primaryKeyValue);
			}

			statement.executeUpdate();
		}
	}

	private static void setValue(PreparedStatement statement, int index, Object value) throws SQLException {
		if (value instanceof Boolean) {
			statement.setBoolean(index, (Boolean) value);
		} else if (value instanceof java.util.Date) {
			statement.setDate(index, new java.sql.Date(((java.util.Date) value).getTime()));
		} else {
			statement.setObject(index, value);
		}
	}

	private static int getSqlType(Field field) {
		final Class<?> fieldType = field.getType();
		if (String.class.isAssignableFrom(fieldType)) {
			return Types.VARCHAR;
		} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
			return Types.NUMERIC;
		} else if (fieldType == boolean.class || Boolean.class.isAssignableFrom(fieldType)) {
			return Types.BIT;
		} else if (fieldType == byte.class || Byte.class.isAssignableFrom(fieldType)) {
			return Types.TINYINT;
		} else if (fieldType == short.class || Short.class.isAssignableFrom(fieldType)) {
			return Types.SMALLINT;
		} else if (fieldType == int.class || Integer.class.isAssignableFrom(fieldType)) {
			return Types.INTEGER;
		} else if (fieldType == long.class || Long.class.isAssignableFrom(fieldType) || BigInteger.class.isAssignableFrom(fieldType)) {
			return Types.BIGINT;
		} else if (fieldType == float.class || Float.class.isAssignableFrom(fieldType)) {
			return Types.REAL;
		} else if (fieldType == byte[].class) {
			return Types.VARBINARY;
		} else if (fieldType == Date.class) {
			return Types.DATE;
		} else if (fieldType == Time.class) {
			return Types.TIME;
		} else if (fieldType == Timestamp.class || fieldType == Calendar.class) {
			return Types.TIMESTAMP;
		} else if (fieldType == double.class || Double.class.isAssignableFrom(fieldType)) {
			return Types.DOUBLE;
		} else if (Enum.class.isAssignableFrom(fieldType)) {
			return Types.VARCHAR;
		} else if (fieldType == InputStream.class) {
			return Types.BLOB;
		} else if (Character.class.isAssignableFrom(fieldType)) {
			return Types.VARCHAR;
		} else {
			throw new IllegalArgumentException("Cannot map " + fieldType.getName() + " to an SQL type.");
		}
	}

	private static Field getPrimaryKeyField(Field[] fields) {
		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				return field;
			}
		}
		throw new IllegalArgumentException("No primary key field found.");
	}
}



