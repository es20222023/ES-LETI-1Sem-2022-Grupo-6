package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 * 
 * Classe que constroi a <b>JFrame</b> que vai conter a demonstração gráfica dos
 * eventos
 *
 */

public class CalendarGUIFrame {
	/**
	 * número de colunas total
	 */
	private static final int NUM_COLUMNS = 4;
	/*
	 * número de linhas ocupadas por eventos inicialmente
	 */
	private static final int NUM_ROWS = 10;
	/**
	 * número de <b>JComponent</b> que servem de suporte à visualização, não são
	 * eles próprios eventos
	 */
	private static final int NUMBER_OF_NON_EVENT_COMPONENTS = 12;

	private int currentIndex;
	private JFrame frame;
	private CalendarManager calendarManager;

	/**
	 * Construtor
	 * 
	 * @param calendarManager o objeto que contém os eventos que vão ser mostrados
	 */

	public CalendarGUIFrame(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
		currentIndex = NUM_ROWS;
		frame = new JFrame();
	}

	/**
	 * Inicia a frame
	 */

	public void start() {
		addCalendarFrameContent();
	}

	/**
	 * Adiciona todos os componentes à frame e implementa <b>ActionListener</b>s que
	 * fazem as várias interações
	 */

	private void addCalendarFrameContent() {
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUMBER_OF_NON_EVENT_COMPONENTS / NUM_COLUMNS + NUM_ROWS, NUM_COLUMNS));

		// componentes datepicker
		UtilDateModel model1 = new UtilDateModel();
		UtilDateModel model2 = new UtilDateModel();
		Properties p = DateLabelFormatter.fillProperties();
		JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, p);
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
		JDatePickerImpl datePickerStart = new JDatePickerImpl(datePanel1, new DateLabelFormatter()); // datePicker.getModel().getValue();
		JDatePickerImpl datePickerEnd = new JDatePickerImpl(datePanel2, new DateLabelFormatter());

		// componente Combobox
		String[] numberOfRowsPerPage = { "5", "10", "15", "20" };
		JComboBox<String> box = new JComboBox<String>(numberOfRowsPerPage); // box.getSelectedIndex()

		box.setSelectedIndex(1);

		JButton refreshButton = new JButton("Carregar eventos entre as datas escolhidas");

		// componentes Header
		JLabel userHeader = new JLabel("User", SwingConstants.CENTER);
		JLabel dateHeader = new JLabel("Date", SwingConstants.CENTER);
		JLabel locationHeader = new JLabel("Location", SwingConstants.CENTER);
		JLabel summaryHeader = new JLabel("Summary", SwingConstants.CENTER);

		setBorder(userHeader);
		setBorder(dateHeader);
		setBorder(locationHeader);
		setBorder(summaryHeader);

		// botões para andar para trás/frente no tempo
		JButton goBack = new JButton("<");
		JButton goForward = new JButton(">");

		frame.add(goBack);
		frame.add(new JLabel("Move back in time", SwingConstants.CENTER));
		frame.add(new JLabel("Move forward in time", SwingConstants.CENTER));
		frame.add(goForward);

		frame.add(datePickerStart);
		frame.add(datePickerEnd);
		frame.add(box);
		frame.add(refreshButton);

		frame.add(userHeader);
		frame.add(dateHeader);
		frame.add(locationHeader);
		frame.add(summaryHeader);

		addEventsToFrame(calendarManager.getEvents(), NUM_ROWS, 0, NUM_ROWS);

//		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<CalendarEvent> events = calendarManager.getEvents();

//		datePicker.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				dates.add((Date) datePicker.getModel().getValue());
//				System.out.println("Date added");
//			}
//		});

		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Refresh");

				// remover componentes antigos
				removeComponentsFromFrame();

				// adicionar novos
				int numEventsPerPage = Integer.parseInt(numberOfRowsPerPage[box.getSelectedIndex()]);
				frame.setLayout(
						new GridLayout(NUMBER_OF_NON_EVENT_COMPONENTS / NUM_COLUMNS + numEventsPerPage, NUM_COLUMNS));

				Date firstDate = (Date) (datePickerStart.getModel().getValue());
				Date lastDate = (Date) (datePickerEnd.getModel().getValue());

				if (firstDate == null || lastDate == null) {
					events.clear();
					events.addAll(calendarManager.getEventsBetweenDates(null, null));
				} else {

					Instant firstInstant = firstDate.toInstant();
					Instant lastInstant = lastDate.toInstant();
					events.clear();
					events.addAll(calendarManager.getEventsBetweenDates(firstInstant, lastInstant));
				}
//				if (dates.size() >= 2) {
//					dates.sort(null);
//					Instant firstInstant = dates.get(0).toInstant();
//					Instant lastInstant = dates.get(dates.size() - 1).toInstant();
//					events.clear();
//					events.addAll(calendarManager.getEventsBetweenDates(firstInstant, lastInstant));
//
//				} else {
//					events.clear();
//					events.addAll(calendarManager.getEventsBetweenDates(null, null));
//				}
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

	/**
	 * Remove da frame os componentes que fazem parte dos eventos
	 */

	private void removeComponentsFromFrame() {
		Component[] components = frame.getContentPane().getComponents();
		for (int i = NUMBER_OF_NON_EVENT_COMPONENTS; i < components.length; i++)
			frame.remove(components[i]);
	}

	/**
	 * Adiciona à frame os eventos dados
	 * 
	 * @param events         a lista que contém os eventos a adicionar
	 * @param numberOfEvents a quantidade de eventos a adicionar
	 * @param start          o índice da lista onde é para começar a adicionar
	 *                       eventos
	 * @param end            o último indice da lista a adicionar
	 */

	private void addEventsToFrame(ArrayList<CalendarEvent> events, int numberOfEvents, int start, int end) {
		for (int i = Math.max(start, 0); i < Math.min(end, events.size()) && events.size() > 0; i++) {
			if (numberOfEvents <= 0)
				break;
			numberOfEvents--;

			CalendarEvent event = events.get(i);
			JLabel userLabel = new JLabel(event.getUsername());
			JLabel dateLabel = new JLabel(event.getDateStartString() + " to " + event.getDateEndString());
			JLabel locationLabel = new JLabel(event.getLocation());
			JLabel summaryLabel = new JLabel(event.getSummary());

			boolean color = i % 2 == 0;
			changeLabelBackgroundColor(userLabel, color);
			changeLabelBackgroundColor(dateLabel, color);
			changeLabelBackgroundColor(locationLabel, color);
			changeLabelBackgroundColor(summaryLabel, color);

			frame.add(userLabel);
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
		currentIndex = Math.min(end, events.size());
//		frame.pack();
	}

	/**
	 * Função que muda a cor de fundo das labels
	 * 
	 * @param label label a ser mudada
	 * @param color decide a cor a ser usada
	 */

	private void changeLabelBackgroundColor(JLabel label, boolean color) {
		setBorder(label);

		label.setOpaque(true);
		if (color)
			label.setBackground(Color.GRAY);
		else
			label.setBackground(Color.WHITE);
	}

	/**
	 * Função que adiciona uma border a uma <b>JLabel</b>
	 * 
	 * @param label a label a ser mudada
	 */

	private void setBorder(JLabel label) {
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		label.setBorder(border);
	}

}
