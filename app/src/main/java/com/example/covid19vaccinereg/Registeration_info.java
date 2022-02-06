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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Registeration_info extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //create variables used in this class
    EditText Name, Age, IcPass, PhoneNo, email, physicalAdd;
    CheckBox cbConsent;
    Spinner State;
    SessionManager sessionManager;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration_info);
        //instantiate the elements using the id resource
        Name = findViewById(R.id.et_Name);
        Age = findViewById(R.id.et_Age);
        IcPass = findViewById(R.id.et_Ic_Passport_No);
        PhoneNo = findViewById(R.id.et_phone_no);
        email = findViewById(R.id.et_Email_add);
        physicalAdd = findViewById(R.id.et_Phys_Add);

        db = new DatabaseManager(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Spinner spinner = findViewById(R.id.spinner_state);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        // Create ArrayAdapter using the string array and default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
        State = findViewById(R.id.spinner_state);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    //when submit button is clicked to submit information
    public void submitRegInfo(View view) {
        //check if validations are met
        if (!validateName() || !validateAge() || !validatePhoneNum() || !validateIcPass() || !validateEmailAdd() || !validateConsent()) {
            //output toast message if validations are not met
            Toast.makeText(this, R.string.ans_all_ques, Toast.LENGTH_SHORT).show();
        } else {
            //if validations are met
            //get the text values for the variables
            String name = Name.getText().toString().trim();
            String age = Age.getText().toString().trim();
            String icPass = IcPass.getText().toString().trim();
            String phoneNo = PhoneNo.getText().toString().trim();
            String emails = email.getText().toString().trim();
            String physicalAdds = physicalAdd.getText().toString().trim();
            String state = State.getSelectedItem().toString();
            //create new intent to start new activity
            Intent intent = new Intent(this, ConfirmRegInfo.class);
            //create bundle and save the strings into the bundle
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("age", age);
            bundle.putString("icPass", icPass);
            bundle.putString("phoneNo", phoneNo);
            bundle.putString("email", emails);
            bundle.putString("physicalAdd", physicalAdds);
            bundle.putString("state", state);
            //pass the bundle as an intent to the new activity
            intent.putExtras(bundle);
            //start the new activity
            startActivity(intent);
        }
    }

    //validate name input
    private boolean validateName() {
        String val = Name.getText().toString().trim();
        //check if empty or if it has numbers
        if (val.isEmpty()) {
            Name.setError("Name cannot be empty");
            return false;
        } else if (!val.matches("^[a-z A-Z]+")) {
            Name.setError("Name cannot have Numbers!");
            return false;
        } else {
            Name.setError(null);
            return true;
        }

    }
    //validate age input
    //only allow age from 0 to 120
    private boolean validateAge() {
        String sVal = Age.getText().toString().trim();
        if (sVal.isEmpty()) {
            Age.setError("Age cannot be empty");
            return false;
        } else {
            try {
                int val = Integer.parseInt(sVal);
                if (val < 0 || val > 120) {
                    Age.setError("Age must be between 0 to 120");
                    return false;
                } else {
                    Age.setError(null);
                    return true;
                }
            } catch (NumberFormatException nfe) {
                Age.setError("Age must be a number");
                return false;
            }
        }

    }
    //validate Ic number and passport number
    //dont allow empty text
    //ic or passport can only be between 9 to 12 characters long
    private boolean validateIcPass() {
        String val = IcPass.getText().toString().trim();
        if (val.isEmpty()) {
            IcPass.setError("Ic or passport cannot be empty");
            return false;
        } else if (val.length() < 9 || val.length() > 12) {
            IcPass.setError("Cannot be less than 9 or greater than 12 characters");
            return false;
        } else {
            IcPass.setError(null);
            return true;
        }
    }

    //validate phone number to check if empty and regex for malaysian phone number
    private boolean validatePhoneNum() {
        String phonepatern = "(\\+?6?01)[0-46-9]-*[0-9]{7,8}";
        String val = PhoneNo.getText().toString().trim();
        if (val.isEmpty()) {
            PhoneNo.setError("Phone number cannot be blank");
            return false;
        } else if (!val.matches(phonepatern)) {
            PhoneNo.setError("Please enter a valid malaysian phone number");
            return false;
        } else {
            PhoneNo.setError(null);
            return true;
        }

    }

    //validate email input
    //check with email regex
    private boolean validateEmailAdd() {
        String val = email.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    //validate consent checkbox has been clicked
    private boolean validateConsent() {
        cbConsent = findViewById(R.id.cbConsent);
        if (!cbConsent.isChecked()) {
            cbConsent.setError("Please check this box to continue!");
            return false;
        } else
            return true;
    }

}