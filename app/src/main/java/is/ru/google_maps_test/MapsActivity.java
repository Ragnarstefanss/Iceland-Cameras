package is.ru.google_maps_test;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, FilterDialog.FilterDialogListener {

    private GoogleMap mMap;
    ProgressDialog pd;
    Map<String, String> mMarkerMap = new HashMap<>();
    public ArrayList<HashMap<String, String>> webcamList = new ArrayList<>();
    String filter_choice = "ALL";
    Button button;
    Spinner spinner;

    private TextView textViewfilterChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SSG: Ef við viljum setja þetta í landscape getum við smellt þessu inn
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_maps);
        // Start background task to load JSON from API
        new JsonTask().execute();
        //textViewfilterChoice.setText(filter_choice);
        //get data from json file
        //tmp comment   String file_data = loadJSONFromAsset();
        //webcamList = parseJsonData(file_data, webcamList);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getMapData();

        spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filter_choice = spinner.getSelectedItem().toString();;
                getMapData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    public void openDialog() {
        FilterDialog filterDialog = new FilterDialog();
        filterDialog.show(getSupportFragmentManager(), "filter dialog");
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("iceland-now-json.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Parses JSON data from string to an array
     */
    public ArrayList<HashMap<String, String>> parseJsonData(String file_data, ArrayList<HashMap<String, String>> results_list) {
        try {
            JSONObject jsonObj = new JSONObject(file_data);
            JSONArray result_array = jsonObj.getJSONArray("results");
            //ArrayList<HashMap<String, String>> camera_feed_results = new ArrayList<>();
            for (int i = 0; i < result_array.length(); i++) {
                JSONObject results_filtered = result_array.getJSONObject(i);
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
                camera_feed.put("data_name", data_name);
                camera_feed.put("data_url", data_url);
                camera_feed.put("data_lat", data_lat);
                camera_feed.put("data_long", data_long);
                camera_feed.put("data_category", data_category);
                results_list.add(camera_feed);
            }
            return results_list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getMapData() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public class JsonTask extends AsyncTask<String, String, String> {

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

            //Prismic.io requires data version ("ref") to be sent, get the latest so updates automaticly pop in
            String masterRef = getMasterRef();
            String apiURL = "https://icelandnow.cdn.prismic.io/api/v2/documents/search?ref=" + masterRef + "&pageSize=100#format=json";

            try {
                URL url = new URL(apiURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String file_data = String.valueOf(buffer);
                try {
                    JSONObject jsonObj = new JSONObject(file_data);
                    JSONArray result_array = jsonObj.getJSONArray("results");
                    ArrayList<HashMap<String, String>> camera_feed_results = new ArrayList<>();
                    for (int i = 0; i < result_array.length(); i++) {
                        JSONObject results_filtered = result_array.getJSONObject(i);
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
                        camera_feed.put("data_name", data_name);
                        camera_feed.put("data_url", data_url);
                        camera_feed.put("data_lat", data_lat);
                        camera_feed.put("data_long", data_long);
                        camera_feed.put("data_category", data_category);
                        camera_feed_results.add(camera_feed);
                        webcamList.add(camera_feed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
            // ssg test
            renderCameras(mMap);
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }

        protected String getMasterRef() {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://icelandnow.cdn.prismic.io/api/v2");
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String file_data = String.valueOf(buffer);
                try {
                    JSONObject jsonObj = new JSONObject(file_data);
                    JSONArray result_array = jsonObj.getJSONArray("refs");
                    ArrayList<HashMap<String, String>> camera_feed_results = new ArrayList<>();
                    for (int i = 0; i < result_array.length(); i++) {
                        JSONObject zz = result_array.getJSONObject(i);
                        String theRef = zz.getString("ref");
                        return theRef;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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


    }

    public HashMap<String, String> getDataPoint(int i, ArrayList<HashMap<String, String>> array) {
        HashMap<String, String> camera_feed = array.get(i);
        return camera_feed;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @return
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        clearMap(googleMap);
        renderCameras (googleMap);

        LatLng iceland = new LatLng(64.9312762, -19.0211697);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iceland, 5.5f));

        showMyLocation(googleMap);

    } //END OF  public void onMapReady(GoogleMap googleMap)


    public ArrayList<HashMap<String, String>> filterMap(ArrayList<HashMap<String, String>> array, String array_filter) {

        ArrayList<HashMap<String, String>> camera_feed_results = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            HashMap<String, String> camera_feed = getDataPoint(i, array);
            String data_name = camera_feed.get("data_name");
            String data_url = camera_feed.get("data_url");
            String data_lat = camera_feed.get("data_lat");
            String data_long = camera_feed.get("data_long");
            String data_category = camera_feed.get("data_category");

            if (!array_filter.equals("ALL")) {
                if (data_category.equalsIgnoreCase(array_filter)) {
                    HashMap<String, String> filtered_category = new HashMap<>();
                    filtered_category.put("data_name", data_name);
                    filtered_category.put("data_url", data_url);
                    filtered_category.put("data_lat", data_lat);
                    filtered_category.put("data_long", data_long);
                    camera_feed.put("data_category", data_category);
                    camera_feed_results.add(camera_feed);
                }
            }
            else {
                HashMap<String, String> filtered_category = new HashMap<>();
                filtered_category.put("data_name", data_name);
                filtered_category.put("data_url", data_url);
                filtered_category.put("data_lat", data_lat);
                filtered_category.put("data_long", data_long);
                camera_feed.put("data_category", data_category);
                camera_feed_results.add(camera_feed);
            }

        }

        return camera_feed_results;
    }

    public void clearMap(GoogleMap  googleMap) {
        googleMap.clear();

    }

    public void renderCameras (GoogleMap googleMap)
    {
        //Log.d(filter_choice, "filterchoice: ");
        //Log.d(String.valueOf(textViewfilterChoice), "textview");
        ArrayList<HashMap<String, String>> filteredList = filterMap(webcamList, filter_choice);
        // LANDMARK  HARBOR  ROAD  TOWN  MOUNTAIN ....

        for(int i=0; i < filteredList.size(); i++){
            HashMap<String, String> camera_feed = getDataPoint(i, filteredList);
            String data_name = camera_feed.get("data_name");
            String data_url = camera_feed.get("data_url");
            String data_lat = camera_feed.get("data_lat");
            String data_long = camera_feed.get("data_long");
            String data_category = camera_feed.get("data_category");


            double i_lat = Double.valueOf(data_lat);
            double i_long = Double.valueOf(data_long);

            LatLng i_position = new LatLng(i_lat, i_long);
            Marker markerX = googleMap.addMarker(new MarkerOptions().position(i_position).title(data_name).snippet(data_category.toLowerCase()));
            markerX.setTag(data_url);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //String placeID = mMarkerMap.get(marker.getId());
                    //String placeName = marker.getTitle();
                    Intent intent = new Intent(getApplicationContext(), WebView_camera.class);
                    intent.putExtra("data_url", (String) marker.getTag());
                    intent.putExtra("data_name", (String) marker.getTitle());
                    //intent.putExtra(PLACE_ID, placeID);
                    startActivity(intent);
                    return false;
                }
            });

        }
    }
    public void showMyLocation (GoogleMap mMap)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            //return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void applyTexts(String spinner) {
        //filter_choice = spinner;
        //textViewfilterChoice.setText(spinner);
    }

}