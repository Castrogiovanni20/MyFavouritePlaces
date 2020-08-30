package com.challenge.myfavouriteplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.PlacesViewHolder> {

    private Context context;
    private ArrayList<Place>placeArrayList;

    CustomAdapter(Context context, ArrayList<Place> arrayPlaces){
        this.context = context;
        this.placeArrayList = arrayPlaces;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int i) {
        holder.txtName.setText(placeArrayList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public PlacesViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
        }
    }
}
