package com.example.wilson.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by wilson on 2015/9/19.
 */
public class SimpleFragment extends Fragment {
    public static final String FRAG_ID = "FRAG_ID";

    private FragmentButtonClickListener listener = null;

    public static SimpleFragment newInstance(int fragId) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAG_ID, fragId);

        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_main, null);
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public interface FragmentButtonClickListener {
        public void onFragmentButtonClickListener();
    }
}
