package app;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class CalendarGUI {

	private static final int NUM_COLUMNS = 3;
	private static final int NUM_ROWS = 10;
//	private static final String HTML_FILES_PATH = "files/html_files/";
	private static final int NUMBER_OF_NON_EVENT_COMPONENTS = 9;
	private int currentIndex;
	private JFrame frame;
	private CalendarManager calendarManager;
	private boolean userInputFrameClosed;

	public CalendarGUI() {
		frame = new JFrame();
		this.calendarManager = new CalendarManager();
		;
		currentIndex = NUM_ROWS;
		userInputFrameClosed = false;
	}

	public void start() throws InterruptedException {
		askUserForNameAndURL();
		while (!userInputFrameClosed)
			Thread.sleep(200);
		addCalendarFrameContent();
	}

	private void askUserForNameAndURL() {
		JFrame userInputFrame = new JFrame("Introduza os seus dados");
		userInputFrame.setSize(500, 300);
		userInputFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addUserInputFrameContent(userInputFrame);
		userInputFrame.pack();
		userInputFrame.setVisible(true);
	}

	private void addUserInputFrameContent(JFrame userInputFrame) {
		userInputFrame.setLayout(new GridLayout(3, 2));

		JLabel askNameLabel = new JLabel("Introduza o seu nome", SwingConstants.CENTER);
		JTextField askName = new JTextField();

		JLabel askURLLabel = new JLabel("Introduza o seu URL", SwingConstants.CENTER);
		JTextField askURL = new JTextField();

		JCheckBox checkBox = new JCheckBox("Individual (x) / Grupo ()");
		JButton buttonSolo = new JButton("Abrir calendário");

		userInputFrame.add(askNameLabel);
		userInputFrame.add(askName);
		userInputFrame.add(askURLLabel);
		userInputFrame.add(askURL);
		userInputFrame.add(checkBox);
		userInputFrame.add(buttonSolo);

		buttonSolo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = askName.getText();
				String URL = askURL.getText();

				userInputFrame.setVisible(false);
				userInputFrame.dispose();

				File txtFileDir = new File("files/text_files/" + name + ".txt");
				if (!txtFileDir.exists()) {
					UrlReader.Urlcatcher(URL, name);
					FileHandler.createNewCalendarFile(name);
				}
				ArrayList<CalendarEvent> events = FileHandler.decodeJSONFile(name + ".json");
				if (checkBox.isSelected())
					calendarManager.addEvents(events);
				else
					calendarManager.fillWithSavedEvents();
				userInputFrameClosed = true;
			}
		});
	}

//	private FileReader getTemplate() throws FileNotFoundException {
//		return new FileReader(new File(HTML_FILES_PATH + "template.html"));
//	}

//	public void createHTMLTable(ArrayList<CalendarEvent> calendarEvents, Instant dateStart, Instant dateEnd,
//			String fileName) throws IOException {
//		ArrayList<CalendarEvent> events = getEventsBetweenDates(calendarEvents, dateStart, dateEnd);
//
//		HashMap<String, Object> scopes = new HashMap<String, Object>();
//		scopes.put("events", events);
//
//		Writer writer = new FileWriter(HTML_FILES_PATH + fileName + ".html");
//		MustacheFactory mf = new DefaultMustacheFactory();
//		Mustache mustache = mf.compile(getTemplate(), "magicName");
//		mustache.execute(writer, scopes);
//		writer.flush();
//
//		openHTMLFile(fileName);
//	}

//	private void openHTMLFile(String fileName) throws IOException {
//		File htmlFile = new File(HTML_FILES_PATH + fileName + ".html");
//		Desktop.getDesktop().browse(htmlFile.toURI());
//	}

