package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import main.CalendarEvent;
import main.CalendarManager;
import main.FileHandler;

public class CalendarManagerTests {

	private String username = "test/meeting_test_Copy";

	private final CalendarEvent c = CalendarEventTests.createTestCalendarEvent(username);

	@Test
	public void testAddEvents() {

		CalendarManager calendarManager = new CalendarManager();

		ArrayList<CalendarEvent> list = new ArrayList<CalendarEvent>();
		list.add(c);
		list.add(c);
		list.add(new CalendarEvent(c.getDateStart(), c.getDateEnd(), c.getSummary(), c.getDescription() + "dif",
				c.getLocation(), username));

		calendarManager.addEvents(list);

		assertEquals(calendarManager.getEvents().size(), 2);
	}

	@Test
	public void testGetEventsBetweenDates() {

		CalendarManager calendarManager = new CalendarManager();
		calendarManager.addEvent(c);

		// Evento c começa em 2022/12/25 00:00
		// Evento c acaba em 2022/12/26 00:00

		CalendarEvent test = calendarManager.getEventsBetweenDates(null, null).get(0);
		assertEquals(test, c);

		Instant start = new Date(2022 - 1900, 11, 24).toInstant(); // 2022/12/24 00:00
		Instant end = new Date(2022 - 1900, 11, 27).toInstant(); // 2022/12/27 00:00
		test = calendarManager.getEventsBetweenDates(start, end).get(0);
		assertEquals(test, c);

		start = new Date(2022 - 1900, 11, 25, 10, 0).toInstant(); // 2022/12/25 10:00
		end = new Date(2022 - 1900, 11, 25, 11, 0).toInstant(); // 2022/12/25 11:00
		test = calendarManager.getEventsBetweenDates(start, end).get(0);
		assertEquals(test, c);

		start = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		end = new Date(2022 - 1900, 11, 27).toInstant(); // 2022/12/27 00:00
		test = calendarManager.getEventsBetweenDates(start, end).get(0);
		assertEquals(test, c);

		start = new Date(2022 - 1900, 11, 25, 10, 0).toInstant(); // 2022/12/25 10:00
		end = new Date(2022 - 1900, 11, 26).toInstant(); // 2022/12/26 00:00
		test = calendarManager.getEventsBetweenDates(start, end).get(0);
		assertEquals(test, c);

		start = new Date(2022 - 1900, 11, 24).toInstant(); // 2022/12/24 00:00
		end = new Date(2022 - 1900, 11, 25, 10, 0).toInstant(); // 2022/12/25 10:00
		test = calendarManager.getEventsBetweenDates(start, end).get(0);
		assertEquals(test, c);

		start = new Date(2022 - 1900, 11, 23).toInstant(); // 2022/12/23 00:00
		end = new Date(2022 - 1900, 11, 24).toInstant(); // 2022/12/24 00:00
		int size = calendarManager.getEventsBetweenDates(start, end).size();
		assertEquals(size, 0);

		start = new Date(2022 - 1900, 11, 27).toInstant(); // 2022/12/27 00:00
		end = new Date(2022 - 1900, 11, 28).toInstant(); // 2022/12/28 00:00
		size = calendarManager.getEventsBetweenDates(start, end).size();
		assertEquals(size, 0);

	}

	@Test
	public void testIsUserAvailable() {
		CalendarManager calendarManager = new CalendarManager();
		calendarManager.addEvent(c);

		Instant instant = new Date(2022 - 1900, 11, 24).toInstant(); // 2022/12/24 00:00
		boolean isAvailable = calendarManager.isUserAvailable(username, instant);
		assertEquals(isAvailable, true);

		instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		isAvailable = calendarManager.isUserAvailable(username, instant);
		assertEquals(isAvailable, false);

		instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		isAvailable = calendarManager.isUserAvailable("otherUser", instant);
		assertEquals(isAvailable, true);

		instant = new Date(2022 - 1900, 11, 25, 10, 0).toInstant(); // 2022/12/25 10:00
		isAvailable = calendarManager.isUserAvailable(username, instant);
		assertEquals(isAvailable, false);

		instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		Instant otherInstant = new Date(2022 - 1900, 11, 26).toInstant();// 2022/12/26 00:00
		isAvailable = calendarManager.isUserAvailable(username, instant, otherInstant);
		assertEquals(isAvailable, false);

		instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		otherInstant = new Date(2022 - 1900, 11, 26).toInstant();// 2022/12/26 00:00
		isAvailable = calendarManager.isUserAvailable("otherUser", instant, otherInstant);
		assertEquals(isAvailable, true);

	}

