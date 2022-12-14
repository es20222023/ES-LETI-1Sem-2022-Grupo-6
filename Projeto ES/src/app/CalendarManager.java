package app;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * Classe que guarda os v�rios eventos de v�rios utilizadores e gere a intera��o
 * entre eles
 *
 */

public class CalendarManager {

	private ArrayList<CalendarEvent> events;
	private ArrayList<String> users;

	/**
	 * A m�xima dura��o de uma reuni�o em minutos
	 */
	public static final int MAX_MEETING_DURATION_IN_MINUTES = 270;
	/**
	 * O tamanho de cada bloco no hor�rio, � assim tamb�m o tempo m�nimo de uma
	 * reuni�o
	 */
	public static final int SCHEDULE_BLOCK_SIZE_IN_MINUTES = 30;
	/**
	 * A hora em que � considerado o come�o da manh�
	 */
	public static final int MORNING_HOUR_FOR_MEETING_START = 8;
	/**
	 * A hora em que � considerado o come�o da tarde
	 */
	public static final int AFTERNOON_HOUR_FOR_MEETING_START = 13;

	/**
	 * Construtor inicializa ambas as <b>ArrayList</b> de <b>CalendarEvent</b> e de
	 * <b>String</b>
	 */

	public CalendarManager() {
		events = new ArrayList<CalendarEvent>();
		users = new ArrayList<String>();
	}

	/**
	 * 
	 * Preenche events com todos os eventos j� existentes na pasta json_files
	 * 
	 */
	public void fillWithSavedEvents() {
		File directoryPath = new File(FileHandler.JSON_FILES_PATH);
		String contents[] = directoryPath.list();
		for (String path : contents)
			addEvents(FileHandler.decodeJSONFile(path));
	}

	/**
	 * Adiciona dados � <b>ArrayList</b> de eventos
	 * <p>
	 * Os eventos dados j� existentes na <b>ArrayList</b> s�o ignorados
	 * 
	 * @param list lista de eventos a adicionar
	 */

	public void addEvents(ArrayList<CalendarEvent> list) {
		for (CalendarEvent e : list)
			if (!events.contains(e)) {
				events.add(e);
				if (!users.contains(e.getUsername()))
					users.add(e.getUsername());
			}
		events.sort(null);
	}

	/**
	 * Obt�m a lista de eventos
	 * 
	 * @return a lista de eventos
	 */

	public ArrayList<CalendarEvent> getEvents() {
		return (ArrayList<CalendarEvent>) events.clone();
	}

	/**
	 * Obt�m a lista de utilizadores
	 * 
	 * @return a lista de utilizadores
	 */

	public String[] getUsers() {
		String[] str = users.toArray(new String[0]);
		return str;
	}

	/**
	 * Diz se um determinado instante est� dentro de um per�odo definido por outros
	 * dois instantes
	 * 
	 * @param instant o instante a considerar
	 * @param start   o in�cio do per�odo
	 * @param end     o fim do per�odo
	 * @return se o instante est� contido no per�do ou n�o
	 */

	private boolean isInstantBetweenInstants(Instant instant, Instant start, Instant end) {
		return instant.isAfter(start) && instant.isBefore(end);
	}

	/**
	 * Retorna os eventos entre duas datas a partir da lista de eventos
	 * 
	 * @param dateStart a data de in�cio
	 * @param dateEnd   a data de fim
	 * @return os eventos entre as duas datas
	 */

	public ArrayList<CalendarEvent> getEventsBetweenDates(Instant dateStart, Instant dateEnd) {
		if (dateEnd == null || dateStart == null)
			return events;
		ArrayList<CalendarEvent> eventsToReturn = new ArrayList<>();
		for (CalendarEvent c : events) {
			if (isInstantBetweenInstants(c.getDateStart(), dateStart, dateEnd) || c.getDateStart().equals(dateStart)
					|| c.getDateEnd().equals(dateEnd) || isInstantBetweenInstants(c.getDateEnd(), dateStart, dateEnd)
					|| (c.getDateStart().isBefore(dateStart) && c.getDateEnd().isAfter(dateEnd)))
				eventsToReturn.add(c);
		}
		return eventsToReturn;
	}

	/**
	 * Verifica se o user est� despon�vel no dado instante
	 * 
	 * @param user    o user a considerar
	 * @param instant o instante a considerar
	 * @return se o user est� dispon�vel no instante
	 */

