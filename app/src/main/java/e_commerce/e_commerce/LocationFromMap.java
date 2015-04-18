package e_commerce.e_commerce;

/**
 * Created by Suganprabu on 17-04-2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFromMap extends FragmentActivity
        implements OnMapReadyCallback {

    MapFragment mapfragment;
    public static double[] location = new double[2]; //location[0] is lat and location[1] is long
    public static LocationManager locationManager;
    private LocationListener myLocationListener;
    private ProgressDialog p1;
    private boolean locationChanged=false;
    private GoogleMap gmap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_location);

        p1=new ProgressDialog(LocationFromMap.this);

        if(location==null) {
            location[0] = 1000.0;
            location[1] = 1000.0;
        }

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            p1.setCancelable(true);
            p1.setTitle("Getting location...");
            p1.show();

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            myLocationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    LocationFromMap.location[0] = location.getLatitude();
                    LocationFromMap.location[1] = location.getLongitude();
                    Log.i("Lat", String.valueOf(LocationFromMap.location[0]));
                    Log.i("Long", String.valueOf(LocationFromMap.location[1]));
                    Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));

                    locationManager.removeUpdates(myLocationListener);
                    CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));

                    if (p1.isShowing()) {
                        p1.hide();
                        p1.dismiss();
                    }

                    locationChanged=true;

                    if(locationChanged==true){
                        if(gmap!=null)
                            gmap.addMarker(new MarkerOptions()
                                    .position(new LatLng(LocationFromMap.location[0],LocationFromMap.location[1]))
                                    .title("Marker"));
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
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        }
        if(!LoginActivity.prefs.getString("Latitude","").equals("")&&!LoginActivity.prefs.getString("Longitude","").equals("")) {
            location[0] = Double.parseDouble(LoginActivity.prefs.getString("Latitude",""));
            location[1] = Double.parseDouble(LoginActivity.prefs.getString("Longitude",""));

            Log.i("Latitude", String.valueOf(location[0]));
            Log.i("Longitude", String.valueOf(location[1]));
        }

        if(!p1.isShowing()) {
            mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
            mapfragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map){

        gmap=map;

        Log.i("Location[0]",String.valueOf(LocationFromMap.location[0]));
        Log.i("Location[1]", String.valueOf(LocationFromMap.location[1]));

        if((LocationFromMap.location[0]==1000.0&&LocationFromMap.location[1]==1000.0) || (LocationFromMap.location[0]==0.0&&LocationFromMap.location[1]==0.0)){

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(12.9915, 80.2336))
                    .title("Marker"));
            Log.i("Latitude","12.9915");
            Log.i("Longitude","80.2336");

        }

        else{
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(LocationFromMap.location[0], LocationFromMap.location[1]))
                    .title("Marker"));
        }


        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
                    map.clear();
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker"));
                    LocationFromMap.location[0] = latLng.latitude;
                    LocationFromMap.location[1] = latLng.longitude;

                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();

                }
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
                    map.clear();
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker"));

                    LocationFromMap.location[0] = latLng.latitude;
                    LocationFromMap.location[1] = latLng.longitude;

                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();

                    Message msg = new Message();
                    msg.arg1=1;
                    msg.arg2=1;
                    Master.locationHandler.sendMessage(msg);

                    if(Master.locationDialog!=null && Master.locationDialog.isShowing()) {
                        Master.locationDialog.hide();
                        Master.locationDialog.dismiss();
                    }

                    finish();

                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
                    map.clear();
                    map.addMarker(new MarkerOptions()
                            .position(marker.getPosition())
                            .title("Marker"));

                    LocationFromMap.location[0] = marker.getPosition().latitude;
                    LocationFromMap.location[1] = marker.getPosition().longitude;

                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();

                }
            }

        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Message msg = new Message();
        msg.arg1=1;
        msg.arg2=1;
        Master.locationHandler.sendMessage(msg);
        if(Master.locationDialog!=null && Master.locationDialog.isShowing()) {
            Master.locationDialog.hide();
            Master.locationDialog.dismiss();
        }

    }

   /* @Override
    public void onLocationChanged(android.location.Location loc) {
        Location.location[0] = loc.getLatitude();
        Location.location[1] = loc.getLongitude();
        Log.i("Lat",String.valueOf(Location.location[0]));
        Log.i("Long",String.valueOf(Location.location[1]));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
