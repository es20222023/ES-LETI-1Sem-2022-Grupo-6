package app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class CalendarEvent {

	private Instant dateStart;
	private Instant dateEnd;
	private String summary;
	private String description;
	private String location;

	public CalendarEvent(String dateStart, String dateEnd, String summary, String description, String location) {
		this.dateStart = parseDate(dateStart).toInstant();
		this.dateEnd = parseDate(dateEnd).toInstant();
		this.summary = summary;
		this.description = description;
		this.location = location;
	}

	/**
	 * Função que dá parse a uma string e retorna um objeto do tipo Date
	 * 
	 * @param date - String de data que segue o formato yyyymmddTHHmmssZ
	 * @return dateToReturn - objeto Date que guarda a data
	 */

	private Date parseDate(String date) {
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
}
