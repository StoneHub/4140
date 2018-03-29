package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.*;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;


/**
 * Created by monro on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {

    private AdapterView.OnItemClickListener onContactClickListener;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

       onContactClickListener = new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(),
                            "Clicked item from auto completion list "
                                    + adapterView.getItemAtPosition(i)
                            , Toast.LENGTH_SHORT).show();
                }
            };

       Button b = (Button) rootView.findViewById(R.id.changeLocBtn);
    b.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment =  new GmapFragment();
                replaceFragment(fragment);
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