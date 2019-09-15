package chdah.umu.restaurantguide.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.controller.Factory;
import chdah.umu.restaurantguide.model.Restaurant;

public class ConfigurationActivity extends AppCompatActivity {

    // Objects
    private Restaurant restaurant;

    // Numeric fields
    private int restaurantKey;
    private boolean isRestaurantPresent;

    // View elements
    private Button getLocationButton;
    private EditText editNameTextBox;
    private EditText editDescriptionTextBox;
    private ImageView placeholderImage;
    private TextView restaurantLocation;
    private RatingBar restaurantRating;

    // Other fields
    private Context c;
    private Uri imageURI;
    private LocationManager coordinateHandler;

    // Static field
    private static final int REQUEST_CODE = 100;


    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        c = this;

        if (savedInstanceState != null) {
            restaurantKey = savedInstanceState.getInt("STATE_Key");
        } else {
            Intent intent = getIntent();
            restaurantKey = intent.getIntExtra("EXTRA_KEY", 0);
        }

        // Setup necessary elements
        setupActionBar();
        prepareRestaurant();
        setupUI();

        // Rating listener
        restaurantRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = restaurantRating.getWidth();
                    float starsFloat = (touchPositionX / width) * 5.0f;
                    int stars = (int)starsFloat + 1;

                    restaurantRating.setRating(stars);

                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }

                return true;
            }
        });

        // Photo listener
        placeholderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photographListenerSetup();
            }
        });

        // Coordinates listener
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinationListenerSetup();
            }
        });
    }

    /**
     * Sets up the OnClickListener for the
     * 'Set Location'-button and formats it
     * accordingly.
     */
    public void coordinationListenerSetup() {
        coordinateHandler = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        Location location = getLastKnownLocation();
        if (location != null) {
            DecimalFormat df = new DecimalFormat("###.000");

            // Get coordinates, format and pass to variables
            double newLat = Double.parseDouble(df.format(location.getLatitude()));
            double newLong = Double.parseDouble(df.format(location.getLongitude()));

            // Give object formatted coordinates
            restaurant.setCoordinatesLatitude(newLat);
            restaurant.setCoordinatesLongitude(newLong);
            restaurantLocation.setText(newLat + ", " + newLong);
        }
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

    /**
     * Determines what kind of object the restaurant
     * object will be. Either it's a new one, or a
     * already valid one.
     */
    public void prepareRestaurant() {
        if (restaurantKey == 0) {
            restaurantKey = Factory.getRestaurants().size() + 1;
            this.restaurant = new Restaurant(restaurantKey);
            isRestaurantPresent = true;
        } else {
            this.restaurant = Factory.getRestaurantByKey(restaurantKey);
            isRestaurantPresent = false;

            if (restaurant == null) {
                restaurantKey = Factory.getRestaurants().size() + 1;
                this.restaurant = new Restaurant(restaurantKey);
                isRestaurantPresent = true;
            }
        }
    }

    /**
     * This method sets up the user inteface (equal to
     * the rest of the setupUI-methods).
     *
     * Will be different depending on if the object
     * is a new object or a already instantiated one.
     */
    private void setupUI(){
        editNameTextBox = findViewById(R.id.title_edit_text);
        editDescriptionTextBox = findViewById(R.id.desc_edit_text);
        placeholderImage = findViewById(R.id.photo_image_view);
        restaurantLocation = findViewById(R.id.location_textView);
        getLocationButton = findViewById(R.id.location_button);
        restaurantLocation = findViewById(R.id.location_textView);
        restaurantRating = findViewById(R.id.edit_rating);

        if (!isRestaurantPresent) {
            placeholderImage.setTag("not placeholder");
            placeholderImage.setImageURI(restaurant.getRestaurantPhotoURI());
            editNameTextBox.setText(restaurant.getName());
            editDescriptionTextBox.setText(restaurant.getRestaurantDescription());
            restaurantRating.setRating(restaurant.getRestaurantRating());
            restaurantLocation.setText(restaurant.getCoordinatesLatitude() + ", " + restaurant.getCoordinatesLongitude());
        }
    }

    /**
     * Creates the action bar menu, including the edit
     * possibility and removin the removal-option for
     * restaurant-views not being saved yet.
     * @param menu the action bar item menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_edit, menu);
        if (isRestaurantPresent) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String returnType = "";
        String tempTitle = "";

        if (item.getItemId() == R.id.action_save) {
            if (editNameTextBox.getText().toString().isEmpty() || placeholderImage.getTag().equals("placeholder")) {
                Toast.makeText(c, "Please fill in the fields above before saving.", Toast.LENGTH_LONG).show();
                return false;
            }
            restaurant.setRestaurantName(editNameTextBox.getText().toString());
            restaurant.setRestaurantDescription(editDescriptionTextBox.getText().toString());
            restaurant.setRestaurantRating(Math.round(restaurantRating.getRating()));
            tempTitle = editNameTextBox.getText().toString();
            returnType = "save";
            Factory.addRestaurant(restaurant, restaurantKey);
        } else if (item.getItemId() == R.id.action_delete) {
            tempTitle = Factory.getRestaurantByKey(restaurantKey).getName();
            returnType = "delete";
            Factory.deleteRestaurant(restaurantKey);
        }

        Factory.saveToInternal();

        Intent intent = new Intent();
        intent.putExtra("EXTRA_TITLE", tempTitle);
        intent.putExtra("EXTRA_RETURN_TYPE", returnType);
        setResult(RESULT_OK, intent);
        finish();

        return true;
    }

    /**
     * This uses an intent to start an activity to take
     * a photograph. It also uses a runtime permission
     * that checks if the user allows camera usage.
     * Start Activity to Take Photo
     */
    private void photographListenerSetup() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ConfigurationActivity.this, new String[] {
                        Manifest.permission.CAMERA}, REQUEST_CODE);
            }
            imageURI = FileProvider.getUriForFile(
                    ConfigurationActivity.this,
                    "chdah.umu.restaurantguide.provider",
                    getOutputMediaFile());

            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            startActivityForResult(pictureIntent, REQUEST_CODE);
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                placeholderImage.setImageURI(imageURI);
                placeholderImage.setTag("not placeholder");
                restaurant.setRestaurantPhotoURI(imageURI);
            }
        }
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    /**
     * This method is used to finding the devices last known
     * location through a LocationManager. Will prompt user
     * for permission if needed.
     * @return returns a valid location that will be used in maps.
     */
    private Location getLastKnownLocation() {

        // If user didn't allow it when opening the application, it will prompt user again.
        if ( ContextCompat.checkSelfPermission( ConfigurationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(ConfigurationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        coordinateHandler = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> provs = coordinateHandler.getProviders(true);
        Location mostAccurateLocation = null;
        for (String provider : provs) {
            Location l = coordinateHandler.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (mostAccurateLocation == null || l.getAccuracy() < mostAccurateLocation.getAccuracy()) {
                mostAccurateLocation = l;
            }
        }
        return mostAccurateLocation;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("STATE_KEY", this.restaurantKey);
    }

}