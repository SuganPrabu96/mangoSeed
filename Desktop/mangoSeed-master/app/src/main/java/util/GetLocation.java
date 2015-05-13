package util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Suganprabu on 10-05-2015.
 */
public class GetLocation implements LocationListener {

    public double[] GetLocation(LocationManager locationManager){
        this.locationManager = locationManager;
        getLocation();
        return loc;
    }

    Location location;
    LocationManager locationManager;
    double[] loc = {-1000.0,-1000.0};

    private void getLocation(){
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000){
            loc[0] = location.getLatitude();
            loc[1] = location.getLongitude();
        }

        else locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,this);

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.i("Location", location.toString());
        locationManager.removeUpdates(this);
        loc[0] = location.getLatitude();
        loc[1] = location.getLongitude();
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
}
