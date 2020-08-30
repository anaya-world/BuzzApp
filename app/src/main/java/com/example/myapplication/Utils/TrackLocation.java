package com.example.myapplication.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TrackLocation extends Service implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;

    private static final long MIN_TIME_BW_UPDATES = 60000L;

    private boolean checkGPS;

    private boolean checkNetwork;

    private double latitude;

    private Location loc;

    private LocationManager locationManager;

    private double longitude;

    Context mContext;
    Location location;

    public TrackLocation(Context paramContext) {
        this.mContext = paramContext;
        getLocation();
    }

    private Location getLocation() {
        try {
            this.locationManager = (LocationManager)this.mContext.getSystemService("location");
            this.checkGPS = this.locationManager.isProviderEnabled("gps");
            this.checkNetwork = this.locationManager.isProviderEnabled("network");
            if (this.checkGPS || this.checkNetwork) {
                boolean bool = this.checkNetwork;
                if (bool)
                    try {
                        this.locationManager.requestLocationUpdates("network", 60000L, 10.0F, this);
                        Log.d("Network", "Network");
                        if (this.locationManager != null)
                            this.loc = this.locationManager.getLastKnownLocation("network");
                        if (this.loc != null) {
                            this.latitude = this.loc.getLatitude();
                            this.longitude = this.loc.getLongitude();
                        }
                    } catch (SecurityException securityException) {}
            }
            if (this.checkGPS) {
                location = this.loc;
                if (location == null)
                    try {
                        this.locationManager.requestLocationUpdates("gps", 60000L, 10.0F, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (this.locationManager != null) {
                            this.loc = this.locationManager.getLastKnownLocation("gps");
                            if (this.loc != null) {
                                this.latitude = this.loc.getLatitude();
                                this.longitude = this.loc.getLongitude();
                            }
                        }
                    } catch (SecurityException location) {}
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return this.loc;
    }

    public double getLatitude() {
        if (this.loc != null)
            this.latitude = this.loc.getLatitude();
        return this.latitude;
    }

    public double getLongitude() {
        if (this.loc != null)
            this.longitude = this.loc.getLongitude();
        return this.longitude;
    }

    @Nullable
    public IBinder onBind(Intent paramIntent) { return null; }

    public void onLocationChanged(Location paramLocation) {}

    public void onProviderDisabled(String paramString) {}

    public void onProviderEnabled(String paramString) {}

    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
}
