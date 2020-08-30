package com.example.myapplication.Activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.TrackLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int RequestPermissionCode = 1;
    Button btn_location_send;
    LatLng latLng;
    LocationManager locationManager;
    /* access modifiers changed from: private */
    public GoogleMap mMap;
    TrackLocation trackLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.btn_location_send = (Button) findViewById(R.id.btn_location_send);
        this.btn_location_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent =  MapsActivity.this.getIntent();
                StringBuilder sb = new StringBuilder();
                sb.append("http://maps.google.com/maps?q=");
                sb.append( MapsActivity.this.latLng.latitude);
                sb.append(",");
                sb.append( MapsActivity.this.latLng.longitude);
                intent.putExtra("latlong_key", sb.toString());
                 MapsActivity.this.setResult(-1, intent);
                 MapsActivity.this.finish();
            }
        });
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Log.d("maps12", "1" + this.locationManager.isProviderEnabled("network"));
            if (this.locationManager.isProviderEnabled("network")) {
                this.locationManager.requestLocationUpdates("network", 0, 0.0f, new LocationListener() {
                    public void onLocationChanged(Location location) {
                        Double lat = Double.valueOf(location.getLatitude());
                        Double lng = Double.valueOf(location.getLongitude());
                         MapsActivity.this.latLng = new LatLng(lat, lng.doubleValue());
                        Log.d("maps12", "2" + latLng);
                        try {
                             MapsActivity.this.mMap.addMarker(new MarkerOptions().position( MapsActivity.this.latLng).title(((Address) new Geocoder( MapsActivity.this).getFromLocation( MapsActivity.this.latLng.latitude,  MapsActivity.this.latLng.longitude, 1).get(0)).getAddressLine(0)));
                             MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( MapsActivity.this.latLng, 15.0f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    public void onProviderEnabled(String s) {
                    }

                    public void onProviderDisabled(String s) {
                    }
                });
            } else if (this.locationManager.isProviderEnabled("gps")) {
                Log.d("maps12", "3" + this.locationManager.isProviderEnabled("gps"));
                this.locationManager.requestLocationUpdates("gps", 0, 0.0f, new LocationListener() {
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double logitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, logitude);
                        try {
                            List<Address> addressList = new Geocoder( MapsActivity.this.getApplicationContext()).getFromLocation(latitude, logitude, 1);
                            StringBuilder sb = new StringBuilder();
                            sb.append(((Address) addressList.get(0)).getLocality());
                            sb.append(",");
                            String str = sb.toString();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(str);
                            sb2.append(((Address) addressList.get(0)).getCountryName());
                             MapsActivity.this.mMap.addMarker(new MarkerOptions().position(latLng).title(sb2.toString()));
                             MapsActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    public void onProviderEnabled(String s) {
                    }

                    public void onProviderDisabled(String s) {
                    }
                });
            }
            return;
        }
        requestPermission();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpMap();
    }
    private void setUpMap() {
        this.trackLocation = new TrackLocation(this);
        Log.d("maps12","5"+trackLocation);
        try {
            new LatLng(this.trackLocation.getLatitude(), this.trackLocation.getLongitude());
            Geocoder geocoder = new Geocoder(this);
            this.latLng = new LatLng(this.trackLocation.getLatitude(), this.trackLocation.getLongitude());
            this.mMap.addMarker(new MarkerOptions().position(this.latLng).title(((Address) geocoder.getFromLocation(this.latLng.latitude, this.latLng.longitude, 1).get(0)).getAddressLine(0)));
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 15.0f));
            if (this.locationManager.isProviderEnabled("network")) {
                if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    this.locationManager.requestLocationUpdates("network", 0, 0.0f, new LocationListener() {
                        public void onLocationChanged(Location location) {
                            Double lat = Double.valueOf(location.getLatitude());
                            Double lng = Double.valueOf(location.getLongitude());
                             MapsActivity.this.latLng = new LatLng(lat.doubleValue(), lng.doubleValue());
                            Log.d("maps12","6"+latLng);
                            try {
                                 MapsActivity.this.mMap.addMarker(new MarkerOptions().position( MapsActivity.this.latLng).title(((Address) new Geocoder( MapsActivity.this).getFromLocation( MapsActivity.this.latLng.latitude,  MapsActivity.this.latLng.longitude, 1).get(0)).getAddressLine(0)));
                                 MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( MapsActivity.this.latLng, 15.0f));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onStatusChanged(String s, int i, Bundle bundle) {
                        }

                        public void onProviderEnabled(String s) {
                        }

                        public void onProviderDisabled(String s) {
                        }
                    });
                }
            } else if (this.locationManager.isProviderEnabled("gps")) {
                Log.d("maps12","7"+this.locationManager.isProviderEnabled("gps"));
                this.locationManager.requestLocationUpdates("gps", 0, 0.0f, new LocationListener() {
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double logitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, logitude);
                        try {
                            List<Address> addressList = new Geocoder( MapsActivity.this.getApplicationContext()).getFromLocation(latitude, logitude, 1);
                            StringBuilder sb = new StringBuilder();
                            sb.append(((Address) addressList.get(0)).getLocality());
                            sb.append(",");
                            String str = sb.toString();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(str);
                            sb2.append(((Address) addressList.get(0)).getCountryName());
                            MapsActivity.this.mMap.addMarker(new MarkerOptions().position(latLng).title(sb2.toString()));
                            MapsActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    public void onProviderEnabled(String s) {
                    }

                    public void onProviderDisabled(String s) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean CoarseLocation = false;
        Log.d("maps12","8"+requestCode);
        if (requestCode != 1) {
            if (requestCode == 4 && grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] == 0;
                if (grantResults[1] == 0) {
                    CoarseLocation = true;
                }
                if (!StoragePermission || !CoarseLocation) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    return;
                }
                setUpMap();
                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            }
        } else if (grantResults.length > 0) {
            boolean StoragePermission2 = grantResults[0] == 0;
            if (grantResults[1] == 0) {
                CoarseLocation = true;
            }
            if (!StoragePermission2 || !CoarseLocation) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                return;
            }
            setUpMap();
           // Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
        }
    }

}
