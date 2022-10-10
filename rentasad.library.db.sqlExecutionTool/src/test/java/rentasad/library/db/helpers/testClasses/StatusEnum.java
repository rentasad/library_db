package rentasad.library.db.helpers.testClasses;

public enum StatusEnum
{

	WRONG_VS4_SKU("Falsche VS/4-Artikelnummer"),
	NEW("Neu"),
	BOUGHT_VOUCHERS_NOT_TRANSFERRED("Gekaufter Gutschein wurde nicht transferiert");

	private String errorDescription;

	StatusEnum(String errorDescription)
	{
		this.errorDescription = errorDescription;
	}

}
