package rentasad.library.db.helpers.testClasses;

public enum PositionErrorTestEnum
{
	WRONG_VS4_SKU("Falsche VS/4-Artikelnummer"),
	NEW("Neu"),
	BOUGHT_VOUCHERS_NOT_TRANSFERRED("Gekaufter Gutschein wurde nicht transferiert");

	private String errorDescription;

	PositionErrorTestEnum(String errorDescription)
	{
		this.errorDescription = errorDescription;
	}
}
