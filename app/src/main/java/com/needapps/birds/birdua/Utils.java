package com.needapps.birds.birdua;

import android.app.Activity;
import android.content.Intent;

/**
 * Utils sets Dark and Light themes of the Activity
 */
public class Utils {
    private static int theme;


    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    /**
     * Sets the theme of the Activity
     * restarts it by creating a new Activity
     */
    public static void changeToTheme(Activity activity, int theme) {
        Utils.theme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void setTheme(int t) {
        theme = t;
    }

    /**
     * Sets the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch (theme) {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }
}
