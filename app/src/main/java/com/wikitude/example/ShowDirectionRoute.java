package com.wikitude.example;

/**
 * Created by buqento on 11/8/2015.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowDirectionRoute extends FragmentActivity implements LocationListener, OnClickListener {

    GoogleMap googleMap;
    public double dLat, dLon, fLat, fLng;
    String k0, k1, k2, k3, k4, k5;
    String dTitle, dPoly, dKet, dVideo, dCategory;
    public LatLng from,to;
    PolylineOptions lineOptions;
    private ImageButton  terrain, satellite, kembali;
    private TextView des;
    private String mode;
    public Location location;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public ProgressDialog pDialog;
    public static final String TAG_NAME = "name";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_POLYGON = "polygon";
    public static final String TAG_VIDEO = "video";
    public static final float TEST_LATITUDE = -3.696541f;
    public static final float TEST_LONGITUDE = 128.178398f;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_map);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        googleMap = fm.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        terrain = (ImageButton) findViewById(R.id.terrain);
        terrain.setOnClickListener(this);
        satellite = (ImageButton) findViewById(R.id.satellite);
        satellite.setOnClickListener(this);
        kembali = (ImageButton) findViewById(R.id.kembali);
        kembali.setOnClickListener(this);

        Intent intent = getIntent();
        dLat = Double.parseDouble(intent.getStringExtra(TAG_LATITUDE));
        dLon = Double.parseDouble(intent.getStringExtra(TAG_LONGITUDE));
        dTitle = intent.getStringExtra(TAG_NAME);
        dPoly = intent.getStringExtra(TAG_POLYGON);
        dKet = intent.getStringExtra(TAG_DESCRIPTION);
        dCategory = intent.getStringExtra(TAG_CATEGORY);
        dVideo = intent.getStringExtra(TAG_VIDEO);
        to = new LatLng(dLat,dLon);
        des = (TextView)findViewById(R.id.tDes);
        des.setText(dTitle);
        des = (TextView)findViewById(R.id.tKet);
        des.setText(dKet);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        // Getting latitude of the current location
        fLat = location.getLatitude();

        // Getting longitude of the current location
        fLng = location.getLongitude();

//         Creating a LatLng object for the current location
        from = new LatLng(fLat, fLng);
//        from = new LatLng(TEST_LATITUDE, TEST_LONGITUDE);

        GetRoute();
        CreateMarker();
        CreatePolyline();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 10));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.terrain:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.satellite:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.kembali:
                finish();
                break;
            default:
                break;
        }
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

    public void tampilVideo(final View view){
        Intent intent = null;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+TAG_VIDEO));
        startActivity(intent);
    }

    public void GetRoute(){
        try {
            String url = getDirectionsUrl(from, to);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        } catch (Exception e) {
            // TODO: handle exception
            Intent inten = new Intent(
                    getApplicationContext(),
                    ShowDirectionRoute.class);
            startActivity(inten);
            finish();
        }
    }

    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
    }

    public void CreateMarker(){
        googleMap.addMarker(new MarkerOptions()
                .position(from)
                .title("Posisi Anda")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        googleMap.addMarker(new MarkerOptions()
                .position(to)
                .title(dTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
                .snippet(dCategory));

    }

    public void CreatePolyline(){
        String polygons = dPoly;
        String[] pecah = polygons.split(";");

        k0 = pecah[0];
        String[] pecah0 = k0.split(", ");
        Double lat0 = Double.parseDouble(pecah0[0]);
        Double lon0 = Double.parseDouble(pecah0[1]);

        k1 = pecah[1];
        String[] pecah1 = k1.split(", ");
        Double lat1 = Double.parseDouble(pecah1[0]);
        Double lon1 = Double.parseDouble(pecah1[1]);

        k2 = pecah[2];
        String[] pecah2 = k2.split(", ");
        Double lat2 = Double.parseDouble(pecah2[0]);
        Double lon2 = Double.parseDouble(pecah2[1]);

        k3 = pecah[3];
        String[] pecah3 = k3.split(", ");
        Double lat3 = Double.parseDouble(pecah3[0]);
        Double lon3 = Double.parseDouble(pecah3[1]);

        k4 = pecah[4];
        String[] pecah4 = k4.split(", ");
        Double lat4 = Double.parseDouble(pecah4[0]);
        Double lon4 = Double.parseDouble(pecah4[1]);

        k5 = pecah[5];
        String[] pecah5 = k5.split(", ");
        Double lat5 = Double.parseDouble(pecah5[0]);
        Double lon5 = Double.parseDouble(pecah5[1]);

        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(lat0,lon0),
                        new LatLng(lat1,lon1),
                        new LatLng(lat2,lon2),
                        new LatLng(lat3,lon3),
                        new LatLng(lat4,lon4),
                        new LatLng(lat5,lon5),
                        new LatLng(lat0,lon0)
                )
                .strokeColor(Color.BLUE));
//        .fillColor(Color.YELLOW));
    }

    // fungsi mendapatkan rute
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Awal rute
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Tujuan rute
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Membuat parameters untuk dimasukkan web service rute map google
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&mode=" + mode;

        // Output format
        String output = "json";

        // URL untuk eksekusi rute
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;

        return url;
    }

    // Metode mendapatkan json data dari url
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // menghubungkan ke url
            urlConnection.connect();

            // Membaca data dari url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // class untuk download data dari Google Directions URL
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Mendowload data dalam non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
                Intent i = new Intent(getApplicationContext(),
                        MenuActivity.class);
                startActivity(i);
                finish();
            }
            return data;
        }

        // di eksekusi di layar tampilan,setelah selesai ekseksui di
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Thread untuk parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** Class untuk mengekstrak Google Directions dalam format JSON */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing data di dalam thread background
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Mulai parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                Intent i = new Intent(getApplicationContext(),
                        MenuActivity.class);
                startActivity(i);
                finish();
            }
            return routes;
        }

        // Mengeksekusi di tampilan setelah proses ektrak data selesai
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                points.clear();

                // Menginisialisasi i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                // Menambahkan semua points dalam rute ke LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);

            }

            googleMap.addPolyline(lineOptions);


        }
    }

}