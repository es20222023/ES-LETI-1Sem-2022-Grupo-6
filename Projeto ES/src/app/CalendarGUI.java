package app;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class CalendarGUI {

	private JFrame frame;

	public CalendarGUI() {
		askUserForNameAndURL();
		
		frame = new JFrame();
	}

	private void askUserForNameAndURL() {
		JFrame newFrame = new JFrame();
		newFrame.setSize(500, 300);
		newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addUserInputFrameContent(newFrame);
		newFrame.pack();
		newFrame.setVisible(true);
	}

	private void addUserInputFrameContent(JFrame newFrame) {
		newFrame.setLayout(new GridLayout(3, 3));

		JLabel askNameLabel = new JLabel("Introduza o seu nome");
		JTextField askName = new JTextField();

		JLabel askURLLabel = new JLabel("Introduza o seu URL");
		JTextField askURL = new JTextField();

		JLabel buttonLabel = new JLabel("Carregue neste botão para carregar o seu calendário");
		JButton button = new JButton("Atualizar");

		newFrame.add(askNameLabel);
		newFrame.add(askName);
		newFrame.add(askURLLabel);
		newFrame.add(askURL);
		newFrame.add(buttonLabel);
		newFrame.add(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = askName.getText();
				String URL = askURL.getText();
				
				newFrame.setVisible(false);
				newFrame.dispose();
				
				UrlReader.Urlcatcher(URL,name);
			}
		});
	}

	public static void main(String[] args) {
		CalendarGUI c = new CalendarGUI();
	}
}
