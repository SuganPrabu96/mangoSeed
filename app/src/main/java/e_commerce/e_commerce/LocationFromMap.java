package e_commerce.e_commerce;

/**
 * Created by Suganprabu on 17-04-2015.
 */

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

public class LocationFromMap extends FragmentActivity{
     //   implements OnMapReadyCallback, LocationListener {

    MapFragment mapfragment;
    public static double[] location = new double[2]; //location[0] is lat and location[1] is long
    public static LocationManager locationManager;
    private ProgressDialog p1;
    private boolean locationChanged=false;
    private Handler locUpdateHandler;
    public int ONE_SECOND = 1000;
    public int SECONDS_PER_MINUTE = 60;
    public int ONE_MINUTE = ONE_SECOND * SECONDS_PER_MINUTE;
    public int NO_OF_MINUTES = 1;
    public long INTERVAL = NO_OF_MINUTES * ONE_MINUTE;

    // no. of providers available
    private enum PROVIDER_STATUS { BOTH_AVAILABLE, ONE_AVAILABLE, UNAVAILABLE };
    // id numbers for notifications
    private enum NOTIFICATION_ID { LOCATION_CHANGE, PROVIDER_ENABLED, PROVIDER_DISABLED };

    private LocationManager mLocationManager;
    private String mProvider;

    private Criteria mCriteria;
    private PROVIDER_STATUS mProviderStatus;
    private boolean mProviderUnavailableDialogDisplayed;
    private MyLocationListener mListener;
    static double lat=0.0,longi=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_location);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("test","Oncreate");
        // Define the criteria how to select the location provider
        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
        mCriteria.setCostAllowed(false);

        // get the best provider depending on the criteria
        mProvider = mLocationManager.getBestProvider(mCriteria, false);
        mListener = new MyLocationListener();
        mProviderStatus = PROVIDER_STATUS.BOTH_AVAILABLE;
        mProviderUnavailableDialogDisplayed = false;

        // location updates: at least 1 unit (depending on accuracy)
        mLocationManager.requestLocationUpdates(mProvider, INTERVAL, 1, mListener);
//        p1=new ProgressDialog(LocationFromMap.this);
//
//        if(location==null) {
//            location[0] = 1000.0;
//            location[1] = 1000.0;
//        }
//
//        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

//        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            p1.setCancelable(true);
//            p1.setTitle("Getting location...");
//            p1.show();
//
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
//                LocationFromMap.location[0] = location.getLatitude();
//                LocationFromMap.location[1] = location.getLongitude();
//                Log.i("Lat", String.valueOf(LocationFromMap.location[0]));
//                Log.i("Long", String.valueOf(LocationFromMap.location[1]));
//                Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
//                if (p1.isShowing()) {
//                    p1.hide();
//                    p1.dismiss();
//                }
//
//                locationChanged=true;
//
//                Message msg = new Message();
//                msg.arg1=1;
//                locUpdateHandler.sendMessage(msg);
//
//                finish();
//            }
//            else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            }

            /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LocationFromMap.location[0] = location.getLatitude();
                    LocationFromMap.location[1] = location.getLongitude();
                    Log.i("Lat", String.valueOf(LocationFromMap.location[0]));
                    Log.i("Long", String.valueOf(LocationFromMap.location[1]));
                    Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));

                    // locationManager.removeUpdates(myLocationListener);

                    if (p1.isShowing()) {
                        p1.hide();
                        p1.dismiss();
                    }

                    locationChanged=true;

                    Message msg = new Message();
                    msg.arg1=1;
                    locUpdateHandler.sendMessage(msg);

                    finish();

                    *//*if(locationChanged==true){
                        if(gmap!=null)
                            gmap.addMarker(new MarkerOptions()
                                    .position(new LatLng(LocationFromMap.location[0],LocationFromMap.location[1]))
                                    .title("Marker"));
                    }*//*
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
            });*/

        }
