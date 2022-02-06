package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class Administrative_center extends AppCompatActivity {
    //reference to the type of views that will be used
    RecyclerView recyclerView;
    //reference the list to be used
    List<AdminCentreGet> adminCentreGetList;
    DatabaseManager db;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrative_center);

        //Initialize DatabaseManager
        db = new DatabaseManager(this);
        //Initialize SessionManager
        sessionManager = new SessionManager(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.rvAdminCentre);

        initData();
        setRecyclerView();

    }

    //Set the adapter class to the recycler view
    private void setRecyclerView() {
        AdmincentreAdapter admincentreAdapter = new AdmincentreAdapter(adminCentreGetList);
        recyclerView.setAdapter(admincentreAdapter);
        recyclerView.setHasFixedSize(true);
    }

    //creates the array of items for AdminCentreGet class
    private void initData() {
        adminCentreGetList = new ArrayList<>();
        //adding items to the adminCentreGetList
        adminCentreGetList.add(new AdminCentreGet("Johor","Hospital Permai","Hospital Segamat","Hospital Tangkak","Dewan Jubli Intan","Dewan Maharani","Dewan Raya Bandar Seri Alam,Masai"));
        adminCentreGetList.add(new AdminCentreGet("Kedah","Dewan Al Hana,Kedah","Dewan Bandaran Kulim","Dewan Serbaguna Guar Chempedak","Hospital Baling","Hospital Langkawi","Hospital Yan"));
        adminCentreGetList.add(new AdminCentreGet("Kelantan","Dewan APMM","Dewan Bunga Raya Pantai Seri Tujuh","Dewan Kopeladar","Hospital Jeli","Hospital Tengku Anis","Hospital Machang"));
        adminCentreGetList.add(new AdminCentreGet("Malacca","Dewan Bunga Tanjung","Dewan Japerun Sg Udang","Dewan MMU","Hospital Alor Gajah","Hospital Melaka","Hospital Jasin"));
        adminCentreGetList.add(new AdminCentreGet("Negeri Sembilan","Dewan Dato Bahaman","Dewan Perdana Tampin","Klinik Kesihatan Nilai","Klinik Kesihatan Bahau","Hospital Tampin","Hospital Jelebu"));
        adminCentreGetList.add(new AdminCentreGet("Pahang","Dewan Jubli Perak","Dewan Balora Benta,Lipis","Dewan Besar Dara Muadzam Shah","Hospital Pekan","Hospital Rompin","Hospital Tengku Ampuan Afzan"));
        adminCentreGetList.add(new AdminCentreGet("Penang","Dewan Milenium Kepala Batas","Dewan Serbaguna Dewi","Kompleks Masyarakat Penyayang","Hospital Serbaguna Dewi","Hospital Balik Pulau","Hospital Seberang Jaya"));
        adminCentreGetList.add(new AdminCentreGet("Perak","Dewan Bandaran Kampar","Dewan Bandaran Pengkalan Hulu","Dewan Merdeka Lenggong","Hospital Batu Gajah","Hospital Kuala Kangsar","Hospital Kampar"));
        adminCentreGetList.add(new AdminCentreGet("Perlis","Dewan 2020","Dewan Seri Melati,Perlis","Dewan Warisan,Perlis","Dewan Warisan, Perlis","Dewan Seri Melati,Perlis","Hospital Tuanku Fauziah"));
        adminCentreGetList.add(new AdminCentreGet("Sabah","Dewan Belia Arena KBBS, Pitas","Dewan Masyarakat,Tawau","Dewan Masyarakat Keningau","Hospital Tawau","Hospital Tenom","Hospital Tuaran"));
        adminCentreGetList.add(new AdminCentreGet("Sarawak","Dewan Eastwood Valley","Dewan Kampung Gua","Dewan Masyarakat Bekenu","Hospital Miri","Hospital Sibu","Hospital Umum Sarawak"));
        adminCentreGetList.add(new AdminCentreGet("Selangor","Dewan Komuniti Serendah","Dewan Sukan Pandaraman","Dewan Semai Bakti Felda Soeharto","Hospital Kajang","Hospital Kuala Kubu Bharu","Hospital Selayang"));
        adminCentreGetList.add(new AdminCentreGet("Terengannu","Dewan Gong Kemuntong","Dewan Sivik Bukit Payong","Dewan Sivik Ajil","Hospital Besut","Hospital Kemaman","Hospital Setiu"));

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