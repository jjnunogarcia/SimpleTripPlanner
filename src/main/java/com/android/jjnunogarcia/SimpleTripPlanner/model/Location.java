package com.android.jjnunogarcia.SimpleTripPlanner.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Date: 29.01.14
 *
 * @author jjnunogarcia@gmail.com
 */
public class Location {
  private String type;
  private int    id;
  private String name;
  private String typeSpecific;
  private LatLng geoPosition;
  private float  distanceToDestiny;

  public Location() {
    type = "";
    id = -1;
    name = "";
    typeSpecific = "";
    geoPosition = null;
    distanceToDestiny = -1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Location)) {
      return false;
    }

    Location location = (Location) o;

    return id == location.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTypeSpecific() {
    return typeSpecific;
  }

  public void setTypeSpecific(String typeSpecific) {
    this.typeSpecific = typeSpecific;
  }

  public LatLng getGeoPosition() {
    return geoPosition;
  }

  public void setGeoPosition(LatLng geoPosition) {
    this.geoPosition = geoPosition;
  }

  public float getDistanceToDestiny() {
    return distanceToDestiny;
  }

  public void setDistanceToDestiny(float distanceToDestiny) {
    this.distanceToDestiny = distanceToDestiny;
  }
}
