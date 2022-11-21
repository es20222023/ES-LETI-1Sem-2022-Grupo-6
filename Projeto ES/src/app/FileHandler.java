package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileHandler {

	private static final String JSON_FILES_PATH = "files/json_files/";
	private static final String TEXT_FILES_PATH = "files/text_files/";
	private static final String LOCATION = "LOCATION:";
	private static final String DESCRIPTION = "DESCRIPTION:";
	private static final String UID = "UID:";
	private static final String SUMMARY = "SUMMARY:";
	private static final String BEGINEVENT = "BEGIN:VEVENT";
	private static final String ENDEVENT = "END:VEVENT";
	private static final String DATESTART = "DTSTART:";
	private static final String DATEEND = "DTEND:";

	/**
	 * Funcao que liga as funcoes desta classe, cria arraylist com elementos do
	 * schedulePath, da encode para JSON dos mesmos e cria a partir deles um
	 * ficheiro JSON
	 * 
	 * @param schedulePath
	 * @param JSONFilePath
	 * @return
	 */

	public static ArrayList<CalendarEvent> createNewCalendarFile(String fileName) {
		try {
			ArrayList<CalendarEvent> calendarEvents = readTextFile(fileName);
			JSONArray arr = JSONEncoder(calendarEvents);
			writeJSONFile(arr, fileName);
			return calendarEvents;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

	private static ArrayList<CalendarEvent> readTextFile(String fileName) throws FileNotFoundException {

		File text = new File(TEXT_FILES_PATH + fileName + ".txt");
		Scanner scanner = new Scanner(text);

		ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
		String calendarEvent = "";

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.equals(BEGINEVENT))
				calendarEvent = "";

			else if (line.equals(ENDEVENT))
				calendarEvents.add(convertStringToCalendarEvent(calendarEvent));
			else
				calendarEvent += line;
		}
		scanner.close();
		return calendarEvents;
	}

	/**
	 * Função que converte a String com os dados respetivos em CalendarEvent
	 * 
	 * @param event
	 * @return
	 */

	private static CalendarEvent convertStringToCalendarEvent(String event) {

		String dateStart = getSubString(event, DATESTART, DATEEND);
		String dateEnd = getSubString(event, DATEEND, SUMMARY);
		String summary = getSubString(event, SUMMARY, UID);
		String description = getSubString(event, DESCRIPTION, LOCATION);
		String location = getSubString(event, LOCATION, "");

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

	@SuppressWarnings("unchecked")
	private static JSONArray JSONEncoder(ArrayList<CalendarEvent> calendarEvents) {
		JSONArray list = new JSONArray();

		for (CalendarEvent event : calendarEvents) {
			JSONObject obj = new JSONObject();

			obj.put("dateStart", event.getDateStart().toString());
			obj.put("dateEnd", event.getDateEnd().toString());
			obj.put("summary", event.getSummary());
			obj.put("description", event.getDescription());
			obj.put("location", event.getLocation());

			list.add(obj);
		}
		return list;
	}

	/**
	 * Funcao que cria ficheiro JSON a partir do JSONArray dado
	 * 
	 * @param arr
	 * @param fileName
	 */

	private static void writeJSONFile(JSONArray arr, String fileName) {
		try {
			File dir = new File("files/json_files");
			File file = new File(dir, fileName +".json"); // necessario para que o ficheiro va para o diretorio das json_files
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(arr.toJSONString());
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("Erro a escrever JSON file");
			return;
		}
	}

	public static ArrayList<CalendarEvent> decodeJSONFile(String fileName) {

		ArrayList<CalendarEvent> dataToReturn = new ArrayList<CalendarEvent>();

		JSONParser parser = new JSONParser();

		JSONArray a;

		try {
			a = (JSONArray) parser.parse(new FileReader(JSON_FILES_PATH + fileName + ".json"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}

		for (Object o : a) {
			JSONObject calendarEvent = (JSONObject) o;

			String dateStart = (String) calendarEvent.get("dateStart");
			String dateEnd = (String) calendarEvent.get("dateEnd");
			String summary = (String) calendarEvent.get("summary");
			String description = (String) calendarEvent.get("description");
			String location = (String) calendarEvent.get("location");

			CalendarEvent event = new CalendarEvent(dateStart, dateEnd, summary, description, location);
			dataToReturn.add(event);

		}
		dataToReturn.sort(null);
		return dataToReturn;
	}

	public static void main(String[] args) {
		createNewCalendarFile("thgas");
//		decodeJSONFile("thgas.json");
	}
}
