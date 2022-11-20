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

	private JFrame frame;

	public CalendarGUI() {

		frame = new JFrame();
		start();

	}

	private void start() {
		askUserForNameAndURL();
		// TODO a parte da GUI a aparecer o calendário
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
		userInputFrame.setLayout(new GridLayout(3, 3));

		JLabel askNameLabel = new JLabel("Introduza o seu nome", SwingConstants.CENTER);
		JTextField askName = new JTextField();

		JLabel askURLLabel = new JLabel("Introduza o seu URL", SwingConstants.CENTER);
		JTextField askURL = new JTextField();

		JLabel buttonLabel = new JLabel("Carregue neste botão para carregar o seu calendário", SwingConstants.CENTER);
		JButton button = new JButton("Carregar calendário");

		userInputFrame.add(askNameLabel);
		userInputFrame.add(askName);
		userInputFrame.add(askURLLabel);
		userInputFrame.add(askURL);
		userInputFrame.add(buttonLabel);
		userInputFrame.add(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = askName.getText();
				String URL = askURL.getText();

				userInputFrame.setVisible(false);
				userInputFrame.dispose();

				UrlReader.Urlcatcher(URL, name);
			}
		});
	}

	private FileReader getTemplate() throws FileNotFoundException {
		return new FileReader(new File("files/html_files/template.html"));
	}

	public void createHTMLTable(ArrayList<CalendarEvent> calendarEvents, Instant dateStart, Instant dateEnd,
			String path) throws IOException {

		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("calendarEvents", calendarEvents);

		Writer writer = new FileWriter(path);
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(getTemplate(), "example");
		mustache.execute(writer, scopes);
		writer.flush();

		File htmlFile = new File(path);
		Desktop.getDesktop().browse(htmlFile.toURI());
	}

	public static void main(String[] args) {
		CalendarGUI c = new CalendarGUI();
		Calendar cccc = new Calendar("thgas.json");
		ArrayList<CalendarEvent> cal = cccc.getCalendarEvents();
		try {
			c.createHTMLTable(cal, null, null, "files/html_files/teste.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
