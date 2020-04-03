package com.example.cabme.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchURL extends AsyncTask<String, Void, String> {
    @SuppressLint("StaticFieldLeak") // Not in production don't even worry about it :)
    private Context mContext;
    private String directionMode = "driving";

    /**
     * This builds a url for routes
     * References & Sources:
     * - https://github.com/divindvm/Android-DrawOnMap
     * - https://github.com/Vysh01/android-maps-directions
     * @param mContext
     */
    public FetchURL(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    /**
     * This is for storing data from web service and then fetching the data from web service
     * @param strings
     * @return
     */
    protected String doInBackground(String... strings) {    
        String data = "";
        directionMode = strings[1];
        try {
            data = downloadUrl(strings[0]);
            Log.d("MAPSLOG", "Background task data " + data);
        } catch (Exception e) {
            Log.d("MAPSLOG-BG Task", e.toString());
        }
        return data;
    }

    @Override
    /**
     * This invokes the thread for parsing the JSON data
     * @param s
     */
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);    
        parserTask.execute(s);
    }

    /**
     * This creats an http connection to communicate with url, connects to the url
     * and reads from it
     * @param strUrl
     * @return
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("MAPSLOG", "Downloaded URL: " + data);
            br.close();
        } catch (Exception e) {
            Log.d("MAPSLOG", "Exception downloading URL: " + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
