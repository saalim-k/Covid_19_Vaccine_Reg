package com.example.covid19vaccinereg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyProfile extends AppCompatActivity {

    List<Account> accountList;
    ImageView imgChangePassword;
    CardView cvChangePassword;
    TextView UserId, UserRole;
    EditText Username, Password, oldPW, newPW, confirmNewPW, newUsername;
    SessionManager sessionManager;
    DatabaseManager db;
    Dialog changeUsername, changePassword;
    Button btnViewRegistrationInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        db = new DatabaseManager(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        UserId = findViewById(R.id.tvUserIDInfo);
        UserRole = findViewById(R.id.tvUserRole);
        Username = findViewById(R.id.etNewUsername);
        Password = findViewById(R.id.etPassword);
        imgChangePassword = findViewById(R.id.imgChangePassword);
        cvChangePassword = findViewById(R.id.cvChangePassword);
        btnViewRegistrationInfo = findViewById(R.id.btnViewRegisteredVaccine);

        String user = sessionManager.getUsername();
        Cursor cursor = db.getAccountInfo(user);
        if (cursor.moveToFirst()) {
            int userid = cursor.getInt(cursor.getColumnIndex("id"));
            String userN = cursor.getString(cursor.getColumnIndex("username"));
            String userPW = cursor.getString(cursor.getColumnIndex("password"));
            String role = cursor.getString(cursor.getColumnIndex("role"));
            int registeredV = cursor.getInt(cursor.getColumnIndex("isvaccineregistered"));
            if (role.equals("1")) {
                UserRole.setText("Admin");
            } else {
                UserRole.setText("Normal User");
            }
            //save info into textviews and edit text
            UserId.setText(String.valueOf(userid));
            Username.setText(userN);
            Password.setText(userPW);
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


    public void EditUsername(View view) {
        //create a dialog box and connect it to change_username_dialog.xml and display it.
        changeUsername = new Dialog(this);
        changeUsername.setContentView(R.layout.change_username_dialog);
        changeUsername.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeUsername.show();
        //instantiate the button inside the dialog box using id resource
        //set onclick listeners for the buttons inside the dialog box.
        Button btnSaveNewUsername = changeUsername.findViewById(R.id.btnSaveUsername);
        Button btnCancelUsername = changeUsername.findViewById(R.id.btnCancelChangeUsername);
        EditText Nuser = changeUsername.findViewById(R.id.etNewUsername);

        //if save changes clicked validations will be done and database will be updated.
        btnSaveNewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Nuser.getText().toString().trim();
                //When fields are empty
                if (!validateNewUsername()) {
                    Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT).show();
                } else {
                    //WHen fields are not empty
                    //Get username from session manager
                    String user = sessionManager.getUsername();
                    //get user id from table in database using user
                    Cursor cursor = db.getUserId(user);
                    cursor.moveToFirst();
                    //Save user id to variable
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    //Check if user is admin
                    if (db.checkisAdmin(user)) {
                        //When user is Admin
                        //Update new username to account table passing in id and username
                        if (db.updateNewUsername(id, username)) {
                            //When username succesfully changed
                            //Set new username in session manager
                            sessionManager.setUsername(username);
                            //Whne user is admin, redirect to Admin Home after changed applied
                            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Changes Not Saved",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //When user is normal user
                        if (db.updateNewUsername(id, username)) {
                            //When update username is successful
                            //Set new username in session manager
                            sessionManager.setUsername(username);
                            //Redirect normal user to main acitivyt afte changes applied
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Changes Not Saved",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //if cancel clicked
        btnCancelUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });

    }

    public void EditPassword(View view) {
        //create a dialog box and connect it to change_username_dialog.xml and display it.
        changePassword = new Dialog(this);
        changePassword.setContentView(R.layout.change_password_dialog);
        changePassword.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changePassword.show();
        //instantiate the button inside the dialog box using id resource
        Button btnSaveNewPassword = changePassword.findViewById(R.id.btnSaveNewPassword);
        Button btnCancelChange = changePassword.findViewById(R.id.btnCancelChangePassword);
        EditText NewPass = changePassword.findViewById(R.id.etNewPassword);

        //set onclick listeners for the buttons inside the dialog box.
        //if Save changes clicked validations will be done and database will be updated.
        btnSaveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassw = NewPass.getText().toString().trim();
                //When validation are not met
                if (!validateOldPassword() || !validateNewPassword() || !validateConfirmNewPassword()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    //When validations are met
                    //Get username from session manager
                    String user = sessionManager.getUsername();
                    //Get user id using username
                    Cursor cursor = db.getUserId(user);
                    cursor.moveToFirst();
                    //Save user id to variable from column id in account table
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    //Check if user is Admin
                    if (db.checkisAdmin(user)){
                        //When user is admin
                        if (db.updateNewPassword(id, newPassw)) {
                            //When update new password is successful
                            Toast.makeText(getApplicationContext(), "Password Change Successful", Toast.LENGTH_SHORT).show();
                            //Redirect to Admin homepage, when user is Admin after changes applied successfully
                            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Changes Not Saved",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (db.updateNewPassword(id, newPassw)) {
                            //When update new password to database is successful
                            Toast.makeText(getApplicationContext(), "Password Change Successful", Toast.LENGTH_SHORT).show();
                            //Redirect to Main Homepage homepage, when user is Normal User after changes applied successfully
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Changes Not Saved",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        btnCancelChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });
    }

    boolean validateOldPassword() {
        oldPW = changePassword.findViewById(R.id.etOldPassword);
        String oldPw = oldPW.getText().toString().trim();
        String user = sessionManager.getUsername();
        Cursor cursor = db.getAccountInfo(user);
        cursor.moveToFirst();
        String oldUserPW = cursor.getString(cursor.getColumnIndex("password"));
        //When fiedl are empty
        if (oldPw.isEmpty()) {
            oldPW.setError("Field required \n Please enter Old Password");
            return false;
        }
        //Compare password given to the password in database
        else if (!oldPw.matches(oldUserPW)) {
            //When password does not match in databased
            oldPW.setError("Password doest not match old password");
            return false;
        } else {
            oldPW.setError(null);
            return true;
        }
    }

    boolean validateNewPassword() {
        newPW = changePassword.findViewById(R.id.etNewPassword);
        String newPw = newPW.getText().toString().trim();
        String patterPW = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        if (newPw.isEmpty()) {
            //When field are empty
            newPW.setError("Field required! \n Please enter New Password");
            return false;
            //When new password does not meet requirements
        } else if (!newPw.matches(patterPW)) {
            newPW.setError("Password Should contain at least 6 characters containing 1 uppercase letter, 1 lowercase letter, 1 number, 1 special character,no spaces");
            return false;
        } else {
            newPW.setError(null);
            return true;
        }
    }

    boolean validateConfirmNewPassword() {
        newPW = changePassword.findViewById(R.id.etNewPassword);
        confirmNewPW = changePassword.findViewById(R.id.etConfirmNewPassword);
        String confirmNPW = confirmNewPW.getText().toString();
        String newPw = newPW.getText().toString();
        //When field is empty
        if (confirmNPW.isEmpty()) {
            confirmNewPW.setError("Confirm password field cannot be empty");
            return false;
            //When confirm new password does not match with new password given
        } else if (!confirmNPW.equals(newPw)) {
            confirmNewPW.setError("Confirm password must be same");
            return false;
        } else {
            confirmNewPW.setError(null);
            return true;
        }
    }

    boolean validateNewUsername() {
        newUsername = changeUsername.findViewById(R.id.etNewUsername);
        String newUN = newUsername.getText().toString().trim();
        //check if empty or if it has numbers
        if (newUN.isEmpty()) {
            newUsername.setError("Field Required! \n Enter New Username");
            return false;
            //Check if username meet requirements
        } else if (!newUN.matches("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$")) {
            //When username does not meet requirements
            newUsername.setError("Name should contain between 5 to 18 characters !");
            return false;
            //Check if username exist in database
        } else if (db.checkUsername(newUN)) {
            //When username exist in database
            newUsername.setError("Username exist");
            return false;
        } else {
            newUsername.setError(null);
            return true;
        }
    }

    public void ViewVaccineRegistration(View view) {
        String user = sessionManager.getUsername();
        Cursor cursor = db.getUserId(user);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        System.out.println(id);
        //Check if user id exist in register table in database
        if (db.getAccountWithVaccRegInfo(String.valueOf(id)).moveToFirst()) {
            //When user id exist in table
            //Redirect user to ViewMyVaccineRegistration page.
            Intent intent = new Intent(getApplicationContext(), ViewMyVaccineRegistration.class);
            //Passinf intent of user id to ViewMyVaccineRegistration page
            intent.putExtra("uid", String.valueOf(id));
            System.out.println(id);
            startActivity(intent);
        } else {
            //When user id does not exist is registered table in database
            AlertDialog.Builder alertB = new AlertDialog.Builder(this);
            alertB.setTitle("View My Registration");
            alertB.setMessage("You have yet to register for a vaccine \n\n Do you wish to register for Vaccine?");
            alertB.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Redirect user to take quiz activity when user not yet registered
                    startActivity(new Intent(getApplicationContext(), TakeQuiz.class));
                    finish();
                }
            });
            alertB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertB.show();
        }

    }

}