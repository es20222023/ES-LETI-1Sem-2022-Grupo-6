package app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class CalendarEvent {

	private Instant dateStart;
	private Instant dateEnd;
	private String summary;
	private String description;
	private String location;

	public CalendarEvent(String dateStart, String dateEnd, String summary, String description, String location) {
		this.dateStart = parseDate(dateStart);
		this.dateEnd = parseDate(dateEnd);
		this.summary = summary;
		this.description = description;
		this.location = location;
	}

	/**
	 * Fun��o que d� parse a uma string e retorna um objeto do tipo Date
	 * 
	 * @param date - String de data que segue o formato yyyymmddTHHmmssZ
	 * @return dateToReturn - objeto Date que guarda a data
	 */

	private Date modifyStringToDateFormat(String date) {
		
		date = date.replace("T", "");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date dateToReturn = format.parse(date);
			return dateToReturn;
		} catch (ParseException e) {
			System.out.println("Erro a dar parse a data");
			return null;
		}
	}

	private Instant parseDate(String date) {
		Instant instant;
		try {
			instant = Instant.parse(date);				//quando se descodifica o ficheiro JSON � mais simples fazer parse a data assim
		} catch (DateTimeParseException e) {
			Date d = modifyStringToDateFormat(date);	//j� a data no ficheiro texto � mais simples dar parse como Date e s� depois converter para Instant
			return d.toInstant();
		}

		return instant;
	}

//	private String removeLineBreaks(String string) {
//		string = string.replace("\\n", "");
//		return string;
//	}
	
	public Instant getDateStart() {
		return dateStart;
	}

	public Instant getDateEnd() {
		return dateEnd;
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public String toString() {
		String string = "Data de in�cio: " + getDateStart().toString() + "\nData de fim: " + getDateEnd().toString()
				+ "\nSumario: " + getSummary() + "\nDescricao: " + getDescription() + "\nLocalizacao: " + getLocation();
		return string;
	}
}
