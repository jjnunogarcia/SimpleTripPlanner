package com.android.jjnunogarcia.SimpleTripPlanner;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.jjnunogarcia.SimpleTripPlanner.fragments.PlannerFragment;
import com.android.jjnunogarcia.SimpleTripPlanner.model.SortOrder;

public class PlannerActivity extends SherlockFragmentActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.planner_activity);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.action_bar_menu, menu);
    addPlannerFragment();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.sort_locations_button_by_distance:
        setNewSortOrder(SortOrder.DISTANCE);
        break;
      case R.id.sort_locations_alphabetically:
        setNewSortOrder(SortOrder.NAME);
        break;
      default:
        break;
    }

    return true;
  }

  public void addPlannerFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    PlannerFragment plannerFragment = new PlannerFragment();
    ft.replace(R.id.planner_frame, plannerFragment, PlannerFragment.TAG).commit();
  }

  private void setNewSortOrder(SortOrder sortOrder) {
    PlannerFragment plannerFragment = (PlannerFragment) getSupportFragmentManager().findFragmentByTag(PlannerFragment.TAG);

    if (plannerFragment != null) {
      plannerFragment.setSortOrder(sortOrder);
    }
  }
}
