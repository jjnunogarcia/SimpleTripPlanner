package com.android.jjnunogarcia.SimpleTripPlanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.helpers.GpsTracker;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class LocationAdapter extends BaseAdapter implements Filterable {
  private final Context             context;
  private       ArrayList<Location> locations;
  private       GpsTracker          gpsTracker;
  private       ArrayList<Location> originalValues;
  private Filter nameFilter = new Filter() {

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
      locations = (ArrayList<Location>) results.values; // has the filtered values
      notifyDataSetChanged();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
      ArrayList<Location> filteredArrList = new ArrayList<Location>();

      if (originalValues == null) {
        originalValues = new ArrayList<Location>(locations); // saves the original data in originalValues
      }

      if (constraint == null || constraint.length() == 0) {
        // set the Original result to return
        results.count = originalValues.size();
        results.values = originalValues;
      } else {
        constraint = constraint.toString().toLowerCase();
        for (Location originalValue : originalValues) {
          String data = originalValue.getName();
          if (data.toLowerCase().startsWith(constraint.toString())) {
            filteredArrList.add(originalValue);
          }
        }
        // set the Filtered result to return
        results.count = filteredArrList.size();
        results.values = filteredArrList;
      }
      return results;
    }
  };

  public LocationAdapter(Context context, ArrayList<Location> locations, GpsTracker gpsTracker) {
    this.context = context;
    this.locations = locations;
    this.gpsTracker = gpsTracker;
  }

  public void add(Location location) {
    locations.add(location);
    notifyDataSetChanged();
  }

  public void addIfNotExisting(Location location) {
    if (!locations.contains(location)) {
      locations.add(location);
    }
    notifyDataSetChanged();
  }

  public void clear() {
    locations.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return locations.size();
  }

  @Override
  public Object getItem(int position) {
    return locations.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    ViewHolder viewHolder;

    if (convertView == null) {
      view = LayoutInflater.from(context).inflate(R.layout.location_suggestion_row, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.rowLayout = (RelativeLayout) view.findViewById(R.id.location_suggestion_row_root_layout);
      viewHolder.name = (TextView) view.findViewById(R.id.location_suggestion_row_name);
      viewHolder.distance = (TextView) view.findViewById(R.id.location_suggestion_row_distance);
      view.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) view.getTag();
    }

    Location location = locations.get(position);
    viewHolder.name.setText(location.getName());

    if (gpsTracker.canGetLocation()) {
      float[] results = new float[1];
      android.location.Location.distanceBetween(gpsTracker.getLatitude(), gpsTracker.getLongitude(), location.getGeoPosition().latitude, location.getGeoPosition().longitude, results);
      BigDecimal distanceInKm = roundNumber(results[0] / 1000, 2);
      viewHolder.distance.setText(String.valueOf(distanceInKm) + " Km");
    } else {
      viewHolder.distance.setText("");
    }


    return view;
  }

  private static BigDecimal roundNumber(float number, int decimals) {
    return new BigDecimal(number).setScale(decimals, BigDecimal.ROUND_HALF_UP);
  }

  @Override
  public Filter getFilter() {
    return nameFilter;
  }

  private static class ViewHolder {
    RelativeLayout rowLayout;
    TextView       name;
    TextView       distance;
  }
}
