package application.view;

public interface IController {
	// Initialize associated pane
	public boolean init(Object argument);

	// Determines if a controller has to be initialized only the first time it is used.
	public boolean onlyFirstTime();

	// Determines which panel should be shown if back button is pressed
	public int backPane();
}
