package co.edu.escuelaing.laboratoriomapas;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private final int ACCESS_LOCATION_PERMISSION_CODE = 0;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private TextView address;
    private FloatingActionButton floatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        address = (TextView) findViewById( R.id.address );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationRequest = LocationRequest.create();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mGoogleApiClient.connect();
        floatButton = findViewById(R.id.fab);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next2 = new Intent(MapsActivity.this,LocationMap.class);
                startActivity(next2);
            }
        });

        setLocationCoordinates();

    }

    private void setLocationCoordinates() {


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle extras = getIntent().getExtras();
        String name,description;
        Double longitude,latitude;
        System.out.println("entro1");
        if (extras != null) {
            System.out.println("entro2");
            name = extras.getString("Name");
            description = extras.getString("Description");
            longitude = Double.parseDouble(extras.getString("Longitude"));
            latitude = Double.parseDouble(extras.getString("Latitude"));
            System.out.println("entro3");
            System.out.println(longitude);
            System.out.println(latitude);
            LatLng location = new LatLng(longitude, latitude);
            mMap.addMarker(new MarkerOptions().position(location).title("Marker in "+name+" and "+description));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            System.out.println("entro4");
            // and get whatever type user account id is
        }else{
            showMyLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void showMyLocation() {
        if (mMap != null) {
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};
            if (hasPermissions(this, permissions)) {
                mMap.setMyLocationEnabled(true);

                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (lastLocation != null) {
                    addMarkerAndZoom(lastLocation, "My Location", 15);
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, ACCESS_LOCATION_PERMISSION_CODE);
            }
        }
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void addMarkerAndZoom(Location location, String title, int zoom) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == -1) {
                return;
            }
        }
        System.out.println(requestCode);
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_CODE:
                showMyLocation();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (hasPermissions(this, permissions)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showMyLocation();
                            stopLocationUpdates();
                        }
                    });
        }else {
            ActivityCompat.requestPermissions(this, permissions, ACCESS_LOCATION_PERMISSION_CODE);
        }


    }

    @Override
    public void onConnectionSuspended( int i )
    {
        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleApiClient, (PendingIntent) null);
    }

    public void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleApiClient, new LocationListener()
        {
            @Override
            public void onLocationChanged( Location location )
            {

            }
        } );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onFindAddressClicked( View view ) {
        startFetchAddressIntentService();
    }

    public void startFetchAddressIntentService()
    {
        @SuppressLint("MissingPermission") Location lastLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );
        if ( lastLocation != null )
        {
            AddressResultReceiver addressResultReceiver = new AddressResultReceiver( new Handler() );
            addressResultReceiver.setAddressResultListener( new AddressResultListener()
            {
                @Override
                public void onAddressFound( final String address )
                {
                    runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            MapsActivity.this.address.setText( address );
                            MapsActivity.this.address.setVisibility( View.VISIBLE );
                        }
                    } );


                }
            } );
            Intent intent = new Intent( this, FetchAddressIntentService.class );
            intent.putExtra( FetchAddressIntentService.RECEIVER, addressResultReceiver );
            intent.putExtra( FetchAddressIntentService.LOCATION_DATA_EXTRA, lastLocation );
            startService( intent );
        }
    }
}

