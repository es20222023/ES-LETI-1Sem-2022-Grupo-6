package app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class CalendarEvent implements Comparable<CalendarEvent> {

	private Instant dateStart;
	private Instant dateEnd;
	private String summary;
	private String description;
	private String location;
	private String username;

	public CalendarEvent(String dateStart, String dateEnd, String summary, String description, String location,
			String username) {
		this.dateStart = parseDate(dateStart);
		this.dateEnd = parseDate(dateEnd);
		this.summary = summary;
		this.description = description;
		this.location = location;
		this.username = username;
	}

	public CalendarEvent(Instant dateStart, Instant dateEnd, String summary, String description, String location,
			String username) {
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.summary = summary;
		this.description = description;
		this.location = location;
		this.username = username;
	}

	/**
	 * Função que dá parse a uma string e retorna um objeto do tipo Date
	 * 
	 * @param date - String de data que segue o formato yyyymmddTHHmmssZ
	 * @return dateToReturn - objeto Date que guarda a data
	 */

	private Date modifyStringToDateFormat(String date) {

		date = date.replace("T", "");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date dateToReturn = format.parse(date);
			// mudar para o fuso horario correto
			dateToReturn.setHours(dateToReturn.getHours() + 1);
			return dateToReturn;
		} catch (ParseException e) {
			System.out.println("Erro a dar parse a data");
			return null;
		}
	}

	private Instant parseDate(String date) {
		Instant instant;
		try {
			instant = Instant.parse(date); // quando se descodifica o ficheiro JSON é mais simples fazer parse a data
											// assim
		} catch (DateTimeParseException e) {
			Date d = modifyStringToDateFormat(date); // já a data no ficheiro texto é mais simples dar parse como Date e
														// só depois converter para Instant
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

	public String getUsername() {
		return username;
	}

	public String toString() {
		String string = "Data de início: " + getDateStart().toString() + "\nData de fim: " + getDateEnd().toString()
				+ "\nSumario: " + getSummary() + "\nDescricao: " + getDescription() + "\nLocalizacao: " + getLocation();
		return string;
	}

	public String getDateStartString() {
		Date date = Date.from(dateStart);
		return date.toString().replace("WEST", "").replace("WET", "");
	}

	public String getDateEndString() {
		Date date = Date.from(dateEnd);
		return date.toString().replace("WEST", "").replace("WET", "");
	}

	@Override
	public int compareTo(CalendarEvent c) {
		return this.dateStart.compareTo(c.dateStart);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarEvent event = (CalendarEvent) obj;

		return username.equals(event.username) && dateStart.equals(event.dateStart) && dateEnd.equals(event.dateEnd)
				&& summary.equals(event.summary) && description.equals(event.description)
				&& location.equals(event.location);
	}
}
