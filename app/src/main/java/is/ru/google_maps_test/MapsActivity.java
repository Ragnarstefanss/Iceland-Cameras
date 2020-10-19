package is.ru.google_maps_test;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog pd;
    Map<String, String> mMarkerMap = new HashMap<>();
    ArrayList<HashMap<String, String>> resultsList;

    //Intent intent = getIntent();
    //String str = intent.getStringExtra("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        resultsList = new ArrayList<>();

        new MapsActivity.JsonTask().execute("https://icelandnow.cdn.prismic.io/api/v2/documents/search?ref=X4rX7xAAACAA_8Ip&pageSize=100#format=json");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MapsActivity.this);
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

                    //txtJson.setText(data_name);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng reykjavik = new LatLng(64, -21);
        mMap.addMarker(new MarkerOptions().position(reykjavik).title("Reykjavík"));

        LatLng akureyri = new LatLng(65.6833306, -18.0999996);
        mMap.addMarker(new MarkerOptions().position(akureyri).title("Akureyri"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(reykjavik));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //String placeID = mMarkerMap.get(marker.getId());
                //String placeName = marker.getTitle();
                Intent intent = new Intent(getApplicationContext(), WebView_camera.class);
                //intent.putExtra(PLACE_NAME, placeName);
                //intent.putExtra(PLACE_ID, placeID);
                startActivity(intent);
                return false;
            }
        });

    } //END OF  public void onMapReady(GoogleMap googleMap)
}