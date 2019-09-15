package chdah.umu.restaurantguide.view;

/**
 * The Restauranteer application provides users with the possibility of
 * snapping a picture of whatever restaurant they approach and then upload
 * this picture, along with a title and a description. The location of the
 * restaurant is also included, which gives the user a map with markers of
 * every restaurant the user has visited and decided to 'save.'
 *
 * See it as an Instagram for specific targets (ie restaurants).
 *
 * @author Christian Dahlberg
 * @version 1.0
 * @since 2019-08-22
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.controller.Factory;

/**
 * MainActivity resembles the first page the user sees when opening the
 * application. It includes a title, navigation- and actionbar with app-
 * functionality.
 */
public class MainActivity extends AppCompatActivity {
    // View elements
    public TextView counterTextView;

    // Static fields
    public static final String STORAGE_KEY = "chdah.umu.restaurantguide.storagekey";
    public static final int REQUEST_CODE = 1;
    public static Factory factory;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                    break;
                case R.id.navigation_overview:
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                    finish();
                    break;
            }
            return true;
        }
    };

    /**
     * Creation method that will be executed when
     * Activity starts. Preventing methods from
     * being called before this method finishes.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        counterTextView = findViewById(R.id.restaurant_counter);
        factory = new Factory(getApplicationContext());

        // Setup necessary elements in the view.
        setMenuHighlighting(navView);
        setupUI();
        setupActionBar();

        checkPermission(PERMISSIONS_STORAGE,REQUEST_CODE);
    }

    /**
     * This method checks the permission of both camera and location.
     *
     * @param permissions is a string array holding each permission.
     * @param requestCode is just a code to put statements against.
     */
    public void checkPermission(String[] permissions, int requestCode)
    {
        ArrayList<String> notGrantPermissions = new ArrayList<>();
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(
                    getBaseContext(),
                    s) != PackageManager.PERMISSION_GRANTED) {
                notGrantPermissions.add(s);
            }
        }

        if (notGrantPermissions.size() > 0) {
            String[] permissionsArray = new String[notGrantPermissions.size()];
            permissionsArray = notGrantPermissions.toArray(permissionsArray);

            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsArray,
                    requestCode);
        }
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
     * Adds the text showing the amount of current
     * restaurants in storage (that user puts in).
     */
    public void setupUI() {
        counterTextView.setText("Currently stored restaurants: " + factory.getRestaurants().size() + "");
    }

    /**
     * Method to set the appropriate text highlighting on
     * the navigation bar, depending on what activity is
     * active.
     * @param navView the bottom navigation bar.
     */
    public void setMenuHighlighting(BottomNavigationView navView) {
        Menu menu = navView.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);
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
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem share = menu.findItem(R.id.action_share);
        edit.setVisible(false);
        share.setVisible(false);

        return true;
    }

    /**
     * ItemListener for when the user presses buttons in
     * the action bar. If the user presses the "add"-button,
     * it will start a new activity, making it possible for
     * the user to add a restaurant.
     * @param item refers to an item in the action bar.
     * @return returns true since there are only one item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
            startActivityForResult(intent, 0);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When activity gets resumed, checks how many restaurants
     * there are in storage; writes text accordingly.
     */
    @Override
    public void onResume(){
        super.onResume();
        setupUI();
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

                setupUI();
            }
        }
    }


}
