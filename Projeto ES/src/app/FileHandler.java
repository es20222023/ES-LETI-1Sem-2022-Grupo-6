package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FileHandler {

	/**
	 * Funcao que liga as funcoes desta classe, cria arraylist com elementos do
	 * schedulePath, da encode para JSON dos mesmos e cria a partir deles um
	 * ficheiro JSON
	 * 
	 * @param schedulePath
	 * @param JSONFilePath
	 * @return
	 */

	public static ArrayList<CalendarEvent> createNewCalendarFile(String schedulePath, String JSONFilePath) {
		try {
			ArrayList<CalendarEvent> calendarEvents = readTextFile(schedulePath);
			JSONArray arr = JSONEncoder(calendarEvents);
			writeJSONFile(arr, JSONFilePath);
			return calendarEvents;
		} catch (FileNotFoundException e) {
			System.out.println("Erro a tentar ler ficheiro texto do horário");
			return null;
		}
	}

	/**
	 * Funcao que le textfile e retorna uma arraylist em que cada elemento é um dos
	 * eventos do calendario
	 * 
	 * @param path - caminho do ficheiro que se quer ler
	 * @return calendar
	 * @throws FileNotFoundException
	 */

	private static ArrayList<CalendarEvent> readTextFile(String path) throws FileNotFoundException {

		File text = new File(path);
		Scanner scanner = new Scanner(text);

		ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
		String calendarEvent = "";

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

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

	/**
	 * Funcao que transforma Objetos CalendarManager em JSON
	 * 
	 * @param calendar
	 * @return
	 */

	private static JSONArray JSONEncoder(ArrayList<CalendarEvent> calendarEvents) {
		JSONArray list = new JSONArray();

		for (CalendarEvent event : calendarEvents) {
			JSONObject obj = new JSONObject();

			obj.put("dateStart", event.getDateStart());
			obj.put("dateEnd", event.getDateEnd());
			obj.put("summary", event.getSummary());
			obj.put("description", event.getDescription());
			obj.put("location", event.getLocation());

			list.add(obj);
		}
		return list;
	}

	private static void writeJSONFile(JSONArray arr, String fileName) {
		try {
			FileWriter file = new FileWriter(fileName);
			file.write(arr.toJSONString());
		} catch (IOException e) {
			System.out.println("Erro a escrever JSON file");
			return;
		}
	}
}
