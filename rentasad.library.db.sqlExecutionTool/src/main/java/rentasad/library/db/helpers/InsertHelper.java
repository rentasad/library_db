package rentasad.library.db.helpers;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * A Helper class which allows generic creation of insert statements for general
 * objects. <br>
 * Can be used like this:
 *
 * <pre>
 * try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/test", "postgres", "password"))
 * {
 * 	int row = new InsertHelper(object).insertIntoDatabase(conn);
 * 	System.out.println(row);
 * }
 * </pre>
 *
 * @author Pascal Bihler
 */

public class InsertHelper<T>
{

	public enum PersistFields
	{
		ALL,
		ONLY_ANNOTATED
	}

	// Static caches
	static final ConcurrentHashMap<ClassMarker, String> tableNames = new ConcurrentHashMap<>();
	static final HashMap<ClassMarker, LinkedHashMap<Field, FieldName>> tableFieldNames = new HashMap<>();

	final T object;
	final boolean skipNullValues;
	@Getter
	final String tableName;
	final LinkedHashMap<Field, FieldName> columnNames;

	final Object updateValueMonitor = new Object();
	LinkedList<FieldValue> lastValues;
	private Logger logger;

	/**
	 * Creates a helper for the given object
	 */
	public InsertHelper(@NonNull final T object, final boolean skipNullValues, final PersistFields persistanceStrategy)
	{
		this.logger = Logger.getGlobal();
		this.object = object;
		this.skipNullValues = skipNullValues;

		final ClassMarker classMarker = new ClassMarker(object.getClass(), persistanceStrategy);
		readStructure(classMarker);

		this.tableName = tableNames.get(classMarker);
		this.columnNames = tableFieldNames.get(classMarker);
	}

	private void readStructure(final ClassMarker classMarker)
	{
		String tableName = tableNames.get(classMarker);
		if (tableName != null)
		{
			return; // already cached
		}

		// Determine table name

		final Class<?> clazz = classMarker.clazz;
		String name = clazz.getSimpleName();
		final DBPersisted classAnnotation = clazz.getAnnotation(DBPersisted.class);
		if (classAnnotation != null)
		{
			final String classAnnotationValue = classAnnotation.value();
			if (classAnnotationValue != null && !"".equals(classAnnotationValue))
			{
				name = classAnnotationValue;
			}
		}

		tableName = tableNames.putIfAbsent(classMarker, name);
		if (tableName != null)
		{
			return; // race condition - some other thread was faster
		}
		tableName = name;

		// Determine data structure
		final Field[] fields = clazz.getDeclaredFields();
		LinkedHashMap<Field, FieldName> fieldNames = new LinkedHashMap<>();

		if (fields != null)
		{
			for (final Field field : fields)
			{
				String fieldName = field.getName();

				final DBPersisted fieldAnnotation = field.getAnnotation(DBPersisted.class);
				if (fieldAnnotation != null)
				{
					final String fieldAnnotationValue = fieldAnnotation.value();
					if (fieldAnnotationValue != null && !"".equals(fieldAnnotationValue))
					{
						fieldName = fieldAnnotationValue;
					}
				}
				else if (classMarker.persistanceStrategy == PersistFields.ONLY_ANNOTATED)
				{
					continue;
				}
				final int sqlType = getSqlType(field, fieldAnnotation);

				fieldNames.put(field, new FieldName(sqlType, fieldName));
			}
		}

		tableFieldNames.put(classMarker, fieldNames);
	}

	private int getSqlType(final Field field, final DBPersisted fieldAnnotation)
	{
		if (fieldAnnotation != null)
		{
			final int sqlType = fieldAnnotation.sqlType();
			if (sqlType > -1)
			{
				return sqlType;
			}
		}

		// Based on
		// https://www.cis.upenn.edu/~bcpierce/courses/629/jdkdocs/guide/jdbc/getstart/mapping.doc.html#1004864
		final Class<?> fieldType = field.getType();
		if (String.class.isAssignableFrom(fieldType))
		{
			return Types.VARCHAR;
		}
		else if (BigDecimal.class.isAssignableFrom(fieldType))
		{
			return Types.NUMERIC;
		}
		else if (fieldType == boolean.class || Boolean.class.isAssignableFrom(fieldType))
		{
			return Types.BIT;
		}
		else if (fieldType == byte.class || Byte.class.isAssignableFrom(fieldType))
		{
			return Types.TINYINT;
		}
		else if (fieldType == short.class || Short.class.isAssignableFrom(fieldType))
		{
			return Types.SMALLINT;
		}
		else if (fieldType == int.class || Integer.class.isAssignableFrom(fieldType))
		{
			return Types.INTEGER;
		}
		else if (fieldType == long.class || Long.class.isAssignableFrom(fieldType) || BigInteger.class.isAssignableFrom(fieldType))
		{
			return Types.BIGINT;
		}
		else if (fieldType == float.class || Float.class.isAssignableFrom(fieldType))
		{
			return Types.REAL;
		}
		else if (fieldType == byte[].class)
		{
			return Types.VARBINARY;
		}
		else if (fieldType == Date.class)
		{
			return Types.DATE;
		}
		else if (fieldType == Time.class)
		{
			return Types.TIME;
		}
		else if (fieldType == Timestamp.class || fieldType == Calendar.class)
		{
			return Types.TIMESTAMP;
		}
		else if (fieldType == Double.class)
		{
			return Types.DOUBLE;
		}
		else if (Enum.class.isAssignableFrom(fieldType))
		{
			return Types.VARCHAR;
		}
		else if (fieldType == InputStream.class)
		{
			return Types.BLOB;
		}

		else
		{
			throw new IllegalArgumentException("Cannot map " + fieldType.getName() + " to an SQL type.");
		}
	}

