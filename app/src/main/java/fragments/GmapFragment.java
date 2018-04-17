package fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

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

import java.util.Random;

/**
 * Created by monro on 3/20/2018.
 */

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = GmapFragment.class.getSimpleName();
    private GoogleMap mMap;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(34.6834, -82.8374); //clemson DT
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private int numOfUnreadNotes = 2;

    //Place marker on touch
    private MarkerOptions marker = null;
    private MarkerOptions myNoteMarker;
    private Marker mailMarker;
    Random r = new Random();
    LatLng[] messageCoord = new LatLng[10];

    //pass arguments to ComposeFragment
    private static final String argKey = "argKey";
    private boolean markerExist = false;
    private boolean mailExist = false;
    private static View view;
    private String note;
    private String receiver;
    private double[] loc = new double[2];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gmap, container, false);

        Bundle bundle = getArguments();
        if (bundle !=null){
            if (bundle.containsKey("locBundle")){
                loc = bundle.getBundle("locBundle").getDoubleArray("locKey");
            }
           if (bundle.containsKey("noteBundle")){
               note = bundle.getBundle("noteBundle").getString("noteKey");
           }
           if (bundle.containsKey("receiverBundle")){
                receiver = bundle.getBundle("receiverBundle").getString("whoBundle");
           }
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
        if(savedInstanceState!=null){
            loc = savedInstanceState.getDoubleArray(KEY_LOCATION);
            Toast.makeText(getActivity(),"Location Restored",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putSerializable(KEY_LOCATION, loc);
            super.onSaveInstanceState(outState);
        }
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

        if (ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        }

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        final Animation btnAnim = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        btnAnim.setInterpolator(interpolator);
        fab.startAnimation(btnAnim);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(btnAnim);
                //Bundle up the current latlng, and spin up a new Fragment with the passed arguments
                composeNoteFragmentSwitcher();
            }
        });

        //load arguments of a placed note
        if (loc != null){
            LatLng myNoteLatLng = new LatLng(loc[1],loc[0]);
            myNoteMarker = new MarkerOptions();
            myNoteMarker.position(myNoteLatLng);

            myNoteMarker.title("Your Note to: " + receiver);
            myNoteMarker.snippet(note);
            myNoteMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            mMap.addMarker(myNoteMarker);
        }

        //Place marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getActivity(), "Press me to Compose a Note", Toast.LENGTH_SHORT).show();
                markerExist = true;
                marker = new MarkerOptions();
                marker.position(latLng);
                marker.title("PostIT here!");
                mMap.clear();

                //rebuild placed notes real fast
                if (loc != null){
                    LatLng myNoteLatLng = new LatLng(loc[1],loc[0]);
                    myNoteMarker = new MarkerOptions();
                    myNoteMarker.position(myNoteLatLng);

                    myNoteMarker.title("Your Note to: " + receiver);
                    myNoteMarker.snippet(note);
                    myNoteMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    mMap.addMarker(myNoteMarker);
                }
                String[] contacts = getResources().getStringArray(R.array.msgNamesArray);
                for(int x = 0; x < numOfUnreadNotes; x++) {
                    mailMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .position(messageCoord[x]).title(contacts[x]).snippet("Unread Note"));
                }
                mMap.addMarker(marker);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                fab.startAnimation(btnAnim);
            }
        });
    }

    private void leaveRandomNotes() {
        for(int x=0;x < numOfUnreadNotes;x++){
            double lat = 0.0200 * r.nextDouble() + mLastKnownLocation.getLatitude();
            double lon = .0200 * r.nextDouble() + mLastKnownLocation.getLongitude();
            messageCoord[x] = new LatLng(lat, lon);
        }

        String[] contacts = getResources().getStringArray(R.array.msgNamesArray);
        for(int x = 0; x < numOfUnreadNotes; x++) {
            mailMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(messageCoord[x]).title(contacts[x]).snippet("Unread Note"));
        }
    }

    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            //LEAVE RANDOM UNREAD NOTES
                            leaveRandomNotes();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
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
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity(),FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void composeNoteFragmentSwitcher(){
        Bundle bundleBundle = new Bundle();
        //check for marker, if no marker is set use current location
        if (!markerExist) {
            loc[0] = mLastKnownLocation.getLatitude();
            loc[1] = mLastKnownLocation.getLongitude();
            Bundle bundle = new Bundle();
            bundle.putSerializable("locKey", loc);
            bundleBundle.putBundle("bundlelocKey",bundle);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundleBundle);
            replaceFragment(fragment);
        }
        else { //use marker location
            double[] loc = {marker.getPosition().longitude, marker.getPosition().latitude};
            Bundle bundle = new Bundle();
            bundle.putSerializable("locKey", loc);
            bundleBundle.putBundle("bundlelocKey",bundle);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundleBundle);
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