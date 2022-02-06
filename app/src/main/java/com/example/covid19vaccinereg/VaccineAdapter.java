package com.example.covid19vaccinereg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.MyViewHolder> {
    //Define string arrays
    Context context;
    String[] vaccineTitle;
    String[] vaccineManufacture;
    String[] vaccineType;
    String[] vaccineEffective;
    String[] vaccineNoShot;
    String[] vaccineSideEffect;
    int images[]; // Define int array for images of vaccine

    //Initialize the dataset of the Adapter.
    //String[] containing the data to populate views to be used by RecyclerView.
    public VaccineAdapter(Context ctx, String[] vTitle, String[] vManufacture, String[] vType, String[] vEffective, String[] vShots, String[] vSideEffect, int[] img) {
        context = ctx;
        vaccineTitle = vTitle;
        vaccineManufacture = vManufacture;
        vaccineType = vType;
        vaccineEffective = vEffective;
        vaccineNoShot = vShots;
        images = img;
        vaccineSideEffect = vSideEffect;
    }

    @NonNull
    // Create new views (invoked by the layout manager)
    @Override
    public VaccineAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.vaccine_info, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        //Set each string array to respective textview
        holder.vaccine.setText(vaccineTitle[position]);
        holder.manufacture.setText(vaccineManufacture[position]);
        holder.vaccineType.setText(vaccineType[position]);
        holder.effective.setText(vaccineEffective[position]);
        holder.numberShots.setText(vaccineNoShot[position]);
        holder.sideEffects.setText(vaccineSideEffect[position]);
        //set int images arrays to image view
        holder.imgVaccine.setImageResource(images[position]);
        // Onclick listener for imageview down button to show expandable layout
        holder.expandDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable.getVisibility() == View.GONE) {
                    //Set to show expandable layout
                    holder.expandable.setVisibility(View.VISIBLE);
                    //Hide the imageview with the down arrow
                    holder.expandDown.setVisibility(View.INVISIBLE);
                    //Show the imageview with the up arrow
                    holder.expandUp.setVisibility(View.VISIBLE);

                }

            }
        });
        // Onclick listener for imageview up button to hide expandable layout
        holder.expandUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable.getVisibility() == View.VISIBLE) {
                    //Set to hide expandable layout to gone.
                    holder.expandable.setVisibility(View.GONE);
                    //Hide the imageview with the up arrow
                    holder.expandUp.setVisibility(View.INVISIBLE);
                    //Show the imageview with the down arrow
                    holder.expandDown.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        //Return the size of the data set.
        return vaccineTitle.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        //reference to the type of views that will be used
        TextView vaccine, manufacture, vaccineType, effective, numberShots, sideEffects;
        ImageView imgVaccine, expandDown, expandUp;
        ConstraintLayout expandable;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Set values to the components
            vaccine = itemView.findViewById(R.id.tvVaccineTitle);
            manufacture = itemView.findViewById(R.id.tvManufacture);
            vaccineType = itemView.findViewById(R.id.tvVaccineType);
            effective = itemView.findViewById(R.id.tvEffectiveness);
            numberShots = itemView.findViewById(R.id.tvNumberShots);
            imgVaccine = itemView.findViewById(R.id.imgVaccine);
            expandDown = itemView.findViewById(R.id.expandDown);
            expandUp = itemView.findViewById(R.id.expandUp);
            expandable = itemView.findViewById(R.id.expandableLayout);
            sideEffects = itemView.findViewById(R.id.tvSideEffect);

        }
    }
}
