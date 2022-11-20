package app;

import java.util.ArrayList;

public class Calendar {

	private ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();

	public Calendar(String path, String newFilePath) {
		this.calendarEvents = FileHandler.createNewCalendarFile(path, newFilePath);
	}

	public Calendar(String jsonFile) {
		this.calendarEvents = FileHandler.decodeJSONFile(jsonFile);
	}

	public ArrayList<CalendarEvent> getCalendarEvents() {
		return calendarEvents;
	}
}
