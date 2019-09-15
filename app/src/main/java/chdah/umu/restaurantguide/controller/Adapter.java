package chdah.umu.restaurantguide.controller;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import chdah.umu.restaurantguide.R;
import chdah.umu.restaurantguide.model.Restaurant;

/**
 * This class resembles a custom adapter that creates a list of all
 * the Restaurant items. Used when storing items to have a general
 * oversight of all the items user puts in.
 */
public class Adapter implements ListAdapter {

    // Lists
    private ArrayList<Restaurant> restaurants;

    // Other
    private Context c;

    public Adapter(Context context, ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Restaurant r = restaurants.get(position);

        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(c);
            convertView = layoutInflater.inflate(R.layout.item_restaurant, null);

            TextView restaurantItemTitle = convertView.findViewById(R.id.item_title);
            TextView restaurantDescription = convertView.findViewById(R.id.item_description);
            TextView restaurantLocation = convertView.findViewById(R.id.item_location);
            ImageView restaurantImage = convertView.findViewById(R.id.item_image);
            RatingBar restaurantRating = convertView.findViewById(R.id.item_rating);

            restaurantItemTitle.setText(r.getName());
            restaurantDescription.setText(r.getRestaurantDescription());
            restaurantImage.setImageURI(r.getRestaurantPhotoURI());
            restaurantRating.setRating(r.getRestaurantRating());

            if(r.getCoordinatesLatitude() != null && r.getCoordinatesLongitude() != null) {
                restaurantLocation.setText(r.getCoordinatesLatitude() + ", " + r.getCoordinatesLongitude());
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        if (restaurants.size() < 1) {
            return 1;
        }

        return restaurants.size();
    }

    /**
     * ListAdapter must-have implementation.
     * @return returns false
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}