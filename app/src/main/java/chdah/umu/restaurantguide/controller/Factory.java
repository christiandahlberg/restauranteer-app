package chdah.umu.restaurantguide.controller;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import chdah.umu.restaurantguide.view.MainActivity;
import chdah.umu.restaurantguide.model.Restaurant;

public class Factory {

    private static HashMap<Integer, Restaurant> restaurantMap;
    private static Context c;

    /**
     * Factory constructor, implementing the Hashmap and
     * context from argument. Adding every restaurant entry
     * from the loadCachedEntries() function.
     * @param context Current context to use while
     *                saving to internal.
     */
    public Factory(Context context){
        // Ignoring SparseArray tip?
        restaurantMap = new HashMap<>();
        c = context;

        ArrayList<Restaurant> cachedEntries = loadCachedEntries();
        if (cachedEntries != null) {
            for (Restaurant r: cachedEntries) {
                addRestaurant(r, r.getObjectKey());
            }
        }
    }

    /**
     * Used when adding a new Restaurant to the HashMap.
     * @param restaurant Restaurant.class object
     * @param key Integer.class identifying the Restaurant.
     */
    public static void addRestaurant(Restaurant restaurant, int key) {
        restaurantMap.put(key, restaurant);
    }

    /**
     * Used when removing a restaurant. Also updating
     * the HashMap containing keys and values of all
     * restaurants to keep it up to date (see resetKeys()).
     * @param key Restaurant item key in HashMap.
     */
    public static void deleteRestaurant(int key) {
        restaurantMap.remove(key);
        resetKeys();
    }

    /**
     * This method is called whenever a restaurant item is
     * deleted. If this was excluded, the key incrementing
     * would keep on rolling even if keys were deleted;
     * which would result in a general mess.
     *
     * Used to always maintain correct keys for each item
     * currently in storage.
     */
    private static void resetKeys() {
        ArrayList<Restaurant> restaurants = getRestaurants();
        restaurantMap = new HashMap<>();

        int index = 1;
        for(Restaurant restaurant : restaurants) {
            restaurant.setObjectKey(index);
            restaurantMap.put(index, restaurant);
            index++;
        }
    }

    /**
     * Retrieves a specific restaurant from the map
     * ('restaurants'-variable) key (instead of value).
     * @param key = the specific restaurant key.
     * @return = returns a Restaurant item.
     */
    public static Restaurant getRestaurantByKey(int key) {
        return restaurantMap.get(key);
    }

    @SuppressWarnings("unchecked")
    /**
     * This method retrieves the list containing all the items
     * from the internal storage. If there are any input/output
     * error or a ClassNotFoundException, the try/catch will
     * catch it.
     */
    public ArrayList<Restaurant> loadCachedEntries() {
        ArrayList<Restaurant> cachedEntries = null;

        try {
            Object obj = Storage.readObject(c, MainActivity.STORAGE_KEY);
            if (obj instanceof ArrayList) {
                cachedEntries = (ArrayList<Restaurant>) obj;
            }
        } catch(ClassNotFoundException e) {
            Log.d("TAG_INTERNAL_STORAGE", "Exception: ClassNotFoundException");
            if (e.getMessage() != null) {
                Log.d("ERROR_INTERNAL_STORAGE", e.getMessage());
            }
        } catch (IOException e) {
            Log.d("TAG_INTERNAL_STORAGE", "Exception: IOException");
            if (e.getMessage() != null) {
                Log.d("ERROR_INTERNAL_STORAGE", e.getMessage());
            }
        }

        return cachedEntries;
    }

    /**
     * This method saves the entries to internal memory
     * while simultaneously caching every entry to later
     * instances. Will print out messages if try/catch
     * catches any input/output exceptions.
     */
    public static void saveToInternal() {
        try {
            Storage.writeObject(c, MainActivity.STORAGE_KEY, getRestaurants());
        } catch (IOException e) {
            Log.d("TAG_INTERNAL_STORAGE", "Exception: IOException");
            if (e.getMessage() != null) {
                Log.d("ERROR_INTERNAL_STORAGE", e.getMessage());
            }
        }
    }

    /**
     * This method retrieves a list of all Restaurant
     * items from the map ('restaurants'-variable)
     * values.
     * @return Returns an ArrayList containing every Restaurant item.
     */
    public static ArrayList<Restaurant> getRestaurants(){
        return new ArrayList<>(restaurantMap.values());
    }
}
