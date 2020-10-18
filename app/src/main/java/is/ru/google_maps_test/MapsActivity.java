package is.ru.google_maps_test;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Map<String, String> mMarkerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot s : dataSnapshot.getChildren()){
            Venue venue = s.getValue(Venue.class);

            venueList.add(venue);
            for (int i = 0; i < venueList.size(); i++)
            {
                LatLng latLng = new LatLng(venue.venueLat,venue.venueLong);
                if (mMap != null) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng).title(venue.venueName));
                    //Added:
                    mMarkerMap.put(marker.getId(), venue.getVenueId());
                }
            }
        }
    }*/

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