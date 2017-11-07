package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import net.quachk.quachk.Fragments.Intents.FragmentIntents;

public class CredentialsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
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
        hideFragment(getFragmentManager().findFragmentById(R.id.LoginCredentials));
    }

    private void showLoginCredentials(){
        showFragment(getFragmentManager().findFragmentById(R.id.LoginCredentials));
    }

    private void hideSignupCredentials(){
        hideFragment(getFragmentManager().findFragmentById(R.id.SignupCredentials));
    }

    private void showSignupCredentials(){
        showFragment(getFragmentManager().findFragmentById(R.id.SignupCredentials));
    }

}
