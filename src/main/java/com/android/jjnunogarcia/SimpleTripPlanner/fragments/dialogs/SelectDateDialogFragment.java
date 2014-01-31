package com.android.jjnunogarcia.SimpleTripPlanner.fragments.dialogs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.jjnunogarcia.SimpleTripPlanner.R;
import com.android.jjnunogarcia.SimpleTripPlanner.interfaces.DateDialogInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Date: 31.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class SelectDateDialogFragment extends SherlockDialogFragment implements OnClickListener {
  public static final String TAG           = SelectDateDialogFragment.class.getSimpleName();
  public static final String KEY_OLD_DAY   = "old_day";
  public static final String KEY_OLD_MONTH = "old_month";
  public static final String KEY_OLD_YEAR  = "old_year";

  private CalendarView        calendarView;
  private DatePicker          datePicker;
  private DateDialogInterface dateDialogInterface;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.date_dialog, null);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      calendarView = (CalendarView) view.findViewById(R.id.planner_calendar_view);
    } else {
      datePicker = (DatePicker) view.findViewById(R.id.planner_date_picker);
    }

    return new AlertDialog.Builder(getActivity())
        .setView(view)
        .setPositiveButton(getResources().getString(R.string.set_date), this)
        .setNegativeButton(getResources().getString(R.string.cancel), this).create();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    dateDialogInterface = (DateDialogInterface) getActivity();
    Bundle arguments = getArguments();

    if (arguments != null) {
      int day = arguments.getInt(KEY_OLD_DAY, -1);
      int month = arguments.getInt(KEY_OLD_MONTH, -1) - 1; // January is 0
      int year = arguments.getInt(KEY_OLD_YEAR, -1);

      if (day != -1 && month != -1 && year != -1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day);
          calendarView.setDate(gregorianCalendar.getTimeInMillis());
        } else {
          datePicker.updateDate(year, month, day);
        }
      }
    }
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    if (which == DialogInterface.BUTTON_POSITIVE) {
      String dateSelected = "";

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        dateSelected = getCalendarViewDate();
      } else {
        dateSelected = getDatePickerDate();
      }

      if (dateDialogInterface != null) {
        dateDialogInterface.onSaveDateDialogClicked(dateSelected);
      }
    }

    dialog.dismiss();
  }

  @TargetApi(11)
  private String getCalendarViewDate() {
    return formatDate(calendarView.getDate());
  }

  private String getDatePickerDate() {
    int day = datePicker.getDayOfMonth();
    int month = datePicker.getMonth();
    int year = datePicker.getYear();

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day);

    return formatDate(calendar.getTime());
  }

  private String formatDate(Object date) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    return sdf.format(date);
  }
}