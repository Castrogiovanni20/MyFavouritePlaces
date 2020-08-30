package com.challenge.myfavouriteplaces;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, final int i) {
        final Place place = new Place(placeArrayList.get(i).getName(),
                                      placeArrayList.get(i).getPlace_id(),
                                      placeArrayList.get(i).getAddress(),
                                      placeArrayList.get(i).getRating(),
                                      placeArrayList.get(i).getPhoto(),
                                      placeArrayList.get(i).getLat(),
                                      placeArrayList.get(i).getLng());

        holder.txtName.setText(placeArrayList.get(i).getName());
        holder.btnDetails.setTag(placeArrayList.get(i).getPlace_id());

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), PlaceDetails.class);
                intent.putExtra("place", place);
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        Button btnDetails;

        public PlacesViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
