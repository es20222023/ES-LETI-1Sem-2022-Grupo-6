package app;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class CalendarGUI {

	private static final String HTML_FILES_PATH = "files/html_files/";
	private JFrame frame;

	public CalendarGUI() {
		frame = new JFrame();
	}

	public void start() {
		askUserForNameAndURL();
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
		userInputFrame.setLayout(new GridLayout(5, 3));

		JLabel askNameLabel = new JLabel("Introduza o seu nome", SwingConstants.CENTER);
		JTextField askName = new JTextField();

		JLabel askURLLabel = new JLabel("Introduza o seu URL", SwingConstants.CENTER);
		JTextField askURL = new JTextField();

		JLabel askDateStartLabel = new JLabel("Introduza a data de início da pesquisa em formato yyyy-MM-dd-HH-mm",
				SwingConstants.CENTER);
		JTextField askDateStart = new JTextField();

		JLabel askDateEndLabel = new JLabel("Introduza a data de fim da pesquisa em formato yyyy-MM-dd-HH-mm",
				SwingConstants.CENTER);
		JTextField askDateEnd = new JTextField();

		JLabel buttonLabel = new JLabel("Carregue neste botão para abrir o calendário", SwingConstants.CENTER);
		JButton button = new JButton("Abrir calendário");

		userInputFrame.add(askNameLabel);
		userInputFrame.add(askName);
		userInputFrame.add(askURLLabel);
		userInputFrame.add(askURL);
		userInputFrame.add(askDateStartLabel);
		userInputFrame.add(askDateStart);
		userInputFrame.add(askDateEndLabel);
		userInputFrame.add(askDateEnd);
		userInputFrame.add(buttonLabel);
		userInputFrame.add(button);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = askName.getText();
				String URL = askURL.getText();
				String dateStart = askDateStart.getText();
				String dateEnd = askDateEnd.getText();

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

				userInputFrame.setVisible(false);
				userInputFrame.dispose();

				File txtFileDir = new File("files/text_files/" + name + ".txt");
				if (!txtFileDir.exists()) {
					UrlReader.Urlcatcher(URL, name);
					FileHandler.createNewCalendarFile(name);
				}
				ArrayList<CalendarEvent> events = FileHandler.decodeJSONFile(name);
				try {
					if (dateStart.equals("") && dateEnd.equals(""))
						createHTMLTable(events, null, null, name);
					else
						createHTMLTable(events, format.parse(dateStart).toInstant(), format.parse(dateEnd).toInstant(),
								name);
				} catch (IOException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private FileReader getTemplate() throws FileNotFoundException {
		return new FileReader(new File(HTML_FILES_PATH + "template.html"));
	}

	public void createHTMLTable(ArrayList<CalendarEvent> calendarEvents, Instant dateStart, Instant dateEnd,
			String fileName) throws IOException {
		ArrayList<CalendarEvent> events = getEventsBetweenDates(calendarEvents, dateStart, dateEnd);

		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("events", events);

		Writer writer = new FileWriter(HTML_FILES_PATH + fileName + ".html");
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(getTemplate(), "magicName");
		mustache.execute(writer, scopes);
		writer.flush();

		openHTMLFile(fileName);
	}

	private void openHTMLFile(String fileName) throws IOException {
		File htmlFile = new File(HTML_FILES_PATH + fileName + ".html");
		Desktop.getDesktop().browse(htmlFile.toURI());
	}

	private ArrayList<CalendarEvent> getEventsBetweenDates(ArrayList<CalendarEvent> calendarEvents, Instant dateStart,
			Instant dateEnd) {
		if (dateEnd == null || dateStart == null)
			return calendarEvents;
		ArrayList<CalendarEvent> events = new ArrayList<>();
		for (CalendarEvent c : calendarEvents)
			if (c.getDateStart().isAfter(dateStart) && c.getDateStart().isBefore(dateEnd)
					|| c.getDateStart().equals(dateStart) || c.getDateStart().equals(dateEnd))
				events.add(c);
		return events;
	}

	public static void main(String[] args) {
		CalendarGUI c = new CalendarGUI();
		c.start();
	}
}
