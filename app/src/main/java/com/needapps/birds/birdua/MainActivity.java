package com.needapps.birds.birdua;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.needapps.birds.birdua.fragments.AllFragment;
import com.needapps.birds.birdua.fragments.CoastalFragment;
import com.needapps.birds.birdua.fragments.ForestFragment;
import com.needapps.birds.birdua.fragments.PredatoryFragment;

/**
 * MainActivity creates tabs (fragments)
 * and Overflow (3 dots) menu
 */
public class MainActivity extends AppCompatActivity {
    // add Android Toolbar, TabLayout and ViewPager.
    Toolbar mainToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private SharedPreferencesManager prefs; //to save theme state
    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get SharedPreferencesManager  instance
        prefs = new SharedPreferencesManager(this);
        // get stored (chosen by user) theme, zero is default
        theme = prefs.retrieveInt("theme", 0);
        Utils.setTheme(theme);  // set the stored theme, light by default
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        // define Android Toolbar, TabLayout and ViewPager.
        // add Toolbar because we need tabLayout
        mainToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mainToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(mainToolbar);
        // define tabLayout
        tabLayout = findViewById(R.id.tab_layout);
        // define viewPager
        // viewPager helps with tabs in tabLayout
        viewPager = findViewById(R.id.view_pager);
        // define PageAdapter
        // viewPager helps with tabs scroll etc.
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        // add fragments tabs
        pageAdapter.AddFragment(new AllFragment(), getString(R.string.all_birds));
        pageAdapter.AddFragment(new ForestFragment(), getString(R.string.forest_steppe_birds));
        pageAdapter.AddFragment(new CoastalFragment(), getString(R.string.coastal_birds));
        pageAdapter.AddFragment(new PredatoryFragment(), getString(R.string.predatory_birds));
        // download all fragments at the same time once it is created
        // set tabs amount from getCount() cause by default it is 3
        viewPager.setOffscreenPageLimit(pageAdapter.getCount() - 1);
        // use pageAdapter as the adapter for Android ViewPager
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Creates overflow menu(3 dots) in toolbar
     * @param item - selected item
     * @return super(parent) onOptionsItemSelected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // first item in overflow menu - About app
            case R.id.action_about_app:
                // when clicked start new Activity
                Intent intent = new Intent(this, AboutAppActivity.class); // start new activity
                startActivity(intent);
                break;
            // second item in overflow menu - Settings
            case R.id.action_settings:
                Intent intent2 = new Intent(this, Settings.class); // start new activity
                startActivity(intent2);
                break;
            // third item in overflow menu - Rate app
            case R.id.action_rate_app:
                // when clicked start new Activity
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Finishes app when back button is pressed
     */
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
