package com.example.fiaz.gpsproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView gps;
    TextView coordinates;
    TextView address;
    TextView distance;
    ImageView imageView;
    static Location prevLoc = new Location("");
    double tDistance = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected  void onStart() {
        super.onStart();
        Log.d("LIFECYCLETAG", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLETAG", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LIFECYCLETAG", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLETAG", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLETAG", "onResume");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LIFECYCLETAG", "onCreate");
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gps = (TextView) findViewById(R.id.gps);
        gps.setText("GPS App");
        gps.setTextColor(Color.LTGRAY);
        gps.setTextSize(40);
        coordinates = (TextView) findViewById(R.id.coordinates);
        coordinates.setTextColor(Color.LTGRAY);
        address = (TextView) findViewById(R.id.address);
        distance = (TextView) findViewById(R.id.distance);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.mapimage);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        prevLoc = null;
        LocationListener locationListener = new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(Location location) {
                //Target
                //location.setLatitude(40.380729);
                //location.setLongitude(-74.575923);

                // Regal
                //location.setLatitude(40.444980);
                //location.setLongitude(-74.503493);

                // Franklin Memorial Park
                //location.setLatitude(40.461753);
                //location.setLongitude(-74.502991);

                coordinates.setText("Latitude: " + Double.toString(location.getLatitude()) + "\nLongitude: " + Double.toString(location.getLongitude()));
                AsyncTask gpsThread = new AsyncTask();
                gpsThread.execute(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                if(prevLoc != null)
                    tDistance += location.distanceTo(prevLoc);
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                decimalFormat.setRoundingMode(RoundingMode.CEILING);
                distance.setText("Total Distance: " + decimalFormat.format((tDistance / 1609.344)) + " mile(s)");
                distance.setTextColor(Color.LTGRAY);
                prevLoc = new Location(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    public class AsyncTask extends android.os.AsyncTask<String, Void, Void> {
        String result = "";
        @Override
        protected Void doInBackground(String... strings) {
            String urlText = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + strings[0] + "," + strings[1] + "&key=AIzaSyBTXT6BEm1p-oPD_g00a2eze-Frt52j3bA";
            try {
                URL url = new URL(urlText);
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String readInfo = "";
                while((readInfo = bufferedReader.readLine()) != null)
                    result+= readInfo;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                JSONObject jsonText = new JSONObject(result);
                JSONArray jsonResults = jsonText.getJSONArray("results");
                JSONObject item = jsonResults.getJSONObject(0);
                String fAddress = item.getString("formatted_address");
                address.setText(fAddress);
                address.setTextColor(Color.LTGRAY);
                address.setTextSize(20);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
