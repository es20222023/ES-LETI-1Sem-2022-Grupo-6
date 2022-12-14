package app;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * Classe que guarda os vários eventos de vários utilizadores e gere a interação
 * entre eles
 *
 */

public class CalendarManager {

	private ArrayList<CalendarEvent> events;
	private ArrayList<String> users;

	/**
	 * A máxima duração de uma reunião em minutos
	 */
	public static final int MAX_MEETING_DURATION_IN_MINUTES = 270;
	/**
	 * O tamanho de cada bloco no horário, é assim também o tempo mínimo de uma
	 * reunião
	 */
	public static final int SCHEDULE_BLOCK_SIZE_IN_MINUTES = 30;
	/**
	 * A hora em que é considerado o começo da manhã
	 */
	public static final int MORNING_HOUR_FOR_MEETING_START = 8;
	/**
	 * A hora em que é considerado o começo da tarde
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
	 * Preenche events com todos os eventos já existentes na pasta json_files
	 * 
	 */
	public void fillWithSavedEvents() {
		File directoryPath = new File(FileHandler.JSON_FILES_PATH);
		String contents[] = directoryPath.list();
		for (String path : contents)
			addEvents(FileHandler.decodeJSONFile(path));
	}

	/**
	 * Adiciona dados à <b>ArrayList</b> de eventos
	 * <p>
	 * Os eventos dados já existentes na <b>ArrayList</b> são ignorados
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
	 * Obtém a lista de eventos
	 * 
	 * @return a lista de eventos
	 */

	public ArrayList<CalendarEvent> getEvents() {
		return (ArrayList<CalendarEvent>) events.clone();
	}

	/**
	 * Obtém a lista de utilizadores
	 * 
	 * @return a lista de utilizadores
	 */

	public String[] getUsers() {
		String[] str = users.toArray(new String[0]);
		return str;
	}

	/**
	 * Diz se um determinado instante está dentro de um período definido por outros
	 * dois instantes
	 * 
	 * @param instant o instante a considerar
	 * @param start   o início do período
	 * @param end     o fim do período
	 * @return se o instante está contido no perído ou não
	 */

	private boolean isInstantBetweenInstants(Instant instant, Instant start, Instant end) {
		return instant.isAfter(start) && instant.isBefore(end);
	}

	/**
	 * Retorna os eventos entre duas datas a partir da lista de eventos
	 * 
	 * @param dateStart a data de início
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
	 * Verifica se o user está desponível no dado instante
	 * 
	 * @param user    o user a considerar
	 * @param instant o instante a considerar
	 * @return se o user está disponível no instante
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
	 * Verifica se o user está disponível entre dois dados instantes
	 * 
	 * @param user  o user a considerar
	 * @param start o início do período
	 * @param end   o fim do período
	 * @return se o user está disponível nesse período
	 */

	public boolean isUSerAvailable(String user, Instant start, Instant end) {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(start, end);
		for (CalendarEvent c : events)
			if (c.getUsername().equals(user))
				return false;
		return true;
	}

	/**
	 * verifica se todos os users dados estão disponíveis entre os instantes dados
	 * 
	 * @param start o início do período
	 * @param end   o fim do período
	 * @param users os users a considerar
	 * @return se todos os users estão disponíveis nesse período
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
//	 * Devolve users não disponíveis entre os instantes dados
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
//	 * Devolve todos os users não disponíveis no instante dado
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
	 * Devolve uma arraylist com um número de instantes ,de acordo com a duração da
	 * reunião, em que todos os users dados estão disponíveis
	 * 
	 * @param instant           o instante a partir do qual é para começar a
	 *                          preencher a lista eventuais instantes de reuniões;
	 *                          este instante deve ser
	 *                          <b>MORNING_HOUR_FOR_MEETING_START</b> ou
	 *                          <b>AFTERNOON_HOUR_FOR_MEETING_START</b>
	 * @param durationInMinutes a duração da reunião
	 * @param users             os users que vão fazer parte da reunião
	 * @param end               os último instante possível a que uma reunião pode
	 *                          ser marcada
	 * @return uma lista com todos os instantes onde é possível marcar uma reunião
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
	 * Retorna uma lista com os instantes onde é possível haver uma reunião de
	 * acordo com os parâmetros fornecidos
	 * 
	 * @param start             só é começada a procura de eventuais instantes de
	 *                          reunião no dia seguinte a este parâmetro
	 * @param end               a data máxima para a procura de eventuais reuniões
	 * @param morning           se é para ser uma reunião de manhã ou de tarde
	 * @param durationInMinutes a duração da reunião em minutos
	 * @param users             os users que participarão na reunião
	 * @return uma lista com todos os instantes onde pode haver reunião
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
	 * Sugere as melhores datas para haver uma reunião
	 * 
	 * @param users             os utilizadores a participar na reunião
	 * @param durationInMinutes a duração da reunião em minutos
	 * @param morning           se é para ser uma reunião de manhã ou de tarde
	 * @param maxDate           a data máxima para a procura de eventuais reuniões
	 * @param repeating         se a reunião se repete semanalmente ou não
	 * @return uma sugestão dos melhores instantes para haver reunião
	 */

	public ArrayList<Instant> sugestMeeting(String[] users, int durationInMinutes, boolean morning, Instant maxDate,
			boolean repeating) {
		if (durationInMinutes % SCHEDULE_BLOCK_SIZE_IN_MINUTES != 0)
			throw new IllegalArgumentException(
					"A duração da reunião tem que ser um múltiplo de " + SCHEDULE_BLOCK_SIZE_IN_MINUTES);

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
	 * Indica se o dado instante existe na lista dada de 7 em 7 dias até à dada data
	 * máxima
	 * 
	 * @param instant           o instante a considerar
	 * @param availableInstants a lista de instantes possíveis
	 * @param maxDate           a data máxima a ser considerada
	 * @return se o dado instante é apto para ser uma reunião semanal
	 */

	private boolean isAvailableWeekly(Instant instant, ArrayList<Instant> availableInstants, Instant maxDate) {
		for (Instant i = instant; i.isBefore(maxDate); i = i.plus(7, ChronoUnit.DAYS))
			if (!availableInstants.contains(i))
				return false;

		return true;
	}

	/**
	 * Cria uma reunião no ficheiro que guarda as reuniões para cada user
	 * 
	 * @param time              o início da reunião
	 * @param endDate           a data máxima da reunião, apenas a ser considerada
	 *                          no caso de ser uma reunião periódica
	 * @param durationInMinutes a duração da reunião em minutos
	 * @param repeating         se é uma reunião periódica
	 * @param users             os users a fazer parte da reunião
	 * @param location          a localização da reunião
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
