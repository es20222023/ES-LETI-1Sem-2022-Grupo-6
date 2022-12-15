package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import main.CalendarEvent;
import main.FileHandler;

public class FileHandlerTests {

	private String username = "test/meeting_test_Copy";

	private final CalendarEvent c = CalendarEventTests.createTestCalendarEvent(username);

	@Test
	public void testCreateNewCalendarFile() throws IOException {
		FileHandler.createNewCalendarFile("test/thgas-teste");
		File file1 = new File("files/json_files/test/thgas-teste.json");
		File file2 = new File("files/json_files/test/create_Calendar_Test.json");

		boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
		assertEquals(isTwoEqual, true);
		file1.delete();

	}

	@Test
	public void testAddMeetingToJSONFileAndDecodeJSONFile() throws IOException {

		// copia o ficheiro, para não estar sempre a adicionar meetings ao mesmo
		// ficheiro
		File original = new File("files/json_files/test/meeting_test.json");
		File copy = new File("files/json_files/test/meeting_test_Copy.json");
		FileUtils.copyFile(original, copy);

		// adiciona a meeting ao ficheiro copiado
		FileHandler.addMeetingToJSONFile(c);

		// vai buscar as meetings do ficheiro copiado
		ArrayList<CalendarEvent> meetingTest = FileHandler.decodeJSONFile(username + ".json");

		// vê se a última criada é igual ao esperado
		assertEquals(c, meetingTest.get(0));

		copy.delete();
	}

}
