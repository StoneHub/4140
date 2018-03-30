package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpsc41400.a4140app.MyAdapter;
import com.cpsc41400.a4140app.R;

/**
 * Created by monro on 3/21/2018.
 */

public class MsgLstFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msglst, container, false);

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.my_activity);
//        mRecyclerView = (RecyclerView) findViewById(R.id.activity_sms_detailed_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
      //  mLayoutManager = new LinearLayoutManager(getActivity());
     //   mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
       // mAdapter = new MyAdapter();
//        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }


}