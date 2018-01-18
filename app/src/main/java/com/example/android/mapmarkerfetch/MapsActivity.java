package com.example.android.mapmarkerfetch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    //Google Map
    private GoogleMap mMap;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    // Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;
//    ArrayList<HashMap<String, String>> location = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //*** Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        networkCall();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void networkCall() {
        new AsyncTask<Void, Void, JSONArray>() {
           JSONArray data = null;
            @Override
            protected JSONArray doInBackground(Void... voids) {
                ArrayList<HashMap<String, String>> location = null;
                String url = "http://192.168.5.127/MapPractice/show_data.php";

                try {
                    Log.i("MapActivity", "making api call");
                    data = new JSONArray(getHttpGet(url));
                    Log.i("MapActivity", "making api calling" + data );
//                    setLocationMapMarker(data);
//                    Log.i("MapActivity", "making api calling" + data + " " + location.size());
                } catch (JSONException e) {
                    Log.e("MapActivity", "api call exception");
                    e.printStackTrace();
                }
                Log.i("MapActivity", "making api calling" +  data );
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray data) {
                Log.i(TAG," on Post execute " + data);
                super.onPostExecute(data);

                if (data == null){
                    Log.e(TAG, "onPostExecute: got network error");
                    return;
                }
//                ArrayList<HashMap<String, String>> location = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> location = new ArrayList<HashMap<String, String>>();
                Log.i("MapActivity", data.toString());
                HashMap<String, String> map;

                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = null;
                        c = data.getJSONObject(i);
                        Log.i("MapActivity", data.toString() + " data " + c.getString("LocationID"));
                        map = new HashMap<String, String>();
                        map.put("LocationID", c.getString("LocationID"));
                        map.put("Latitude", c.getString("Latitude"));
                        map.put("Longitude", c.getString("Longitude"));
                        map.put("LocationName", c.getString("LocationName"));
                        location.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(location.size()>=1)
                {
                    for (int i = 0; i < location.size(); i++)
                    {
                        Latitude = Double.parseDouble(location.get(i).get("Latitude").toString());
                        Longitude = Double.parseDouble(location.get(i).get("Longitude").toString());
                        String name = location.get(i).get("LocationName").toString();
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(name);
                        mMap.addMarker(marker);
                    }
                }

            }
        }.execute();
    }
//    private void setLocationMapMarker(JSONArray data){
//        if (data == null){
//            Log.e(TAG, "onPostExecute: got network error");
//            return;
//        }
//        //            location = new ArrayList<HashMap<String, String>>();
//        Log.i("MapActivity", data.toString());
//        HashMap<String, String> map;
//
//        try {
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject c = null;
//                c = data.getJSONObject(i);
//                Log.i("MapActivity", data.toString() + " data " + c.getString("LocationID"));
//                map = new HashMap<String, String>();
//                map.put("LocationID", c.getString("LocationID"));
//                map.put("Latitude", c.getString("Latitude"));
//                map.put("Longitude", c.getString("Longitude"));
//                map.put("LocationName", c.getString("LocationName"));
//                location.add(map);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

    private String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        Log.e("MapActivity", url + "23");
        HttpClient client = new DefaultHttpClient();
        Log.e("MapActivity", url + "24");
        HttpGet httpGet = new HttpGet(url);
        Log.e("MapActivity", url + "25");
        try {
            Log.e("MapActivity", url + "26");
            HttpResponse response = client.execute(httpGet);
            Log.e("MapActivity", url + "27");
            StatusLine statusLine = response.getStatusLine();
            Log.e("MapActivity", url + "28");
            int statusCode = statusLine.getStatusCode();
            Log.e("MapActivity", "name" + statusCode);
            Log.e("MapActivity", "name" + statusCode);
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("MapActivity", url + "1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MapActivity", url + "2");
        }
        Log.e("MapActivity", url + "3" + str.toString());
        return str.toString();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng karachi = new LatLng(25.0700, 67.2848);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(karachi));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {
//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        // *** Marker (Loop)
//        Log.i(TAG,"MapLocation List"+location.size());
//        if(location.size()>=1)
//        {
//            for (int i = 0; i < location.size(); i++)
//            {
//                Latitude = Double.parseDouble(location.get(i).get("Latitude").toString());
//                Longitude = Double.parseDouble(location.get(i).get("Longitude").toString());
//                String name = location.get(i).get("LocationName").toString();
//                MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(name);
//                mMap.addMarker(marker);
//            }
//        }
//        Log.i(TAG,"MapLocation List after call "+location.size());

//        GetAddress("24.859957","67.049079");
//        GetAddress("24.859884","67.047375");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        Toast.makeText(this.getApplicationContext(), "map gps", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "This is require", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //Geocoder function that received two parameter latitiude and longitude as a String and return the Location name as a string
    public String GetAddress(String lat, String lon) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                ret = returnedAddress.getAddressLine(0);
                String[] name = new String[3];
                name = ret.split(",");
                ret = name[0];
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }
}
