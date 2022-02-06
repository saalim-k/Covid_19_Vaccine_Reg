package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    DatabaseManager db;
    private Button signin, signup;
    private EditText sUsername, sPW, sConfirmPW;
    private int role = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Initialize items in the view
        db = new DatabaseManager(this);
        sUsername = findViewById(R.id.etUsernameSignUp);
        sPW = findViewById(R.id.etPasswordSignUp);
        sConfirmPW = findViewById(R.id.etConfirmPasswordSignUp);

    }
    //when user clicks on backtologin button
    public void BackToLogin(View view) {
        Intent intent = new Intent(this,SignIn.class);
        startActivity(intent);

    }
    //when user clicks on signup button
    public void SignUp(View view) {
        //save the username and passwords to strings
        String username = sUsername.getText().toString();
        String password = sPW.getText().toString();
        String cPassword = sConfirmPW.getText().toString();
        //create ints for the role and isregistered and set them to 0
        // since on account created user will be normal user by default and will not be registered
        int role = 0;
        int registeredVaccine = 0;
        //if username validations and password validations dont pass
        if (!validateUsername() || !validatePassword() || !validateConfirmPW()){
            Toast.makeText(this,"All field are required",Toast.LENGTH_SHORT).show();
        }else{//if all validations pass
           //add the new users information into the databse
           db.addAccount(username,password,role,registeredVaccine);
           Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show();
           //set the textviews to empty
           sUsername.setText("");
           sPW.setText("");
           sConfirmPW.setText("");
           //send user to signin page
           startActivity(new Intent(this,SignIn.class));
        }
    }

    boolean validateUsername(){
        String val = sUsername.getText().toString().trim();
        //check if empty or if it has numbers
        if (val.isEmpty()) {
            sUsername.setError("Name cannot be empty");
            return false;
        } else if (!val.matches("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$")) {
            sUsername.setError("Username should contain between 5 to 18 characters!");
            return false;
        } else if (db.checkUsername(val)){//if username exists in the database
            sUsername.setError("Username exist");
            return false;
        }
        else {//remove all errors displayed
            sUsername.setError(null);
            return true;
        }
    }

    boolean validatePassword(){
        String val = sPW.getText().toString().trim();
        String patterPW= "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        if (val.isEmpty()){//check if empty
            sPW.setError("Password field cannot be empty");
            return false;
        }else if (!val.matches(patterPW)){//check if matches regex
            sPW.setError("Password Should contain at least 6 characters with 1 special character,no spaces");
            return false;
        }else{//remove all errors displayed and return true
            sPW.setError(null);
            return true;
        }
    }

    boolean validateConfirmPW(){
        String val = sConfirmPW.getText().toString();
        String valPW = sPW.getText().toString();
        if(val.isEmpty()){//check if empty
            sConfirmPW.setError("Confirm password field cannot be empty");
            return false;
        }
        else if (!val.equals(valPW)){//check if password and confirmpassword match
            sConfirmPW.setError("Confirm password must be same");
            return false;
        }else{
            sConfirmPW.setError(null);//remove all errors displayed and return true
            return true;
        }
    }
}