	/**
	 * Simple constructor persisting only annotated fields and skipping null values
	 *
	 * @param object
	 */
	public InsertHelper(@NonNull final T object)
	{
		this(object, true, PersistFields.ONLY_ANNOTATED);
	}

	/**
	 * Reads the field values and returns an appropriate insert statement. The
	 * values are referenced until {@link #getValues()} is called.
	 */
	@NonNull
	@Synchronized("updateValueMonitor")
	public String getInsertStatement()
	{

		final List<String> validColumnNames = getValidColumnNamesAndSetLastValues();

		final StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(tableName);
		sb.append(" (");
		sb.append(validColumnNames.stream().collect(Collectors.joining(",")));
		sb.append(") VALUES (");
		sb.append(validColumnNames.stream().map(col -> "?").collect(Collectors.joining(",")));
		sb.append(")");
		String message = String.format("Insertquery: %s", sb.toString());
		logger.log(Level.FINEST, message);
		return sb.toString();
	}

	private List<String> getValidColumnNamesAndSetLastValues()
	{
		lastValues = new LinkedList<>();

		final List<String> validColumnNames = columnNames.entrySet().stream().map(entry -> {
			final Field field = entry.getKey();

			final Object value = getFieldValue(field);
			if (value == null && skipNullValues)
			{
				return null;
			}

			final FieldName fieldName = entry.getValue();
			lastValues.add(new FieldValue(fieldName.sqlType, value));

			return fieldName.name;
		}).filter(name -> (name != null)).collect(Collectors.toList());
		return validColumnNames;
	}

	private Object getFieldValue(final Field field)
	{
		try
		{
			field.setAccessible(true);
			try
			{
				return field.get(object);
			} catch (final Exception e)
			{
				e.printStackTrace();
				return null;
			}
		} finally
		{
			field.setAccessible(false);
		}
	}

	/**
	 * Returns the values for the last requested insert statement.
	 * <p>
	 * Returning a LinkedList, so it can be used as List or as Queue
	 */
	@NonNull
	@Synchronized("updateValueMonitor")
	public LinkedList<Object> getValues()
	{
		if (lastValues == null)
		{
			getValidColumnNamesAndSetLastValues();
			assert lastValues != null;
		}

		final LinkedList<Object> valuesToReturn = lastValues.stream().map(fieldValue -> fieldValue.value).collect(Collectors.toCollection(() -> new LinkedList<Object>()));

		lastValues = null;
		return valuesToReturn;
	}

	/**
	 * Prepares an SQL insert statement and executes it, returning the index of the
	 * new row
	 *
	 * @throws SQLException
	 */
	@Synchronized("updateValueMonitor")
	public int insertIntoDatabase(@NonNull final Connection conn) throws SQLException
	{
		String insertStatement = getInsertStatement();
		try (final PreparedStatement preparedStatement = conn.prepareStatement(insertStatement))
		{
			setValuesInStatement(preparedStatement);
			return preparedStatement.executeUpdate();
		}
	}

	public void setValuesInStatement(final PreparedStatement stmt) throws SQLException
	{
		try
		{
			if (lastValues == null)
			{
				getValidColumnNamesAndSetLastValues();
				assert lastValues != null;
			}

			int index = 1;
			for (final FieldValue fieldValue : lastValues)
			{
				stmt.setObject(index, fieldValue.value, fieldValue.sqlType);
				index++;
			}
		} finally
		{
			lastValues = null;
		}
	}

	/**
	 * Helper class to allow field caching
	 */
	@Data
	private static class ClassMarker
	{
		@NonNull
		final Class<?> clazz;
		final PersistFields persistanceStrategy;
	}

	@Data
	private static class FieldName
	{
		final int sqlType;
		@NonNull
		final String name;
	}

	@Data
	private static class FieldValue
	{
		final int sqlType;
		final Object value;
	}
}
