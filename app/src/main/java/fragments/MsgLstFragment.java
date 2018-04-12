package fragments;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpsc41400.a4140app.R;

/**
 * Created by monro on 3/21/2018.
 */

public class MsgLstFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msglst, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] contacts = getResources().getStringArray(R.array.msgNamesArray);
        TypedArray contactID = getResources().obtainTypedArray(R.array.sender_id_array);
        int count = contactID.length();
        int[] ids = new int[count];
        for (int i=0; i < ids.length; i++){
            ids[i] = contactID.getResourceId(i,0);
        }
        contactID.recycle();

        for (int i=0; i < ids.length; i++){
            TextView senderTxtView = getView().findViewById(ids[i]);
            senderTxtView.setText(contacts[i]);
        }
    }
}