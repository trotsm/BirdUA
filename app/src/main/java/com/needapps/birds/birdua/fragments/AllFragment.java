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
 * A simple {@link Fragment} subclass.
 * first tab
 */
public class AllFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView; // helps to show items
    private ArrayList<BirdItem> lstBird = new ArrayList<>(); // items
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
        recyclerView = v.findViewById(R.id.all_recyclerview);
        new AsyncLoadDatabase().execute(); //retrieve appropriate data from database

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true); // allow search
        lstBird = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(getActivity(), lstBird); //add recycler adapter to array items
        recyclerView.setAdapter(recyclerAdapter);
    }

    /**
     * add SearchView
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);

        //if is clicked search icon in FOREST tab, start this tab and EXPAND THIS SEARCH WITH KEYBOARD
        if (ForestFragment.expand) {
            item.expandActionView();
            ForestFragment.expand = !ForestFragment.expand;
        }
        //if is clicked search icon in COASTAL tab, start this tab and EXPAND THIS SEARCH WITH KEYBOARD
        if (CoastalFragment.expand) {
            item.expandActionView();
            CoastalFragment.expand = !CoastalFragment.expand;
        }
        //if is clicked search icon in PREDATORY tab, start this tab and EXPAND THIS SEARCH WITH KEYBOARD
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
                        recyclerAdapter.setFilter(lstBird);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do when expanded
                        return true; // Return true to expand action view
                    }
                });
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        final List<BirdItem> filteredModelList = filter(lstBird, newText);

        recyclerAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * filter to search in Toolbar
     *
     * @param models
     * @param query
     * @return
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
     * to load database with AsyncTask
     */
    class AsyncLoadDatabase extends AsyncTask<Void, Void, Void> {
        /**
         * load in first thread
         */
        protected void onPreExecute() {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerAdapter = new RecyclerAdapter(getActivity(), lstBird);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }

        /**
         * load database in background thread
         */
        @Override
        protected Void doInBackground(Void... voids) {
            loadDatabaseAll();
            return null;
        }

        protected void onPostExecute(Void param) {
            recyclerAdapter.notifyDataSetChanged();
        }

    }

    /**
     * load all database
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
            //load by alphabet
            cursor = databaseHelper.QueryData("SELECT * FROM tbl_bird ORDER BY name COLLATE NOCASE;");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        BirdItem birdItem = new BirdItem();

                        birdItem.setId(cursor.getInt(0));// first column - id
                        birdItem.setName(cursor.getString(1));// second column - name
                        birdItem.setDescription(cursor.getString(2));// third column - description
                        // fourth column - imagename
                        String image_names = cursor.getString(3);
                        int image = getResources().getIdentifier("com.needapps.birds.birdua:drawable/" + image_names, null, null);
                        birdItem.setPhoto(image);

                        // fifth column - audioname
                        String audio_name = cursor.getString(4);
                        int audio = getResources().getIdentifier("com.needapps.birds.birdua:raw/" + audio_name, null, null);
                        birdItem.setAudio(audio);
                        // sixth column - imagenameslider
                        String image_names_slider = cursor.getString(5);
                        String[] images = image_names_slider.split(",");

                        int[] ids = new int[images.length];
                        for (int i = 0; i < ids.length; i++) {
                            ids[i] = getResources().getIdentifier("com.needapps.birds.birdua:drawable/" + images[i], null, null);
                        }
                        birdItem.setPhotosDetail(ids);

                        birdItem.setMoresounds(cursor.getString(7));// eighth column - moresounds

                        //add all information to item
                        lstBird.add(birdItem);

                    } while (cursor.moveToNext());
                }
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

}
