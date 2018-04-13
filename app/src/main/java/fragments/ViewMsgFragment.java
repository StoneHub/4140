package fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ViewMsgFragment extends Fragment {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private int cID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.single_msg_chat_view, container, false);

        //get get arguments from msgLstFragment when called
        cID = getArguments().getInt("argKey");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        TextView noteView = getView().findViewById(R.id.message);
        TextView senderView = getView().findViewById(R.id.sender);
        noteView.setAllCaps(false);
        senderView.setAllCaps(false);
        noteView.setMovementMethod(new ScrollingMovementMethod());
        TypedArray contactListID = getResources().obtainTypedArray(R.array.contact_id_array);
        String[] notes = getResources().getStringArray(R.array.msg_contents_array);
        String[] contacts = getResources().getStringArray(R.array.msgNamesArray);

        //build an array of ints that hold the R.id numbers for each contact block
        int[] contactListIds = new int[contactListID.length()];
        for (int i = 0; i < contactListID.length(); i++) {
            contactListIds[i] = contactListID.getResourceId(i, 0);
        }

        final Bundle senderBundle = new Bundle();

        for (int i = 0; i < contactListIds.length; i++) {
            if (contactListIds[i] == cID) {
                noteView.setText(notes[i]);
                senderView.setText(contacts[i]);
                senderBundle.putSerializable("senderKey",contacts[i]);

            }
        }



        TextView time = getView().findViewById(R.id.time);
        time.setText(R.string.randomDate);

        Toast.makeText(getActivity(), R.string.msgViewReply , Toast.LENGTH_LONG).show();
        final FloatingActionButton fab = getActivity().findViewById(R.id.fab2);
        final Animation btnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(.02, 20);
        btnAnim.setInterpolator(interpolator);
        fab.startAnimation(btnAnim);

        RelativeLayout relativeLayout = getView().findViewById(R.id.layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.msgViewReply, Toast.LENGTH_LONG).show();
                fab.startAnimation(btnAnim);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(btnAnim);
                //Bundle up the current latlng, and spin up a new Fragment with the passed arguments
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), R.string.pfailed, Toast.LENGTH_SHORT).show();
                    return;
                }
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Bundle mainBundle = new Bundle();

                            mLastKnownLocation = task.getResult();
                            double[] loc = {mLastKnownLocation.getLongitude(),mLastKnownLocation.getLatitude()};
                            Bundle locBundle = new Bundle();
                            locBundle.putSerializable("locKey", loc);


                            mainBundle.putBundle("bundlelocKey", locBundle);
                            mainBundle.putBundle("bundlesenderKey", senderBundle);

                            Fragment fragment = new ComposeMsgFragment();
                            fragment.setArguments(mainBundle);
                            replaceFragment(fragment);
                        }
                    }
                });
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
