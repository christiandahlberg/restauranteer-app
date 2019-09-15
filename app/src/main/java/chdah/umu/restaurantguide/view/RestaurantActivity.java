package chdah.umu.restaurantguide.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.controller.Factory;
import chdah.umu.restaurantguide.model.Restaurant;

/**
 * This class resembles the actual activity for showing
 * a specific Restaurant. Uses the item_restaurant.xml
 * that defines the GUI for this activity.
 */
public class RestaurantActivity extends AppCompatActivity {

    // Objects
    private Restaurant restaurant;

    // Numeric fields
    private int restaurantKey;

    /**
     * Creation method that will be executed when
     * Activity starts. Preventing methods from
     * being called before this method finishes.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        setupActionBar();

        this.restaurantKey = getIntent().getIntExtra("EXTRA_KEY", 0);
        this.restaurant = Factory.getRestaurantByKey(restaurantKey);

        setupUI();
    }

    /**
     * Implements the action bar, which will include
     * the menu bar holding 'Edit'- and 'Share'-buttons,
     * excluding the 'Add' button.
     * @param menu the action bar menu
     * @return always returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_options, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return true;
    }

    /**
     * The itemSelectedListener for the Action bar;
     * will finish() current activity if the user presses
     * the cross in top left corner; will send user to 'Edit'-mode
     * if the user presses 'Edit'-button; will allow uer to share
     * current item to social media if user presses 'Share'-button.
     * @param item the selected item from action bar
     * @return will always return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_edit:
                // User chose the "Edit" item, show the app edit UI...
                Intent intent = new Intent(RestaurantActivity.this, ConfigurationActivity.class);
                intent.putExtra("EXTRA_KEY", restaurantKey);
                startActivityForResult(intent, 0);
                return true;

            case R.id.action_share:
                // User chose the "Share" action, making item possible of sharing...
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey, check out this amazing restaurant! You will love it!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Method that starts when there are an incoming result
     * to this activity. For example when starting an intent.
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

                if (returnType.equals("delete")) {
                    Intent intent = new Intent();
                    intent.putExtra("EXTRA_TITLE", title);
                    intent.putExtra("EXTRA_RETURN_TYPE", returnType);
                    setResult(RESULT_OK, intent);
                    finish();

                } else if (returnType.equals("save")) {
                    Toast.makeText(this, title + " has been successfully saved.", Toast.LENGTH_LONG).show();
                    setupUI();
                } else {
                    Toast.makeText(this, "No changes.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////// CUSTOM FUNCTIONS ////////////////////////
    /////////////////////////////////////////////////////////////////

    /**
     * Sets up the user interface with correct textviews,
     * imageviews and locations on the specific active item.
     */
    private void setupUI(){
        TextView titleView = findViewById(R.id.title_text_view);
        TextView descView = findViewById(R.id.desc_text_view);
        TextView location = findViewById(R.id.location_text);
        ImageView photoView = findViewById(R.id.photo_image_view);
        RatingBar rating = findViewById(R.id.restaurant_rating);

        rating.setRating(restaurant.getRestaurantRating());
        titleView.setText(restaurant.getName());
        descView.setText(restaurant.getRestaurantDescription());
        if (restaurant.getCoordinatesLatitude() != null || restaurant.getCoordinatesLongitude() != null) {
            location.setText(restaurant.getCoordinatesLatitude() + ", " + restaurant.getCoordinatesLongitude());
        } else {
            location.setText(R.string.restaurant_location);
        }
        photoView.setImageURI(restaurant.getRestaurantPhotoURI());
    }

    /**
     * Sets up the activity Action Bar, which will
     * put a cross in top left corner to be able to
     * finish() current activity. See onOptionsItemSelected().
     */
    public void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }
}
