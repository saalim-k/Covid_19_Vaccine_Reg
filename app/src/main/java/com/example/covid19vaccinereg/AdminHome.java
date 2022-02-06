package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AdminHome extends AppCompatActivity {
    //create variables to be used
    Button mBtnRegisterForvacc, mBtnManageUsers, mBtnManageNews, mBtnmanageMyProfile;
    SessionManager sessionManager;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //get the sessionmanager and databasemanager context
        sessionManager = new SessionManager(getApplicationContext());
        db = new DatabaseManager(getBaseContext());

        //set the toolbar content
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //initialize the buttons used
        mBtnRegisterForvacc=findViewById(R.id.btnRegisterVacAdmin);
        mBtnManageUsers=findViewById(R.id.btnManageUsers);
        mBtnManageNews=findViewById(R.id.btnManageNews);
        mBtnmanageMyProfile=findViewById(R.id.btnMyprofileAdmin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        if (sessionManager.getLogin()) {
            //Get username from session
            String user = sessionManager.getUsername();
            signin.setTitle(user);
            signin.setEnabled(false);
            super.onCreateOptionsMenu(menu);
        } else {
            signout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            case R.id.signin:
                Intent intent = new Intent(this, SignIn.class);
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
                        startActivity(new Intent(getApplicationContext(), SignIn.class));
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
            case R.id.myProfile:
                //check if the user is logged in from the session manager
                if (sessionManager.getLogin()) {
                    //send user to myprofile page
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    //create alert dialog box
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("My Profile");
                    alert.setMessage("Log in to access My Profile \n\n Do you wish to sign in?");
                    //create onclick listener for yes button
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                        }
                    });
                    //create onclick listener for the cancerl button
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return true;
            case R.id.Home:
                if(!sessionManager.getLogin()){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    String user = sessionManager.getUsername();
                    Cursor cursor = db.getAccountInfo(user);
                    cursor.moveToFirst();
                    if(db.checkisAdmin(user)){
                        startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    }else{
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void ManageUsers(View view) {
        //send user to view user info activity
        Intent intent = new Intent(this,ViewAccountInfoActivity.class);
        startActivity(intent);
    }

    public void ManageNews(View view) {
        //send user to manage news class
        Intent i = new Intent(this,ManageNews.class);
        startActivity(i);
    }

    public void viewMyProfile(View view) {
        startActivity(new Intent(this,MyProfile.class));
    }

    //when user clicks on ergister button
    public void RegisterForVaccine(View view) {
        //check username in session manager
        String user = sessionManager.getUsername();
        //get user id from database
        Cursor cursor = db.getUserId(user);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        //check if user is registered for vaccine
        if(db.getAccountWithVaccRegInfo(String.valueOf(id)).moveToFirst()){
            //if user is registered show dialog to direct them to view their registration information
            AlertDialog.Builder alertAlreadyRegistered = new AlertDialog.Builder(this);
            alertAlreadyRegistered.setTitle("Registered");
            alertAlreadyRegistered.setMessage("You have already registered for Vaccine \n\n Do you wish to view your registration information?");
            alertAlreadyRegistered.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentR = new Intent(getApplicationContext(),ViewMyVaccineRegistration.class);
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
            alertAlreadyRegistered.show();
        }else{
            //if the user has not registered send them to take quiz
            startActivity(new Intent(this,TakeQuiz.class));
        }

    }
    //switch to homepage for users
    public void SwitchToUserView(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}