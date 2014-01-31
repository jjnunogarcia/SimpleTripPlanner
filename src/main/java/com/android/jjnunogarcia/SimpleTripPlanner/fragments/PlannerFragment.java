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
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.android.jjnunogarcia.SimpleTripPlanner.PlannerActivity;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.adapters.LocationAdapter;
import com.android.jjnunogarcia.SimpleTripPlanner.helpers.GpsTracker;
import com.android.jjnunogarcia.SimpleTripPlanner.helpers.GpsTracker.OnLocationChangeListener;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.model.SortOrder;
import com.android.jjnunogarcia.SimpleTripPlanner.requests.RemoteLocationParsingAsyncTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class PlannerFragment extends SherlockFragment implements OnClickListener, TextWatcher, OnLocationChangeListener {
  public static final  String TAG                                 = PlannerFragment.class.getSimpleName();
  private static final int    MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS = 3;

  private AutoCompleteTextView originEditText;
  private AutoCompleteTextView destinationEditText;
  private Button               searchButton;
  private EditText             selectedDateTextView;
  private LocationAdapter      autocompleteAdapter;
  private SortOrder            sortOrder;
  private OnItemClickListener originItemClickListener      = new OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Location location = (Location) parent.getAdapter().getItem(position);
      originEditText.setText(location.getName());
    }
  };
  private OnItemClickListener destinationItemClickListener = new OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Location location = (Location) parent.getAdapter().getItem(position);
      destinationEditText.setText(location.getName());
    }
  };

  public PlannerFragment() {
    sortOrder = SortOrder.DISTANCE;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.planner_fragment, container, false);
    originEditText = (AutoCompleteTextView) view.findViewById(R.id.planner_origin_edittext);
    destinationEditText = (AutoCompleteTextView) view.findViewById(R.id.planner_destination_edittext);
    searchButton = (Button) view.findViewById(R.id.planner_search_button);
    selectedDateTextView = (EditText) view.findViewById(R.id.planner_date_selected);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    GpsTracker gpsTracker = new GpsTracker(getActivity().getApplicationContext());
    gpsTracker.setLocationChangeListener(this);
    autocompleteAdapter = new LocationAdapter(getActivity().getApplicationContext(), new ArrayList<Location>(), gpsTracker, sortOrder);
    originEditText.setAdapter(autocompleteAdapter);
    originEditText.addTextChangedListener(this);
    originEditText.setOnClickListener(this);
    originEditText.setOnItemClickListener(originItemClickListener);
    destinationEditText.setAdapter(autocompleteAdapter);
    destinationEditText.addTextChangedListener(this);
    destinationEditText.setOnClickListener(this);
    destinationEditText.setOnItemClickListener(destinationItemClickListener);
    selectedDateTextView.setOnClickListener(this);
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    selectedDateTextView.setText(sdf.format(calendar.getTime()));
    searchButton.setOnClickListener(this);
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
    if (v.getId() == R.id.planner_search_button) {
      Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.search_button_message), Toast.LENGTH_SHORT).show();
    } else if (v.getId() == R.id.planner_date_selected) {
      String[] dateElements = selectedDateTextView.getText().toString().split("\\.");
      ((PlannerActivity) getActivity()).showDateDialogFragment(Integer.valueOf(dateElements[0]), Integer.valueOf(dateElements[1]), Integer.valueOf(dateElements[2]));
    } else if (v.getId() == R.id.planner_origin_edittext) {
      if (originEditText.getText().length() >= MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS) {
        originEditText.setText(originEditText.getText());
        originEditText.setSelection(originEditText.getText().length());
      }
    } else if (v.getId() == R.id.planner_destination_edittext) {
      if (destinationEditText.getText().length() >= MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS) {
        destinationEditText.setText(destinationEditText.getText());
        destinationEditText.setSelection(destinationEditText.getText().length());
      }
    }
  }

  @Override
  public void onLocationChanged(android.location.Location location) {
    autocompleteAdapter.notifyDataSetChanged();
  }

  public void setSortOrder(SortOrder sortOrder) {
    this.sortOrder = sortOrder;
    autocompleteAdapter.setSortOrder(sortOrder);
    autocompleteAdapter.notifyDataSetChanged();
  }

  public void setDateText(String dateSelected) {
    selectedDateTextView.setText(dateSelected);
  }
}
