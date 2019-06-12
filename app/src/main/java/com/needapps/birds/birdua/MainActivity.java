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

public class MainActivity extends AppCompatActivity {
    //add Android Toolbar, TabLayout and ViewPager.
    Toolbar myToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private SharedPreferencesManager prefs; //to save theme state
    int t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);//get SharedPreferencesManager  instance
        t = prefs.retrieveInt("theme", 0); //get stored theme, zero is default
        Utils.setTheme(t);  //Set the stored theme, will default to light
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        //define Android Toolbar, TabLayout and ViewPager.
        //add Toolbar because we need tablayout
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(myToolbar);
        // define tabLayout
        tabLayout = findViewById(R.id.tab_layout);
        // define viewPager
        //viewPager helps with tabs in tablayout
        viewPager = findViewById(R.id.view_pager);
        //define com.needapps.birds.birdua.PageAdapter
        //viewPager helps with tabs scroll etc.
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        //add fragments tabs
        pageAdapter.AddFragment(new AllFragment(), getString(R.string.all_birds));
        pageAdapter.AddFragment(new ForestFragment(), getString(R.string.forest_steppe_birds));
        pageAdapter.AddFragment(new CoastalFragment(), getString(R.string.coastal_birds));
        pageAdapter.AddFragment(new PredatoryFragment(), getString(R.string.predatory_birds));
        // download all fragments at the same time once it is created
        //because by default downloads only 3
        viewPager.setOffscreenPageLimit(pageAdapter.getCount() - 1);
        //use pageAdapter as the adapter for Android ViewPager.
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    // make overflow menu(3 dots) in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // first item in overflow menu - about app
            case R.id.action_about_app:
                // when clicked start new Activity
                Intent intent = new Intent(this, AboutAppActitivy.class);// start new activity
                startActivity(intent);
                break;

            case R.id.action_theme:
                Intent intent2 = new Intent(this, Settings.class);// start new activity
                startActivity(intent2);

                break;

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

    // finish app when back button pressed
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
