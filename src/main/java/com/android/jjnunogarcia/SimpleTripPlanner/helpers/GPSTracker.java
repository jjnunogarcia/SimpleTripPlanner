package com.android.jjnunogarcia.SimpleTripPlanner.helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Date: 30.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class GpsTracker extends Service implements LocationListener {
  private static final String TAG                             = GpsTracker.class.getSimpleName();
  private static final long   MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 meters
  private static final long   MIN_TIME_BETWEEN_UPDATES        = 1000 * 60 * 1; // 1 minute

  private   Context         context;
  private   boolean         isGPSEnabled;
  private   boolean         isNetworkEnabled;
  private   boolean         canGetLocation;
  private   Location        location;
  private   double          latitude;
  private   double          longitude;
  protected LocationManager locationManager;

  public GpsTracker(Context context) {
    this.context = context;
    isGPSEnabled = false;
    isNetworkEnabled = false;
    canGetLocation = false;
    getLocation();
  }

  public Location getLocation() {
    try {
      locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
      isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

      if (isGPSEnabled || isNetworkEnabled) {
        canGetLocation = true;

        if (isNetworkEnabled) {
          locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

          if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            updateGPSCoordinates();
          }
        }

        if (isGPSEnabled) {
          if (location == null) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            Log.d(TAG, "GPS Enabled");

            if (locationManager != null) {
              location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
              updateGPSCoordinates();
            }
          }
        }
      } else {
        // no network provider is enabled
      }
    } catch (Exception e) {
      Log.e(TAG, "Impossible to connect to LocationManager", e);
    }

    return location;
  }

  public void updateGPSCoordinates() {
    if (location != null) {
      latitude = location.getLatitude();
      longitude = location.getLongitude();
    }
  }

  public void stopUsingGPS() {
    if (locationManager != null) {
      locationManager.removeUpdates(this);
    }
  }

  public double getLatitude() {
    if (location != null) {
      latitude = location.getLatitude();
    }

    return latitude;
  }

  public double getLongitude() {
    if (location != null) {
      longitude = location.getLongitude();
    }

    return longitude;
  }

  public boolean canGetLocation() {
    return canGetLocation;
  }

  @Override
  public void onLocationChanged(Location location) {
  }

  @Override
  public void onProviderDisabled(String provider) {
  }

  @Override
  public void onProviderEnabled(String provider) {
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
