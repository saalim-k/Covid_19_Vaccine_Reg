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
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ViewAccountInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    RecyclerView rvAccountInfo;
    List<Account> accountList;
    DatabaseManager mDatabase;
    EditText mEtSearchUser;
    Button mbtnSearchUser;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account_info);

        sessionManager = new SessionManager(getApplicationContext());
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Spinner spinner = findViewById(R.id.SpinnerFilterBy);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        // Create ArrayAdapter using the string array and default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_of_users_filter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }

        mEtSearchUser = findViewById(R.id.etSearchUser);
        mbtnSearchUser = findViewById(R.id.btnSearchUser);

        mDatabase = new DatabaseManager(getBaseContext());
        accountList = new ArrayList<>();
        rvAccountInfo = (RecyclerView) findViewById(R.id.rvUserInfo);
        LoadAccountsFromDatabase();
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
                    Cursor cursor = mDatabase.getAccountInfo(user);
                    cursor.moveToFirst();
                    if(mDatabase.checkisAdmin(user)){
                        startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    }else{
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void LoadAccountsFromDatabase(){
        Cursor cursor = mDatabase.getAllAccounts(sessionManager.getUsername());

        if(cursor.moveToFirst()){
            accountList.clear();
            do{
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this,R.layout.user_layout_view,accountList,mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(this));
    }
    private void LoadRegularAccountsFromDatabase() {
        Cursor cursor = mDatabase.getNonAdminsAccounts();

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this,R.layout.user_layout_view,accountList,mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(this));
    }
    private void LoadRegisteredAccountsFromDatabase() {
        Cursor cursor = mDatabase.getRegisteredAccounts(sessionManager.getUsername());

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this, R.layout.user_layout_view, accountList, mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(ViewAccountInfoActivity.this));
    }
    private void LoadUnregisteredAccountsFromDatabase() {
        Cursor cursor = mDatabase.getUnregisteredAccounts(sessionManager.getUsername());

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this,R.layout.user_layout_view,accountList,mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(this));
    }
    private void LoadAdminAccountsFromDatabase() {
        Cursor cursor = mDatabase.getAdminsAccounts(sessionManager.getUsername());

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this,R.layout.user_layout_view,accountList,mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(parent.getItemAtPosition(position).toString());
        mEtSearchUser.setText("");
        if(parent.getItemAtPosition(position).toString().trim().equalsIgnoreCase("All"))
        {
            LoadAccountsFromDatabase();
        }
        else if(parent.getItemAtPosition(position).toString().trim().equalsIgnoreCase("Admin"))
        {
            LoadAdminAccountsFromDatabase();
        }
        else if (parent.getItemAtPosition(position).toString().trim().equalsIgnoreCase("Regular User"))
        {
            LoadRegularAccountsFromDatabase();
        }
        else if(parent.getItemAtPosition(position).toString().trim().equalsIgnoreCase("Registered"))
        {
            LoadRegisteredAccountsFromDatabase();
        }
        else
        {
            LoadUnregisteredAccountsFromDatabase();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void SearchUsers(View view) {
        Cursor cursor = mDatabase.getSearchedUser(mEtSearchUser.getText().toString(),sessionManager.getUsername());

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        else
        {
            accountList.clear();
        }
        AccountAdapter adapter = new AccountAdapter(this,R.layout.user_layout_view,accountList,mDatabase);
        rvAccountInfo.setAdapter(adapter);
        rvAccountInfo.setLayoutManager(new LinearLayoutManager(this));
    }

}