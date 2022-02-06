package com.example.covid19vaccinereg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConfirmRegInfo extends AppCompatActivity {
    //define variables used in this class
    TextView tvName, tvAge, tvIcPass, tvPhoneNo, tvState, tvPhysAdd, tvEmail;
    TextView tvLblName, tvLblAge, tvLblIcPass, tvLblPhoneNo, tvLblState, tvLblPhysAdd, tvLblEmail;
    DatabaseManager db;
    SessionManager sessionManager;
    Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reg_info);
        //Retrieve items passed from previous intent in the bundle
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String age = bundle.getString("age");
        String icPass = bundle.getString("icPass");
        String phoneNo = bundle.getString("phoneNo");
        String state = bundle.getString("state");
        String physAdd = bundle.getString("physicalAdd");
        String email = bundle.getString("email");

        //Initialize database
        db = new DatabaseManager(getApplicationContext());

        //Initialize Session Manager
        sessionManager = new SessionManager(getApplicationContext());
        //Get username form session
        String user = sessionManager.getUsername();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //instantiate the textview label elements using the id resource
        tvLblName = findViewById(R.id.tvLabelConfName);
        tvLblAge = findViewById(R.id.tvLabelConfAge);
        tvLblIcPass = findViewById(R.id.tvLabel_Ic_Passport);
        tvLblPhoneNo = findViewById(R.id.tvLabelConfPhoneNo);
        tvLblState = findViewById(R.id.tvLabelConfState);
        tvLblPhysAdd = findViewById(R.id.tvLabelConfPhysAdd);
        tvLblEmail = findViewById(R.id.tvLabelConf_email);
        btnConfirm = findViewById(R.id.btnConfirmRegInfo);

        //instantiate the textview elements using id resource
        //and setting the texts of the textviews
        tvName = findViewById(R.id.tvConfName);
        tvName.setText(name);
        tvAge = findViewById(R.id.tvConfAge);
        tvAge.setText(age);
        tvIcPass = findViewById(R.id.tvConf_Ic_Pass);
        tvIcPass.setText(icPass);
        tvPhoneNo = findViewById(R.id.tvConfPhoneNumber);
        tvPhoneNo.setText(phoneNo);
        tvState = findViewById(R.id.tvConfState);
        tvState.setText(state);
        tvPhysAdd = findViewById(R.id.tvConfPhysAdd);
        tvPhysAdd.setText(physAdd);
        tvEmail = findViewById(R.id.tvConfEmail);
        tvEmail.setText(email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        if (sessionManager.getLogin()) {
            //Get username from session
            String user = sessionManager.getUsername();
            //Set title of sign in to name of user logged in
            signin.setTitle(user);
            //disable the sign in menu item
            signin.setEnabled(false);
            super.onCreateOptionsMenu(menu);
        } else {
            //hide sign out menu item
            signout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            //When user select Sign in from menu
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
                //Initialize ALERT DIALOG
                builder.show();
                return true;
            //When user select My Profile from menu
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
                //When user select Home from menu
            case R.id.Home:
                //Check if user signed in
                if(!sessionManager.getLogin()){
                    //when user not signed in, redirect to main homepage
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    String user = sessionManager.getUsername();
                    Cursor cursor = db.getAccountInfo(user);
                    cursor.moveToFirst();
                    //Check type of user
                    if(db.checkisAdmin(user)){
                        //When user is Admin redirect to Admin Homepage
                        startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    }else{
                        //When user is Normal User redirect to Main Homepage
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }



    public void shareDetails(View view) {
        //Retrieve items passed from previous intent in the bundle
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String age = bundle.getString("age");
        String icPass = bundle.getString("icPass");
        String phoneNo = bundle.getString("phoneNo");
        String state = bundle.getString("state");
        String physAdd = bundle.getString("physicalAdd");
        String email = bundle.getString("email");

        //save the text values of the labels into variables
        String lblName = tvLblName.getText().toString();
        String lblAge = tvLblAge.getText().toString();
        String lblIp = tvLblIcPass.getText().toString();
        String lblPhNo = tvLblPhoneNo.getText().toString();
        String lblState = tvLblState.getText().toString();
        String lblPhA = tvLblPhysAdd.getText().toString();
        String lblEmail = tvLblEmail.getText().toString();

        //concatenate the strings into one string for easy sharing
        String all = lblName + " : " + name + "\n" + lblAge + " : " + age + "\n" + lblIp + " : " + icPass + "\n" + lblPhNo + " : " + phoneNo + "\n" +
                lblState + " : " + state + "\n" + lblPhA + " : " + physAdd + "\n" + lblEmail + " : " + email;

        String mimeType = "text/plain";
        //perform the sharing process.
        ShareCompat.IntentBuilder.
                from(this).
                setType(mimeType).
                setChooserTitle("Share Via: ").
                setText(all).startChooser();
    }

    public void ConfirmRegInfo(View view) {
        //Check if user had logged into the app
        if (sessionManager.getLogin()) {
            //Retrieve items passed from previous intent in the bundle
            Bundle bundle = getIntent().getExtras();
            String name = bundle.getString("name");
            String age = bundle.getString("age");
            String icPass = bundle.getString("icPass");
            String phoneNo = bundle.getString("phoneNo");
            String state = bundle.getString("state");
            String physAdd = bundle.getString("physicalAdd");
            String email = bundle.getString("email");
            //Get date from calendar
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String registeredDate = sdf.format(cal.getTime());
            //Get username from session
            String user = sessionManager.getUsername();
            try {
                //Check if user is already registered for vaccine
                if (db.getUserId(user).getCount() > 0) {
                    Cursor cursor = db.getUserId(user);
                    //Change cursor location to the start
                    cursor.moveToFirst();
                    //Get the user id from account table
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    //add user information to registration table in database
                    if (db.addUserForVaccineReg(id, name, Integer.parseInt(age.trim()), icPass, phoneNo, state, email, physAdd, registeredDate)) {
                        Toast.makeText(this, "User have registered for vaccine successfully", Toast.LENGTH_SHORT).show();
                        //change user to registered in account table
                        db.changeToRegistered(id);
                        //Hide the Confirm button
                        btnConfirm.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else{
            Toast.makeText(this, "Log in to register for vaccine", Toast.LENGTH_SHORT).show();
        }
    }
}