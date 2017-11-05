package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import net.quachk.quachk.Fragments.Intents.FragmentIntents;

public class CredentialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_credentials);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideLoginCredentials();
        hideSignupCredentials();
        Bundle b = getIntent().getExtras();
        if(b != null)
            showIntentFragment(b.getInt(FragmentIntents.FRAGMENT_KEY));

    }

    private void showIntentFragment(int fragment){
        switch (fragment){
            case FragmentIntents.LOGIN_CREDENTIALS:
                showLoginCredentials();
                return;
            case FragmentIntents.SIGNUP_CREDENTIALS:
                showSignupCredentials();
                return;
        }
    }

    private void hideLoginCredentials(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.LoginCredentials);
        ft.hide(f);
        ft.commit();
    }

    private void showLoginCredentials(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.LoginCredentials);
        ft.show(f);
        ft.commit();
    }

    private void hideSignupCredentials(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.SignupCredentials);
        ft.hide(f);
        ft.commit();
    }

    private void showSignupCredentials(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentById(R.id.SignupCredentials);
        ft.show(f);
        ft.commit();
    }

}