//	private ArrayList<CalendarEvent> getEventsBetweenDates(ArrayList<CalendarEvent> calendarEvents, Instant dateStart,
//			Instant dateEnd) {
//		if (dateEnd == null || dateStart == null)
//			return calendarEvents;
//		ArrayList<CalendarEvent> events = new ArrayList<>();
//		for (CalendarEvent c : calendarEvents)
//			if (c.getDateStart().isAfter(dateStart) && c.getDateStart().isBefore(dateEnd)
//					|| c.getDateStart().equals(dateStart) || c.getDateStart().equals(dateEnd))
//				events.add(c);
//		return events;
//	}

	private void addCalendarFrameContent() {
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUMBER_OF_NON_EVENT_COMPONENTS / 3 + NUM_ROWS, NUM_COLUMNS));

		// componente datepicker
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter()); // datePicker.getModel().getValue();

		// componente Combobox
		String[] numberOfRowsPerPage = { "5", "10", "15", "20" };
		JComboBox box = new JComboBox<>(numberOfRowsPerPage); // box.getSelectedIndex()

		
		

		box.setSelectedIndex(1);

		JButton refreshButton = new JButton("Carregar eventos entre as datas escolhidas");


		// componentes Header
		JLabel dateHeader = new JLabel("Date", SwingConstants.CENTER);
		JLabel locationHeader = new JLabel("Location", SwingConstants.CENTER);
		JLabel summaryHeader = new JLabel("Summary", SwingConstants.CENTER);

		// botões para andar para trás/frente no tempo
		JButton goBack = new JButton("<");
		JButton goForward = new JButton(">");

		frame.add(goBack);
		frame.add(new JLabel("Move in time", SwingConstants.CENTER));
		frame.add(goForward);
		frame.add(datePicker);
		frame.add(box);
		frame.add(refreshButton);
		frame.add(dateHeader);
		frame.add(locationHeader);
		frame.add(summaryHeader);

		addEventsToFrame(calendarManager.getEvents(), NUM_ROWS, 0, NUM_ROWS);

		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<CalendarEvent> events = calendarManager.getEvents();

		datePicker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dates.add((Date) datePicker.getModel().getValue());
				System.out.println("Date added");
			}
		});

		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Refresh");

				// remover componentes antigos
				removeComponentsFromFrame();

				// adicionar novos
				int numEventsPerPage = Integer.parseInt(numberOfRowsPerPage[box.getSelectedIndex()]);
				frame.setLayout(new GridLayout(NUMBER_OF_NON_EVENT_COMPONENTS / 3 + numEventsPerPage, NUM_COLUMNS));
				if (dates.size() >= 2) {
					dates.sort(null);
					Instant firstInstant = dates.get(0).toInstant();
					Instant lastInstant = dates.get(dates.size() - 1).toInstant();
					events.clear();
					events.addAll(calendarManager.getEventsBetweenDates(firstInstant, lastInstant));

				} else {
					events.clear();
					events.addAll(calendarManager.getEventsBetweenDates(null, null));
				}
				addEventsToFrame(events, numEventsPerPage, 0, numEventsPerPage);
				frame.revalidate();
				frame.repaint();
			}
		});

		goForward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int numEventsPerPage = Integer.parseInt(numberOfRowsPerPage[box.getSelectedIndex()]);
				removeComponentsFromFrame();
				addEventsToFrame(events, numEventsPerPage, Integer.min(currentIndex, events.size() - numEventsPerPage),
						Integer.min(currentIndex + numEventsPerPage, events.size()));

				frame.revalidate();
				frame.repaint();
			}
		});

		goBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int numEventsPerPage = Integer.parseInt(numberOfRowsPerPage[box.getSelectedIndex()]);
				removeComponentsFromFrame();
				addEventsToFrame(events, numEventsPerPage, Integer.max(0, currentIndex - 2 * numEventsPerPage),
						Integer.max(currentIndex - numEventsPerPage, numEventsPerPage));

				frame.revalidate();
				frame.repaint();

			}
		});

		frame.pack();
		frame.setVisible(true);
	}

	private void removeComponentsFromFrame() {
		Component[] components = frame.getContentPane().getComponents();
		for (int i = NUMBER_OF_NON_EVENT_COMPONENTS; i < components.length; i++)
			frame.remove(components[i]);
	}

	private void addEventsToFrame(ArrayList<CalendarEvent> events, int numberOfEvents, int start, int end) {
		for (int i = start; i < end && events.size() > 0; i++) {
			if (numberOfEvents <= 0)
				break;
			numberOfEvents--;

			CalendarEvent event = events.get(i);
			JLabel dateLabel = new JLabel(event.getDateStartString());
			JLabel locationLabel = new JLabel(event.getLocation());
			JLabel summaryLabel = new JLabel(event.getSummary());

			frame.add(dateLabel);
			frame.add(locationLabel);
			frame.add(summaryLabel);
		}
		// Caso não haja uma qunatidade de numberOfEvents durante aquelas datas, para
		// nao desformatar a GUI
		if (numberOfEvents > 0)
			for (int i = numberOfEvents; i > 0; i--) {
				frame.add(new JLabel(""));
				frame.add(new JLabel(""));
				frame.add(new JLabel(""));
			}
		currentIndex = end;
	}

	public static void main(String[] args) {
		CalendarGUI gui = new CalendarGUI();
		try {
			gui.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
