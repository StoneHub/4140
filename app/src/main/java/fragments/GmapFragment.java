package fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.ResultSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by monro on 3/20/2018.
 */

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = GmapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(34.6834, -82.8374);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //Place marker on touch
    MarkerOptions marker = null;
    private Marker mailMarker;
    Random r = new Random();
    LatLng[] messageCoord = new LatLng[10];
    // double lat = .0030*r.nextDouble()+34.6804;
    //double lon = .0030*r.nextDouble()+82.8344;
    //MarkerOptions mailMarker = null;

    //pass arguments to ComposeFragment
    private static final String argKey = "argKey";
    private boolean markerExist = false;
    private boolean mailExist = false;
    private static View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
           if (view != null) {

            ViewGroup parent = (ViewGroup) view.getParent();

            if (parent != null)

                parent.removeView(view);

        }

        try {

            view = inflater.inflate(R.layout.fragment_gmap, container, false);

        } catch (android.view.InflateException e) {

        /* map is already there, just return view as it is */

        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Prompt the user for permission.
        getLocationPermission();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        //disable Map Toolbar
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //get location
        mLastKnownLocation = new Location(LocationManager.GPS_PROVIDER);
        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        //enable find current location button on map
        mMap.setMyLocationEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Bundle up the current latlng, and spin up a new Fragment with the passed arguments
                FragmentArgs();

            }
        });

        //Place marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public double randomLat(double lat){


                return(lat);
            }

            @Override
            public void onMapClick(LatLng latLng) {
                markerExist = true;
                mailExist = true;
                marker = new MarkerOptions();
                marker.position(latLng);
                marker.title(marker.getPosition().longitude + " : " + marker.getPosition().latitude);
                //clear previously touch position
                mMap.clear();
                mMap.addMarker(marker);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                //LatLng[] messageCoord = new LatLng[10];

                /*for(int x=0;x<10;x++){
                    //if(mailExist) {
                        double lat = .0030 * r.nextDouble() + 34.6804;
                        double lon = .0030 * r.nextDouble() + 82.8344;
                        messageCoord[x] = new LatLng(lat, -lon);
                    //}
                    //else {
                       // messageCoord[x] = new LatLng(lat, -lon);
                    //}
                    //}
                   // double lat = .0030*r.nextDouble()+34.6804;
                    //double lon = .0030*r.nextDouble()+82.8344;
                   // messageCoord[x] = new LatLng(lat,-lon);
                }*/

                for(int x=0; x< 10; x++) {
                    mailMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(messageCoord[x]).title("Sender of Msg"));
                }
            }
        });

        for(int x=0;x<10;x++){
            //if(mailExist) {
            double lat = .0200 * r.nextDouble() + 34.6634;
            double lon = .0200 * r.nextDouble() + 82.8174;
            messageCoord[x] = new LatLng(lat, -lon);
            //}
            //else {
            // messageCoord[x] = new LatLng(lat, -lon);
            //}
            //}
            // double lat = .0030*r.nextDouble()+34.6804;
            //double lon = .0030*r.nextDouble()+82.8344;
            // messageCoord[x] = new LatLng(lat,-lon);
        }


        //  LatLng[] messageCoord = new LatLng[10];
        //Random r = new Random();
        //for(int x=0;x<10;x++){
        //double lat = .0030*r.nextDouble()+34.6804;
        // double lon = .0030*r.nextDouble()+82.8344;
        //   messageCoord[x] = new LatLng(lat,-lon);
        // }

        for(int x=0; x< 10; x++) {
            mailMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .position(messageCoord[x]).title("Sender of Msg"));
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> locationResult) {
                        if (locationResult.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),  //mLastKnownLocation is Null here //Try finding your location in the Google Maps app first, then launch the PostIT app
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", locationResult.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }

                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }
        if (mLocationPermissionGranted) {} else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");
            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title("Defalut")
                    .position(mDefaultLocation)
                    .snippet("INFO SNNIPIT"));
            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void FragmentArgs(){
        //check for marker, if no marker is set use current location
        if (!markerExist) {
            double[] loc ={mLastKnownLocation.getLongitude(),mLastKnownLocation.getLatitude()};
            Bundle bundle = new Bundle();
            bundle.putSerializable(argKey, loc);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
        //use marker location
        else {
            double[] loc = {marker.getPosition().longitude, marker.getPosition().latitude};
            Bundle bundle = new Bundle();
            bundle.putSerializable(argKey, loc);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
    }

    public void replaceFragment(Fragment someFragment) {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