	public boolean isUserAvailable(String user, Instant instant) {
		for (CalendarEvent c : events)
			if (c.getUsername().equals(user))
				if (isInstantBetweenInstants(instant, c.getDateStart(), c.getDateEnd())
						|| c.getDateStart().equals(instant))
					return false;
		return true;
	}

	/**
	 * Verifica se o user est� dispon�vel entre dois dados instantes
	 * 
	 * @param user  o user a considerar
	 * @param start o in�cio do per�odo
	 * @param end   o fim do per�odo
	 * @return se o user est� dispon�vel nesse per�odo
	 */

	public boolean isUSerAvailable(String user, Instant start, Instant end) {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(start, end);
		for (CalendarEvent c : events)
			if (c.getUsername().equals(user))
				return false;
		return true;
	}

	/**
	 * verifica se todos os users dados est�o dispon�veis entre os instantes dados
	 * 
	 * @param start o in�cio do per�odo
	 * @param end   o fim do per�odo
	 * @param users os users a considerar
	 * @return se todos os users est�o dispon�veis nesse per�odo
	 */

	public boolean areAllUsersAvailable(Instant start, Instant end, String[] users) {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(start, end);
		for (CalendarEvent c : events) {
			for (String user : users)
				if (c.getUsername().equals(user))
					return false;
		}
		return true;
	}

//	/**
//	 * Devolve users n�o dispon�veis entre os instantes dados
//	 * 
//	 * @param start
//	 * @param end
//	 * @return
//	 */
//
//	public ArrayList<String> getUnavailableUsers(Instant start, Instant end) {
//		ArrayList<String> usersToReturn = new ArrayList<String>();
//		for (String user : users)
//			if (!isUSerAvailable(user, start, end))
//				usersToReturn.add(user);
//		return usersToReturn;
//	}

//	/**
//	 * Devolve todos os users n�o dispon�veis no instante dado
//	 * 
//	 * @param instant
//	 * @return
//	 */
//
//	public ArrayList<String> getUnavailableUsers(Instant instant) {
//		ArrayList<String> usersToReturn = new ArrayList<String>();
//		for (String user : users)
//			if (!isUserAvailable(user, instant))
//				usersToReturn.add(user);
//		return usersToReturn;
//	}

	/**
	 * Devolve uma arraylist com um n�mero de instantes ,de acordo com a dura��o da
	 * reuni�o, em que todos os users dados est�o dispon�veis
	 * 
	 * @param instant           o instante a partir do qual � para come�ar a
	 *                          preencher a lista eventuais instantes de reuni�es;
	 *                          este instante deve ser
	 *                          <b>MORNING_HOUR_FOR_MEETING_START</b> ou
	 *                          <b>AFTERNOON_HOUR_FOR_MEETING_START</b>
	 * @param durationInMinutes a dura��o da reuni�o
	 * @param users             os users que v�o fazer parte da reuni�o
	 * @param end               os �ltimo instante poss�vel a que uma reuni�o pode
	 *                          ser marcada
	 * @return uma lista com todos os instantes onde � poss�vel marcar uma reuni�o
	 */

	private ArrayList<Instant> fillBlocks(Instant instant, int durationInMinutes, String[] users, Instant end) {
		ArrayList<Instant> instants = new ArrayList<Instant>();

		for (int i = 0; i < MAX_MEETING_DURATION_IN_MINUTES && instant.plus(i, ChronoUnit.MINUTES)
				.isBefore(end.plusSeconds(1)); i += SCHEDULE_BLOCK_SIZE_IN_MINUTES) {

			Instant newMeetingStartTime = instant.plus(i, ChronoUnit.MINUTES);
			if (areAllUsersAvailable(newMeetingStartTime,
					newMeetingStartTime.plus(SCHEDULE_BLOCK_SIZE_IN_MINUTES, ChronoUnit.MINUTES), users)) {
				instants.add(newMeetingStartTime);
			}
		}
		return instants;
	}

	/**
	 * Retorna uma lista com os instantes onde � poss�vel haver uma reuni�o de
	 * acordo com os par�metros fornecidos
	 * 
	 * @param start             s� � come�ada a procura de eventuais instantes de
	 *                          reuni�o no dia seguinte a este par�metro
	 * @param end               a data m�xima para a procura de eventuais reuni�es
	 * @param morning           se � para ser uma reuni�o de manh� ou de tarde
	 * @param durationInMinutes a dura��o da reuni�o em minutos
	 * @param users             os users que participar�o na reuni�o
	 * @return uma lista com todos os instantes onde pode haver reuni�o
	 */

