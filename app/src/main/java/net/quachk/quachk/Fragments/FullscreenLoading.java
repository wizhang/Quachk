package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.quachk.quachk.R;

/**
 * Created by Elijah on 10/6/2017.
 */

public class FullscreenLoading extends Fragment{

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fullscreen_loading, container, false);
        return view;
    }
}
