package app;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class CalendarManager {

	private ArrayList<CalendarEvent> events;
	private ArrayList<String> users;
	public static final long SECONDS_IN_AN_HOUR = 3600;
	public static final long SECONDS_IN_A_DAY = 86400;
	public static final int MAX_MEETING_DURATION_IN_MINUTES = 270;
	public static final int SCHEDULE_BLOCK_SIZE_IN_MINUTES = 30;
	public static final int NUM_MAX_BLOCKS = MAX_MEETING_DURATION_IN_MINUTES / SCHEDULE_BLOCK_SIZE_IN_MINUTES;
	public static final int MORNING_HOUR_FOR_MEETING_START = 8;
	public static final int AFTERNOON_HOUR_FOR_MEETING_START = 13;

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

	public void addEvents(ArrayList<CalendarEvent> list) {
		for (CalendarEvent e : list)
			if (!events.contains(e)) {
				events.add(e);
				if (!users.contains(e.getUsername()))
					users.add(e.getUsername());
			}
		events.sort(null);
	}

	public ArrayList<CalendarEvent> getEvents() {
		return (ArrayList<CalendarEvent>) events.clone();
	}

	public String[] getUsers() {
		String[] str = users.toArray(new String[0]);
		return str;
	}

	private boolean isInstantBetweenInstants(Instant instant, Instant start, Instant end) {
		return instant.isAfter(start) && instant.isBefore(end);
	}

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
	 * @param user
	 * @param instant
	 * @return
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
	 * @param user
	 * @param start
	 * @param end
	 * @return
	 */

	public boolean isUSerAvailable(String user, Instant start, Instant end) {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(start, end);
		for (CalendarEvent c : events)
			if (c.getUsername().equals(user))
				return false;
		return true;
	}

	public boolean areAllUsersAvailable(Instant start, Instant end, String[] users) {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(start, end);
		for (CalendarEvent c : events) {
			for (String user : users)
				if (c.getUsername().equals(user))
					return false;
		}
		return true;
	}

	/**
	 * Devolve users não disponíveis entre os instantes dados
	 * 
	 * @param start
	 * @param end
	 * @return
	 */

	public ArrayList<String> getUnavailableUsers(Instant start, Instant end) {
		ArrayList<String> usersToReturn = new ArrayList<String>();
		for (String user : users)
			if (!isUSerAvailable(user, start, end))
				usersToReturn.add(user);
		return usersToReturn;
	}

	/**
	 * Devolve todos os users não disponíveis no instante dado
	 * 
	 * @param instant
	 * @return
	 */

	public ArrayList<String> getUnavailableUsers(Instant instant) {
		ArrayList<String> usersToReturn = new ArrayList<String>();
		for (String user : users)
			if (!isUserAvailable(user, instant))
				usersToReturn.add(user);
		return usersToReturn;
	}

	/**
	 * Devolve uma arraylist com um número de instantes de acordo com a duração da
	 * reunião
	 * 
	 * 
	 * @param instant
	 * @param end
	 * @param numBlocks Caso seja igual a 1, quer dizer que é um bloco de 270
	 *                  minutos, logo só cabe um, seja no período da manhã ou da
	 *                  tarde; 2 cada bloco tem 180 min; 3 cada bloco tem 90 min
	 * @return
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
	 * sugere as melhores datas para haver uma reunião uníca ou periódica
	 * 
	 * @param users
	 * @param durationInSeconds
	 * @param morning
	 * @param maxDate
	 * @param repeating         se repete semanalmente ou não
	 * @return
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

	private boolean isAvailableWeekly(Instant instant, ArrayList<Instant> availableInstants, Instant maxDate) {
		for (Instant i = instant; i.isBefore(maxDate); i = i.plus(7, ChronoUnit.DAYS))
			if (!availableInstants.contains(i))
				return false;

		return true;
	}

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

	public static void main(String[] args) {
		CalendarManager c = new CalendarManager();
		c.fillWithSavedEvents();

		String[] users = new String[2];
		users[0] = "thgas";
		users[1] = "tamos";

		Instant date = new Date(2024 - 1900, 0, 1).toInstant();

		c.createMeeting(date, null, 30, false, users, "No Discord");

	}

//	public static void main(String[] args) {
//		CalendarManager c = new CalendarManager();
//		c.fillWithSavedEvents();
//
//		String[] users = new String[2];
//		users[0] = "thgas";
//		users[1] = "tamos";
//
//		Instant maxDate = new Date(2022 - 1900, 11, 30).toInstant();
//
//		ArrayList<Instant> i = c.sugestMeeting(users, 30, false, maxDate, true);
//
//		for (Instant ins : i)
//			System.out.println(ins);
//
//	}
}
