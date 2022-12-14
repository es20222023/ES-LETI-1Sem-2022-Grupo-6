package app;

public class CalendarGUI {


	private CalendarManager calendarManager;
	private boolean userInputFrameClosed;

	public CalendarGUI() {
		this.calendarManager = new CalendarManager();
		userInputFrameClosed = false;
	}

	public void start() throws InterruptedException {
		UserInformationGUI askForInformationGUI = new UserInformationGUI(calendarManager);
		askForInformationGUI.start();
		CalendarGUIFrame frame = new CalendarGUIFrame(calendarManager);
		frame.start();
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
