package net.quachk.quachk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.Player;

/**
 * Created by Elijah on 10/2/2017.
 */

public class App {
    public static Game GAME;

    private static Context CONTEXT;
    private static Activity ACTIVITY;
    private static int LOAD_STATUS = 0;
    private static boolean LOCATION_ENABLED = false;
    private static boolean LOCATION_PERMISSION_DIALOG_OPEN = false;
    private static boolean INTERNET_ENABLED = false;
    private static boolean INTERNET_PERMISSION_DIALOG_OPEN = false;

    public static void setContext(Context ctxt){
        CONTEXT = ctxt;
    }

    public static Context getContext(){
        return CONTEXT;
    }

    public static void setActivity(Activity act){
        ACTIVITY = act;
        CONTEXT = act;
    }

    public static Activity getActivity(){ return ACTIVITY; }

    public static int getLoadStatus(){
        LOAD_STATUS = 0;
        if(Permissions.INITIALIZED)
            LOAD_STATUS += 5;
        if(Permissions.hasPermission(Permissions.INTERNET))
            LOAD_STATUS += 45;
        if(Permissions.hasPermission(Permissions.LOCATION))
            LOAD_STATUS += 50;
        return LOAD_STATUS;
    }

    public static void setLocationEnabled(boolean enabled){ LOCATION_ENABLED = enabled; }

    public static boolean isLocationEnabled(){ return LOCATION_ENABLED; }

    public static void setInternetEnabled(boolean enabled){ INTERNET_ENABLED = enabled; }

    public static boolean isInternetEnabled(){ return INTERNET_ENABLED; }

    public static void setPermissionDialogOpen(int permission, boolean open){
        switch(permission) {
            case Permissions.LOCATION:
                LOCATION_PERMISSION_DIALOG_OPEN = open;
            case Permissions.INTERNET:
                INTERNET_PERMISSION_DIALOG_OPEN = open;
        }
    }

    public static boolean isPermissionDialogOpen(int permission){
        switch(permission) {
            case Permissions.LOCATION:
                return LOCATION_PERMISSION_DIALOG_OPEN;
            case Permissions.INTERNET:
                return INTERNET_PERMISSION_DIALOG_OPEN;
        }
        return false;
    }
}
