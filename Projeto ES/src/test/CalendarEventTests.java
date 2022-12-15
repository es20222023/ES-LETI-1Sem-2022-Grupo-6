package test;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import main.CalendarEvent;

public class CalendarEventTests {

	private static Instant dateStart = new Date(2022 - 1900, 11, 25).toInstant();
	private static Instant dateEnd = new Date(2022 - 1900, 11, 26).toInstant();
	private static String summary = "test_summary";
	private static String description = "test_description";
	private static String location = "test_location";
	private static String username = "test_username";

	private final CalendarEvent c = createTestCalendarEvent(username);

	public static CalendarEvent createTestCalendarEvent(String name) {
		return new CalendarEvent(dateStart, dateEnd, summary, description, location, name);

	}

	@Test
	public void testEquals() {
		assertEquals(c.equals(c), true);
		assertEquals(c.equals(null), false);
		assertEquals(c.equals(1), false);
		assertEquals(c.equals(new CalendarEvent(dateStart, dateEnd, summary, description, location, username)), true);
	}

	@Test
	public void testDataInstantToString() {
		String expected = "2022/12/25 00:00";
		assertEquals(CalendarEvent.dateInstantToString(dateStart), expected);
	}

}
