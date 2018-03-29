package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by monro on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {

    private AdapterView.OnItemClickListener onContactClickListener;
    private static final String argKey = "argKey";
    private Button updateLocBtn;

    MapView mMapView;
    private GoogleMap googleMap;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

        double[] loc = getArguments().getDoubleArray(argKey);
        final String lat = Double.toString(loc[1]);
        final String lng = Double.toString(loc[0]);
        final LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));



        updateLocBtn = rootView.findViewById(R.id.changeLocBtn);
        String latlng = lat + " : " +lng;
        updateLocBtn.setText(latlng);
        updateLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =  new GmapFragment();
                replaceFragment(fragment);
            }
        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For dropping a marker at a point on the Map
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),3000,null);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView actv = getView().findViewById(R.id.whoNo);
        actv.setOnItemClickListener(onContactClickListener);





    }

    public void replaceFragment(Fragment someFragment) {
   android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.content_frame, someFragment);
    transaction.addToBackStack(null);
    transaction.commit();
}







}