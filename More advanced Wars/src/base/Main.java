package base;

/***
 * This class represents the Runtime of the application.
 * 
 * @author Alexander Dreher
 *
 */
public class Main {

	public static int version = 1;
	private static Window window;
	private static boolean showGrid = false;

	public static Window getWindow() {
		return window;
	}

	public static boolean showGrid() {
		return showGrid;
	}

	/**
	 * This is the main method of the package, and initializes the game if run.
	 * 
	 * @param args
	 *            Additional arguments. (currently not used)
	 */
	public static void main(String[] args) {
		window = new Window();
		window.setVisible(true);
		window.grabFocusOnStart();
	}

	/**
	 * This method simply replaces the System.out.println in name and provides some
	 * additional information and formatting.
	 * 
	 * @param s
	 *            The message object to be printed on the output console.
	 */
	public static void log(Object s) {
		System.out.println(new java.util.Date(System.currentTimeMillis()) + " '" + Thread.currentThread().getId() + "' "
				+ "[DEBUG] " + s);
	}
}