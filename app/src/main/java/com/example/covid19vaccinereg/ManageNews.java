package com.example.covid19vaccinereg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ManageNews extends AppCompatActivity {

    RecyclerView rvNews;
    List<News> newsList;
    DatabaseManager db;
    Button mBtnAddNews;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_news);
        mBtnAddNews=findViewById(R.id.btnAddNewNews);
        rvNews=findViewById(R.id.rv_ManageNews);
        //Initialize database manager
        db = new DatabaseManager(getBaseContext());
        //Initialize session manager
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        newsList = new ArrayList<>();
        loadNewsFromDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        //Check if user logged in
        if (sessionManager.getLogin()) {
            //Get username from session
            String user = sessionManager.getUsername();
            //Set title of sign in menu item to username from sessionmanager
            signin.setTitle(user);
            //Disable menu item when user logged in
            signin.setEnabled(false);
            super.onCreateOptionsMenu(menu);
        } else {
            //Hide sign out buttom when user not logged in
            signout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            //When sign in selected
            case R.id.signin:
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                return true;
            //When sign out selected
            case R.id.signout:
                //Initialize alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //Set title
                builder.setTitle("Sign Out");
                //Set message
                builder.setMessage("Are you sure to Sign Out?");
                //set positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Set login false
                        sessionManager.setLogin(false);
                        //Set Username empty
                        sessionManager.setUsername("");
                        //Redirect to main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //Finish activity
                        finish();
                    }
                });
                //Set negative button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //CANCEL DIALOG
                        dialog.cancel();
                    }
                });
                //iNITIALIZE ALERT DIALOG
                builder.show();
                return true;
                //When my profile selected
            case R.id.myProfile:
                //Check if user logged in
                if (sessionManager.getLogin()) {
                    //When user is logged in, redirect to My Profile
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    //When user not logged in
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("My Profile");
                    alert.setMessage("Log in to access My Profile \n\n Do you wish to sign in?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return true;
                //When home is selected
            case R.id.Home:
                //Check if user logged in
                if(!sessionManager.getLogin()){
                    //When user not logged in, redirec to Main Homepage
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    //When user is logged in
                    //Get the username from session maanger
                    String user = sessionManager.getUsername();
                    //Get account infor using username
                    Cursor cursor = db.getAccountInfo(user);
                    cursor.moveToFirst();
                    //Check if user is Admin
                    if(db.checkisAdmin(user)){
                        //When user is Admin, redirect to Admin Homepage
                        startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    }else{
                        //When user is Normal, redirect to Admin Homepage
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToAddNewsActivity(View view) {
        Intent intent = new Intent(this,EditNews.class);
        startActivity(intent);
    }

    private void loadNewsFromDatabase(){
        //retrive news from database as a cursor
        Cursor cursor = db.getAllNews();
        //clear the list
        newsList.clear();
        //add new items to the list
        //check if the cursor retrieves any data by using the move to first method
        if (cursor.moveToFirst()) {//if the cursor is not null
            do {
                //create a byte array to store the image in bytes and store the blob as a byte array
                byte[] imagebytes=cursor.getBlob(2);
                //convert the byte to a bitmap so that it can be used as a drawable
                Bitmap nImage = BitmapFactory.decodeByteArray(imagebytes,0,imagebytes.length);
                //add the items retrived from the database into the list
                newsList.add(new News(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        nImage
                ));
            } while (cursor.moveToNext());//keep adding items to the list until row by row until there is no more items
        }
        else//if there are no items retrived from the database
        {
            newsList.clear();
        }
        //set the adapter to the layout and initialize it
        ManageNewsAdapter adapter = new ManageNewsAdapter(this,R.layout.managenewslayout,newsList,db);
        rvNews.setAdapter(adapter);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
    }

    public void goBackToAdminHome(View view) {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }
}