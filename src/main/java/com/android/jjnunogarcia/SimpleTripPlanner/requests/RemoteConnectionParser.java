package com.android.jjnunogarcia.SimpleTripPlanner.requests;

import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.requests.RemoteLocationParsingAsyncTask.RemoteLocationParsingInterface;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * User: Jesús
 * Date: 29/01/14
 * Parses the json response from an url and creates objects according to the data received.
 */
public class RemoteConnectionParser {
  public static final String JSON_RESULTS       = "results";
  public static final String JSON_TYPE          = "_type";
  public static final String JSON_ID            = "_id";
  public static final String JSON_NAME          = "name";
  public static final String JSON_TYPE_SPECIFIC = "type";
  public static final String JSON_GEO_POSITION  = "geo_position";
  public static final String JSON_LATITUDE      = "latitude";
  public static final String JSON_LONGITUDE     = "longitude";

  private String                         url;
  private RemoteLocationParsingInterface remoteParsingInterface;

  public RemoteConnectionParser(String url) {
    this.url = url;
  }

  public void refreshDatabaseWithParsedData() {
    try {
      JSONObject jsonObject = readJsonFromUrl();
      buildLocationsFromJsonObject(jsonObject);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private JSONObject readJsonFromUrl() throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName(HTTP.UTF_8)));
      String jsonText = readAll(rd);
      return new JSONObject(jsonText);
    } finally {
      is.close();
    }
  }

  private ArrayList<Location> buildLocationsFromJsonObject(JSONObject jsonObject) throws JSONException {
    ArrayList<Location> locations = new ArrayList<Location>();

    if (jsonObject.has(JSON_RESULTS)) {
      JSONArray jsonArray = jsonObject.optJSONArray(JSON_RESULTS);

      for (int i = 0, tasksSize = jsonArray.length(); i < tasksSize; i++) {
        Location location = new Location();
        JSONObject jsonObjectRetrieved = jsonArray.getJSONObject(i);

        if (jsonObjectRetrieved.has(JSON_TYPE)) {
          location.setType(jsonObjectRetrieved.getString(JSON_TYPE));
        }

        if (jsonObjectRetrieved.has(JSON_ID)) {
          location.setId(jsonObjectRetrieved.getInt(JSON_ID));
        }

        if (jsonObjectRetrieved.has(JSON_NAME)) {
          location.setName(jsonObjectRetrieved.getString(JSON_NAME));
        }

        if (jsonObjectRetrieved.has(JSON_TYPE_SPECIFIC)) {
          location.setTypeSpecific(jsonObjectRetrieved.getString(JSON_TYPE_SPECIFIC));
        }

        if (jsonObjectRetrieved.has(JSON_GEO_POSITION)) {
          JSONObject geoPosition = jsonObjectRetrieved.getJSONObject(JSON_GEO_POSITION);
          LatLng position = null;

          if (geoPosition.has(JSON_LATITUDE) && geoPosition.has(JSON_LONGITUDE)) {
            position = new LatLng(geoPosition.getDouble(JSON_LATITUDE), geoPosition.getDouble(JSON_LONGITUDE));
          }

          location.setGeoPosition(position);
        }

        if (!locations.contains(location)) {
          locations.add(location);
          if (remoteParsingInterface != null) {
            remoteParsingInterface.onLocationAdded(location);
          }
        }
      }
    }

    return locations;
  }

  private String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public void setRemoteParsingInterface(RemoteLocationParsingInterface remoteParsingInterface) {
    this.remoteParsingInterface = remoteParsingInterface;
  }
}
