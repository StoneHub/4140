package fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpsc41400.a4140app.R;


/**
 * Created by monro on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

        return rootView;
    }

    public void noteLocationChange() {

    }

    public void getContacts() {

    }

}
