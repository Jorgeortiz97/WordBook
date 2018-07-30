package application;

import java.io.IOException;

import application.persistence.SQLFactoryDAO;
import application.util.FatalError;
import application.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @web http://www.jorgeortizesc.com
 * @author Jorge
 *
 */
public class MainApp extends Application {

	private Stage primaryStage;
	private AnchorPane rootLayout;

	public static final byte MAJOR = 1;
	public static final byte MINOR = 0;
	public static final boolean IS_RELEASE_CANDIDATE = true;
	public static final byte RC = 1;
	public static String VERSION;

	@Override
	public void start(Stage primaryStage) {

		VERSION = "v" + MAJOR + "." + MINOR;
		if (IS_RELEASE_CANDIDATE)
			VERSION += "rc" + RC;

		// Initialize persistence
		if (!SQLFactoryDAO.getInstance().init())
			FatalError.show(FatalError.DB_INIT);

		// Create the stage
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WordBook");

		primaryStage.setMinHeight(500);
		primaryStage.setMinWidth(800);

		// Set the icon
		primaryStage.getIcons().add(new Image("file:img/icon.png"));

		// Initialize GUI Controllers:
		MenuController mc = new MenuController(primaryStage);
		initRootLayout(mc);
		if (!mc.init(rootLayout))
			FatalError.show(FatalError.RES_NOT_FOUND);
	}

	public void initRootLayout(MenuController mc) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			loader.setController(mc);
			rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}