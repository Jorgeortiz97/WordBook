package application.util;

public class MultipleInputAnswer {

	private boolean answer;
	private String[] args;

	public MultipleInputAnswer(boolean answer) {
		this(answer, null);
	}

	public MultipleInputAnswer(boolean answer, String[] args) {
		this.answer = answer;
		this.args = args;
	}

	public boolean isAnswered() {
		return answer;
	}

	public String[] getAnswers() {
		return args;
	}

}
