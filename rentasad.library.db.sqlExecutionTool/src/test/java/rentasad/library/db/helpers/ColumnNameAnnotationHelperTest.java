package rentasad.library.db.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class ColumnNameAnnotationHelperTest
{

	@Test
	public void testGetOrderColumnNameFromOrdersAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field columNameActual = ColumnNameAnnotationHelperTestObject.class.getDeclaredField("lastName");
		String actualColumName = ColumnNameAnnotationHelper.getAnnotationFromSingleField(columNameActual);
		String expectedColumnName = "last_name";
		assertEquals(expectedColumnName, actualColumName);
	}

}
