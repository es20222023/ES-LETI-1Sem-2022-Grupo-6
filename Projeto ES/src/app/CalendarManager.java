package app;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;

public class CalendarManager {

	private ArrayList<CalendarEvent> events;

	public CalendarManager() {
		events = new ArrayList<CalendarEvent>();
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
			if (!events.contains(e))
				events.add(e);
		events.sort(null);
	}

	public ArrayList<CalendarEvent> getEvents() {
		return (ArrayList<CalendarEvent>)events.clone();
	}

	public ArrayList<CalendarEvent> getEventsBetweenDates(Instant dateStart, Instant dateEnd) {
		if (dateEnd == null || dateStart == null)
			return events;
		ArrayList<CalendarEvent> eventsToReturn = new ArrayList<>();
		for (CalendarEvent c : events)
			if (c.getDateStart().isAfter(dateStart) && c.getDateStart().isBefore(dateEnd)
					|| c.getDateStart().equals(dateStart) || c.getDateStart().equals(dateEnd))
				eventsToReturn.add(c);
		return eventsToReturn;
	}

//	public static void main(String[] args) {
//		CalendarManager c = new CalendarManager();
//		c.fillCalendarManager();
//		for(CalendarEvent e : c.events)
//			System.out.println(e.getDateStartString());
//	}
}
