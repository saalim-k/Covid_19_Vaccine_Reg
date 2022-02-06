package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //Initialize variables used in this class
    private ConstraintLayout expandDosAndDonts, clexpandCovidVideo;
    private Button BtnExpandDosAndDonts, btnExpandVideo;
    private TextView tvNewsWebsite1, tvNewsWebsite2, tvNewsWebsite3;
    Dialog HotlineDialog;
    SessionManager sessionManager;
    DatabaseManager db;
    List<News> newsList;
    RecyclerView mrvViewNews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Session Manager
        sessionManager = new SessionManager(getApplicationContext());
        db = new DatabaseManager(getBaseContext());
        //Initialize new array list of newList
        newsList = new ArrayList<>();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //instantiate the elements using the id resource
        expandDosAndDonts = findViewById(R.id.expandDosAndDonts);
        BtnExpandDosAndDonts = findViewById(R.id.BtnExpandDosAndDonts);
        mrvViewNews = findViewById(R.id.rvViewNews);
        loadNewsFromDatabase();
        if(!db.checkUsername("Admin"))
        {
            db.addDefaultAdmin();
        }

        //set onclick listener for the Do's and Don'ts cardview to allow it to expand and contract.
        BtnExpandDosAndDonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the constraint inside the cardview is not visible
                if (expandDosAndDonts.getVisibility() == View.GONE) {
                    //make it visible(expand it)
                    expandDosAndDonts.setVisibility(View.VISIBLE);
                    //change the down arrow into an up arrow
                    BtnExpandDosAndDonts.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_up_black_24dp);
                } else//if the constraint inside the cardview is visible(expanded)
                {
                    //make it Gone (contract)
                    expandDosAndDonts.setVisibility(View.GONE);
                    //change the up facing arrow to down facing arrow
                    BtnExpandDosAndDonts.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_down_black_24dp);
                }
            }
        });

        btnExpandVideo = findViewById(R.id.btnExpanCovidVideo);
        clexpandCovidVideo = findViewById(R.id.expandNewsVideo);
        //initilize the videoview
        VideoView mVideoViewNews = findViewById(R.id.vvNews);
        //set the path for the video
        mVideoViewNews.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.covid_video);
        //create the media controller and set it to control the video player
        MediaController mc = new MediaController(this);
        mc.setAnchorView(mVideoViewNews);
        mVideoViewNews.setMediaController(mc);

        btnExpandVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clexpandCovidVideo.getVisibility() == View.GONE) {
                    clexpandCovidVideo.setVisibility(View.VISIBLE);
                    btnExpandVideo.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_up_black_24dp);
                } else {
                    clexpandCovidVideo.setVisibility(View.GONE);
                    btnExpandVideo.setBackgroundResource(R.drawable.ic_keyboard_name_arrow_down_black_24dp);
                }
            }
        });


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
            //Set tittle of sign in menu item to username that logged in
            signin.setTitle(user);
            //Disable the menu item
            signin.setEnabled(false);
            super.onCreateOptionsMenu(menu);
        } else {
            //when user not logged in
            //Hide sign out menu item
            signout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            //When sign in is selected
            case R.id.signin:
                //Redirect to Sign In page
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                return true;
            //When sign out selected
            case R.id.signout:
                //Initialize alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
            //When My Profile is selected
            case R.id.myProfile:
                //Check if user is logged in
                if (sessionManager.getLogin()) {
                    //Redirect user to My Profile activity when is logged in
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    //When user not logged in
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    //Set titel of Alert
                    alert.setTitle("My Profile");
                    //Set Message of Alert
                    alert.setMessage("Log in to access My Profile \n\n Do you wish to sign in?");
                    //Set Positive button on click listener
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Redirect user to sign in activity
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                        }
                    });
                    //Set Positive button on click listener
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return true;
                //Whne home is selected
            case R.id.Home:
                //When user is not logged in
                if(!sessionManager.getLogin()){
                    //Redired to main homepage
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    //When user is logged in
                    //Get username of user that logged in
                    String user = sessionManager.getUsername();
                    //get user info using username
                    Cursor cursor = db.getAccountInfo(user);
                    cursor.moveToFirst();
                    //Check if user is Admin
                    if(db.checkisAdmin(user)){
                        //When user is Admin, redirect to Admin Homepage
                        startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    }else{
                        //When user is normal user redirect to Main Homepage
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //start a new activity(take quiz) when the Take quiz image is clicked
    public void launchTakeQuiz(View view) {
        //Check if user is logged in
        if (sessionManager.getLogin()){
            //When user is logged in
            //Get username from session
            String user = sessionManager.getUsername();
            //Get user id from account table using Username
            Cursor cursor = db.getUserId(user);
            cursor.moveToFirst();
            //Save user id to variable from column in account table
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            //Check if user id exist in registered table in datbaase
            if(db.getAccountWithVaccRegInfo(String.valueOf(id)).moveToFirst()){
                //When user id exist in register table in database
                //user have registered for vaccine
                AlertDialog.Builder alertAlreadyRegistered = new AlertDialog.Builder(this);
                alertAlreadyRegistered.setTitle("Registered");
                alertAlreadyRegistered.setMessage("You have registered for the Vaccine \n\n Do you wish to view your registration information?");
                alertAlreadyRegistered.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Redirect user to view their registration information
                        Intent intentR = new Intent(getApplicationContext(),ViewMyVaccineRegistration.class);
                        //Sending intent of uid to ViewMyVaccineRegistration class
                        intentR.putExtra("uid", String.valueOf(id));
                        startActivity(intentR);
                        finish();
                    }
                });
                alertAlreadyRegistered.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //Initialize alert dialog
                alertAlreadyRegistered.show();
            }else{
                //When user not yet registered for vaccine
                startActivity(new Intent(this,TakeQuiz.class));
            }
        }else{
            //When user is not logged in
            AlertDialog.Builder alertQuiz = new AlertDialog.Builder(this);
            alertQuiz.setTitle("Take Quiz");
            alertQuiz.setMessage("Please Sign In to Take Quiz \n\n Do you wish to Sign In?");
            alertQuiz.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Redirect user to sign in page
                    startActivity(new Intent(getApplicationContext(),SignIn.class));
                    finish();
                }
            });
            alertQuiz.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            //Initialize alert dialog
            alertQuiz.show();
        }

    }

    //start a new activity (covidinfo) when the covid info image is clicked
    public void launchCovidInfo(View view) {
        Intent intent = new Intent(MainActivity.this, VaccineInfo.class);
        startActivity(intent);
    }

    //go to administrativecentre activity when administrative center image is clicked
    public void launchAdministrativeCentre(View view) {
        Intent intent = new Intent(MainActivity.this, Administrative_center.class);
        startActivity(intent);
    }

    //go to covidSymptoms activity when covid symptoms image is clicked
    public void launchCovidSymptoms(View view) {
        Intent intent = new Intent(MainActivity.this, CovidSymptoms.class);
        startActivity(intent);
    }

    //display the dialog box when the call hotline button is clicked
    public void LaunchCallDialog(View view) {
        //instantiate the dialog and link with id resource to the hotline_dialog.xml
        HotlineDialog = new Dialog(this);
        HotlineDialog.setContentView(R.layout.hotline_dialog);
        HotlineDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HotlineDialog.show();
        //instantiate the elements inside the dialog using the id resource
        TextView tvHotlineNo1 = HotlineDialog.findViewById(R.id.tvHotline1);
        TextView tvHotlineNo2 = HotlineDialog.findViewById(R.id.tvHotline2);
        TextView tvHotlineNo3 = HotlineDialog.findViewById(R.id.tvHotline3);
        Button btnCallHot1 = HotlineDialog.findViewById(R.id.btnCallHotline1);
        //set onclick listeners for the buttons inside the dialog
        btnCallHot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start a call activity with the first number
                Uri number = Uri.parse("tel:" + tvHotlineNo1.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        Button btnCallHot2 = HotlineDialog.findViewById(R.id.btnCallHotline2);
        btnCallHot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start a call activity with the 2nd number
                Uri number = Uri.parse("tel:" + tvHotlineNo2.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        Button btnCallHot3 = HotlineDialog.findViewById(R.id.btnCallHotline3);
        btnCallHot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start a call activity with the 3rd number
                Uri number = Uri.parse("tel:" + tvHotlineNo3.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
    }

    //Retrieve all news from database
    private void loadNewsFromDatabase() {
        Cursor cursor = db.getAllNews();
        if (cursor.moveToFirst()) {
            newsList.clear();
            do {
                //Get information of news from every column in News table in database
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
        NewsAdapter adapter = new NewsAdapter(this, R.layout.news_layout_view, newsList, db);
        //Set adapter to recycler view in Main Homepage
        mrvViewNews.setAdapter(adapter);
        mrvViewNews.setLayoutManager(new LinearLayoutManager(this));
    }


}