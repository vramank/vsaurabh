package com.aidor.secchargemobile.seccharge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;


@SuppressWarnings("ALL")
public class GPSTracker extends Service implements LocationListener {
    Context mContext;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    boolean canGetLoaction = false;

    Location location;

    double lattitude;
    double longitide;

    // THE MINIMUM DISTANCE TO CHANGE UPDATES IN METERS
    public static final long MIN_DISTANCE_CANGE_FOR_UPDATES = 100;
    // THE MINIMUM TIME BETWEEN UPDATES IN MILLISECONDS
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    //DECLARE LOCATION MANAGER
    LocationManager locationManager;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //GET GPS STATUS
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //GETTING NETWORK STATUS
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable && !isNetworkEnable) {
                //NO NETWORK PROVIDER IS ENABLE
            } else {
                this.canGetLoaction = true;
                //FIRST GET LOCATOIN FROM N/W PROVIDER
                if (isNetworkEnable) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            lattitude = location.getLatitude();
                            longitide = location.getLongitude();
                        }
                    }
                }
                //IF GPS ENABLE GET LONGITUDE AND LATTITUDE
                if (isGPSEnable) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                longitide = location.getLongitude();
                                lattitude = location.getLatitude();
                            }else {
                                getLocation();
                            }
                        }

                    }
                }
            }


        } catch (Exception ex) {
        }
        return location;
    }

    /**
     * STOP USING GPS LISTENER
     * CALLING THIS FUNCTION WILL STOP USING GPS IN YOUR APP
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /*FUNCTION TO GET LATTITUDE*/
    public double getLattitude() {
        if (location != null) {
            lattitude = location.getLatitude();
        }
        return lattitude;
    }
    /*FUNCTION TO GET LONGITUDE */
    public  double getLongitide(){
        if(location != null){
            longitide = location.getLongitude();
        }
        return  longitide;
    }
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLoaction(){
        return  this.canGetLoaction;
    }

    @Override
    public void onLocationChanged(Location location) {
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
