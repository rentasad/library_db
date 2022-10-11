package rentasad.library.db.helpers.testClasses;

import lombok.Data;
import rentasad.library.db.helpers.ColumnName;

@Data
public class ColumnNameAnnotationHelperTestObject
{
	@ColumnName("last_name")
	private String lastName;

}
