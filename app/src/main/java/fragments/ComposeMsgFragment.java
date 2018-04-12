package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
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
 * Created by monroe on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {

    private static final String argKey = "argKey";
    private Button updateLocBtn;
    private Button cameraBtn;

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
        updateLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =  new GmapFragment();
                replaceFragment(fragment);
            }
        });

        cameraBtn = rootView.findViewById(R.id.camBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Camera Feature Coming Soon!", Toast.LENGTH_SHORT).show();
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
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                // For dropping a marker at a point on the Map
                googleMap.addMarker(new MarkerOptions().position(latLng).title("PostIT here!").snippet(lat + lng));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),3000,null);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] contacts = getResources().getStringArray(R.array.senders);
        MultiAutoCompleteTextView whoNum = getView().findViewById(R.id.whoNumTxt);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line ,contacts);
        whoNum.setAdapter(adapter);
        whoNum.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    public void replaceFragment(Fragment someFragment) {
   android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.content_frame, someFragment);
    transaction.addToBackStack(null);
    transaction.commit();
}
}