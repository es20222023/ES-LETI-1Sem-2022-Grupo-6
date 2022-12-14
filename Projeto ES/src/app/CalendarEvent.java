package app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Classe que contém todas as informações importantes para definir um evento no
 * calendário
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
	 * @param dateStart   a data de início do evento
	 * @param dateEnd     a data de fim do evento
	 * @param summary     o sumário do evento
	 * @param description a descrição do evento
	 * @param location    a localização do evento
	 * @param username    o nome da pessoa a quem este evento está associado
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
	 * @param dateStart   a data de início do evento
	 * @param dateEnd     a data de fim do evento
	 * @param summary     o sumário do evento
	 * @param description a descrição do evento
	 * @param location    a localização do evento
	 * @param username    o nome da pessoa a quem este evento está associado
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
	 * Função que recebe uma string com uma data e a retorna como um objeto do tipo
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
	 * Função que dá parse a uma <b>String</b> com uma data e a retorna como uma
	 * objeto do tipo <b>Instant</b>
	 * 
	 * @param date data em texto
	 * @return objeto <b>Instant</b> com a data
	 */

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

	/**
	 * Obtém a data de início do evento
	 * 
	 * @return a data de início do evento
	 */

	public Instant getDateStart() {
		return dateStart;
	}

	/**
	 * Obtém a data de fim do evento
	 * 
	 * @return a data de fim do evento
	 */

	public Instant getDateEnd() {
		return dateEnd;
	}

	/**
	 * Obtém o sumário do evento
	 * 
	 * @return o sumário do evento
	 */

	public String getSummary() {
		return summary;
	}

	/**
	 * Obtém a descrição do evento
	 * 
	 * @return a descrição do evento
	 */

	public String getDescription() {
		return description;
	}

	/**
	 * Obtém a localização do evento
	 * 
	 * @return a localização do evento
	 */

	public String getLocation() {
		return location;
	}

	/**
	 * Obtém o nome da pessoa a qual este evento está associado
	 * 
	 * @return o nome da pessoa a qual este evento está associado
	 */

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		String string = "Data de início: " + getDateStart().toString() + "\nData de fim: " + getDateEnd().toString()
				+ "\nSumario: " + getSummary() + "\nDescricao: " + getDescription() + "\nLocalizacao: " + getLocation();
		return string;
	}

	/**
	 * Obtém a data de início do evento formatada de acordo com o método
	 * {@link #dateInstantToString()}
	 * 
	 * @return data de início do evento formatada
	 */

	public String getDateStartString() {
		return dateInstantToString(dateStart);
	}

	/**
	 * Obtém a data de fim do evento formatada de acordo com o método
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
