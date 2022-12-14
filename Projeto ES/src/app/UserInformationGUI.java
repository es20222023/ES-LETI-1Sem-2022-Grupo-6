package app;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class UserInformationGUI {

	private JFrame frame;
	private CalendarManager calendarManager;
	private boolean userInputFrameClosed = false;;

	public UserInformationGUI(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

	/**
	 * 
	 * @return true quando já se tem os inputs necessários
	 * @throws InterruptedException
	 */
	
	public boolean start() throws InterruptedException {
		frame = new JFrame("Introduza os seus dados");
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addUserInputFrameContent(frame);
		frame.pack();
		frame.setVisible(true);
		while(!userInputFrameClosed)
			Thread.sleep(100);
		return true;
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

}
