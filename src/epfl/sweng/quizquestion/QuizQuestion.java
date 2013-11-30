package epfl.sweng.quizquestion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizQuestion {
	protected long id;
	protected String question;
	protected List<String> answers;
	protected int solutionIndex;
	protected Set<String> tags;
	
	public QuizQuestion(final long id, final String question, 
						final List<String> answers, final int solutionIndex,
						final Set<String> tags){
		// TODO:  check if having answers and tags as null pose a problem
		this.id = id;
		this.question = question;
		this.answers = new ArrayList<String>(answers);
		this.solutionIndex = solutionIndex;
		this.tags = new HashSet<String>(tags);
	}
	
	
	
}