//        if(!LoginActivity.prefs.getString("Latitude","").equals("")&&!LoginActivity.prefs.getString("Longitude","").equals("")) {
//            location[0] = Double.parseDouble(LoginActivity.prefs.getString("Latitude",""));
//            location[1] = Double.parseDouble(LoginActivity.prefs.getString("Longitude",""));
//
//            Log.i("Latitude", String.valueOf(location[0]));
//            Log.i("Longitude", String.valueOf(location[1]));
//        }
//
//        if(!p1.isShowing()) {
//            mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
//            mapfragment.getMapAsync(this);
//        }
    //}

    @Override
 //   public void onMapReady(final GoogleMap map){
//
//        Log.i("Location[0]",String.valueOf(LocationFromMap.location[0]));
//        Log.i("Location[1]", String.valueOf(LocationFromMap.location[1]));
//
//        if((LocationFromMap.location[0]==1000.0&&LocationFromMap.location[1]==1000.0) || (LocationFromMap.location[0]==0.0&&LocationFromMap.location[1]==0.0)){
//
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(12.9915, 80.2336))
//                    .title("Marker"));
//            Log.i("Latitude","12.9915");
//            Log.i("Longitude","80.2336");
//
//        }
//
//        else{
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(LocationFromMap.location[0], LocationFromMap.location[1]))
//                    .title("Marker"));
//        }
//
//
//        locUpdateHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                if (msg.arg1 == 1) {
//                    map.addMarker(new MarkerOptions()
//                            .position(new LatLng(LocationFromMap.location[0],LocationFromMap.location[1]))
//                            .title("Marker"));
//                    CameraUpdateFactory.newLatLng(new LatLng(LocationFromMap.location[0], LocationFromMap.location[1]));
//
//                }
//            }
//        };
//        map.setIndoorEnabled(false);
//        map.setBuildingsEnabled(true);
//
//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
//                    map.clear();
//                    map.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title("Marker"));
//                    LocationFromMap.location[0] = latLng.latitude;
//                    LocationFromMap.location[1] = latLng.longitude;
//
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();
//
//                }
//            }
//        });

//        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
//                    map.clear();
//                    map.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title("Marker"));
//
//                    LocationFromMap.location[0] = latLng.latitude;
//                    LocationFromMap.location[1] = latLng.longitude;
//
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();
//
//                    Message msg = new Message();
//                    msg.arg1=1;
//                    msg.arg2=1;
//                    Master.locationHandler.sendMessage(msg);
//
//                    if(Master.locationDialog!=null && Master.locationDialog.isShowing()) {
//                        Master.locationDialog.hide();
//                        Master.locationDialog.dismiss();
//                    }
//
//                    finish();
//
//                }
//            }
//        });

//        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDragStart(Marker marker) {
//
//            }
//
//            @Override
//            public void onMarkerDrag(Marker marker) {
//
//            }
//
//            @Override
//            public void onMarkerDragEnd(Marker marker) {
//                if (MainActivity.internetConnection.isConnectingToInternet() == true) {
//                    map.clear();
//                    map.addMarker(new MarkerOptions()
//                            .position(marker.getPosition())
//                            .title("Marker"));
//
//                    LocationFromMap.location[0] = marker.getPosition().latitude;
//                    LocationFromMap.location[1] = marker.getPosition().longitude;
//
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).apply();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).apply();
//                    LoginActivity.prefs.edit().putString("Latitude",String.valueOf(location[0])).commit();
//                    LoginActivity.prefs.edit().putString("Longitude",String.valueOf(location[1])).commit();
//
//                }
//            }
//
//        });
//    }


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

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
		/*Toast.makeText(LocationService.this, "In LocationService onBind function",
				Toast.LENGTH_SHORT).show();*/
        return null;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//		Toast.makeText(LocationFromMap.this, "In LocationService onStartCommand function",
//				Toast.LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
//    }
    public void onDestroy() {
        mLocationManager.removeUpdates(mListener);

        super.onDestroy();
    };
