package application.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import application.MainApp;

public class RTFWriter {

	private BufferedWriter writer;
	private String path;
	private final static String SEPARATOR = System.getProperty("line.separator");

	private final static int TITLE_SIZE = 80;
	private final static int TOPIC_TITLE_SIZE = 60;
	private final static int SUBTITLE_SIZE = 40;
	private final static int BODY_SIZE = 30;
	private final static int VERSION_SIZE = 18;

	public RTFWriter(String path) {
		this.path = path;
	}

	public void start() throws IOException {
		writer = new BufferedWriter(new FileWriter(path));
		wrln("{\\rtf1\\ansi\\deff0");
	}

	public void appendTitle(String title) throws IOException {
		wrln("\\qc\\par\\fs" + TITLE_SIZE + " " + title);
		BufferedReader reader = new BufferedReader(new FileReader("img/rawicon"));
		char[] buffer = new char[8096];

		int n;

		while ((n = reader.read(buffer)) > 0) {
			writer.write(buffer, 0, n);
		}

		reader.close();
	}

	public void appendTopicTitle(String topicTitle) throws IOException {
		wrln("\\par\\par\\ql\\fs" + TOPIC_TITLE_SIZE + " " + topicTitle + "");
	}

	public void appendSubtitle(String subtitle) throws IOException {
		wrln("\\par\\qc\\fs" + SUBTITLE_SIZE + " " + subtitle + "\\par");
	}

	public void appendBlock(String title, String content) throws IOException {
		wrln("\\par\\ql\\b1\\fs" + BODY_SIZE + " " + title + "\\b0\\par");
		wrln("\\par\\fs" + BODY_SIZE + " " + getFormattedText(content) + "\\par");
	}

	public void appendTable(String[] headers, String[][] data) throws IOException {

		int cols = headers.length;
		int rows = data.length;

		wrln("\\fs" + BODY_SIZE + "\\qc\\trowd"); // Table's beginning
		for (int i = 1; i <= cols; i++)
			wrln("\\cellx" + 3000 * i);

		// Print the table's header:
		wrln("\\b1"); // Bold enabled
		for (int c = 0; c < cols; c++)
			wrln(headers[c] + "\\intbl\\cell");
		wrln("\\b0\\row"); // Bold disabled

		// Print the table's content:

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++)
				wrln(data[r][c] + "\\intbl\\cell");
			wrln("\\row");
		}

		wrln("\\pard");

	}

	public void appendVersion() throws IOException {
		wrln("\\qr\\i1\\par\\fs" + VERSION_SIZE + " Document generated with WordBook " + MainApp.VERSION + "\\par");
	}

	public void end() throws IOException {
		wrln("}");
		writer.close();
	}

	private static String getFormattedText(String content) {
		return content.replaceAll("\\n", "\\\\par ");
	}

	/***
	 * Writes a line into the file.
	 *
	 * @param data
	 *            Data to be written
	 * @throws IOException
	 */
	private void wrln(String data) throws IOException {
		writer.write(data + SEPARATOR);
	}
}
