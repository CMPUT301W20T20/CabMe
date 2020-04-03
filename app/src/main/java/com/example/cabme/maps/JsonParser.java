package com.example.cabme.maps;

import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * Class parses the json file from the url created fr long and lat
 * The information extracted is used for a riders ride request
 * Setters unneeded
 *
 * Purposes:
 * - When a rider makes a new ride request the places/maps/directions API used to parse JSON files from
 *   the request made then use the information extracted from class to store ride requests from the
 *   Firebase DB.
 * - Literally just parses information (if there is any - some long&lats may be null - UNHANDLED CASE)
 *   from a start and end location via google. You can use this to get the actual address of a place or
 *   tbh any of the information i got out - check the JSON file in the log tagged "urldirection" it will
 *   be a link to the JSON file. Take whatever needed :)
 *
 * References & Sources:
 * - https://stackoverflow.com/questions/10339353/parsing-json-directions-google-maps
 *
 * TODO:
 *  [x] Parse for "end_address" -> JSON
 *  [x] Parse for "start_address" -> JSON
 *  [ ] Handle if GeoPoints don't correspond to an actual place (What is returned)
 */
public class JsonParser {

    // API KEY
    private String API_KEY;

    // Gets
    private Integer distanceValue;
    private Integer durationValue;

    private String distanceText;
    private String durationText;

    private String endAddress;
    private String startAddress;

    // Long and lat as doubles extracted from GeoPoint
    private Double startLat;
    private Double startLng;
    private Double destLat;
    private Double destLng;

    public JsonParser(GeoPoint startGeo, GeoPoint destGeo, String API_KEY) {
        this.API_KEY = API_KEY;
        GeoPointToDouble(startGeo, destGeo);
        GeoParsing();
    }

    private void GeoPointToDouble(GeoPoint startGeo, GeoPoint destGeo){
        startLat = startGeo.getLatitude();
        startLng = startGeo.getLongitude();
        destLat = destGeo.getLatitude();
        destLng = destGeo.getLongitude();
    }

    private void GeoParsing(){
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Origin of route
                    String str_origin = "origin=" + startLat + "," + startLng;
                    // Destination of route
                    String str_dest = "destination=" + destLat + "," + destLng;
                    // Mode
                    String mode = "mode=" + "driving";
                    // Building the parameters to the web service
                    String parameters = str_origin + "&" + str_dest + "&" + mode;
                    // Output format
                    String output = "json";
                    // Building the url to the web service
                    String urlstr = "https://maps.googleapis.com/maps/api/directions/"
                            + output + "?" + parameters + "&key=" + API_KEY;

                    URL url = new URL(urlstr);
                    Log.v("urldirection", url.toString());
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());

                    // Going through the JSON file -> Check URL IN logv "urldirection"
                    response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] = distance.getString("text");
                    distanceText = parsedDistance[0];
                    parsedDistance[0] = distance.getString("value");
                    distanceValue = Integer.valueOf(parsedDistance[0]);
                    JSONObject duration = steps.getJSONObject("duration");
                    parsedDistance[0] = duration.getString("text");
                    durationText = parsedDistance[0];
                    parsedDistance[0] = duration.getString("value");
                    durationValue = Integer.valueOf(parsedDistance[0]);
                    endAddress = steps.getString("end_address");
                    startAddress = steps.getString("start_address");

                    Log.wtf("parsedDistance", "distext: " + distanceText);
                    Log.wtf("parsedDistance", "disvalue: " + distanceValue);
                    Log.wtf("parsedDistance", "durtext: " + durationText);
                    Log.wtf("parsedDistance", "durvalue: " + durationValue);
                    Log.wtf("parsedDistance", "start: " + startAddress);
                    Log.wtf("parsedDistance", "end: " + endAddress);

                } catch (JSONException | IOException e) {
                    Log.v("JSONExeption", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.v("DistanceGoogleAPi", "Interrupted" + e);
            Thread.currentThread().interrupt();
        }
    }

    public String getDistanceText(){return this.distanceText; }

    public Integer getDistanceValue(){return this.distanceValue; }

    public String getDurationText(){return this.durationText; }

    public Integer getDurationValue(){return this.durationValue; }

    public String getStartAddress(){return this.startAddress; }

    public String getEndAddress(){return this.endAddress; }


}
