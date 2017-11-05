package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.quachk.quachk.CredentialsActivity;
import net.quachk.quachk.R;
import net.quachk.quachk.Fragments.Intents.FragmentIntents;


/**
 * Created by Elijah on 10/3/2017.
 */

public class LoginOptions extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.login_options, container, false);
        view.findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
        view.findViewById(R.id.SignupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignupActivity();
            }
        });
        return view;
    }

    private void openLoginActivity(){
        Intent i = new Intent(getActivity(), CredentialsActivity.class);
        Bundle b = new Bundle();
        b.putInt(FragmentIntents.FRAGMENT_KEY, FragmentIntents.LOGIN_CREDENTIALS);
        i.putExtras(b);
        startActivity(i);
    }

    private void openSignupActivity(){
        //TODO: Add signup activity
        Intent i = new Intent(getActivity(), CredentialsActivity.class);
        Bundle b = new Bundle();
        b.putInt(FragmentIntents.FRAGMENT_KEY, FragmentIntents.SIGNUP_CREDENTIALS);
        i.putExtras(b);
        startActivity(i);
    }
}
