package com.willvelida.quickqotes;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.BuildConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // UI Variables
    TextView quoteTextView;
    TextView authorTextView;
    Button getQuoteButton;
    Button saveQuoteButton;
    Button tweetComposer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Twitter.initialize(this);

        // Initialize UI Variables on creation of app
        quoteTextView = (TextView) findViewById(R.id.quoteTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        getQuoteButton = (Button) findViewById(R.id.generateQuoteButton);
        saveQuoteButton = (Button) findViewById(R.id.saveQuoteButton);
        tweetComposer = (Button) findViewById(R.id.tweet_composer);

        saveQuoteButton.setVisibility(View.INVISIBLE);
        tweetComposer.setEnabled(false);
    }

    // Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // When an option is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.favoritequotes:
                // // TODO: 18/08/2017 Navigate to Favorite Quotes 
                Log.i("Menu item selected", "Favorite Quotes");
                return true;
            default:
                return false;
        }
    }

    // Generate Quote from Button
    public void generateQuote(View view) {
        tweetComposer.setEnabled(true);
        saveQuoteButton.setVisibility(View.VISIBLE);
        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://api.forismatic.com/api/1.0/?method=getQuote&key=457653&format=json&lang=en");
        } catch (Exception e) {
            // Show user that we could not retrieve data
            Toast.makeText(getApplicationContext(), "Could not retrieve quote", Toast.LENGTH_LONG).show();
            // Show Exception trace to dev team
            e.printStackTrace();
        }

    }

    // Save Quote
    public void saveQuote(View view) {
        // TODO: Save the quote to Favorites
        Log.i("Button Pressed:", "Save!");
    }

    // Tweet the quote to twitter
    public void composeTweet(View view) {
        try {
            new TweetComposer.Builder(MainActivity.this)
                    .text(quoteTextView.getText() + "by " + authorTextView.getText())
                    .show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sorry! We can tweet this right now!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Download task to establish connection with Forismatic API
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // Initialized the result variable
            String result = "";
            // Create a URL variable
            URL url;
            // Create HTTP URL Connection
            HttpURLConnection urlConnection = null;

            // set url to our urls
            try {
                url = new URL(urls[0]);
                // Open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                // Create new input stream
                InputStream in = urlConnection.getInputStream();
                // Create new Input Stream Reader
                InputStreamReader reader = new InputStreamReader(in);
                // create data variable
                int data = reader.read();
                // while loop to read data from API
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                // Once we have the data, return the result
                return result;
            } catch (Exception e) {
                // Show user that we could not retrieve data
                Toast.makeText(getApplicationContext(), "Could not retrieve quote", Toast.LENGTH_LONG).show();
                // Show Exception trace to dev team
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                String message = "";
                // Create a JSON object
                JSONObject jsonObject = new JSONObject(result);
                String quote = "";
                String author = "";
                quote = jsonObject.getString("quoteText");
                author = jsonObject.getString("quoteAuthor");

                // Unless if the author is unknown
                if (quote != "" & author == " ") {
                    // set the quote to the quote view
                    quoteTextView.setText(quote);
                    // set the author to the String value "Unknown"
                    authorTextView.setText("Unknown");
                    // If the quote and author are found
                } else if (quote != "" & author != "") {
                    // set the values to the text view
                    quoteTextView.setText(quote);
                    authorTextView.setText("- " + author);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
