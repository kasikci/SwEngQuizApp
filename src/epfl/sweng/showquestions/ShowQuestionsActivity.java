package epfl.sweng.showquestions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.swengquizapp.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ShowQuestionsActivity extends Activity {
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_questions);
		// Show the Up button in the action bar.
		setupActionBar();
		// create an async task to fetch the question
		showARandomQuestion();
	}

	private void showARandomQuestion() {
		// TODO Auto-generated method stub
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()) {
			new GetAsyncTask().execute("https://sweng-quiz.appspot.com/quizquestions/random");
		} else {
			Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// TODO: pass in a quiz question here, perhaps?
	public void displayQuizQuestion(int id, String question,
			List<String> answers, int solutionIndex, JSONArray tagsArray) {
		// display the question
		
		// display the answers
		String[] answersArray = answers.toArray(new String[answers.size()]);
		ArrayAdapter adapter = new ArrayAdapter<String>(this, 
		        android.R.layout.simple_list_item_1, answersArray);
		ListView listView = (ListView) findViewById(R.id.answers);
		listView.setAdapter(adapter);
	}
	
	/*
	 * The AsyncTask of ShowQuestionsActivity
	 * Fetches a random quiz question from the server
	 */	
	private class GetAsyncTask extends AsyncTask<String, Void, String> {
		private static final String DEBUG_TAG = "GetAsyncTask";
		
		@Override
		protected String doInBackground(String... urls) {
			if (urls.length != 1)
				throw new IllegalArgumentException("GET not called with 1 argument!");
			
        	HttpGet randomQuestion = new HttpGet(urls[0]);
        	ResponseHandler<String> firstHandler = new BasicResponseHandler();
			try {
				String question = SwengHttpClientFactory.getInstance().execute(randomQuestion, firstHandler);
				return question;
			} catch (ClientProtocolException e) {
				Log.d(DEBUG_TAG, "ClientProtocolException: Unable to retrieve web page. URL may be invalid.");			
			} catch (IOException e) {
				return "IOException: Unable to retrieve web page. URL may be invalid.";
			}
			return "No answer from the server!";
		}
		
		// onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(String questionString) {
	        textView.setText(questionString);
	        
	        try {
				JSONObject jsonObject = new JSONObject(questionString);
				int id = (Integer) jsonObject.get("id");
				String question = (String) jsonObject.get("question");
				JSONArray answersArray = (JSONArray) jsonObject.get("answers");
				List<String> answers = new ArrayList<String>();
				for (int i = 0; i < answersArray.length(); i++) {
					answers.add(answersArray.get(i).toString());
				}
				int solutionIndex = (Integer) jsonObject.get("solutionIndex");
				JSONArray tagsArray = (JSONArray) jsonObject.get("tags");
				List<String> tags = new ArrayList<String>();
				for (int i = 0; i < answersArray.length(); i++) {
					tags.add(answersArray.get(i).toString());
				}
				ShowQuestionsActivity.this.displayQuizQuestion(id, question, answers, solutionIndex, tagsArray);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }	
	}	

}
