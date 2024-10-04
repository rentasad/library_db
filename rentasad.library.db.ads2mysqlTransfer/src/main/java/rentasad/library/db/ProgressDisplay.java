package rentasad.library.db;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProgressDisplay
{

	// Standardbreite der Progressbar
	private int progressBarWidth = 50;
	private String message = "";

	// Für die Zeitschätzung
	private long startTime = 0;

	// Konstruktor mit Standardbreite
	public ProgressDisplay()
	{
	}

	public static int getProgressInPercent(int counter,int size)
	{
		if (size == 0)
		{
			return 0;
		}
		return (int) (counter * 100.0 / size);
	}

	// Konstruktor, um die Breite der Progressbar zu setzen
	public ProgressDisplay(int progressBarWidth)
	{
		this.progressBarWidth = progressBarWidth;
	}

	// Setze eine Nachricht, die über dem Fortschritt angezeigt wird
	public void setMessage(String message)
	{
		this.message = message;
		// Zeige die Nachricht, ohne die Progressbar zu beeinflussen
		displayMessage();
	}

	// Zeige die Nachricht eine Zeile über dem Fortschritt an
	private void displayMessage()
	{
		System.out.print("\033[1A"); // Bewege den Cursor eine Zeile nach oben
		System.out.print("\r" + message + "\n"); // Überschreibe die Zeile mit der Nachricht
	}

	// Zeitschätzung starten
	public void startProgress()
	{
		this.startTime = System.currentTimeMillis();
	}

	// Variante 1: Direkte prozentuale Fortschrittsanzeige mit Zeitschätzung
	public void displayPercentageProgress(BigDecimal percentage, BigDecimal total)
	{
		long estimatedTimeRemaining = estimateTimeRemaining(percentage, total);
		displayMessage(); // Zeige die Nachricht
		System.out.print("\rFortschritt: " + percentage.setScale(2, RoundingMode.HALF_UP) + "%, Restzeit: " + formatTime(estimatedTimeRemaining));
	}

	// Variante 2: Berechne den prozentualen Fortschritt anhand des aktuellen Fortschritts und des Ziels
	public void displayProgressWithPercentage(BigDecimal current, BigDecimal total)
	{
		BigDecimal percentage = current.divide(total, 5, RoundingMode.HALF_UP)
									   .multiply(BigDecimal.valueOf(100));
		long estimatedTimeRemaining = estimateTimeRemaining(current, total);
		displayMessage(); // Zeige die Nachricht
		System.out.print("\rFortschritt: " + percentage.setScale(2, RoundingMode.HALF_UP) + "%, Restzeit: " + formatTime(estimatedTimeRemaining));
	}

	// Variante 3: Gib den Fortschritt in absoluten Werten an (z.B. 40 von 400) und zeige zusätzlich Prozent und Zeitschätzung
	public void displayAbsoluteProgress(int currentInt, int totalInt)
	{
		BigDecimal current = BigDecimal.valueOf(currentInt);
		BigDecimal total = BigDecimal.valueOf(totalInt);
		BigDecimal percentage = current.divide(total, 5, RoundingMode.HALF_UP)
									   .multiply(BigDecimal.valueOf(100));
		long estimatedTimeRemaining = estimateTimeRemaining(current, total);
		displayMessage(); // Zeige die Nachricht
		System.out.print("\rFortschritt: " + current + " von " + total + " (" + percentage.setScale(2, RoundingMode.HALF_UP) + "%), Restzeit: " + formatTime(estimatedTimeRemaining));
	}

	// Variante 4: Zeichne eine Progressbar (mit optionaler Breite) mit Zeitschätzung
	public void displayProgressBar(BigDecimal percentage, BigDecimal total)
	{
		displayProgressBar(percentage, total, this.progressBarWidth);
	}

	// Variante 4 (mit optionaler Breite): Zeichne eine Progressbar mit angegebener Breite und Zeitschätzung
	public void displayProgressBar(BigDecimal percentage, BigDecimal total, int barWidth)
	{
		int completedBars = percentage.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP)
									  .multiply(BigDecimal.valueOf(barWidth))
									  .intValue();
		StringBuilder progressBar = new StringBuilder("[");

		// Fülle die Fortschrittsanzeige
		for (int i = 0; i < completedBars; i++)
		{
			progressBar.append("=");
		}
		// Fülle den Rest der Progressbar
		for (int i = completedBars; i < barWidth; i++)
		{
			progressBar.append(" ");
		}

		progressBar.append("] ")
				   .append(percentage.setScale(2, RoundingMode.HALF_UP))
				   .append("%");

		long estimatedTimeRemaining = estimateTimeRemaining(percentage, total);
		displayMessage(); // Zeige die Nachricht
		System.out.print("\r" + progressBar + " Restzeit: " + formatTime(estimatedTimeRemaining));
	}

	// Variante 4 (mit absoluten Werten): Berechne Prozentsatz anhand von current/total und zeichne die Progressbar mit Zeitschätzung
	public void displayProgressBarWithAbsolute(BigDecimal current, BigDecimal total)
	{
		BigDecimal percentage = current.divide(total, 5, RoundingMode.HALF_UP)
									   .multiply(BigDecimal.valueOf(100));
		displayProgressBar(percentage, total); // Zeige die Progressbar basierend auf dem berechneten Prozentsatz
	}

	// Methode zur Schätzung der verbleibenden Zeit basierend auf den Schritten
	private long estimateTimeRemaining(BigDecimal current, BigDecimal total)
	{
		if (current.compareTo(BigDecimal.ZERO) <= 0 || current.compareTo(total) >= 0)
		{
			return -1; // Unbekannte Restzeit oder bereits abgeschlossen
		}

		// Berechne die verstrichene Zeit in Millisekunden
		long currentTime = System.currentTimeMillis();
		if (startTime == 0)
		{
			startProgress();
		}
		long elapsedTime = currentTime - startTime;

		// Berechne die Zeit pro Schritt
		BigDecimal timePerStep = BigDecimal.valueOf(elapsedTime)
										   .divide(current, 5, RoundingMode.HALF_UP);

		// Berechne die verbleibenden Schritte
		BigDecimal remainingSteps = total.subtract(current);

		// Berechne die verbleibende Zeit
		BigDecimal remainingTime = timePerStep.multiply(remainingSteps);

		return remainingTime.longValue();
	}

	// Formatierung der Zeit in Stunden, Minuten und Sekunden für bessere Lesbarkeit
	private String formatTime(long millis)
	{
		if (millis < 0)
		{
			return "unbekannt"; // Für den Fall, dass der Fortschritt 0 ist oder Fehler
		}
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		minutes = minutes % 60;
		seconds = seconds % 60;

		if (days > 0)
		{
			return String.format("%d days, %d h, %d min", days, hours, minutes);
		}

		if (hours > 0)
		{
			return String.format("%d h, %d min", hours, minutes);
		}
		else if (minutes > 0)
		{
			return String.format("%d min, %d sec", minutes, seconds);
		}
		else
		{
			return String.format("%d sec", seconds);
		}
	}

	// Beispielaufruf für die Klasse
	public static void main(String[] args) throws InterruptedException
	{
		ProgressDisplay progressDisplay = new ProgressDisplay();

		// Progressbar mit Zeitschätzung starten
		progressDisplay.setMessage("Customer 22122");
		progressDisplay.startProgress(); // Start der Zeitmessung

		// Beispiel für Variante 4: Progressbar mit Standardbreite und Zeitschätzung
		BigDecimal totalSteps = new BigDecimal(37206);
		for (BigDecimal i = BigDecimal.ZERO; i.compareTo(totalSteps) <= 0; i = i.add(new BigDecimal(100)))
		{
			progressDisplay.displayAbsoluteProgress(i.intValue(), totalSteps.intValue());
			Thread.sleep(500); // Simuliere 0,5s pro Schritt
		}

		System.out.println("\nAufgabe abgeschlossen.");
	}
}
