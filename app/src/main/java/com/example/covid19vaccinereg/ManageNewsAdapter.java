package com.example.covid19vaccinereg;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ManageNewsAdapter extends RecyclerView.Adapter<ManageNewsAdapter.ManageNewsHolder> {

    Context mCtx;
    int layoutRes;
    List<News> newsList;
    DatabaseManager mDatabase;

    // creating a constructor for our adapter class.
    public ManageNewsAdapter(Context mCtx, int layoutRes, List<News> newsList, DatabaseManager mDatabase) {
        this.mCtx = mCtx;
        this.layoutRes = layoutRes;
        this.newsList = newsList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    //Initizalize the viewholder
    public ManageNewsAdapter.ManageNewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is use to inflate our layout
        // file for each item of our recycler view.
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.managenewslayout, parent, false);
        return new ManageNewsAdapter.ManageNewsHolder(view);
    }

    //Bind view to the data from database
    @Override
    public void onBindViewHolder(@NonNull ManageNewsAdapter.ManageNewsHolder holder, int position) {
        // below line of code is use to set data to
        // each item of our recycler view.
        final News news = newsList.get(position);
        holder.mtvNewsID.setText(String.valueOf(news.getNewsId()));
        holder.mivNewsImageAdmin.setImageBitmap(news.getNewsImage());
        holder.mtvNewsHeaderAdmin.setText(news.getNewsHeader());
        holder.mtvNewsLinkAdmin.setText(news.getNewsLink());
        holder.mtvNewsTextAdmin.setText(news.getNewsText());
        // adding on click listener for text viw with url tp redirect to web page in recycler view.
        holder.mtvNewsLinkAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the text in the textview that is selected
                String url = holder.mtvNewsLinkAdmin.getText().toString();
                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                v.getContext().startActivity(intent);
            }
        });

        // adding on click listener to expand layout in recycler view.
        holder.mbtnNewsExpanderAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mclNewsExpanderAdmin.getVisibility() == View.GONE) {
                    //make it visible(expand it)
                    holder.mclNewsExpanderAdmin.setVisibility(View.VISIBLE);
                    //change the down arrow into an up arrow
                    holder.mbtnNewsExpanderAdmin.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_up_black_24dp);
                } else//if the constraint inside the cardview is visible(expanded)
                {
                    //make it Gone (contract)
                    holder.mclNewsExpanderAdmin.setVisibility(View.GONE);
                    //change the up facing arrow to down facing arrow
                    holder.mbtnNewsExpanderAdmin.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_down_black_24dp);
                }
            }
        });
        // adding on click listener for Edit button in recycler view.
        holder.mBtnEditNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(mCtx, EditNews.class);
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    //Passing the news id as intent to editnews (activity)
                    intent.putExtra("NewsId", String.valueOf(newsList.get(position).getNewsId()));
                    v.getContext().startActivity(intent);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        });

        // adding on click listener for Delete button in recycler view.
        holder.mBtnDeleteNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int newsID = newsList.get(position).getNewsId();
                    //Calling the method from database to delete news using news Id
                    mDatabase.deleteNews(newsID);
                    //Calling the method to reload  all news existing in database
                    loadNewsFromDatabase();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    @Override
    //return the number of items in the list
    public int getItemCount() {
        return newsList.size();
    }

    public class ManageNewsHolder extends RecyclerView.ViewHolder {
        //Below this line is the
        // view holder class to create a variable for each view.
        ImageView mivNewsImageAdmin;
        TextView mtvNewsHeaderAdmin, mtvNewsLinkAdmin, mtvNewsTextAdmin, mtvNewsID;
        Button mbtnNewsExpanderAdmin, mBtnEditNews, mBtnDeleteNews;
        ConstraintLayout mclNewsExpanderAdmin;


        public ManageNewsHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            mivNewsImageAdmin = itemView.findViewById(R.id.ivNewsImageUser);
            mtvNewsHeaderAdmin = itemView.findViewById(R.id.tvNewsHeadingManage);
            mtvNewsLinkAdmin = itemView.findViewById(R.id.tvNewsWebsite);
            mtvNewsTextAdmin = itemView.findViewById(R.id.tvNewsText);
            mbtnNewsExpanderAdmin = itemView.findViewById(R.id.btnExpandNewsManage);
            mclNewsExpanderAdmin = itemView.findViewById(R.id.clexpandNewsManage);
            mtvNewsID = itemView.findViewById(R.id.tvNewsID);
            mBtnEditNews = itemView.findViewById(R.id.btnEditNews);
            mBtnDeleteNews = itemView.findViewById(R.id.btnDeleteNews);

        }
    }

    //Method to fetch all news existing in database after changes have occured
    private void loadNewsFromDatabase() {
        Cursor cursor = mDatabase.getAllNews();
        newsList.clear();
        if (cursor.moveToFirst()) {
            do {
                byte[] imagebytes = cursor.getBlob(2);
                Bitmap nImage = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
                newsList.add(new News(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        nImage
                ));
            } while (cursor.moveToNext());
        } else {
            newsList.clear();
        }
        notifyDataSetChanged();
    }
}
