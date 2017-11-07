package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 11/6/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public BaseActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public PlayerApi network(){
        return new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build().create(PlayerApi.class);
    }

    public void hideFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(frag);
        ft.commit();
    }

    public void showFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.show(frag);
        ft.commit();
    }

    public void hideView(View v){
        if(v != null)
            v.setVisibility(View.GONE);
    }

    public void showView(View v){
        if(v != null)
            v.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        View v = findViewById(R.id.FullscreenLoading);
        if(v != null)
            v.setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        View v = findViewById(R.id.FullscreenLoading);
        if(v != null)
            v.setVisibility(View.GONE);
    }

}
