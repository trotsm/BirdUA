package com.needapps.birds.birdua.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.needapps.birds.birdua.BirdItem;
import com.needapps.birds.birdua.R;
import com.needapps.birds.birdua.RecyclerAdapter;
import com.needapps.birds.birdua.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * AllFragment is first All birds tab
 */
public class AllFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView allRecyclerView; // helps to show items
    private ArrayList<BirdItem> birdsList = new ArrayList<>(); // items
    private RecyclerAdapter recyclerAdapter; // helps to show items
    private DatabaseHelper databaseHelper;
    private Cursor cursor; // to retrieve some data from database
    private SearchView searchView;


    public AllFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all, container, false);
        allRecyclerView = v.findViewById(R.id.all_recyclerview);
        // retrieve appropriate data from database
        new AsyncLoadDatabase().execute();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true); // allow search
        birdsList = new ArrayList<>();
        // add recycler adapter to array items
        recyclerAdapter = new RecyclerAdapter(getActivity(), birdsList);
        allRecyclerView.setAdapter(recyclerAdapter);
    }

    /**
     * add SearchView
     *
     * @param menu     - menu
     * @param inflater - inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);

        // if is clicked search icon in FOREST || COASTAL || PREDATORY tabs,
        // start this tab and EXPAND THIS SEARCH WITH KEYBOARD
        if (ForestFragment.expand) {
            item.expandActionView();
            ForestFragment.expand = !ForestFragment.expand;
        }
        if (CoastalFragment.expand) {
            item.expandActionView();
            CoastalFragment.expand = !CoastalFragment.expand;
        }
        if (PredatoryFragment.expand) {
            item.expandActionView();
            PredatoryFragment.expand = !PredatoryFragment.expand;
        }

        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do when collapsed
                        recyclerAdapter.setFilter(birdsList);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    /**
     * Is called when the query text is changed by the user
     * @param newText - changed text
     * @return true
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        final List<BirdItem> filteredModelList = filter(birdsList, newText);
        recyclerAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Filters to search in Toolbar
     *
     * @param models - list of items
     * @param query  - text
     * @return filtered list
     */
    public List<BirdItem> filter(List<BirdItem> models, String query) {
        query = query.toLowerCase();
        final List<BirdItem> filteredModelList = new ArrayList<>();
        for (BirdItem model : models) {
            final String text = model.getDescription().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    /**
     * Loads database with AsyncTask
     */
    class AsyncLoadDatabase extends AsyncTask<Void, Void, Void> {

        /**
         * Loads in first thread
         */
        protected void onPreExecute() {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerAdapter = new RecyclerAdapter(getActivity(), birdsList);

            allRecyclerView.setHasFixedSize(true);
            allRecyclerView.setLayoutManager(gridLayoutManager);
            allRecyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }

        /**
         * Loads database in background thread
         */
        @Override
        protected Void doInBackground(Void... voids) {
            // load database data
            loadDatabaseAll();
            return null;
        }

        protected void onPostExecute(Void param) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Loads all database
     */
    public void loadDatabaseAll() {
        databaseHelper = new DatabaseHelper(getActivity());
        try {
            databaseHelper.checkAndCopyDatabase();
            databaseHelper.openDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            // load by alphabet
            cursor = databaseHelper.QueryData("SELECT * FROM tbl_bird ORDER BY name COLLATE NOCASE;");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        BirdItem birdItem = new BirdItem();

                        birdItem.setId(cursor.getInt(cursor.getColumnIndex("_id")));// first column - id
                        birdItem.setName(cursor.getString(cursor.getColumnIndex("name")));// second column - name
                        birdItem.setDescription(cursor.getString(cursor.getColumnIndex("description")));// third column - description
                        // fourth column - imagename
                        String image_names = cursor.getString(cursor.getColumnIndex("imagename"));
                        int image = getResources().getIdentifier("com.needapps.birds.birdua:drawable/" + image_names, null, null);
                        birdItem.setPhoto(image);

                        // fifth column - audioname
                        String audio_name = cursor.getString(cursor.getColumnIndex("audioname"));
                        int audio = getResources().getIdentifier("com.needapps.birds.birdua:raw/" + audio_name, null, null);
                        birdItem.setAudio(audio);
                        // sixth column - imagenameslider
                        String image_names_slider = cursor.getString(cursor.getColumnIndex("imagenameslider"));
                        String[] images = image_names_slider.split(",");

                        int[] ids = new int[images.length];
                        for (int i = 0; i < ids.length; i++) {
                            ids[i] = getResources().getIdentifier("com.needapps.birds.birdua:drawable/" + images[i], null, null);
                        }
                        birdItem.setPhotosDetail(ids);
                        // eighth column - moresounds
                        birdItem.setMoreSounds(cursor.getString(cursor.getColumnIndex("moresounds")));

                        //add all information to item
                        birdsList.add(birdItem);

                    } while (cursor.moveToNext());
                }
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
