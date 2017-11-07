package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.view.View;

import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;
import net.quachk.quachk.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 11/6/2017.
 */

public class BaseFragment extends Fragment {

    public BaseFragment(){}

    public PlayerApi network(){
        return new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build().create(PlayerApi.class);
    }

    public void showLoading(){
        View v = getActivity().findViewById(R.id.FullscreenLoading);
        if(v != null)
            v.setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        View v = getActivity().findViewById(R.id.FullscreenLoading);
        if(v != null)
            v.setVisibility(View.GONE);
    }
}
