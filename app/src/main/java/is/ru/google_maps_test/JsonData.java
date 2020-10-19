package is.ru.google_maps_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;


import javax.net.ssl.HttpsURLConnection;


public class JsonData extends AppCompatActivity {
    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;

    ArrayList<HashMap<String, String>> resultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_data);

        //final TextView helloTextView = (TextView) findViewById(R.id.textView);
        //helloTextView.setText(getJSON(url));

        //btnHit = (Button) findViewById(R.id.btnHit);
        txtJson = (TextView) findViewById(R.id.textView);
        resultsList = new ArrayList<>();

        new JsonTask().execute("https://icelandnow.cdn.prismic.io/api/v2/documents/search?ref=X4rX7xAAACAA_8Ip&pageSize=100#format=json");
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(JsonData.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            try {
                JSONObject jsonObj = new JSONObject(result);;
                JSONArray result_array = jsonObj.getJSONArray("results");

                for(int i=0; i < result_array.length(); i++){

                    JSONObject results_filtered = result_array.getJSONObject(i);
                    String id = results_filtered.getString("id");
                    String uid = results_filtered.getString("uid");
                    String type = results_filtered.getString("type");
                    String href = results_filtered.getString("href");
                    String tags = results_filtered.getString("tags");
                    String first_publication_date = results_filtered.getString("first_publication_date");
                    String last_publication_date = results_filtered.getString("last_publication_date");
                    String slugs = results_filtered.getString("slugs");
                    String linked_documents = results_filtered.getString("linked_documents");
                    String lang = results_filtered.getString("lang");
                    String alternate_languages = results_filtered.getString("alternate_languages");

                    // data node is JSON Object
                    JSONObject data_filtered = results_filtered.getJSONObject("data");
                    String data_name = data_filtered.getString("name");
                    String data_url = data_filtered.getString("url");
                    String data_category = data_filtered.getString("category");
                    String data_provider = data_filtered.getString("provider");
                    String data_photovideo = data_filtered.getString("photovideo");
                    String data_lat = data_filtered.getString("lat");
                    String data_long = data_filtered.getString("long");

                    txtJson.setText(data_name);
                    // tmp hash map for single camera feed
                    HashMap<String, String> camera_feed = new HashMap<>();
                    // add each child node to Hashmap key => value
                    camera_feed.put("id", id);
                    //camera_feed.put("type", type);
                    //camera_feed.put("data_url", data_url);
                    //camera_feed.put("", );
                    //camera_feed.put("", );
                    //camera_feed.put("", );
                    //camera_feed.put("", );
                    resultsList.add(camera_feed);
                }

                //Intent intent  = new Intent(getApplicationContext(), MapsActivity.class);
                //intent.putExtra("message", resultsList);
                //startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}

