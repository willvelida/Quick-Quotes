package com.willvelida.quickqotes;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI Variables on creation of app
        quoteTextView = (TextView) findViewById(R.id.quoteTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        getQuoteButton = (Button) findViewById(R.id.generateQuoteButton);
    }

    // Generate Quote from Button
    public void generateQuote(View view) {
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

                // If the quote and author are found
                if (quote != "" & author != "") {
                    // set the values to the text view
                    quoteTextView.setText(quote);
                    authorTextView.setText(author);
                    // Unless if the author is unknown
                } else if (quote != "" & author == "") {
                    // set the quote to the quote view
                    quoteTextView.setText(quote);
                    // set the author to the String value "Unknown"
                    authorTextView.setText("Unknown author");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
