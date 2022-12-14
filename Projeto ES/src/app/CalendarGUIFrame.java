package app;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class CalendarGUIFrame {

	private static final int NUM_COLUMNS = 3;
	private static final int NUM_ROWS = 10;
	private static final int NUMBER_OF_NON_EVENT_COMPONENTS = 9;

	private int currentIndex;
	private JFrame frame;
	private CalendarManager calendarManager;

	public CalendarGUIFrame(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
		currentIndex = NUM_ROWS;
		frame = new JFrame();
	}

	public void start() {
		addCalendarFrameContent();
	}
	
	private void addCalendarFrameContent() {
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUMBER_OF_NON_EVENT_COMPONENTS / 3 + NUM_ROWS, NUM_COLUMNS));

		// componente datepicker
		UtilDateModel model = new UtilDateModel();
		Properties p = DateLabelFormatter.fillProperties();
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter()); // datePicker.getModel().getValue();

		// componente Combobox
		String[] numberOfRowsPerPage = { "5", "10", "15", "20" };
		JComboBox<String> box = new JComboBox<String>(numberOfRowsPerPage); // box.getSelectedIndex()

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
			JLabel dateLabel = new JLabel(event.getDateStartString() + " to " + event.getDateEndString());
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

}
