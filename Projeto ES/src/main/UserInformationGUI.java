package main;

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

/**
 * 
 * Classe que lida com a parte da interface gráfica que pede ao utilizador o seu
 * nome o o seu URL
 *
 */

public class UserInformationGUI {

	private JFrame frame;
	private CalendarManager calendarManager;
	private boolean userInputFrameClosed = false;

	/**
	 * Contrutor
	 * 
	 * @param calendarManager calendarManager a ser guardado
	 */

	public UserInformationGUI(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
		frame = new JFrame("Introduza os seus dados");
	}

	/**
	 * Função que inicia a GUI
	 * 
	 * @return true quando já se tem os inputs necessários, para que a frame da
	 *         outra classe se possa iniciar
	 * @throws InterruptedException
	 */

	public boolean start() throws InterruptedException {
		addUserInputFrameContent();
		while (!userInputFrameClosed)
			Thread.sleep(100);
		return true;
	}

	/**
	 * 
	 * Função que adiciona à frame todos os componentes
	 * 
	 */

	private void addUserInputFrameContent() {
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(3, 2));

		JLabel askNameLabel = new JLabel("Introduza o seu nome", SwingConstants.CENTER);
		JTextField askName = new JTextField();

		JLabel askURLLabel = new JLabel("Introduza o seu URL", SwingConstants.CENTER);
		JTextField askURL = new JTextField();

		JCheckBox checkBox = new JCheckBox("Individual (x) / Grupo ()");
		JButton buttonSolo = new JButton("Abrir calendário");

		frame.add(askNameLabel);
		frame.add(askName);
		frame.add(askURLLabel);
		frame.add(askURL);
		frame.add(checkBox);
		frame.add(buttonSolo);

		buttonSolo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = askName.getText();
				String URL = askURL.getText();

				frame.setVisible(false);
				frame.dispose();

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

		frame.pack();
		frame.setVisible(true);
	}

}
