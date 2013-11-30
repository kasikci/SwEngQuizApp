package epfl.sweng.showquestions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

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
		textView = (TextView) findViewById(R.id.showQuestionTextView); 
		// create an async task to fetch the question
		showARandomQuestion();
	}

	private void showARandomQuestion() {
		// TODO Auto-generated method stub
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()) {
			new GetAsyncTaskHttpURL().execute("https://sweng-quiz.appspot.com/quizquestions/random");
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
	
	private class GetAsyncTaskHttpURL extends AsyncTask<String, Void, String> {
		private static final String DEBUG_TAG = "GetAsyncTask";
		
		@Override
		protected String doInBackground(String... urls) {
	        try {
	            return downloadUrl(urls[0]);
	        } catch (IOException e) {
	            return "Unable to retrieve web page. URL may be invalid.";
	        }
		}
		
		// onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(String result) {
	        textView.setText(result);
	   }

		private String downloadUrl(String downloadUrl) throws IOException{
			InputStream inputStream = null;
			// only display the first 1000 characters of the page
			int len = 1000; 
			try{
				URL url = new URL(downloadUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setReadTimeout(10000); // milliseconds
				connection.setConnectTimeout(15000);
				connection.setRequestMethod("GET");
				connection.setDoInput(true);
				// initiate the query
				connection.connect();
				int response = connection.getResponseCode();
				Log.d(DEBUG_TAG, "The response is: " + response);
				inputStream = connection.getInputStream();			
				// Convert the InputStream into a string
				String contentAsString = readIt(inputStream, len);
				return contentAsString;
			} finally{
				if(inputStream != null)
					inputStream.close();
			}
		}

		private String readIt(InputStream inputStream, int len) throws IOException {
			Reader reader = null;
			reader = new InputStreamReader(inputStream, "UTF-8");
			char [] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}	
	}

}
