package application.util;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.model.Topic;
import application.model.Word;
import application.persistence.SQLFactoryDAO;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class XMLReader {

	public static void readXMLFile(String path) {

		List<Topic> topicList = new LinkedList<Topic>();

		try {
			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			if (!doc.getDocumentElement().getNodeName().equals("WordBook")) {
				SevereError.show(SevereError.IMPORT_FAILED);
				return;
			}

			NodeList topics = doc.getElementsByTagName("topic");

			for (int t = 0; t < topics.getLength(); t++) {
				Node topicNode = topics.item(t);

				if (topicNode.getNodeType() == Node.ELEMENT_NODE) {
					Element xmlTopic = (Element) topicNode;

					Topic topic = new Topic(xmlTopic.getAttribute("name"));

					NodeList words = xmlTopic.getElementsByTagName("word");

					for (int w = 0; w < words.getLength(); w++) {
						Node wordNode = words.item(w);

						if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
							Element xmlWord = (Element) wordNode;
							String name = xmlWord.getAttribute("name");
							String translation = xmlWord.getAttribute("translation");
							String other = xmlWord.getAttribute("other");

							Word word = new Word(name, translation, other);

							topic.addWord(word);
						}
					}
					topicList.add(topic);
				}
			}
		} catch (Exception e) {
			SevereError.show(SevereError.IMPORT_FAILED);
			return;
		}

		persistContent(topicList);

	}

	/**
	 * Persist a list of topics with vocabulary into a DB.
	 * 
	 * @param topics
	 */
	private static void persistContent(List<Topic> topics) {

		SQLFactoryDAO fDAO = SQLFactoryDAO.getInstance();

		int newTopics = 0, newWords = 0;

		// We go through the topics
		for (Topic t : topics) {
			try {
				// Insert topic and set id.
				// Something went wrong, go to next topic.
				if (!fDAO.getTopicDAO().insert(t))
					continue;
				else
					newTopics++;
			} catch (SQLException | SQLGeneratedKeyException e) {
				// DB error, exit.
				break;
			} catch (SQLDuplicateEntityException e) {
				// If that topic existed, then we seek it and set the id.
				try {
					long idTopic = fDAO.getTopicDAO().findByName(t.getName());
					t.setId(idTopic);
				} catch (SQLException e1) {
					// DB error, exit
					break;
				}
			}

			// In this point, we already have a topic with t.getName() stored in
			// the DB.
			for (Word w : t.getWords()) {
				try {
					fDAO.getWordDAO().insertWord(t, w);
					newWords++;
				} catch (SQLException | SQLGeneratedKeyException e) {
					// DB error, exit
					break;
				} catch (SQLDuplicateEntityException e) {
					// That word existed
					continue;
				}
			}
		}

		InfoDialog.show("File was imported", "New objects. Topics: " + newTopics + ", words: " + newWords);

	}
}
