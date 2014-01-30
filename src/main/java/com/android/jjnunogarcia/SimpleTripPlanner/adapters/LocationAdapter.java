package com.android.jjnunogarcia.SimpleTripPlanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.comparators.DistanceComparator;
import com.android.jjnunogarcia.SimpleTripPlanner.comparators.NameComparator;
import com.android.jjnunogarcia.SimpleTripPlanner.helpers.GpsTracker;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.model.SortOrder;
import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class LocationAdapter extends BaseAdapter implements Filterable {
  private final Context             context;
  private       ArrayList<Location> locations;
  private       GpsTracker          gpsTracker;
  private       SortOrder           sortOrder;
  private       ArrayList<Location> originalValues;
  private       LatLng              userLocation;
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

  public LocationAdapter(Context context, ArrayList<Location> locations, GpsTracker gpsTracker, SortOrder sortOrder) {
    this.context = context;
    this.locations = locations;
    this.gpsTracker = gpsTracker;
    this.sortOrder = sortOrder;
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

  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    if (gpsTracker.canGetLocation()) {
      userLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
      setNewDistance();

      if (sortOrder == SortOrder.NAME) {
        Collections.sort(locations, new NameComparator());
      } else {
        Collections.sort(locations, new DistanceComparator());
      }
    }
  }

  private void setNewDistance() {
    for (Location location : locations) {
      float[] results = new float[1];
      android.location.Location.distanceBetween(userLocation.latitude, userLocation.longitude, location.getGeoPosition().latitude, location.getGeoPosition().longitude, results);
      location.setDistanceToDestiny(roundNumber(results[0] / 1000, 2));
    }
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

    if (location.getDistanceToDestiny() != -1) {
      viewHolder.distance.setText(String.valueOf(location.getDistanceToDestiny()) + " Km");
    } else {
      viewHolder.distance.setText("");
    }


    return view;
  }

  private static float roundNumber(float number, int decimals) {
    return new BigDecimal(number).setScale(decimals, BigDecimal.ROUND_HALF_UP).floatValue();
  }

  @Override
  public Filter getFilter() {
    return nameFilter;
  }

  public void setSortOrder(SortOrder sortOrder) {
    this.sortOrder = sortOrder;
  }

  private static class ViewHolder {
    RelativeLayout rowLayout;
    TextView       name;
    TextView       distance;
  }
}
