package app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Classe que cont�m todas as informa��es importantes para definir um evento no
 * calend�rio
 * 
 * @author thgas
 *
 */

public class CalendarEvent implements Comparable<CalendarEvent> {

	private Instant dateStart;
	private Instant dateEnd;
	private String summary;
	private String description;
	private String location;
	private String username;

	/**
	 * Construtor que recebe datas no formato <b>String</b>
	 * 
	 * @param dateStart   a data de in�cio do evento
	 * @param dateEnd     a data de fim do evento
	 * @param summary     o sum�rio do evento
	 * @param description a descri��o do evento
	 * @param location    a localiza��o do evento
	 * @param username    o nome da pessoa a quem este evento est� associado
	 */

	public CalendarEvent(String dateStart, String dateEnd, String summary, String description, String location,
			String username) {
		this.dateStart = parseDate(dateStart);
		this.dateEnd = parseDate(dateEnd);
		this.summary = summary;
		this.description = description;
		this.location = location;
		this.username = username;
	}

	/**
	 * Construtor que recebe datas no formato <b>Instant</b>
	 * 
	 * @param dateStart   a data de in�cio do evento
	 * @param dateEnd     a data de fim do evento
	 * @param summary     o sum�rio do evento
	 * @param description a descri��o do evento
	 * @param location    a localiza��o do evento
	 * @param username    o nome da pessoa a quem este evento est� associado
	 */

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
	 * Fun��o que recebe uma string com uma data e a retorna como um objeto do tipo
	 * <b>Date</b>
	 * 
	 * @param date String de data que segue o formato <i>yyyymmddTHHmmssZ</i>
	 * @return objeto Date que guarda a data
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

	/**
	 * Fun��o que d� parse a uma <b>String</b> com uma data e a retorna como uma
	 * objeto do tipo <b>Instant</b>
	 * 
	 * @param date data em texto
	 * @return objeto <b>Instant</b> com a data
	 */

	private Instant parseDate(String date) {
		Instant instant;
		try {
			instant = Instant.parse(date); // quando se descodifica o ficheiro JSON � mais simples fazer parse a data
											// assim
		} catch (DateTimeParseException e) {
			Date d = modifyStringToDateFormat(date); // j� a data no ficheiro texto � mais simples dar parse como Date e
														// s� depois converter para Instant
			return d.toInstant();
		}

		return instant;
	}

	/**
	 * Obt�m a data de in�cio do evento
	 * 
	 * @return a data de in�cio do evento
	 */

	public Instant getDateStart() {
		return dateStart;
	}

	/**
	 * Obt�m a data de fim do evento
	 * 
	 * @return a data de fim do evento
	 */

	public Instant getDateEnd() {
		return dateEnd;
	}

	/**
	 * Obt�m o sum�rio do evento
	 * 
	 * @return o sum�rio do evento
	 */

	public String getSummary() {
		return summary;
	}

	/**
	 * Obt�m a descri��o do evento
	 * 
	 * @return a descri��o do evento
	 */

	public String getDescription() {
		return description;
	}

	/**
	 * Obt�m a localiza��o do evento
	 * 
	 * @return a localiza��o do evento
	 */

	public String getLocation() {
		return location;
	}

	/**
	 * Obt�m o nome da pessoa a qual este evento est� associado
	 * 
	 * @return o nome da pessoa a qual este evento est� associado
	 */

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		String string = "Data de in�cio: " + getDateStart().toString() + "\nData de fim: " + getDateEnd().toString()
				+ "\nSumario: " + getSummary() + "\nDescricao: " + getDescription() + "\nLocalizacao: " + getLocation();
		return string;
	}

	/**
	 * Obt�m a data de in�cio do evento formatada de acordo com o m�todo
	 * {@link #dateInstantToString()}
	 * 
	 * @return data de in�cio do evento formatada
	 */

	public String getDateStartString() {
		return dateInstantToString(dateStart);
	}

	/**
	 * Obt�m a data de fim do evento formatada de acordo com o m�todo
	 * {@link #dateInstantToString()}
	 * 
	 * @return data de fim do evento formatada
	 */

	public String getDateEndString() {
		return dateInstantToString(dateEnd);
	}

	/**
	 * Devolve a data em texto formatada de acordo com: YYYY/MM/DD HH:mm
	 * 
	 * @param instant data a ser formatada
	 * @return a data em texto
	 */

	public static String dateInstantToString(Instant instant) {
		Date date = Date.from(instant);
		String str = (date.getYear() + 1900) + "/" + date.getMonth() + "/" + date.getDate() + " " + date.getHours()
				+ ":" + (date.getMinutes() == 0 ? "00" : date.getMinutes());
		return str;
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
