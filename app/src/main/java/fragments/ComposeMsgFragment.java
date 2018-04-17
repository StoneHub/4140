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
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by monroe on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {

    private Button updateLocBtn;
    private Button cameraBtn;
    private String noteText;
    private String whoToText;

    Bundle locBundle;

    MapView mMapView;
    private GoogleMap googleMap;
    String senderID;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

        locBundle = getArguments().getBundle("bundlelocKey");
        Bundle senderBundle = getArguments().getBundle("bundlesenderKey");

        if (senderBundle != null) {
            senderID = senderBundle.getString("senderKey");
        }

        double[] loc = locBundle.getDoubleArray("locKey");
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
                if (noteText != null){
                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).position(latLng).title("Your Note!").snippet(noteText));
                }else{
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Post-IT here!").snippet("Say Something"));
                }
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),3000,null);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] contacts = getResources().getStringArray(R.array.msgNamesArray);
        MultiAutoCompleteTextView whoNum = getView().findViewById(R.id.whoNumTxt);
        if(senderID != null){
            whoNum.setText(senderID);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line ,contacts);
        whoNum.setAdapter(adapter);
        whoNum.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        final EditText noteTextView = getView().findViewById(R.id.noteText);
        final EditText whoToTextView = getView().findViewById(R.id.whoNumTxt);

        Button PostBtn = getView().findViewById(R.id.postBtn);
        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteTextView.getText().length() == 0) {
                    Toast.makeText(getActivity(),"No Note text! You must leave something!", Toast.LENGTH_LONG).show();
                    return;
                }
                noteText = noteTextView.getText().toString();
                if (whoToTextView.getText().length() == 0) {
                    Toast.makeText(getActivity(),"No Contact selected! You must leave the note for someone!", Toast.LENGTH_LONG).show();
                    return;
                }
                whoToText = whoToTextView.getText().toString();

                Bundle mainBundle = new Bundle();

                Bundle noteBundle = new Bundle();
                noteBundle.putSerializable("noteKey", noteText);

                Bundle whoBundle = new Bundle();
                whoBundle.putSerializable("whoBundle", whoToText);

                mainBundle.putBundle("noteBundle", noteBundle);
                mainBundle.putBundle("locBundle", locBundle);
                mainBundle.putBundle("receiverBundle", whoBundle);

                Fragment fragment = new GmapFragment();
                fragment.setArguments(mainBundle);
                replaceFragment(fragment);
            }
        });
    }

    public void replaceFragment(Fragment someFragment) {
   android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.content_frame, someFragment);
    transaction.addToBackStack(null);
    transaction.commit();
}
}