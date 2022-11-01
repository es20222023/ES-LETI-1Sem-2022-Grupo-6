package app;

import java.util.ArrayList;

public class CalendarManager {

	private ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();

	public CalendarManager(String path, String newFilePath) {
		this.calendarEvents = FileHandler.createNewCalendarFile(path, newFilePath);
	}

	public ArrayList<CalendarEvent> getCalendarEvents() {
		return calendarEvents;
	}
}
