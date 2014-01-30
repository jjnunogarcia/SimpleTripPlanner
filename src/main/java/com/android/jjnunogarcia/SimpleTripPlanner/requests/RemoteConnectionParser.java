package com.android.jjnunogarcia.SimpleTripPlanner.requests;

import android.util.Log;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;
import com.android.jjnunogarcia.SimpleTripPlanner.requests.RemoteLocationParsingAsyncTask.RemoteLocationParsingInterface;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * User: Jesús
 * Date: 29/01/14
 * Parses the json response from an url and creates objects according to the data received.
 */
public class RemoteConnectionParser {
  public static final String TAG                = RemoteConnectionParser.class.getSimpleName();
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
    HttpURLConnection http;
    URL theUrl = new URL(url);

    if (theUrl.getProtocol().toLowerCase().equals("https")) {
      trustAllHosts();
      HttpsURLConnection https = (HttpsURLConnection) theUrl.openConnection();
      https.setHostnameVerifier(DO_NOT_VERIFY);
      http = https;
    } else {
      http = (HttpURLConnection) theUrl.openConnection();
    }

    String jsonText = getJSON(http, 30000);

    return new JSONObject(jsonText);
  }

  private String getJSON(HttpURLConnection http, int timeout) {
    InputStream inputStream = null;
    try {
      http.setRequestMethod("GET");
      http.setRequestProperty("Content-length", "0");
      http.setUseCaches(false);
      http.setAllowUserInteraction(false);
      http.setConnectTimeout(timeout);
      http.setReadTimeout(timeout);
      http.connect();
      int status = http.getResponseCode();

      switch (status) {
        case 200:
        case 201:
          inputStream = http.getInputStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
          return readAll(br);
      }
    } catch (MalformedURLException ex) {
      Log.e(TAG, "Error retrieving Json", ex);
    } catch (IOException ex) {
      Log.e(TAG, "Error retrieving Json", ex);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          Log.e(TAG, "Error closing stream", e);
        }
      }
    }
    return null;
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

  /*
   * Always verify the host - don't check for certificate
   */
  private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  };

  /**
   * This doesn't  check any certificate. That means, it trusts every server.
   * DO NOT USE THIS IN PRODUCTION
   */
  private static void trustAllHosts() {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[] {};
      }

      public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      }

      public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      }
    }};

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      Log.e(TAG, "Error installing the all-trusting certificate", e);
    }
  }

  public void setRemoteParsingInterface(RemoteLocationParsingInterface remoteParsingInterface) {
    this.remoteParsingInterface = remoteParsingInterface;
  }

}
