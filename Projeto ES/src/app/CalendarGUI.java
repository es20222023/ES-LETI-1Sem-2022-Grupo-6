package app;

/**
 * 
 * Classe que serve para iniciar as Interfaces gr�ficas da aplica��o
 *
 */

public class CalendarGUI {

	private CalendarManager calendarManager;

	/**
	 * Construtor inicializa o objeto <b>CalendarManager</b>
	 */

	public CalendarGUI() {
		this.calendarManager = new CalendarManager();
	}

	/**
	 * Inicia as interfaces gr�ficas
	 * 
	 * @throws InterruptedException
	 */

	public void start() throws InterruptedException {
		UserInformationGUI askForInformationGUI = new UserInformationGUI(calendarManager);
		askForInformationGUI.start();
		CalendarGUIFrame frame = new CalendarGUIFrame(calendarManager);
		frame.start();
		MeetingPlannerGUI m = new MeetingPlannerGUI();
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
