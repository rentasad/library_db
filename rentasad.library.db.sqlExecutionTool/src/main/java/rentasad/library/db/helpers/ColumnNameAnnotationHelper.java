package rentasad.library.db.helpers;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Calendar;

/**
 * //	EXAMPLE USE
 * //	/**
 * //	 * Extract ColumnName Annotation Value from OrderObject
 * //	 *
 * //	 * @param orderFieldName
 * //	 * @return
 * //	 * @throws NoSuchFieldException
 * //	 * @throws SecurityException
 * //
 * //	public static String getOrderColumnNameFromOrdersAnnotation(String orderFieldName) throws NoSuchFieldException, SecurityException
 * //	{
 * //		val fieldSingle = Order.class.getDeclaredField(orderFieldName);
 * //		return getAnnotationFromSingleField(fieldSingle);
 * //	}
 */
public class ColumnNameAnnotationHelper
{
	//	EXAMPLE USE
	//	/**
	//	 * Extract ColumnName Annotation Value from OrderObject
	//	 *
	//	 * @param orderFieldName
	//	 * @return
	//	 * @throws NoSuchFieldException
	//	 * @throws SecurityException
	//	 */
	//	public static String getOrderColumnNameFromOrdersAnnotation(String orderFieldName) throws NoSuchFieldException, SecurityException
	//	{
	//		val fieldSingle = Order.class.getDeclaredField(orderFieldName);
	//		return getAnnotationFromSingleField(fieldSingle);
	//	}

	/**
	 * @param fieldSingle
	 * @return
	 */
	static String getAnnotationFromSingleField(final Field fieldSingle)
	{
		String columnNameString = null;
		ColumnName[] annotations = fieldSingle.getDeclaredAnnotationsByType(ColumnName.class);
		if (annotations.length == 1)
		{
			ColumnName columnNameAnnotation = annotations[0];
			columnNameString = columnNameAnnotation.value();
		}
		return columnNameString;
	}

	/**
	 * @param <T>
	 * @param cls
	 * @param rs
	 * @return
	 */
	public static <T> T fromResultSet(final Class<T> cls, final ResultSet rs)
	{
		try
		{
			final T newObject = cls.newInstance();
			for (final Field field : cls.getDeclaredFields())
			{
				try
				{
					final String columnName = ColumnNameAnnotationHelper.getAnnotationFromSingleField(field);
					final String fieldName = field.getName();
					if (columnName == null)
					{
						continue;
					}

					final String setterName = getSetterName(fieldName);
					Class<?> fieldType = field.getType();
					Object value = getObjectFromResultSet(field.getType(), rs, columnName);

					if (rs.wasNull())
					{
						continue; // don't call setter of field
					}
					Method setter = cls.getDeclaredMethod(setterName, fieldType);
					setter.invoke(newObject, value);

				} catch (final Exception ex)
				{
					System.out.println(field.getName());
					ex.printStackTrace();
				}
			}

			return newObject;
		} catch (final Exception ex)
		{
			ex.printStackTrace();
		}
		return null;

	}

	private static String getSetterName(String fieldName)
	{
		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	private static Object getObjectFromResultSet(Class<?> type, final ResultSet rs, final String columnName)
			throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if (type == String.class)
		{
			return rs.getString(columnName);
		}
		else if (type == long.class || type == Long.class)
		{
			return rs.getLong(columnName);
		}
		else if (type == int.class || type == Integer.class)
		{
			return rs.getInt(columnName);
		}
		else if (type == boolean.class || type == Boolean.class)
		{
			return rs.getBoolean(columnName);
		}
		else if (type == double.class || type == Double.class)
		{
			return rs.getDouble(columnName);
		}
		else if (type == char.class || type == Character.class)
		{
			final String charValue = rs.getString(columnName);
			if (charValue == null || charValue.isEmpty())
			{
				return '\0'; // oder default Wert für Empty?
			}
			return charValue.toUpperCase().charAt(0);
		}
		else if (type == Calendar.class)
		{
			return getCalendarFromSqlTimestamp(rs.getTimestamp(columnName));
		}
		else if (type == Timestamp.class)
		{
			return rs.getTimestamp(columnName);
		}
		else if (type == Date.class)
		{
			return rs.getDate(columnName);
		}
		else if (type == InputStream.class)
		{
			Blob blob = rs.getBlob(columnName);
			return blob.getBinaryStream();
		}
		else if (Enum.class.isAssignableFrom(type))
		{
			final String enumValue = rs.getString(columnName);
			if (enumValue == null)
			{
				return null;
			}
			Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
			return valueOfMethod.invoke(null, enumValue.toUpperCase().trim());
		}
		throw new IllegalArgumentException("No mapping for type " + type.getName() + " defined, yet.");
	}

	/**
	 * Convert sql.timestamp to java.lang.calendar
	 *
	 * @param timestamp sql timestamp
	 * @return Calendar
	 */
	private static Calendar getCalendarFromSqlTimestamp(Timestamp timestamp)
	{
		if (timestamp == null)
		{
			return null;
		}

		Calendar calendarValue = Calendar.getInstance();
		calendarValue.setTime(timestamp);
		return calendarValue;

	}

}
