package com.android.jjnunogarcia.SimpleTripPlanner.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.android.jjnunogarcia.SimpleTripPlanner.adapters.LocationAdapter;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.requests.RemoteLocationParsingAsyncTask;
import com.example.SimpleTripPlanner.R;

import java.util.ArrayList;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class PlannerFragment extends Fragment {
  public static final  String TAG                                 = PlannerFragment.class.getSimpleName();
  private static final int    MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS = 3;

  private EditText             originEditText;
  private EditText             destinationEditText;
  private AutoCompleteTextView autocomplete;
  private CalendarView         calendarView;
  private Button               searchButton;
  private LocationAdapter      autocompleteAdapter;
  private OnClickListener searchButtonClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.search_button_message), Toast.LENGTH_SHORT).show();
    }
  };
  private TextWatcher     originFieldTextWatcher    = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() >= MINIMUM_TEXT_LENGHT_FOR_SUGGESTIONS) {
        // TODO call task
        // TODO maybe add some delay with text unchanged to call the task
        String url = String.format(getResources().getString(R.string.url), s.toString());
        new RemoteLocationParsingAsyncTask(url, autocompleteAdapter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
      }
    }
  };

  public PlannerFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.planner_fragment, container, false);
    originEditText = (EditText) view.findViewById(R.id.planner_origin_edittext);
    destinationEditText = (EditText) view.findViewById(R.id.planner_destination_edittext);
    autocomplete = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_country);
    searchButton = (Button) view.findViewById(R.id.planner_search_button);
//    calendarView = (CalendarView) view.findViewById(R.id.planner_calendar_view);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    autocompleteAdapter = new LocationAdapter(getActivity().getApplicationContext(), R.layout.location_suggestion_row, new ArrayList<Location>());
    autocomplete.setAdapter(autocompleteAdapter);
    searchButton.setOnClickListener(searchButtonClickListener);
    autocomplete.addTextChangedListener(originFieldTextWatcher);
  }
}
