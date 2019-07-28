package com.needapps.birds.birdua;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * About App Activity - first in 3 dots(More) menu
 */
public class AboutAppActivity extends AppCompatActivity {
    Toolbar aboutAppToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_about_app);
        //define Android Toolbar because we have theme noActionBar
        aboutAppToolbar = (Toolbar) findViewById(R.id.my_toolbar_about_app);
        aboutAppToolbar.setTitle(getResources().getString(R.string.settings_toolbar)); //get activity name
        setSupportActionBar(aboutAppToolbar);
        //add up/home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Starts email app to send a letter
     *
     * @param view - view
     */
    public void sendLetter(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // only email apps should handle this
        intent.setData(Uri.parse("mailto:appkolomyia@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Птахи України"); //subject
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Enables up/home button return to parent page
     *
     * @param item - selected item
     * @return super(parent) onOptionsItemSelected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }
}
