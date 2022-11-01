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
		File file1 = new File("files/thgas.json");
		File file2 = new File("teste.json");
		try {
			boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
			System.out.println(isTwoEqual);
			assertEquals(isTwoEqual, true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
