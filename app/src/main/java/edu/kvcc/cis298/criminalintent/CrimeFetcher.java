package edu.kvcc.cis298.criminalintent;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeFetcher {

    // TAG constant for the LogCat
    private static final String TAG = "CrimeFetcher";

    // Method to get the raw bytes from the web source.
    // Conversion from bytes to something more meaningful
    // will happen in a different method.
    // This method has one parameter which is the URL that
    // we want to connect to.
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        // Create a new URL object from the url string that was passed in.
        URL url = new URL(urlSpec);

        // Create a new HTTP connection to the specified url.
        // If we were to load data from a secure site, it would need
        // to use HttpsURLConnection
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            // Create a output stream to hold the data that is read
            // in from the url source
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Create a input stream from the http connection
            InputStream in = connection.getInputStream();

            // Check to see that the response code from the http request is
            // 200, which is the same as http_ok. Every web request will return
            // some sort of response code. You can google them.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        connection.getResponseMessage()
                        + ": with " + urlSpec
                );
            }

            // Create an int to hold how many bytes were read in.
            int bytesRead = 0;

            // Create a byte array to act as a buffer that will read
            // in up to 1024 bytes at a time.
            byte[] buffer = new byte[1024];
            // While we can read bytes from the input stream
            while ((bytesRead = in.read(buffer)) > 0) {
                // Write the bytes out to the output stream
                out.write(buffer, 0, bytesRead);
            }
            // Once everything has been read and written, close the output stream
            out.close();

            // Convert the output stream to a byte array
            return out.toByteArray();
        } finally {
            // Make sure the connection to the web is closed.
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    // Method to fetch the crimes from the web url and parse them into a list.
    public List<Crime> fetchCrimes() {

        // Make a list of crimes to get populated
        List<Crime> crimes = new ArrayList<>();

        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi")
                    .buildUpon()
                    // Add extra parameters here with the appendQueryParameter method like so.
                    // .appendQueryParameter("param", "Value")
                    // This will add a query param to the url.
                    // So, if our URL was domain.com/api
                    // it would turn it into domain.com/api?param=value
                    .build()
                    .toString();

            // This calls the above methods to use the URL to get the JSON
            // from the web service/server. After the call, we will actually
            // have the JSON that we need to parse.
            String jsonString = getUrlString(url);
            // Log out the result from the web request
            Log.i(TAG, "Received JSON: " + jsonString);
            // Convert the string into a JSON Array which is the root object.
            // This will take the jsonString that we got back and put it into
            // a jsonArray object. We have to use a jsonArray because our
            // jsonString starts out with an Array. If it started with an object (Dictionary)
            // "{}" we would need to use JSONObject instead of JSONArray.
            // The code in the book uses JSONObject for their parse.
            JSONArray jsonArray = new JSONArray(jsonString);

            // Parse the jsonArray into a list of crimes.
            parseCrimes(crimes, jsonArray);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        // Return the crimes that were parsed.
        return crimes;
    }

    // Method to parse out the crimes from the JSONArray
    private void parseCrimes(List<Crime> crimes, JSONArray jsonArray)
            throws IOException, JSONException {

        // Loop through all of the elements in the JSONArray that was sent into
        // this method
        for (int i=0; i<jsonArray.length(); i++) {
            // Fetch a single JSONObject out from the JSONArray based on
            // the current index that we are on.
            JSONObject crimeJsonObject = jsonArray.getJSONObject(i);

            // Pull out the data and start making crimes from it.
            String uuidString = crimeJsonObject.getString("uuid");
            UUID uuidForNewCrime = UUID.fromString(uuidString);

            String title = crimeJsonObject.getString("title");
            Date crimeDate = new Date();
            try {
                DateFormat format = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
                crimeDate = format.parse(crimeJsonObject.getString("incident_date"));
            } catch (Exception e) {
                Log.e(TAG, "Unable to parse date");
                crimeDate = new Date();
            }

            boolean isSolved = crimeJsonObject.getString("is_solved").equals("1");

            // Create a new Crime with the UUID we pulled out.
            Crime crime = new Crime(
                    uuidForNewCrime,
                    title,
                    crimeDate,
                    isSolved
            );

            // Add crime to the list
            crimes.add(crime);
        }
    }







}
