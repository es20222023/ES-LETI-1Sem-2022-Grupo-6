package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import app.FileHandler;

public class FileHandlerTests {

	@Test
	public void testCreateNewCalendarFile() {
		FileHandler.createNewCalendarFile("files/text_files/thgas.txt", "teste.json");
		File file1 = new File("files/json_files/thgas.json");
		File file2 = new File("files/json_files/teste.json");
		try {
			boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
			assertEquals(isTwoEqual, true);
			file2.delete();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
