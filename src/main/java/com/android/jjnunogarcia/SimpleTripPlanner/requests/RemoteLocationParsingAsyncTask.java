package com.android.jjnunogarcia.SimpleTripPlanner.requests;

import android.os.AsyncTask;
import com.android.jjnunogarcia.SimpleTripPlanner.adapters.LocationAdapter;
import com.android.jjnunogarcia.SimpleTripPlanner.model.Location;

/**
 * User: Jesús
 * Date: 27/01/14
 * Asynchronous task to parse a remote connection and update the UI. It's executed when the {@link com.android.jjnunogarcia.SimpleTripPlanner.fragments.PlannerFragment} is added.
 */
public class RemoteLocationParsingAsyncTask extends AsyncTask<Void, Location, Void> {
  private String                 url;
  private LocationAdapter        adapter;
  private RemoteConnectionParser remoteConnectionParser;

  public RemoteLocationParsingAsyncTask(String url, LocationAdapter adapter) {
    this.url = url;
    this.adapter = adapter;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    remoteConnectionParser = new RemoteConnectionParser(url);
    remoteConnectionParser.setRemoteParsingInterface(new RemoteLocationParsingInterface() {
      @Override
      public void onLocationAdded(Location location) {
        publishProgress(location);
      }
    });
  }

  @Override
  protected void onProgressUpdate(Location... values) {
    super.onProgressUpdate(values);
    if (adapter != null && values.length > 0) {
      adapter.addIfNotExisting(values[0]);
    }
  }

  @Override
  protected Void doInBackground(Void... params) {
    remoteConnectionParser.refreshDatabaseWithParsedData();
    return null;
  }

  public interface RemoteLocationParsingInterface {
    void onLocationAdded(Location location);
  }
}