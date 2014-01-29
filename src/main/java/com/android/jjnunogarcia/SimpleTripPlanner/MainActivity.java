package com.android.jjnunogarcia.SimpleTripPlanner;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.jjnunogarcia.SimpleTripPlanner.fragments.PlannerFragment;
import com.example.SimpleTripPlanner.R;

public class MainActivity extends FragmentActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    addPlannerFragment();
  }

  public void addPlannerFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    PlannerFragment plannerFragment = new PlannerFragment();
    ft.replace(R.id.planner_frame, plannerFragment, PlannerFragment.TAG).commit();
  }
}
