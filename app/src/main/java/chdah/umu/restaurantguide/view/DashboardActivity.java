package chdah.umu.restaurantguide.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.controller.Adapter;
import chdah.umu.restaurantguide.controller.Factory;
import chdah.umu.restaurantguide.model.Restaurant;

/**
 * This class resembles the Instagram-like dashboard where
 * all the posts will be stored by the user when adding new
 * ones.
 */
public class DashboardActivity extends AppCompatActivity {

    // View elements
    private ListView restaurantListView;
    private TextView emptyText;

    // Static fields
    private static final int REQUEST_CODE = 0;
    private static Factory factory;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.navigation_overview:
                    startActivity(new Intent(DashboardActivity.this, MapsActivity.class));
                    finish();
                    break;
            }
            return true;
        }
    };

    /**
     * Method that gets executed when the activity is run,
     * in my cause through startActivity(intent). Will
     * execute all the code below before any methods can
     * use objects or elements in the view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setMenuHighlighting(navView);

        // Assignments
        emptyText = findViewById(R.id.empty_list_text);
        factory = new Factory(getApplicationContext());
        restaurantListView = findViewById(R.id.restaurant_view);

        // Configurations
        setupActionBar();
        setupCorrectInterface();

        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Restaurant> restaurants = factory.getRestaurants();
                Collections.reverse(restaurants);
                Restaurant r = restaurants.get(position);

                Intent intent = new Intent(DashboardActivity.this, RestaurantActivity.class);
                intent.putExtra("EXTRA_KEY", r.getObjectKey());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
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
     * This method gets called in onCreate to set up correct
     * view. If there are no restaurant posts, the list will
     * get 'disabled' (View.GONE) and a text message will
     * appear, telling the user that there are no current
     * items in the dashboard, and refer them to add new ones.
     */
    public void setupCorrectInterface() {
        if (Factory.getRestaurants().size() > 0) {
            restaurantListView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        } else {
            restaurantListView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.no_restaurants);
        }
    }

    /**
     * Method to set the appropriate text highlighting on
     * the navigation bar, depending on what activity is
     * active.
     * @param navView the bottom navigation bar.
     */
    public void setMenuHighlighting(BottomNavigationView navView) {
        navView.getMenu().getItem(1).setChecked(true);
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
            Intent intent = new Intent(DashboardActivity.this, ConfigurationActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Whenever this starts, the list gets updated.
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateList();
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

                updateList();
                setupCorrectInterface();
            }
        }
    }

    /**
     * Used whenever an item (Restaurant) gets updated or added.
     */
    private void updateList() {
        ArrayList<Restaurant> restaurants = factory.getRestaurants();
        Collections.reverse(restaurants);

        Adapter restaurantAdapter = new Adapter(this, restaurants);
        restaurantListView.setAdapter(restaurantAdapter);
    }
}