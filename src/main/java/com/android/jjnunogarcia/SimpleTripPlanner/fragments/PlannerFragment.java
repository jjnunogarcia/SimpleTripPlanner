package com.android.jjnunogarcia.SimpleTripPlanner.fragments;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.adapters.LocationAdapter;
import com.android.jjnunogarcia.SimpleTripPlanner.helpers.GpsTracker;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.requests.RemoteLocationParsingAsyncTask;

import java.util.ArrayList;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class PlannerFragment extends SherlockFragment implements OnClickListener, TextWatcher {
  public static final  String TAG                                 = PlannerFragment.class.getSimpleName();
  private static final int    MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS = 3;

  private AutoCompleteTextView originEditText;
  private AutoCompleteTextView destinationEditText;
  private CalendarView         calendarView;
  private Button               searchButton;
  private LocationAdapter      autocompleteAdapter;
  private GpsTracker           gpsTracker;
  private AdapterView.OnItemClickListener originItemClickListener      = new AdapterView.OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Location location = (Location) parent.getAdapter().getItem(position);
      originEditText.setText(location.getName());
    }
  };
  private AdapterView.OnItemClickListener destinationItemClickListener = new AdapterView.OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Location location = (Location) parent.getAdapter().getItem(position);
      destinationEditText.setText(location.getName());
    }
  };

  public PlannerFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.planner_fragment, container, false);
    originEditText = (AutoCompleteTextView) view.findViewById(R.id.planner_origin_edittext);
    destinationEditText = (AutoCompleteTextView) view.findViewById(R.id.planner_destination_edittext);
    searchButton = (Button) view.findViewById(R.id.planner_search_button);
    calendarView = (CalendarView) view.findViewById(R.id.planner_calendar_view);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    gpsTracker = new GpsTracker(getActivity().getApplicationContext());
    autocompleteAdapter = new LocationAdapter(getActivity().getApplicationContext(), new ArrayList<Location>(), gpsTracker);
    originEditText.setAdapter(autocompleteAdapter);
    destinationEditText.setAdapter(autocompleteAdapter);
    originEditText.addTextChangedListener(this);
    destinationEditText.addTextChangedListener(this);
    searchButton.setOnClickListener(this);
    originEditText.setOnItemClickListener(originItemClickListener);
    destinationEditText.setOnItemClickListener(destinationItemClickListener);
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    autocompleteAdapter.getFilter().filter(s.toString());
  }

  @TargetApi(11)
  @Override
  public void afterTextChanged(Editable s) {
    if (s.length() >= MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS) {
      String url = String.format(getResources().getString(R.string.url), s.toString());

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        new RemoteLocationParsingAsyncTask(url, autocompleteAdapter).execute();
      } else {
        new RemoteLocationParsingAsyncTask(url, autocompleteAdapter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
      }
    }
  }

  @Override
  public void onClick(View v) {
    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.search_button_message), Toast.LENGTH_SHORT).show();
  }
}
