package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LoadingActivity extends AppCompatActivity {

    Handler loadingHandler = new Handler();
    Runnable loadingRunnable = new Runnable() {
        @Override
        public void run() {
            if(App.getLoadStatus() >= 100){
                hideProgress();
                showLoginOptions();
                return;
            }
            if(App.getLoadStatus() < 100 && Permissions.INITIALIZED)
            {
                hideProgress();
            }
            loadingHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        hideLoginOptions();

        App.setActivity(this);
        Permissions.initialize();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadingHandler.postDelayed(loadingRunnable, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        loadingHandler.removeCallbacks(loadingRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void hideProgress(){
        findViewById(R.id.LoadingProgress).setVisibility(View.GONE);
    }

    private void showProgress(){
        findViewById(R.id.LoadingProgress).setVisibility(View.VISIBLE);
    }

    private void hideLoginOptions(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.LoginOptions);
        ft.hide(f);
        ft.commit();
    }

    private void showLoginOptions(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.LoginOptions);
        ft.show(f);
        ft.commit();
    }
}