	private ArrayList<Instant> createInstantsForMeetings(Instant start, Instant end, boolean morning,
			int durationInMinutes, String users[]) {
		ArrayList<Instant> instants = new ArrayList<Instant>();
		Date tomorrow = Date.from(start.plus(1, ChronoUnit.DAYS));

		if (morning) {
			Date date = new Date(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDate(),
					MORNING_HOUR_FOR_MEETING_START, 0);

			Instant instant = date.toInstant();

			while (instant.isBefore(end)) {
				instants.addAll(fillBlocks(instant, durationInMinutes, users, end));
				instant = instant.plus(1, ChronoUnit.DAYS);
			}
		} else {
			Date date = new Date(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDate(),
					AFTERNOON_HOUR_FOR_MEETING_START, 0);

			Instant instant = date.toInstant();

			while (instant.isBefore(end)) {
				instants.addAll(fillBlocks(instant, durationInMinutes, users, end));
				instant = instant.plus(1, ChronoUnit.DAYS);
			}
		}

		return instants;
	}

	/**
	 * Sugere as melhores datas para haver uma reuni�o
	 * 
	 * @param users             os utilizadores a participar na reuni�o
	 * @param durationInMinutes a dura��o da reuni�o em minutos
	 * @param morning           se � para ser uma reuni�o de manh� ou de tarde
	 * @param maxDate           a data m�xima para a procura de eventuais reuni�es
	 * @param repeating         se a reuni�o se repete semanalmente ou n�o
	 * @return uma sugest�o dos melhores instantes para haver reuni�o
	 */

	public ArrayList<Instant> sugestMeeting(String[] users, int durationInMinutes, boolean morning, Instant maxDate,
			boolean repeating) {
		if (durationInMinutes % SCHEDULE_BLOCK_SIZE_IN_MINUTES != 0)
			throw new IllegalArgumentException(
					"A dura��o da reuni�o tem que ser um m�ltiplo de " + SCHEDULE_BLOCK_SIZE_IN_MINUTES);

		Instant now = Instant.now();

		ArrayList<Instant> availableInstants = createInstantsForMeetings(now, maxDate, morning, durationInMinutes,
				users);

		if (repeating) {

			ArrayList<Instant> repeatingInstants = new ArrayList<Instant>();

			for (Instant i : availableInstants)
				if (isAvailableWeekly(i, availableInstants, maxDate))
					repeatingInstants.add(i);

			return repeatingInstants;
		}

		return availableInstants;
	}

	/**
	 * Indica se o dado instante existe na lista dada de 7 em 7 dias at� � dada data
	 * m�xima
	 * 
	 * @param instant           o instante a considerar
	 * @param availableInstants a lista de instantes poss�veis
	 * @param maxDate           a data m�xima a ser considerada
	 * @return se o dado instante � apto para ser uma reuni�o semanal
	 */

	private boolean isAvailableWeekly(Instant instant, ArrayList<Instant> availableInstants, Instant maxDate) {
		for (Instant i = instant; i.isBefore(maxDate); i = i.plus(7, ChronoUnit.DAYS))
			if (!availableInstants.contains(i))
				return false;

		return true;
	}

	/**
	 * Cria uma reuni�o no ficheiro que guarda as reuni�es para cada user
	 * 
	 * @param time              o in�cio da reuni�o
	 * @param endDate           a data m�xima da reuni�o, apenas a ser considerada
	 *                          no caso de ser uma reuni�o peri�dica
	 * @param durationInMinutes a dura��o da reuni�o em minutos
	 * @param repeating         se � uma reuni�o peri�dica
	 * @param users             os users a fazer parte da reuni�o
	 * @param location          a localiza��o da reuni�o
	 */

	public void createMeeting(Instant time, Instant endDate, int durationInMinutes, boolean repeating, String[] users,
			String location) {

		Instant dateStart = time;
		Instant dateEnd = time.plus(durationInMinutes, ChronoUnit.MINUTES);

		if (repeating) {
			for (Instant i = time; i.isBefore(endDate); i = i.plus(7, ChronoUnit.DAYS)) {
				for (String user : users) {
					CalendarEvent meeting = new CalendarEvent(dateStart, dateEnd, "Meeting", "Meeting", location, user);
					FileHandler.addMeetingToJSONFile(meeting);
				}
			}
		} else
			for (String user : users) {
				CalendarEvent meeting = new CalendarEvent(dateStart, dateEnd, "Meeting", "Meeting", location, user);
				FileHandler.addMeetingToJSONFile(meeting);
			}

		System.out.println("Meeting scheduled for " + time + (repeating ? " repeating" : ""));

	}
}
