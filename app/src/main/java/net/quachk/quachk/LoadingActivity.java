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

public class LoadingActivity extends BaseActivity {

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
        setContentView(R.layout.activity_loading);

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

    private void hideProgress(){
        hideView(findViewById(R.id.LoadingProgress));
    }

    private void showProgress(){
        showView(findViewById(R.id.LoadingProgress));
    }

    private void hideLoginOptions(){
        hideFragment(getFragmentManager().findFragmentById(R.id.LoginOptions));
    }

    private void showLoginOptions(){
        showFragment(getFragmentManager().findFragmentById(R.id.LoginOptions));
    }
}
