package com.android.jjnunogarcia.SimpleTripPlanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;

import java.util.ArrayList;

/**
 * Date: 29.01.14
 *
 * @author nuno@neofonie.de
 */
public class LocationAdapter extends ArrayAdapter<Location> {
  private final Context             context;
  private       ArrayList<Location> locations;

  public LocationAdapter(Context context, int resource, ArrayList<Location> locations) {
    super(context, resource);
    this.context = context;
    clear();
    addAll(locations);
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
      view.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) view.getTag();
    }

    Location location = this.locations.get(position);
    viewHolder.name.setText(location.getName());


    return view;
  }

  // TODO Create more than one filter, i. e., name or proximity
//  @Override
//  public Filter getFilter() {
//    return nameFilter;
//  }
//
//  Filter nameFilter = new Filter() {
//    public String convertResultToString(Object resultValue) {
//      String str = ((Customer)(resultValue)).getName();
//      return str;
//    }
//    @Override
//    protected FilterResults performFiltering(CharSequence constraint) {
//      if(constraint != null) {
//        suggestions.clear();
//        for (Customer customer : itemsAll) {
//          if(customer.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
//            suggestions.add(customer);
//          }
//        }
//        FilterResults filterResults = new FilterResults();
//        filterResults.values = suggestions;
//        filterResults.count = suggestions.size();
//        return filterResults;
//      } else {
//        return new FilterResults();
//      }
//    }
//    @Override
//    protected void publishResults(CharSequence constraint, FilterResults results) {
//      ArrayList<Customer> filteredList = (ArrayList<Customer>) results.values;
//      if(results != null && results.count > 0) {
//        clear();
//        for (Customer c : filteredList) {
//          add(c);
//        }
//        notifyDataSetChanged();
//      }
//    }
//  };

  private static class ViewHolder {
    RelativeLayout rowLayout;
    TextView       name;
  }
}