//    public void onLocationChanged(Location location) {
//        if(location!=null){
//            locationManager.removeUpdates(this);
//            LocationFromMap.location[0] = location.getLatitude();
//            LocationFromMap.location[1] = location.getLongitude();
//            Log.i("Lat", String.valueOf(LocationFromMap.location[0]));
//            Log.i("Long", String.valueOf(LocationFromMap.location[1]));
//            Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
//
//            // locationManager.removeUpdates(myLocationListener);
//
//            if (p1.isShowing()) {
//                p1.hide();
//                p1.dismiss();
//            }
//
//            locationChanged=true;
//
//            Message msg = new Message();
//            msg.arg1=1;
//            locUpdateHandler.sendMessage(msg);
//
//            finish();
//        }
//    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

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
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lat=location.getLatitude();
            longi = location.getLongitude();
            Log.d("test",lat + " " + longi);
            //phonetxt.setText("Latitude: "+String.valueOf(location.getLatitude()));
            //messagetxt.setText("Longitude: "+String.valueOf(location.getLongitude()));
            // provText.setText(mProvider + " provider has been selected.");

            Toast.makeText(LocationFromMap.this, "Location changed!",
             Toast.LENGTH_SHORT).show();
            String ns = Context.NOTIFICATION_SERVICE;
            //NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
            //int icon = R.drawable.icon;
            //CharSequence tickerText = "Your location has changed.Want to notify?";
            long when = System.currentTimeMillis();

//            Notification notification = new Notification(icon, tickerText, when);
//            Context context = getApplicationContext();
//            CharSequence contentTitle = "Notification";
//            CharSequence contentText = "Your location has changed.Want to notify?";
//            Intent notificationIntent = new Intent(LocUpdate.this, Map.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(LocUpdate.this, 0, notificationIntent, 0);
//
//            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            final int HELLO_ID = 1;
            //check=1;
            //mNotificationManager.notify(HELLO_ID, notification);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
			  Toast.makeText(getApplicationContext(), provider + "'s status changed to "+status +"!",
				        Toast.LENGTH_SHORT).show();
			  mProvider = mLocationManager.getBestProvider(mCriteria, false);
        }

        @Override
        public void onProviderEnabled(String provider) {
			  Toast.makeText(getApplicationContext(), "Provider " + provider + " enabled!",
		        Toast.LENGTH_SHORT).show();
//            mNotificationBuilder.setContentIntent(null);
//            set_notification(NOTIFICATION_ID.PROVIDER_ENABLED,
//                    "Provider " + provider + " enabled!",
//                    null);

            if (mProviderStatus == PROVIDER_STATUS.UNAVAILABLE)
                mProviderStatus = PROVIDER_STATUS.ONE_AVAILABLE;
            else if (mProviderStatus == PROVIDER_STATUS.ONE_AVAILABLE)
                mProviderStatus = PROVIDER_STATUS.BOTH_AVAILABLE;

            if (mProviderUnavailableDialogDisplayed == true)
                mProviderUnavailableDialogDisplayed = false;
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (mProviderStatus != PROVIDER_STATUS.UNAVAILABLE)
            {
				  Toast.makeText(getApplicationContext(), "Provider " + provider + " disabled!",
						  Toast.LENGTH_SHORT).show();
//                set_notification(NOTIFICATION_ID.PROVIDER_DISABLED,
//                        "Provider " + provider + " disabled!",
//                        null);

                if (mProvider == LocationManager.NETWORK_PROVIDER)
                    mProvider = LocationManager.GPS_PROVIDER;
                else mProvider = LocationManager.NETWORK_PROVIDER;

                if (mProviderStatus == PROVIDER_STATUS.BOTH_AVAILABLE)
                    mProviderStatus = PROVIDER_STATUS.ONE_AVAILABLE;
                else mProviderStatus = PROVIDER_STATUS.UNAVAILABLE;

                mLocationManager.requestLocationUpdates(mProvider, INTERVAL, 1, this);
            }
            else if (mProviderUnavailableDialogDisplayed == false)
            {
				  Toast.makeText(getApplicationContext(), "No providers available",
						  Toast.LENGTH_LONG).show();
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                PendingIntent settingsPendingIntent = PendingIntent.getActivity
                        (getApplicationContext(), 0, settingsIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
//                mNotificationBuilder.setContentTitle
//                        (getResources().getString(R.string.app_name) + " - No providers available");
//                set_notification(NOTIFICATION_ID.PROVIDER_DISABLED,
//                        "Touch to go to Location Settings",
//                        settingsPendingIntent);
                mProviderUnavailableDialogDisplayed = true;
            }
        }
    }

}
