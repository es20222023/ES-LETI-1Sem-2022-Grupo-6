package main;

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

/**
 * 
 * Classe que lida com todas as escritas/leituras de ficheiros
 *
 */

public class FileHandler {

	public static final String JSON_FILES_PATH = "files/json_files/";
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
	 * Função que que lê o ficheiro .txt com os eventos e os converte para um
	 * ficheiro .json
	 * 
	 * @param fileName o nome do ficheiro de texto
	 * @return a lista de eventos obtida do ficheiro de texto
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
	 * @param fileName o nome do ficheiro .txt
	 * @return uma lista com todos os eventos no ficheiro
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
				calendarEvents.add(convertStringToCalendarEvent(calendarEvent, fileName));
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

	/**
	 * Função que converte a string dada num objeto <b>CalendarEvent</b>
	 * 
	 * @param event    String que corresponde a um evento
	 * @param username o nome do user a quem corresponde o evento
	 * @return o evento <b>CalendarEvent</b>
	 */

	private static CalendarEvent convertStringToCalendarEvent(String event, String username) {

		String dateStart = getSubString(event, DATESTART, DATEEND);
		String dateEnd = getSubString(event, DATEEND, SUMMARY);
		String summary = getSubString(event, SUMMARY, UID);
		String description = getSubString(event, DESCRIPTION, LOCATION);
		String location = getSubString(event, LOCATION, "");

		return (new CalendarEvent(dateStart, dateEnd, summary, description, location, username));

	}

	/**
	 * Funcao que retorna a substring entre firstSubString e secondSubString
	 * 
	 * @param totalString     toda a String a ser considerada
	 * @param firstSubString  a primeira subString
	 * @param secondSubString a segunda subString
	 * @return a string entre as duas string dadas
	 */
	private static String getSubString(String totalString, String firstSubString, String secondSubString) {
		int start = totalString.indexOf(firstSubString) + firstSubString.length();
		int end = totalString.lastIndexOf(secondSubString);

		return totalString.substring(start, end);
	}

	/**
	 * Funcao que transforma lista de Objetos <b>CalendarManager</b> em
	 * <b>JSONArray</b>
	 * 
	 * @param calendarEvents a lista de eventos
	 * @return um array JSON
	 */

	@SuppressWarnings("unchecked")
	private static JSONArray JSONEncoder(ArrayList<CalendarEvent> calendarEvents) {
		JSONArray list = new JSONArray();

		for (CalendarEvent event : calendarEvents) {

			list.add(createJSONObject(event));
		}
		return list;
	}

	/**
	 * Função que cria um objeto JSON a partir do evento dado
	 * 
	 * @param event o evento a considerar
	 * @return o evento em formato <b>JSONObject</b>
	 */

	@SuppressWarnings("unchecked")
	private static JSONObject createJSONObject(CalendarEvent event) {
		JSONObject obj = new JSONObject();

		obj.put("dateStart", event.getDateStart().toString());
		obj.put("dateEnd", event.getDateEnd().toString());
		obj.put("summary", event.getSummary());
		obj.put("description", event.getDescription());
		obj.put("location", event.getLocation());

		return obj;
	}

	/**
	 * Função que cria ficheiro .json a partir do array JSON dado
	 * 
	 * @param arr      o array JSON a ser considerado
	 * @param fileName o nome do ficheiro .json a ser criado
	 */

	private static void writeJSONFile(JSONArray arr, String fileName) {
		try {
			File dir = new File("files/json_files");
			File file = new File(dir, fileName + ".json"); // necessario para que o ficheiro va para o diretorio das
															// json_files
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(arr.toJSONString());
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("Erro a escrever JSON file");
			return;
		}
	}

	/**
	 * Função que a partir do ficheiro .json dado retorna uma lista com todos os eventos
	 * presentes nesse array JSON
	 * 
	 * @param fileName o nome do ficheiro a ser descodificado
	 * @return uma lista com todos os eventos no ficheiro
	 */

	public static ArrayList<CalendarEvent> decodeJSONFile(String fileName) {

		ArrayList<CalendarEvent> dataToReturn = new ArrayList<CalendarEvent>();

		JSONParser parser = new JSONParser();

		JSONArray a;

		try {
			a = (JSONArray) parser.parse(new FileReader(JSON_FILES_PATH + fileName));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		for (Object o : a) {

			dataToReturn.add(getCalendarEventFromJSONObject(o, fileName));

		}
		dataToReturn.sort(null);
		return dataToReturn;
	}

	/**
	 * Função que retorna o <b>CalendarEvent</b> a partir do objeto JSON dado
	 * 
	 * @param o    o objeto JSON a ser considerado
	 * @param name o nome do utilizador
	 * @return o evento presente no objeto dado
	 */

	private static CalendarEvent getCalendarEventFromJSONObject(Object o, String name) {
		JSONObject calendarEvent = (JSONObject) o;

		String dateStart = (String) calendarEvent.get("dateStart");
		String dateEnd = (String) calendarEvent.get("dateEnd");
		String summary = (String) calendarEvent.get("summary");
		String description = (String) calendarEvent.get("description");
		String location = (String) calendarEvent.get("location");

		CalendarEvent event = new CalendarEvent(dateStart, dateEnd, summary, description, location,
				name.replace(".json", ""));

		return event;
	}

	/**
	 * Função que adiciona a reunião ao ficheiro JSON do user associado a essa
	 * reunião
	 * 
	 * @param meeting o evento associado à reunião
	 */

	@SuppressWarnings("unchecked")
	public static void addMeetingToJSONFile(CalendarEvent meeting) {
		String fileName = meeting.getUsername();
		JSONObject obj = createJSONObject(meeting);
		JSONParser parser = new JSONParser();
		JSONArray a;

		try {
			a = (JSONArray) parser.parse(new FileReader(JSON_FILES_PATH + fileName + ".json"));
			a.add(obj);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return;
		}

		writeJSONFile(a, fileName);

	}

	public static void main(String[] args) {
		createNewCalendarFile("thgas");
//		decodeJSONFile("thgas.json");
	}
}
