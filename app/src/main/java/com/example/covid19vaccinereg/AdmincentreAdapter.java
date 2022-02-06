package com.example.covid19vaccinereg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdmincentreAdapter extends RecyclerView.Adapter<AdmincentreAdapter.AdminHolder> {
    List<AdminCentreGet> adminCentreGetList;
    //Initialize the dataset of the Adapter.
    //List containing the data to populate views to be used by RecyclerView.
    public AdmincentreAdapter(List<AdminCentreGet> adminCentreGetList) {
        this.adminCentreGetList = adminCentreGetList;
    }

    @NonNull
    // Create new views (invoked by the layout manager)
    @Override
    public AdmincentreAdapter.AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.administrative_center, parent, false);
        return new AdminHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AdmincentreAdapter.AdminHolder holder, int position) {
        AdminCentreGet adminCentreGet = adminCentreGetList.get(position);
        // Get element from the  dataset at this position and replace the
        // contents of the view with that element
        holder.txtStates.setText(adminCentreGet.getStates());
        holder.txtCentre1.setText(adminCentreGet.getCentre1());
        holder.txtCentre2.setText(adminCentreGet.getCentre2());
        holder.txtCentre3.setText(adminCentreGet.getCentre3());
        holder.txtCentre4.setText(adminCentreGet.getCentre4());
        holder.txtCentre5.setText(adminCentreGet.getCentre5());
        holder.txtCentre6.setText(adminCentreGet.getCentre6());

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre1.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre2.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre3.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre4.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre5.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        //Set onclicklistener to each text view for Centres to redirect to Map
        holder.txtCentre6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.txtCentre6.getText().toString();
                Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                v.getContext().startActivity(intent);
            }
        });

        boolean isExpandable = adminCentreGetList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return adminCentreGetList.size();
    }

    public class AdminHolder extends RecyclerView.ViewHolder {
        //reference to the type of views and layouts that will be used
        private TextView txtStates, txtCentre1, txtCentre2, txtCentre3, txtCentre4, txtCentre5, txtCentre6;
        ConstraintLayout expandableLayout,stateLayout;

        public AdminHolder(@NonNull View itemView) {
            super(itemView);
            //Set values to the components
            txtStates = itemView.findViewById(R.id.tvStates);
            txtCentre1 = itemView.findViewById(R.id.tvCentre1);
            txtCentre2 = itemView.findViewById(R.id.tvCentre2);
            txtCentre3 = itemView.findViewById(R.id.tvCentre3);
            txtCentre4 = itemView.findViewById(R.id.tvCentre4);
            txtCentre5 = itemView.findViewById(R.id.tvCentre5);
            txtCentre6 = itemView.findViewById(R.id.tvCentre6);

            stateLayout = itemView.findViewById(R.id.stateLayout);
            expandableLayout = itemView.findViewById(R.id.CentreExpandableLayout);

            //Set onclick to hide and show expandable layout
            stateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminCentreGet adminCentreGet = adminCentreGetList.get(getAdapterPosition());
                    adminCentreGet.setExpandable(!adminCentreGet.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });


        }
    }


}
