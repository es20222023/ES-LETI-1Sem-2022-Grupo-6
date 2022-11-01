package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {

	/**
	 * Funcao que le textfile e retorna uma arraylist em que cada elemento é um dos
	 * eventos do calendario
	 * 
	 * @param path - caminho do ficheiro que se quer ler
	 * @return calendar
	 * @throws FileNotFoundException
	 */

	public static ArrayList<CalendarEvent> readTextFile(String path) throws FileNotFoundException {

		File text = new File(path);
		Scanner scanner = new Scanner(text);

		ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
		String calendarEvent = "";

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

//			calendarEvent += line + "\n";

			if (line.equals("BEGIN:VEVENT"))
				calendarEvent = "";

			else if (line.equals("END:VEVENT"))
				calendarEvents.add(convertStringToCalendarEvent(calendarEvent));
			else
				calendarEvent += line + "\n";
		}
		return calendarEvents;
	}

	/**
	 * Função que converte a String com os dados respetivos em CalendarEvent
	 * 
	 * @param event
	 * @return
	 */

	private static CalendarEvent convertStringToCalendarEvent(String event) {

		String dateStart = getSubString(event, "DTSTART:", "DTEND:");
		String dateEnd = getSubString(event, "DTEND:", "SUMMARY:");
		String summary = getSubString(event, "SUMMARY:", "UID:");
		String description = getSubString(event, "DESCRIPTION:", "LOCATION:");
		String location = getSubString(event, "LOCATION:", "");

		return (new CalendarEvent(dateStart, dateEnd, summary, description, location));

	}

	/**
	 * Funcao que retorna a substring entre firstSubString e secondSubString
	 * 
	 * @param totalString
	 * @param firstSubString
	 * @param secondSubString
	 * @return
	 */
	private static String getSubString(String totalString, String firstSubString, String secondSubString) {
		int start = totalString.indexOf(firstSubString) + firstSubString.length();
		int end = totalString.lastIndexOf(secondSubString);

		return totalString.substring(start, end);
	}
}
