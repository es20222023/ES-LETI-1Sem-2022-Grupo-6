package app;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class MeetingPlannerGUI {

	private JFrame frame;
	private static final int DIMX = 500;
	private static final int DIMY = 150;
	private CalendarManager calendarManager;

	public MeetingPlannerGUI() {
		frame = new JFrame("Meeting Planner");
		calendarManager = new CalendarManager();
		calendarManager.fillWithSavedEvents();
		addFrameContent();
	}

	private void addFrameContent() {
		frame.setSize(DIMX, DIMY);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(7, 2));

		// componente datepicker
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		// component comboBox duração
		String[] meetingDurationInMinutes = new String[CalendarManager.MAX_MEETING_DURATION_IN_MINUTES
				/ CalendarManager.SCHEDULE_BLOCK_SIZE_IN_MINUTES];
		for (int i = CalendarManager.SCHEDULE_BLOCK_SIZE_IN_MINUTES; i <= CalendarManager.MAX_MEETING_DURATION_IN_MINUTES; i += CalendarManager.SCHEDULE_BLOCK_SIZE_IN_MINUTES)
			meetingDurationInMinutes[i / CalendarManager.SCHEDULE_BLOCK_SIZE_IN_MINUTES - 1] = i + "";
		JComboBox<String> boxDuration = new JComboBox<String>(meetingDurationInMinutes);
		boxDuration.setSelectedIndex(0);

		// component comboBox users
		String[] users = calendarManager.getUsers();
		ArrayList<String> selectedUsers = new ArrayList<>();
		JComboBox<String> boxUsers = new JComboBox<String>(users);
		boxUsers.setSelectedIndex(0);
		boxUsers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedUsers.add(users[boxUsers.getSelectedIndex()]);
			}
		});

		// componentes checkbox
		JCheckBox checkBoxMorning = new JCheckBox("Reunião de manhã");
		JCheckBox checkBoxRepeating = new JCheckBox("Reunião periódica");

		// componente textfield
		JTextField location = new JTextField();

		// component button
		JButton loadButton = new JButton("Carregar horários possíveis das reuniões");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Date date = (Date) datePicker.getModel().getValue();
				int duration = Integer.parseInt(meetingDurationInMinutes[boxDuration.getSelectedIndex()]);
				boolean morning = checkBoxMorning.isSelected();
				boolean repeating = checkBoxRepeating.isSelected();
				String[] users = selectedUsers.toArray(new String[0]);

				ArrayList<Instant> possibleInstants = calendarManager.sugestMeeting(users, duration, morning,
						date.toInstant(), repeating);

				loadNewFrame(possibleInstants, date.toInstant(), duration, repeating, users, location.getText());

			}
		});

		// componentes label
		JLabel chooseDate = new JLabel("Escolha uma data máxima para a reunião", SwingConstants.CENTER);
		JLabel chooseMeetingDuration = new JLabel("Escolha a duração da reunião, em minutos", SwingConstants.CENTER);
		JLabel chooseUsers = new JLabel("Escolha os users que participarão na reunião", SwingConstants.CENTER);
		JLabel chooseMorning = new JLabel("(X) Reunião de manhã / () Reunião à tarde", SwingConstants.CENTER);
		JLabel chooseRepeating = new JLabel("(X) Reunião periódica / () Reunião única", SwingConstants.CENTER);
		JLabel chooseLocation = new JLabel("Insira a localização", SwingConstants.CENTER);
		JLabel load = new JLabel("Carregue no botão para carregar os instantes possíveis", SwingConstants.CENTER);

		// adcionar componentes à frame
		frame.add(chooseDate);
		frame.add(datePicker);
		frame.add(chooseMeetingDuration);
		frame.add(boxDuration);
		frame.add(chooseUsers);
		frame.add(boxUsers);
		frame.add(chooseLocation);
		frame.add(location);
		frame.add(chooseMorning);
		frame.add(checkBoxMorning);
		frame.add(chooseRepeating);
		frame.add(checkBoxRepeating);
		frame.add(load);
		frame.add(loadButton);

		frame.pack();
		frame.setVisible(true);

	}

	private void loadNewFrame(ArrayList<Instant> possibleInstants, Instant endDate, int durationInMinutes,
			boolean repeating, String[] users, String location) {

		JFrame newFrame = new JFrame();
		newFrame.setSize(DIMX, DIMY);
		newFrame.setLayout(new GridLayout(2, 2));

		JLabel instantLabel = new JLabel("Escolha um dos horários para a reunião", SwingConstants.CENTER);
		JLabel buttonLabel = new JLabel("Carregue no botão para criar a reunião", SwingConstants.CENTER);

		Instant[] instants = possibleInstants.toArray(new Instant[0]);

		String[] dates = new String[instants.length];
		for (int i = 0; i < dates.length; i++) {
			dates[i] = CalendarEvent.dateInstantToString(instants[i]);
		}

		JComboBox<String> instantsBox = new JComboBox<String>(dates);

		JButton button = new JButton("Criar reunião");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				calendarManager.createMeeting(instants[instantsBox.getSelectedIndex()], endDate, durationInMinutes,
						repeating, users, location);
			}
		});

		newFrame.add(instantLabel);
		newFrame.add(instantsBox);
		newFrame.add(buttonLabel);
		newFrame.add(button);

		newFrame.pack();
		newFrame.setVisible(true);
	}

	public static void main(String[] args) {
		MeetingPlannerGUI m = new MeetingPlannerGUI();
	}

}
