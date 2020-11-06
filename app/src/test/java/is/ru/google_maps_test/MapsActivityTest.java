package is.ru.google_maps_test;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.Assert.*;

public class MapsActivityTest {
    private Object EqualsBuilder;

    @Test
    public void checkJsonPoints(){
        ArrayList<HashMap<String, String>> resultsList = new ArrayList<>();
        MapsActivity activity = new MapsActivity();

        HashMap<String, String> compare_true = new HashMap<>();
        compare_true.put("data_name", "Tindastóll");
        compare_true.put("data_url", "https://skidi.gunnartr.net/skitindastoll.jpg");
        compare_true.put("data_lat", "65.8");
        compare_true.put("data_long", "-19.7166667");
        resultsList.add(compare_true);

        HashMap<String, String> compare_false = new HashMap<>();
        compare_false.put("data_name", "Hvammstangi");
        compare_false.put("data_lat", "65.3999984");
        compare_false.put("data_long", "-20.9499962");

        HashMap<String, String> camera_feed = activity.getDataPoint(0, resultsList);

        assertEquals(compare_true, camera_feed);
        assertNotEquals(compare_false, camera_feed);
    }

    // TODO: Make a check that looks at the type of camera feed (photo/video) and gives a pass if correct icon

    // TODO: Make a check if filtering works

    //TODO: GPS points in correct lacation -> lat and long


    // Filtering on for example "MOUNTAIN" does  "lat": 64.250486,  and "long": -15.191914  exist on map (ANSWER should be yes)
    ////  -> then change filter to "HARBOR" does the same points still exists (SHOULD NOT exist)
    @Test
    public void checkFiltering(){
        MapsActivity activity = new MapsActivity();
        ArrayList<HashMap<String, String>> webcamList = new ArrayList<>();
        
        // Create three different places
        HashMap<String, String> place_one = new HashMap<>();
        place_one.put("data_name", "Tindastóll");
        place_one.put("data_url", "https://skidi.gunnartr.net/skitindastoll.jpg");
        place_one.put("data_lat", "65.8");
        place_one.put("data_long", "-19.7166667");
        place_one.put("data_category", "MOUNTAIN");
        webcamList.add(place_one);

        HashMap<String, String> place_two = new HashMap<>();
        place_two.put("data_name", "Hveravellir");
        place_two.put("data_url", "http://193.4.144.154/cgi-bin/viewer/video.jpg?resolution=1400x1000&t=0.25348094508700614");
        place_two.put("data_lat", "64.819");
        place_two.put("data_long", "-19.672");
        place_two.put("data_category", "LANDMARK");
        webcamList.add(place_two);

        HashMap<String, String> place_three = new HashMap<>();
        place_three.put("data_name", "Hornafjörður");
        place_three.put("data_url", "http://213.167.138.133:3001/axis-cgi/jpg/image.cgi?rand=0.5156160063055386");
        place_three.put("data_lat", "64.250486");
        place_three.put("data_long", "-15.191914");
        place_three.put("data_category", "MOUNTAIN");
        webcamList.add(place_three);

        String filter_choice = "MOUNTAIN";
        ArrayList<HashMap<String, String>> filteredList = activity.filterMap(webcamList, filter_choice);

        int sum_of_filtered_mountains = 2;
        int count_actual_filtered_mountains = 0;
        boolean contains_lat_long = false;

        for(int i=0; i < filteredList.size(); i++){
            HashMap<String, String> camera_feed = activity.getDataPoint(i, filteredList);
            String data_name = camera_feed.get("data_name");
            String data_lat = camera_feed.get("data_lat");
            String data_long = camera_feed.get("data_long");
            //check if data point exists for filter = "MOUNTAIN"
            /// if category changed to "HARBOR" for example then this data point should not exists and the test fails
            if(data_name == "Hornafjörður" && data_lat == "64.250486" && data_long == "-15.191914"){
                contains_lat_long = true;
            }
            count_actual_filtered_mountains += 1;
        }
        
        assertEquals(contains_lat_long, true);
        assertEquals(sum_of_filtered_mountains, count_actual_filtered_mountains);


    }
}