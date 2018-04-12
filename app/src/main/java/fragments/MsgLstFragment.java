package fragments;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
        String[] notes = getResources().getStringArray(R.array.msg_contents_array);

        TypedArray notesID = getResources().obtainTypedArray(R.array.msg_id_array);
        TypedArray contactID = getResources().obtainTypedArray(R.array.sender_id_array);
        TypedArray contactListID = getResources().obtainTypedArray(R.array.contact_id_array);

        int count = contactListID.length();
        int[] senderIds = new int[count];
        int[] msgIds = new int[count];
        int[] contactListIds = new int[count];
        for (int i=0; i < senderIds.length; i++){
            senderIds[i] = contactID.getResourceId(i,0);
            msgIds[i] = notesID.getResourceId(i,0);
            contactListIds[i] = contactListID.getResourceId(i,0);
        }
        contactID.recycle();
        notesID.recycle();
        contactListID.recycle();

        for (int i=0; i < senderIds.length; i++){
            TextView senderTxtView = getView().findViewById(senderIds[i]);
            senderTxtView.setAllCaps(false);
            senderTxtView.setText(contacts[i]);
        }
        for (int i =0; i < msgIds.length; i++){
            TextView msgTxtView = getView().findViewById(msgIds[i]);
            msgTxtView.setText(notes[i]);
        }

        for(int i=0; i < contactListIds.length; i++){
            RelativeLayout rl = (RelativeLayout)getView().findViewById(contactListIds[i]);
            rl.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Fragment fragment = new ViewMsgFragment();
//        fragment.setArguments(bundle);
                    replaceFragment(fragment);
                }
            });
        }



    }


public void replaceFragment(Fragment someFragment) {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        }
}