package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {

	/**
	 * Funcao que le textfile e retorna uma arraylist em que cada elemento é um dos
	 * eventos do calendario em texto
	 * 
	 * @param path - caminho do ficheiro que se quer ler
	 * @return calendar
	 * @throws FileNotFoundException
	 */

	public static ArrayList<String> readTextFile(String path) throws FileNotFoundException {

		File text = new File(path);
		Scanner scanner = new Scanner(text);

		ArrayList<String> calendar = new ArrayList<String>();
		String calendarEvent = "";

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

//			calendarEvent += line + "\n";
			
			if (line.equals("BEGIN:VEVENT"))
				calendarEvent = "";

			else if (line.equals("END:VEVENT"))
				calendar.add(calendarEvent);
			else
				calendarEvent += line + "\n";
		}
		return calendar;
	}

	

	public static void main(String[] args) {
		String path = "C:\\Users\\7jtom\\git\\ES-LETI-1Sem-2022-Grupo-6\\Projeto ES\\files\\thgas";
		ArrayList<String> teste = new ArrayList<String>();
		try {
			teste = readTextFile(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(teste.get(0));
	}

}
