package com.android.jjnunogarcia.SimpleTripPlanner.comparators;

import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;

import java.util.Comparator;

/**
 * User: Jesús
 * Date: 30/01/14
 */
public class DistanceComparator implements Comparator<Location> {
  @Override
  public int compare(Location lhs, Location rhs) {
    return (int) (lhs.getDistanceToDestiny() - rhs.getDistanceToDestiny());
  }
}
