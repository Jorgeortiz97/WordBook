package application.style;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import application.persistence.SQLFactoryDAO;
import application.util.FatalError;
import application.view.IStylish;

public class Style {

	public static final int DARK_THEME = 0;
	public static final int LIMA = 1;
	public static final int SOFT_PINK = 2;
	public static final int BLUE_SKY = 3;
	public static final int COLORFUL = 4;
	public static final int STAT = 5;

	private static final String[] THEMES = { "DarkTheme.css", "GreenTheme.css", "SoftPink.css", "BlueSky.css",
			"Colorful.css", "Stat.css" };

	private static Style instance;
	private List<IStylish> listeners = new LinkedList<IStylish>();
	private String currentStyle;

	private Style() {
	}

	public static Style getInstance() {
		if (instance == null) {
			instance = new Style();

			int initialStyle = -1;
			try {
				initialStyle = SQLFactoryDAO.getInstance().getProfileDAO().getStyle();
			} catch (SQLException e) {
			}

			if (initialStyle != -1)
				instance.currentStyle = instance.getResource(initialStyle);
			else
				FatalError.show(FatalError.DB_GET);
		}
		return instance;
	}

	private void notify(String newStyle) {
		List<IStylish> list;
		synchronized (this) {
			list = new LinkedList<IStylish>(listeners);
		}
		for (IStylish l : list)
			l.styleChange(newStyle);
	}

	public void changeStyle(int newStyle) {
		currentStyle = getResource(newStyle);
		notify(currentStyle);
	}

	public void addListener(IStylish listener) {
		listeners.add(listener);
	}

	public void removeListener(IStylish listener) {
		listeners.remove(listener);
	}

	public String getResource(int i) {
		return Style.class.getResource(THEMES[i]).toExternalForm();
	}

	public String getStyle() {
		return currentStyle;
	}

}
