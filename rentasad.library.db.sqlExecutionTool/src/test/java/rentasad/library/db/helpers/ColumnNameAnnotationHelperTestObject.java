package rentasad.library.db.helpers;

import lombok.Data;

@Data
public class ColumnNameAnnotationHelperTestObject
{
	@ColumnName("last_name")
	private String lastName;

}
