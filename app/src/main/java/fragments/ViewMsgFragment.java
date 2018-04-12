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


public class ViewMsgFragment extends Fragment {

    private static final String argKey = "argKey";
    int cID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.single_msg_chat_view, container, false);

        //get get arguments from msgLstFragment when called
        cID = getArguments().getInt(argKey);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView noteView = getView().findViewById(R.id.message);
        noteView.setAllCaps(false);
        TypedArray contactListID = getResources().obtainTypedArray(R.array.contact_id_array);
        String[] notes = getResources().getStringArray(R.array.msg_contents_array);


        int[] contactListIds = new int[contactListID.length()];
        for (int i=0; i < contactListID.length(); i++){
            contactListIds[i] = contactListID.getResourceId(i, 0);
        }

        for (int i=0; i < contactListIds.length; i++) {
            if(contactListIds[i] == cID){
                noteView.setText(notes[i]);
            }
        }




    }
}
