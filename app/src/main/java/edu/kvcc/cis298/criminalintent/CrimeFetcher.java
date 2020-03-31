package edu.kvcc.cis298.criminalintent;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public void fetchCrimes() {
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
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
    }
}
