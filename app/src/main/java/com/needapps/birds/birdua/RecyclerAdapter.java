package com.needapps.birds.birdua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerAdapter is used to handle lists of birds in every tab(fragment)
 * Provides a reference to the views for each data item
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    // extras to transfer info through intent to DetailActivity
    public static final String EXTRA_NAME = "com.needapps.birds.birdua.NAME";
    public static final String EXTRA_DESCRIPTION = "com.needapps.birds.birdua.DESCRIPTION";
    public static final String EXTRA_IMAGE_SLIDER = "com.needapps.birds.birdua.IMAGE";
    public static final String EXTRA_AUDIO = "com.needapps.birds.birdua.AUDIO";
    public static final String EXTRA_MORE_SOUNDS = "com.needapps.birds.birdua.MORESOUNDS";

    // two arrays for Search functionality
    private ArrayList<BirdItem> birdsList;
    private ArrayList<BirdItem> fullBirdsList;
    Context context;

    RecyclerAdapter() {
    }

    public RecyclerAdapter(Context context, ArrayList<BirdItem> lstBird) {
        this.context = context;
        this.birdsList = lstBird;
        fullBirdsList = new ArrayList<>(lstBird);
    }

    /**
     * Creates new view (invoked by the layout manager)
     * @param parent   - parent
     * @param viewType - viewType
     * @return ViewHolder with Item bird view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bird, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder   - holder
     * @param position - position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.name.setText(birdsList.get(position).getName()); // get name from BirdItem
        Glide.with(context).load(birdsList.get(position).getPhoto()).asBitmap().into(holder.photo); // get photo
        // pass data from item in RecyclerView to DetailActivity by clicking
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            // open new activity when item is clicked
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String name = holder.name.getText().toString(); // get name of item
                String description = birdsList.get(holder.getAdapterPosition()).getDescription(); // get description of item
                // transfer some info from BirdItem to BirdDetailActivity
                Intent intent = new Intent(context, BirdDetailActivity.class);
                // get Images and Audio
                Bundle bundle = new Bundle();
                bundle.putIntArray(EXTRA_IMAGE_SLIDER, birdsList.get(holder.getAdapterPosition()).getPhotosDetail());
                bundle.putInt(EXTRA_AUDIO, birdsList.get(holder.getAdapterPosition()).getAudio());
                intent.putExtras(bundle); // send photos and audio

                intent.putExtra(EXTRA_NAME, name); // send name to DetailActivity
                intent.putExtra(EXTRA_DESCRIPTION, description); // send description to DetailActivity
                intent.putExtra(EXTRA_MORE_SOUNDS, birdsList.get(holder.getAdapterPosition()).getMoreSounds());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return birdsList.size();
    }

    /**
     * Makes Search filter for tabs
     * @param searchBird - list of birds
     */
    public void setFilter(List<BirdItem> searchBird) {
        birdsList = new ArrayList<>();
        birdsList.addAll(searchBird);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return birdFilter;
    }

    /**
     * Adds Search Filter
     */
    private Filter birdFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BirdItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullBirdsList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BirdItem item : fullBirdsList) {
                    if (item.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            birdsList.clear();
            birdsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * ViewHolder is used to display data
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // initialize view to show as item in Recycler Adapter
        public ImageView photo;
        public TextView name;

        public ViewHolder(final View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo_bird_id); // from item_bird
            name = itemView.findViewById(R.id.name_bird_id); // from item_bird
        }
    }
}
