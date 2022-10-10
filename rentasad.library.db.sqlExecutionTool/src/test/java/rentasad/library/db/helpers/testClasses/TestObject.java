package rentasad.library.db.helpers.testClasses;

import lombok.Data;
import rentasad.library.db.helpers.ColumnName;
import rentasad.library.db.helpers.DBPersisted;
import rentasad.library.db.helpers.testClasses.StatusEnum;

import java.io.InputStream;

@Data
@DBPersisted("greetingcards")
public class TestObject
{
	@DBPersisted("id")
	@ColumnName("id")
	private Long id;
	@DBPersisted("order_number")
	@ColumnName("order_number")
	private Long orderNumber;
	@DBPersisted("svg_greetingcard")
	@ColumnName("svg_greetingcard")
	private String svgGreetingCard;
	@DBPersisted("pdf_inputstream")
	@ColumnName("pdf_inputstream")
	private InputStream pdfInputStream;
	@DBPersisted("vs4_order_number")
	@ColumnName("vs4_order_number")
	private Long vs4OrderNumber;
	@DBPersisted("vs4_invoice_number")
	@ColumnName("vs4_invoice_number")
	private Long vs4InvoiceNumber;
	@DBPersisted("vs4_customer_number")
	@ColumnName("vs4_customer_number")
	private Long vs4CustomerNumber;
	@DBPersisted("manually_provided")
	@ColumnName("manually_provided")
	private Boolean manuallyProvided;
	@DBPersisted("status")
	@ColumnName("status")
	private StatusEnum status;

}
