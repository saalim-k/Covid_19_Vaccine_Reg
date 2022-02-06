package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class VaccineInfo extends AppCompatActivity {
    //Define the string arrays
    RecyclerView recyclerView;
    String[] title;
    String[] manufacture;
    String[] type;
    String[] effective;
    String[] noshot;
    String[] sideEffect;
    //Define the int array for images
    int images[] = {R.drawable.pfizer_icon, R.drawable.astrazeneca_icom, R.drawable.sinovac_icon, R.drawable.cansinobio_icon, R.drawable.gamaleya_icon};

    private ConstraintLayout expandVaccineInfo;
    private Button BtnExpandVaccineInfo;
    DatabaseManager db;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_info);

        //Initialize DatabaseManager
        db = new DatabaseManager(this);
        //Initialize SessionManager
        sessionManager = new SessionManager(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //instantiate the RecyclerView using the id resource
        recyclerView = findViewById(R.id.rvVaccineInfo);
        //----------Retrieving the the string array from string XML resource------------
        title = getResources().getStringArray(R.array.vaccines);
        manufacture = getResources().getStringArray(R.array.manufacturer);
        type = getResources().getStringArray(R.array.vaccineType);
        effective = getResources().getStringArray(R.array.effective);
        noshot = getResources().getStringArray(R.array.numberShots);
        sideEffect = getResources().getStringArray(R.array.sideEffectVaccine);
        //------------------------------------------------------------------------------

        //instantiate a new VaccineAdapter that passes in the string arrays
        VaccineAdapter vaccineAdapter = new VaccineAdapter(this, title, manufacture, type, effective, noshot, sideEffect,images);
        // Attach the VaccineAdapter as the adapter for RecyclerView.
        recyclerView.setAdapter(vaccineAdapter);
        //Predefine framework to show how data to be displayed
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            case R.id.myProfile:
                if (sessionManager.getLogin()) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
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

}