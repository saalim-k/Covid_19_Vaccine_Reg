package com.example.covid19vaccinereg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRegisteredUserInfo extends AppCompatActivity {
    TextView mtvHeader,mtvUserId,mtvRegDate,mtvUserFullName,mtvUsertype,mtvUserAge,mtvUserIcPass,mtvPhone,mtvState,mtvEmail,mtvAddress;
    DatabaseManager db;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_registered_user_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        sessionManager = new SessionManager(this);
        db=new DatabaseManager(getApplicationContext());
        String UserId=getIntent().getStringExtra("Uid");
        System.out.println(UserId);
        String Username=getIntent().getStringExtra("Uname");
        String UserType=getIntent().getStringExtra("URole");
        String UserIsReg=getIntent().getStringExtra("isRegistered");
        mtvHeader=findViewById(R.id.HeaderRegistrationInformation);
        mtvUserId=findViewById(R.id.tvUserIDSet);
        mtvUserFullName = findViewById(R.id.tvSetFullName);
        mtvUsertype = findViewById(R.id.tvSetUserRole);
        mtvUserAge = findViewById(R.id.tvSetAge);
        mtvUserIcPass = findViewById(R.id.tvSet_Ic_Pass);
        mtvPhone=findViewById(R.id.tvSetPhoneNumber);
        mtvState = findViewById(R.id.tvSetState);
        mtvEmail = findViewById(R.id.tvSetEmail);
        mtvAddress=findViewById(R.id.tvSetPhysAdd);
        mtvRegDate=findViewById(R.id.tvSetRegDate);
        setRegistrationContent(Username,UserId,UserType);

    }

    void setRegistrationContent(String Username,String UserId,String Utype)
    {
        //setHeader to contain users name
        mtvHeader.setText(Username+"'s Registration Information");
        //retrieve information from database
        Cursor cursor = db.getAccountWithVaccRegInfo(UserId);
        if(cursor.moveToFirst()) {
            String FullName = cursor.getString(cursor.getColumnIndex("name"));
            int UserAge = cursor.getInt(cursor.getColumnIndex("age"));
            String UserIcPass = cursor.getString(cursor.getColumnIndex("identification"));
            String UserPhone = cursor.getString(cursor.getColumnIndex("phone"));
            String UserState = cursor.getString(cursor.getColumnIndex("state"));
            String UserEmail = cursor.getString(cursor.getColumnIndex("email"));

            if (Utype.equals("1")) {
                mtvUsertype.setText("Admin");
            } else {
                mtvUsertype.setText("Normal User");
            }
            String UserAddress = cursor.getString(cursor.getColumnIndex("address"));
            String UserRegDate = cursor.getString(cursor.getColumnIndex("registerDate"));
            //save info into TextViews
            mtvUserId.setText(UserId);
            mtvUserFullName.setText(FullName);
            mtvUserAge.setText(String.valueOf(UserAge));
            mtvUserIcPass.setText(UserIcPass);
            mtvPhone.setText(UserPhone);
            mtvState.setText(UserState);
            mtvEmail.setText(UserEmail);
            mtvAddress.setText(UserAddress);
            mtvRegDate.setText(UserRegDate);
        }else
        {
            Toast.makeText(this, "User has not registered for the vaccine", Toast.LENGTH_SHORT).show();
        }
    }
    //Set information into fields

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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