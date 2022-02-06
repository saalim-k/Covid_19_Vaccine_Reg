package com.example.covid19vaccinereg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder>{

    Context mCtx;
    int layoutRes;
    List<News> newsList;
    DatabaseManager mDatabase;

    //modified the constructor and we are taking the DatabaseManager instance here
    public NewsAdapter(Context mCtx, int layoutRes, List<News> newsList, DatabaseManager mDatabase) {
        this.mCtx = mCtx;
        this.layoutRes = layoutRes;
        this.newsList = newsList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    //initialize the news adapter
    public NewsAdapter.NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout for the recycler view
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(R.layout.news_layout_view,parent, false);
        return new NewsAdapter.NewsHolder(view);
    }

    //bind the view to the data from the database
    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsHolder holder, int position) {
        final News news = newsList.get(position);
        holder.mivNewsImage.setImageBitmap(news.getNewsImage());
        holder.mtvNewsHeader.setText(news.getNewsHeader());
        holder.mtvNewsLink.setText(news.getNewsLink());
        //set onclick listener for the url in the news
        holder.mtvNewsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = holder.mtvNewsLink.getText().toString();
                //send user to the webpage based on the url
                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                v.getContext().startActivity(intent);
            }
        });
        holder.mtvNewsText.setText(news.getNewsText());
        //set onclick listener for the news expander button
        holder.mbtnNewsExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mclNewsExpander.getVisibility() == View.GONE) {
                    //make it visible(expand it)
                    holder.mclNewsExpander.setVisibility(View.VISIBLE);
                    //change the down arrow into an up arrow
                    holder.mbtnNewsExpander.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_up_black_24dp);
                } else//if the constraint inside the cardview is visible(expanded)
                {
                    //make it Gone (contract)
                    holder.mclNewsExpander.setVisibility(View.GONE);
                    //change the up facing arrow to down facing arrow
                    holder.mbtnNewsExpander.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_down_black_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    //create view holder class to create variables for each view in the recycler view
    public class NewsHolder extends RecyclerView.ViewHolder {
        ImageView mivNewsImage;
        TextView mtvNewsHeader, mtvNewsLink, mtvNewsText;
        Button mbtnNewsExpander;
        ConstraintLayout mclNewsExpander;

        //initialize variables in each item of the recycler view
        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            mivNewsImage = itemView.findViewById(R.id.ivNewsImageUser);
            mtvNewsHeader = itemView.findViewById(R.id.tvNewsHeadingManage);
            mtvNewsLink = itemView.findViewById(R.id.tvNewsWebsite);
            mtvNewsText = itemView.findViewById(R.id.tvNewsText);
            mbtnNewsExpander = itemView.findViewById(R.id.btnExpandNewsManage);
            mclNewsExpander = itemView.findViewById(R.id.clexpandNewsManage);

        }
    }
}
