package com.needapps.birds.birdua;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

/**
 * Settings is functionality from Overflow(3 dots) menu
 * Changes theme and text size
 */
public class Settings extends AppCompatActivity {
    Switch switchTheme;
    private SharedPreferencesManager prefs; //to save theme state
    Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);

        prefs = new SharedPreferencesManager(this);// get SharedPreferencesManager instance
        // get stored checked radio button for text size, medium is default
        int radioChecked = prefs.retrieveInt("radio", R.id.radio_medium);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_size);
        radioGroup.check(radioChecked); // check selected radiobutton

        settingsToolbar = (Toolbar) findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(settingsToolbar);
        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }
        // switch button to change app theme
        switchTheme = (Switch) findViewById(R.id.switch_theme);
        boolean s = prefs.retrieveBoolean("switch", false);
        switchTheme.setChecked(s);

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.storeBoolean("switch", true);
                    // if ON, set Dark theme
                    prefs.storeInt("theme", Utils.THEME_DARK);
                    Utils.changeToTheme(Settings.this, Utils.THEME_DARK);
                    overridePendingTransition(0, 0);
                } else {
                    prefs.storeBoolean("switch", false);
                    // if OFF, set Light theme
                    prefs.storeInt("theme", Utils.THEME_DEFAULT);
                    Utils.changeToTheme(Settings.this, Utils.THEME_DEFAULT);
                    overridePendingTransition(0, 0);
                }
            }
        });
        // radio button to change text size
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prefs.storeInt("radio", checkedId);// store state of checked radio button
                radioGroup.check(checkedId);
                switch (checkedId) {
                    case R.id.radio_small:
                        prefs.storeInt("size", 12); // set size in detail activity textView
                        break;
                    case R.id.radio_medium:
                        prefs.storeInt("size", 16); // set size in detail activity textView
                        break;
                    case R.id.radio_large:
                        prefs.storeInt("size", 20); // set size in detail activity textView
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Allows back and up/home button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts MainActivity to reload theme
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