	@Test
	public void testAreAllUsersAvailable() {
		CalendarManager calendarManager = new CalendarManager();
		calendarManager.addEvent(c);

		String[] usernames = { username, "otherUser" };

		Instant instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		Instant otherInstant = new Date(2022 - 1900, 11, 26).toInstant();// 2022/12/26 00:00
		boolean areAvailable = calendarManager.areAllUsersAvailable(instant, otherInstant, usernames);
		assertEquals(areAvailable, false);

		String[] newUsernames = { "otherUser" };
		instant = new Date(2022 - 1900, 11, 25).toInstant(); // 2022/12/25 00:00
		otherInstant = new Date(2022 - 1900, 11, 26).toInstant();// 2022/12/26 00:00
		areAvailable = calendarManager.areAllUsersAvailable(instant, otherInstant, newUsernames);
		assertEquals(areAvailable, true);
	}

	@Test
	public void testSugestMeeting() {
		CalendarManager calendarManager = new CalendarManager();

		int meetingDuration = 30;

		Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
		Date date = new Date(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDate(),
				CalendarManager.MORNING_HOUR_FOR_MEETING_START, 0);

		// startEvent é amanha às 8h
		Instant startEvent = date.toInstant();
		Instant endEvent = startEvent.plus(meetingDuration, ChronoUnit.MINUTES);

		CalendarEvent e = new CalendarEvent(startEvent, endEvent, "", "", "", username);
		calendarManager.addEvent(e);

		String[] users = { username };

		// maxDate é amanha às 23h
		Date maxtomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
		maxtomorrow.setHours(23);
		Instant maxDate = maxtomorrow.toInstant();

		ArrayList<Instant> instantsForMeeting = calendarManager.sugestMeeting(users, meetingDuration, true, maxDate,
				false);

		Date firstMeeting = Date.from(instantsForMeeting.get(0));
		date.setMinutes(meetingDuration);
		assertEquals(firstMeeting, date);
		assertEquals(instantsForMeeting.size(), CalendarManager.MAX_MEETING_DURATION_IN_MINUTES / meetingDuration - 1);

		// maxDate agora é daqui a dois dias
		instantsForMeeting = calendarManager.sugestMeeting(users, meetingDuration, true,
				maxDate.plus(1, ChronoUnit.DAYS), true);

		assertEquals(instantsForMeeting.size(),
				2 * CalendarManager.MAX_MEETING_DURATION_IN_MINUTES / meetingDuration - 1);
	}

	@Test
	public void testCreateMeeting() throws IOException {

		CalendarManager calendarManager = new CalendarManager();
		String[] users = { "test/meeting_test_Copy1" };
		// copia o ficheiro, para não estar sempre a adicionar meetings ao mesmo
		// ficheiro
		File original = new File("files/json_files/test/meeting_test.json");
		File copy = new File("files/json_files/test/meeting_test_Copy1.json");
		FileUtils.copyFile(original, copy);

		Instant startMeeting = new Date(2022 - 1900, 11, 25).toInstant();
		Instant maxDate = new Date(2022 - 1900, 11, 27).toInstant();

		calendarManager.createMeeting(startMeeting, maxDate, 30, false, users, "loc");

		CalendarEvent meeting = new CalendarEvent(startMeeting, startMeeting.plus(30, ChronoUnit.MINUTES), "Meeting",
				"Meeting", "loc", "test/meeting_test_Copy1");
		ArrayList<CalendarEvent> events = FileHandler.decodeJSONFile("test/meeting_test_Copy1.json");

		copy.delete();

		assertEquals(events.get(0), meeting);

	}

}
