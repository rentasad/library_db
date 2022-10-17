package rentasad.library.db.helpers.testClasses;

import lombok.Data;
import rentasad.library.db.helpers.ColumnName;
import rentasad.library.db.helpers.DBPersisted;
import rentasad.library.db.helpers.testClasses.StatusEnum;

import java.io.InputStream;

@Data
@DBPersisted("test_objects")
public class TestObject
{
	@ColumnName("id")
	private Long id;
	@ColumnName("order_number")
	private Long orderNumber;
	@ColumnName("svg_greetingcard")
	private String svgGreetingCard;
	@ColumnName("pdf_inputstream")
	private InputStream pdfInputStream;
	@ColumnName("vs4_order_number")
	private Long vs4OrderNumber;
	@ColumnName("vs4_invoice_number")
	private Long vs4InvoiceNumber;
	@ColumnName("vs4_customer_number")
	private Long vs4CustomerNumber;
	@ColumnName("manually_provided")
	private Boolean manuallyProvided;
	@ColumnName("status")
	private StatusEnum status;

}
