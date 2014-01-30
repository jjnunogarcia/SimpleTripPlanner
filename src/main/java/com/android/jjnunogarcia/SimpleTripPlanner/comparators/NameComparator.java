package com.android.jjnunogarcia.SimpleTripPlanner.comparators;

import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;

import java.util.Comparator;

/**
 * User: Jesús
 * Date: 30/01/14
 */
public class NameComparator implements Comparator<Location> {
  @Override
  public int compare(Location lhs, Location rhs) {
    return lhs.getName().compareTo(rhs.getName());
  }
}
