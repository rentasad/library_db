package rentasad.library.db.dataObjects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TableSelectObject
{
	private String columnListString;
	private String quotedColumnListString;
	private String columnPlaceHolder;
	private String selectString;
}
