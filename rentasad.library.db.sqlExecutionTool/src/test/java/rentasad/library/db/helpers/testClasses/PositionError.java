package rentasad.library.db.helpers.testClasses;



import lombok.Data;
import rentasad.library.db.helpers.ColumnName;
import rentasad.library.db.helpers.DBPersisted;

@DBPersisted("position_errors")
@Data
public class PositionError
{
	@DBPersisted("order_id")
	@ColumnName("order_id")
	private Long orderId;
	@DBPersisted("position_id")
	@ColumnName("position_id")
	private Long PositionId;
	@DBPersisted("error")
	@ColumnName("error")
	private PositionErrorTestEnum errorEnum;
}

