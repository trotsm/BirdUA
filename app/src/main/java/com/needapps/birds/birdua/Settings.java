package com.needapps.birds.birdua;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    Switch mySwitch;
    private SharedPreferencesManager prefs; //to save theme state
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);

        prefs = new SharedPreferencesManager(this);//get SharedPreferencesManager  instance
        int radioChecked = prefs.retrieveInt("radio", R.id.radio_medium); //get stored checked radio button, medium is default
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_size);
        radioGroup.check(radioChecked); // check selected radiobutton

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(myToolbar);
        // add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Налаштування");
        }

        mySwitch = (Switch) findViewById(R.id.switch_theme);//switch button for changing theme
        boolean s = prefs.retrieveBoolean("switch", false);
        mySwitch.setChecked(s);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.storeBoolean("switch", true);
                    prefs.storeInt("theme", Utils.THEME_DARK);// if ON, set Dark theme
                    Utils.changeToTheme(Settings.this, Utils.THEME_DARK);
                    overridePendingTransition(0, 0);
                } else {
                    prefs.storeBoolean("switch", false);
                    prefs.storeInt("theme", Utils.THEME_DEFAULT); // if OFF, set Light theme
                    Utils.changeToTheme(Settings.this, Utils.THEME_DEFAULT);
                    overridePendingTransition(0, 0);
                }

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prefs.storeInt("radio", checkedId);// store state of checked radio button
                radioGroup.check(checkedId);
                switch (checkedId) {
                    case R.id.radio_small:
                        prefs.storeInt("size", 12);// set size in detail activity textView
                        break;
                    case R.id.radio_medium:
                        prefs.storeInt("size", 16);// set size in detail activity textView
                        break;
                    case R.id.radio_large:
                        prefs.storeInt("size", 20);// set size in detail activity textView
                        break;

                    default:
                        break;
                }
            }
        });

    }

    /**
     * allow back and up/home button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    // start MainActivity to reload theme
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

}
