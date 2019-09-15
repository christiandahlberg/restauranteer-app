package chdah.umu.restaurantguide.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.controller.Factory;
import chdah.umu.restaurantguide.model.Restaurant;

/**
 * MapsActivity that includes Google Maps API v2.0 to be able to show
 * the Google Maps input. Used to show all locations of the items that
 * user puts in (will show marker on the map if location is given).
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private HashMap<String, Restaurant> markers;
    public static final int REQUEST_LOCATION_PERMISSION = 99;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(MapsActivity.this, DashboardActivity.class));
                    finish();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Setup necessary elements in the view.
        setMenuHighlighting(navView);
        setupActionBar();
    }

    /**
     * Method for settings up the action bar.
     * This adds a logo on the top left part of
     * the action bar.
     */
    public void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.logo);
        }
    }

    /**
     * Method that gets called when coming back from another
     * activity which made it hidden in the first place.
     *
     * Null-check prevents the application from crashing
     * if the map hasn't started yet (doesn't exist).
     */
    @Override
    protected void onResume(){
        super.onResume();

        if(googleMap != null){
            googleMap.clear();
            loadAllPins();
        }
    }

    /**
     * Sets correct text highlighting to actual activity.
     * @param navView the navbar
     */
    public void setMenuHighlighting(BottomNavigationView navView) {
        navView.getMenu().getItem(2).setChecked(true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param gMap representing Google Map Object
     */
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        loadAllPins();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Restaurant restaurant = markers.get(marker.getId());

                Intent intent = new Intent(MapsActivity.this, RestaurantActivity.class);
                intent.putExtra("EXTRA_KEY", restaurant.getObjectKey());
                startActivity(intent);
            }
        });

    }

    /**
     * Itemlistener for when the user presses buttons in
     * the action bar. If the user presses the "add"-button,
     * it will start a new activity, making it possible for
     * the user to add a restaurant.
     * @param item refers to an item in the action bar.
     * @return returns true since there are only one item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(MapsActivity.this, ConfigurationActivity.class);
            startActivityForResult(intent, 0);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 'This will load all the markers/pins on the map
     * depending on the item locations when initiated.
     *
     * Will ues both a list of Restaurant items and a
     * list of LatLng-objects to maintain proper locations.
     */
    private void loadAllPins(){
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        ArrayList<LatLng> restaurantLocations = new ArrayList<>();
        markers = new HashMap<>();

        for(Restaurant r : Factory.getRestaurants()) {
            if (r.getCoordinatesLatitude() != null && r.getCoordinatesLatitude() != null) {
                restaurantLocations.add(
                        new LatLng(r.getCoordinatesLatitude(), r.getCoordinatesLongitude())
                );

                restaurants.add(r);
            }
        }

        // Zoom levels: (0-5-10-15-...)
        float zoom = 10;

        if (restaurants.size() > 0) {
            // Handle zoom when opening map depending on zoom-variable
            CameraUpdateFactory.newLatLngZoom(restaurantLocations.get(0), zoom);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocations.get(0), zoom));

            int index = 0;
            for(LatLng location : restaurantLocations) {
                markers.put(googleMap.addMarker(
                        new MarkerOptions().position(location).title(
                                restaurants.get(index).getName()
                        )
                ).getId(), restaurants.get(index));
                index++;
            }
        }
    }

    /**
     * Request permission for device/user location during runtime.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * This method implements the top action bar menu,
     * including the "Add"-button, excluding the "Edit"-,
     * and "Share"-buttons (to be present in other views).
     * @param menu the top action bar
     * @return returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_options, menu);

        // Exclude 'Edit' and 'Share' buttons
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);

        return true;
    }

    /**
     * This method will check if the location permission is granted,
     * and if so enable the location data layer to be used and therefore
     * showing the current location.
     *
     * In an Emulator, this will show the built in coordinates, but will
     * work as it should (taking the device in questions' coordinates)
     * on a mobile phone.
     *
     * @param requestCode The actual data which handle the output.
     * @param permissions Permission results
     * @param grantResults Granted results.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    /**
     * Method that starts when there are an incoming result
     * to this activity. For example when adding a new
     * restaurant.
     * @param requestCode code to check against values.
     * @param resultCode code to check against values.
     * @param data what specific intent (includes all data)
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                String returnType = data.getStringExtra("EXTRA_RETURN_TYPE");
                String title = data.getStringExtra("EXTRA_TITLE");

                if (returnType.equals("save")) {
                    Toast.makeText(this, title + " added.", Toast.LENGTH_LONG).show();
                } else if (returnType.equals("delete")) {
                    Toast.makeText(this, title + " deleted.", Toast.LENGTH_LONG).show();
                }

                loadAllPins();
            }
        }
    }

}