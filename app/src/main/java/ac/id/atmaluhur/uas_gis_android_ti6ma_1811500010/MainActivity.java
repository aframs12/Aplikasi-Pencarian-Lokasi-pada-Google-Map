package ac.id.atmaluhur.uas_gis_android_ti6ma_1811500010;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.util.List;

import ac.id.atmaluhur.uas_gis_android_ti6ma_1811500010.ui.hospital.HospitalFragment;
import ac.id.atmaluhur.uas_gis_android_ti6ma_1811500010.ui.restaurant.RestaurantFragment;
import ac.id.atmaluhur.uas_gis_android_ti6ma_1811500010.ui.school.SchoolFragment;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    public Marker mCurrLocationMarker, mLastMarker =null;
    int PROXIMITY_RADIUS = 5000;
    public double latitude, longitude;
    Button btnSearch;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        if(!CheckGooglePlayService()){
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else{
            Log.d("onCreate", "Google Play Services available");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        mapFragment.getMapAsync(this);

        btnSearch = findViewById(R.id.B_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                if (view.getId()==R.id.B_search){
                    EditText tf_Location = (EditText) findViewById(R.id.TF_Location);
                    String location=tf_Location.getText().toString();
                    List<Address> addressList = null;
                    MarkerOptions markerOptions = new MarkerOptions();
                    try{
                        if(!location.equals("")){
                            Geocoder geocoder = new Geocoder(MainActivity.this);
                            addressList = geocoder.getFromLocationName(location, 5);

                        }
                        for (int i=0; i<addressList.size(); i++){
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            markerOptions.position(latLng);
                            markerOptions.title("YOUR SEARCH RESULT");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        mLastMarker =  mMap.addMarker(markerOptions);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        navView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            switch (menuItem.getItemId()){
                case R.id.navigation_restaurant:
                    restaurant();
                    fragment = new RestaurantFragment();
                    break;
                case R.id.navigation_school:
                    school();
                    fragment = new SchoolFragment();
                    break;
                case R.id.navigation_hospital:
                    hospital();
                    fragment = new HospitalFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment1, fragment).commit();
            return true;
        }
    };

    private boolean CheckGooglePlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result !=ConnectionResult.SUCCESS){
            if(googleAPI.isUserResolvableError(result)){
                googleAPI.getErrorDialog(this, result, 0).show();
            }
            return false;
        }
        return true;
    }
    public void school(){
        mMap.clear();
        String School = "school";
        Log.d("onClick", "Button is Clicked");
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }
        String url = getUrl(latitude, longitude, School);
        Object [] DataTransfer = new Object[2];
        DataTransfer[0]=mMap;
        DataTransfer[1]=url;
        Log.d("onClick", url);
        GetNearBy getNearbyPlaceData = new GetNearBy();
        getNearbyPlaceData.execute(DataTransfer);
        Toast.makeText(MainActivity.this, "Nearby School", Toast.LENGTH_LONG).show();
    }
    public void restaurant(){
        mMap.clear();
        String Restaurant = "restaurant";
        Log.d("onClick", "Button is Clicked");
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }
        String url = getUrl(latitude, longitude, Restaurant);
        Object [] DataTransfer = new Object[2];
        DataTransfer[0]=mMap;
        DataTransfer[1]=url;
        Log.d("onClick", url);
        GetNearBy getNearbyPlaceData = new GetNearBy();
        getNearbyPlaceData.execute(DataTransfer);
        Toast.makeText(MainActivity.this, "Nearby Restaurant", Toast.LENGTH_LONG).show();
    }
    public void hospital(){
        mMap.clear();
        String Hostpital = "hospital";
        Log.d("onClick", "Button is Clicked");
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }
        String url = getUrl(latitude, longitude, Hostpital);
        Object [] DataTransfer = new Object[2];
        DataTransfer[0]=mMap;
        DataTransfer[1]=url;
        Log.d("onClick", url);
        GetNearBy getNearbyPlaceData = new GetNearBy();
        getNearbyPlaceData.execute(DataTransfer);
        Toast.makeText(MainActivity.this, "Nearby Hospital", Toast.LENGTH_LONG).show();
    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PEMISSIONS_REQUEST_LOCATION);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PEMISSIONS_REQUEST_LOCATION);
            }
            return  false;
        }else{
            return true;
        }
    }

    public String getUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," +longitude);
        googlePlaceUrl.append("&radius="+ PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBWMCnR8wUXc0e-rYbLcbbQJQ7wz5Tbtms");
        Log.d("getUrl", googlePlaceUrl.toString());
        return (googlePlaceUrl.toString());
    }

    public  static  final int MY_PEMISSIONS_REQUEST_LOCATION=99;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]grantResults){
        switch (requestCode){
            case MY_PEMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length>0){
                    if(ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this, "permission denied",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        System.out.println(mCurrLocationMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        Toast.makeText(MainActivity.this, "Your Current Location", Toast.LENGTH_LONG).show();
        try{
            if(mGoogleApiClient != null){
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                Log.d("onLocationCurrent", "Removing Location Updates");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }else{
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    public void rc(View view) {
        school();
    }

}
