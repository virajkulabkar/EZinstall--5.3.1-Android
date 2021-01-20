package com.CL.slcscanner.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.Util;

/**
 * Created by vrajesh on 3/14/2018.
 */

public class ScanFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle state) {
/*
        ViewGroup vg= (ViewGroup) getActivity().findViewById(R.id.frm1);
        mScannerView = new ZXingScannerView(getActivity());
        vg.addView(mScannerView);*/
        view = inflater.inflate(R.layout.fragment_scan, null);

        view.findViewById(R.id.txtTest)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getActivity(), "Contents = ", Toast.LENGTH_SHORT).show();
                                            loadFragment(new PoleIdFragment());
                                        }
                                    }
                );

        return view;
    }
    private void loadFragment(Fragment fragment) {
        try {
            // create a FragmentManager
            FragmentManager fm = getChildFragmentManager();
            // create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            // replace the FrameLayout with new Fragment
            fragmentTransaction.replace(R.id.frm1, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit(); // save the changes
        }catch (Exception e){ }
    }

}