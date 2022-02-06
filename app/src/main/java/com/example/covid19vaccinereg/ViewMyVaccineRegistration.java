package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewMyVaccineRegistration extends AppCompatActivity {
    DatabaseManager db;
    SessionManager sessionManager;
    TextView UID,UNAME,UAGE,UIC,UPHONE,USTATE,UEMAIL,UADDRESS,UREGISTERDATE;
    TextView lUID,lUNAME,lUAGE,lUIC,lUPHONE,lUSTATE,lUEMAIL,lUADDRESS,lUREGISTERDATE;
    Button BackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_vaccine_registration);
        //instantiate the textview elements using id resource
        UID = findViewById(R.id.tvUserIDSet);
        UNAME = findViewById(R.id.tvUserFullNameSet);
        UAGE = findViewById(R.id.tvAgeSet);
        UIC = findViewById(R.id.tvUserIdentificationSet);
        UPHONE = findViewById(R.id.tvPhoneSet);
        USTATE = findViewById(R.id.tvStateSet);
        UEMAIL = findViewById(R.id.tvEmailSet);
        UADDRESS = findViewById(R.id.tvAddressSet);
        UREGISTERDATE = findViewById(R.id.tvRegisteredDateSet);
        //instantiate the textview label elements using the id resource
        lUID = findViewById(R.id.tvUserIDLabel);
        lUNAME = findViewById(R.id.tvUserFullNameLabel);
        lUAGE = findViewById(R.id.tvAgeLabel);
        lUIC = findViewById(R.id.tvUserIdentificationLabel);
        lUPHONE = findViewById(R.id.tvPhoneLabel);
        lUSTATE = findViewById(R.id.tvStateLabel);
        lUEMAIL = findViewById(R.id.tvEmailLabel);
        lUADDRESS = findViewById(R.id.tvAddressLabel);
        lUREGISTERDATE = findViewById(R.id.tvRegisteredDateLabel);
        BackToHome = findViewById(R.id.btnBackToHome);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        sessionManager = new SessionManager(this);
        db = new DatabaseManager(this);
        //Retrieve item passed from previous intent
        String UserId=getIntent().getStringExtra("uid");
        Cursor cursor = db.getAccountWithVaccRegInfo(UserId);
        if(cursor.moveToFirst()) {
            String FullName = cursor.getString(cursor.getColumnIndex("name"));
            int UserAge = cursor.getInt(cursor.getColumnIndex("age"));
            String UserIcPass = cursor.getString(cursor.getColumnIndex("identification"));
            String UserPhone = cursor.getString(cursor.getColumnIndex("phone"));
            String UserState = cursor.getString(cursor.getColumnIndex("state"));
            String UserEmail = cursor.getString(cursor.getColumnIndex("email"));
            String UserAddress = cursor.getString(cursor.getColumnIndex("address"));
            String UserRegDate = cursor.getString(cursor.getColumnIndex("registerDate"));
            //save info into TextViews
            UID.setText(UserId);
            UNAME.setText(FullName);
            UAGE.setText(String.valueOf(UserAge));
            UIC.setText(UserIcPass);
            UPHONE.setText(UserPhone);
            USTATE.setText(UserState);
            UEMAIL.setText(UserEmail);
            UADDRESS.setText(UserAddress);
            UREGISTERDATE.setText(UserRegDate);
        }
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

    public void BackHome(View view) {
        String user = sessionManager.getUsername();
        Cursor cursor = db.getAccountInfo(user);
        cursor.moveToFirst();
        if(db.checkisAdmin(user)){
            startActivity(new Intent(getApplicationContext(),AdminHome.class));
        }else{
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

    public void ShareRegisteredInfo(View view) {
        //Get the label from the textview and set to variables
        String luid = lUID.getText().toString().trim();
        String ln = lUNAME.getText().toString().trim();
        String lage = lUAGE.getText().toString().trim();
        String lphone = lUPHONE.getText().toString().trim();
        String luic = lUIC.getText().toString().trim();
        String lus = lUSTATE.getText().toString().trim();
        String lemail = lUEMAIL.getText().toString().trim();
        String laddress = lUADDRESS.getText().toString().trim();
        String lregisterdate = lUREGISTERDATE.getText().toString().trim();
        //Retrieve items set in the textview from database
       String name =  UNAME.getText().toString().trim();
        String age =  UAGE.getText().toString().trim();
        String icPass = UIC.getText().toString().trim();
        String phoneNo = UPHONE.getText().toString().trim();
        String state = USTATE.getText().toString().trim();
        String email =  UEMAIL.getText().toString().trim();
        String physAdd =  UADDRESS.getText().toString().trim();
        String registerdate = UREGISTERDATE.getText().toString().trim();

        //concatenate the strings into one string for easy sharing
        String all = ln + " : " + name + "\n" + lage + " : " + age + "\n" + luic + " : " + icPass + "\n" + lphone + " : " + phoneNo + "\n" +
                lus + " : " + state + "\n" + laddress+ " : " + physAdd + "\n" + lemail + " : " + email + "\n" + lregisterdate + ":" + registerdate;

        String mimeType = "text/plain";
        //perform the sharing process.
        ShareCompat.IntentBuilder.
                from(this).
                setType(mimeType).
                setChooserTitle("Share Via: ").
                setText(all).startChooser();



    }
}