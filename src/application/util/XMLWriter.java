package application.util;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.Topic;
import application.model.Word;
import application.persistence.SQLFactoryDAO;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class XMLWriter {

	public static boolean writeXMLFile(String destination) {

		List<Topic> topics = getTopics();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element root = doc.createElement("WordBook");
			doc.appendChild(root);

			for (Topic t: topics) {
				Element topic = doc.createElement("topic");
				root.appendChild(topic);

				Attr name = doc.createAttribute("name");
				name.setValue(t.getName());
				topic.setAttributeNode(name);

				for (Word w: t.getWords()) {
					Element word = doc.createElement("word");
					// Name
					Attr attrName = doc.createAttribute("name");
					attrName.setValue(w.getName());
					word.setAttributeNode(attrName);
					// Translation
					Attr attrTrans = doc.createAttribute("translation");
					attrTrans.setValue(w.getTranslation());
					word.setAttributeNode(attrTrans);
					topic.appendChild(word);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(destination));
			transformer.transform(source, result);

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private static List<Topic> getTopics() {

		SQLFactoryDAO fDAO = SQLFactoryDAO.getInstance();

		List<Topic> topicNames = null;
		List<Topic> topics = new LinkedList<Topic>();

		int expWords = 0, expTopics = 0;
		Topic topic;

		try {
			topicNames = fDAO.getTopicDAO().findAll();
			for (Topic t: topicNames) {
				topic = fDAO.getTopicDAO().findById(t.getId());
				expTopics++;
				expWords += topic.getWords().size();
				topics.add(topic);
			}
		} catch (SQLException e) {
			return null;
		}

		if (topics.isEmpty())
			return null;

		InfoDialog.show("Database was exported", "Exported objects. Topics: " +
				expTopics + " words: " + expWords);
		return topics;
	}
}