package chdah.umu.restaurantguide.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * The basic model class for Restaurant. Implemenets
 * java.io.Serializable to be able to read from a file
 * and then deserialized to recreate the object from
 * memory.
 */
public class Restaurant implements Serializable {

    // Integer fields
    private int objectKey;

    // String fields
    private String restaurantName;
    private String restaurantDescription;
    private String restaurantPhotoURI;

    // Numeric fields
    private int restaurantRating;
    private Double coordinatesLat;
    private Double coordinatesLong;

    /**
     * Initiates a Restaurant object using
     * a unique key (identifier).
     * @param restaurantKey A specific key holding a value (restaurant).
     */
    public Restaurant(int restaurantKey) {
        this.setObjectKey(restaurantKey);
    }

    /**
     * Below are just usual set and get methods.
     * @return
     */
    public String getName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public void setRestaurantDescription(String description) {
        restaurantDescription = description;
    }

    public Uri getRestaurantPhotoURI() {
        return Uri.parse(restaurantPhotoURI);
    }

    public void setRestaurantPhotoURI(Uri photo) {
        this.restaurantPhotoURI = photo.toString();
    }

    public int getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(int restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public Double getCoordinatesLatitude() {
        return this.coordinatesLat;
    }

    public void setCoordinatesLatitude(Double lat) {
        this.coordinatesLat = lat;
    }

    public Double getCoordinatesLongitude() {
        return this.coordinatesLong;
    }

    public void setCoordinatesLongitude(Double lg) {
        this.coordinatesLong = lg;
    }

    public void setObjectKey(int k) {
        this.objectKey = k;
    }

    public int getObjectKey() {
        return this.objectKey;
    }

    /**
     * Override toString to save time.
     * @return Returning the name of the restaurant.
     */
    public String toString(){
        return this.restaurantName;
    }
}
