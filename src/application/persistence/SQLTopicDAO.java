package application.persistence;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import application.model.Essay;
import application.model.Topic;
import application.model.Word;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLTopicDAO {

	private String tableName = "TTopic";
	private String[] selParams = { "name" };
	private String[] updParams = selParams;
	private String[] insParams = selParams;

	public List<Topic> findAll() throws SQLException {
		List<IModelObj> list = SQLOperation.getAll(tableName);
		List<Topic> topics = new LinkedList<Topic>();
		if (list != null)
			for (IModelObj obj : list)
				topics.add(new Topic(obj.getName(), obj.getId()));
		return topics;
	}

	public long findByName(String name) throws SQLException {

		return SQLOperation.getId(name, tableName);
	}

	public Topic findById(long id) throws SQLException {

		String[] result = SQLOperation.get(id, tableName, selParams);
		Topic topic = new Topic(result[0]);
		topic.setId(id);

		SQLEssayDAO essayDAO = SQLFactoryDAO.getInstance().getEssayDAO();
		List<Essay> essays = essayDAO.findAllByTopic(topic);
		for (Essay e : essays)
			topic.addEssay(e);

		SQLWordDAO wordDAO = SQLFactoryDAO.getInstance().getWordDAO();
		List<Word> words = wordDAO.findAllByTopic(topic);
		for (Word w : words)
			topic.addWord(w);

		return topic;
	}

	public boolean delete(long id) throws SQLException {
		Topic topic = findById(id);

		SQLWordDAO wdao = SQLFactoryDAO.getInstance().getWordDAO();
		SQLEssayDAO edao = SQLFactoryDAO.getInstance().getEssayDAO();

		List<Word> words = topic.getWords();
		for (Word w : words)
			wdao.deleteWord(w.getId());

		List<Essay> essays = topic.getEssays();
		for (Essay e : essays)
			edao.deleteEssay(e.getId());

		SQLOperation.delete(id, "TStatTopic");

		return SQLOperation.delete(id, tableName);
	}

	public boolean update(Topic topic) throws SQLException, SQLDuplicateEntityException {
		String[] newName = { topic.getName() };
		return SQLOperation.update(topic.getId(), tableName, topic.getName(), updParams, newName);
	}

	public boolean insert(Topic topic) throws SQLException, SQLDuplicateEntityException, SQLGeneratedKeyException {
		long id = SQLOperation.insert(topic, tableName, insParams, new String[] { topic.getName() });
		if (id != -1) {
			topic.setId(id);
			return true;
		}
		return false;
	}

}
