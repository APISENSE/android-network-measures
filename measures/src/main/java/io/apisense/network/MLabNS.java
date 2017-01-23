package io.apisense.network;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Determines the closest MLab server.
 *
 * @see <a href="https://www.measurementlab.net/">https://www.measurementlab.net/</a>
 */
public class MLabNS extends AsyncTask<Void, Void, List<String>> {
  private static final String TAG = "MLabNS";
  private static final String MLAB_URL = "http://mlab-ns.appspot.com/mobiperf?format=json";
  private static final String IP_FIELD = "ip";
  private final MLabListener callback;

  public MLabNS(MLabListener listener) {
    this.callback = listener;
  }

  /**
   * Returns an {@link List} containing IPV4/IPV6 addresses of MLab server to run a
   * TCP or UDP Test
   *
   * @return List of IP addresses to run TCP/UDP tests
   */
  @Override
  protected List<String> doInBackground(Void... voids) {
    ArrayList<String> mlabNSResult = new ArrayList<>();
    String response;
    HttpURLConnection con = null;
    InputStream inputStream = null;
    try {
      URL target = new URL(MLAB_URL);
      con = (HttpURLConnection) target.openConnection();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();

      if (responseCode != 200) {
        throw new InvalidParameterException("Received status " + responseCode + " from mlab-ns");
      }

      inputStream = con.getInputStream();
      response = getResponseString(inputStream);
    } catch (SocketTimeoutException e) {
      throw new InvalidParameterException("Connect to m-lab-ns timeout. Please try again.");
    } catch (IOException e) {
      throw new InvalidParameterException(e.getMessage());
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          Log.e(TAG, "Error while closing stream", e);
        }
      }
      if (con != null) {
        con.disconnect();
      }
    }
    mlabNSResult.addAll(retrieveIps(response));
    return mlabNSResult;
  }

  /**
   * Read the given stream to retrieve the response body
   * and return it as a String.
   *
   * @param inputStream The stream to read
   * @return The body as String.
   * @throws IOException If the stream interaction fails.
   */
  @NonNull
  private String getResponseString(InputStream inputStream) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    return response.toString();
  }

  /**
   * Parse the response Json to retrieve the server IPs.
   *
   * @param response The String representation of the server response.
   * @return The list of available IPs.
   */
  @NonNull
  private List<String> retrieveIps(String response) {
    List<String> result = new ArrayList<>();
    try {
      JSONObject json = new JSONObject(response);
      if (json.get(IP_FIELD) instanceof JSONArray) {
        // Convert array value into ArrayList
        JSONArray jsonArray = null;
        jsonArray = (JSONArray) json.get(IP_FIELD);
        for (int i = 0; i < jsonArray.length(); i++) {
          result.add(jsonArray.get(i).toString());
        }
      } else if (json.get(IP_FIELD) instanceof String) {
        // Append the string into ArrayList
        result.add(String.valueOf(json.getString(IP_FIELD)));
      } else {
        throw new InvalidParameterException("Unknown type " +
            json.get(IP_FIELD).getClass().toString() + " of value " + json.get(IP_FIELD));
      }
    } catch (JSONException e) {
      throw new InvalidParameterException(e.getMessage());
    }
    return result;
  }

  @Override
  protected void onPostExecute(List<String> result) {
    callback.onMLabFinished(result);
  }
}

