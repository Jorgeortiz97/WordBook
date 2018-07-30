package application.util;

import java.io.IOException;
import java.util.List;

import application.model.Essay;
import application.model.Grammar;
import application.model.Topic;
import application.model.Word;

public class RTFCreator {

	private final static String[] HEADERS = new String[] { "Word", "Translation", "Other" };

	public static boolean writeRTFDocument(String path, List<Topic> topics, List<Grammar> entries) {
		RTFWriter rtfw = new RTFWriter(path);

		try {
			rtfw.start();

			rtfw.appendTitle("WordBook document");

			for (Topic topic : topics) {
				rtfw.appendTopicTitle(topic.getName());

				// Vocabulary
				if (topic.getWords().size() != 0) {
					rtfw.appendSubtitle("Vocabulary");
					String[][] data = new String[topic.getWords().size()][3];
					List<Word> words = topic.getWords();
					for (int i = 0; i < words.size(); i++) {
						Word w = words.get(i);
						data[i][0] = w.getName();
						data[i][1] = w.getTranslation();
						data[i][2] = w.getOther();
					}
					rtfw.appendTable(HEADERS, data);
				}

				// Essays
				if (topic.getEssays().size() != 0) {
					rtfw.appendSubtitle("Essays");
					for (Essay e : topic.getEssays())
						rtfw.appendBlock(e.getName(), e.getContent());
				}
			}

			// Grammar entries
			if (entries.size() != 0) {
				rtfw.appendTopicTitle("Grammar entries");
				for (Grammar e : entries)
					rtfw.appendBlock(e.getName(), e.getContent());

			}

			rtfw.appendVersion();

			rtfw.end();

		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
