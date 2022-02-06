package com.example.covid19vaccinereg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditNews extends AppCompatActivity {
    DatabaseManager db;
    Button mBtnSave, mBtnCancel;
    EditText mEtHeader, mEtLink, mEtContent;
    TextView mTvNewsID;
    ImageView mIvNewsImage;
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri imageFilePath;
    private Bitmap imageToStore;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_news);
        //Initialize database manager
        db = new DatabaseManager(getBaseContext());
        mBtnSave = findViewById(R.id.btnSaveNews);
        mBtnCancel = findViewById(R.id.btnCancelNewsEdit);
        mEtHeader = findViewById(R.id.etHeader);
        mEtLink = findViewById(R.id.etLink);
        mEtContent = findViewById(R.id.etNewsContent);
        mTvNewsID = findViewById(R.id.tvId);
        mIvNewsImage = findViewById(R.id.ivEditNewsImage);

        //Initialize sessionmanager
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //Get the intent that passes the News Id from previous activity
        String newsId = getIntent().getStringExtra("NewsId");
        //Check if news id exist
        if (newsId != null) {
            //set text from getintent
            mTvNewsID.setText(newsId);
            //Get the news information from New table in database using News Id
            Cursor cursor = db.getNewsFromId(Integer.parseInt(newsId.trim()));
            if (cursor.moveToFirst()) {
                //Set the information of the news from the column and save to variable
                String newsHeader = cursor.getString(1);
                String newsLink = cursor.getString(3);
                String newsText = cursor.getString(4);
                byte[] imagebytes = cursor.getBlob(2);
                Bitmap nImage = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
                //Set the news information to their respective views
                mEtHeader.setText(newsHeader);
                mEtLink.setText(newsLink);
                mEtContent.setText(newsText);
                mIvNewsImage.setImageBitmap(nImage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        //check if user logged in
        if (sessionManager.getLogin()) {
            //Get username from session
            String user = sessionManager.getUsername();
            //Set the title of sign in menu item to username that logged in
            signin.setTitle(user);
            //Disable the sign in menu item after user logged in
            signin.setEnabled(false);
            super.onCreateOptionsMenu(menu);
        } else {
            //Hide the sign out menu item
            signout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            //When sign in selected
            case R.id.signin:
                //Redirect user to sign in page
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
            //When My Profile selected
            case R.id.myProfile:
                //Check if user logged in
                if (sessionManager.getLogin()) {
                    //Redirect user to my profle page after logged in
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    //Set Alert Title
                    alert.setTitle("My Profile");
                    //Set Alert Message
                    alert.setMessage("Log in to access My Profile \n\n Do you wish to sign in?");
                    //Set positive button
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Redirect user to sign in page if not logged in
                            startActivity(new Intent(getApplicationContext(), SignIn.class));
                        }
                    });
                    //Set negative button
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel action
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return true;
                //When home is selected
            case R.id.Home:
                //Check if user had logged in
                if (!sessionManager.getLogin()) {
                    //Redirect user to main homepage if user not logged in
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    //if user logged in
                    //Get the username
                    String user = sessionManager.getUsername();
                    //Get the user info from account table
                    Cursor cursor = db.getAccountInfo(user);
                    cursor.moveToFirst();
                    //Check if user is Admin from Account table in database
                    if (db.checkisAdmin(user)) {
                        //If user is Admin, redirect Admin Homepage
                        startActivity(new Intent(getApplicationContext(), AdminHome.class));
                    } else {
                        //If user is Normal, redirect Main Homepage
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadNewsImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageFilePath = data.getData();
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
                mIvNewsImage.setImageBitmap(imageToStore);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println(e);
        }
    }

    public void saveNewsToDatabase(View view) {
        if (mTvNewsID.getText().toString().isEmpty()) {
            if (!validateAll()) {
                Toast.makeText(this, "Please make sure there are no errors", Toast.LENGTH_SHORT).show();
            } else {
                //new news
                String nHeader = mEtHeader.getText().toString().trim();
                String nLink = mEtLink.getText().toString().trim();
                String nContent = mEtContent.getText().toString().trim();
                BitmapDrawable bd = (BitmapDrawable) mIvNewsImage.getDrawable();
                Bitmap newsImageBitmap = bd.getBitmap();
                if (db.addNews(nHeader, newsImageBitmap, nLink, nContent)) {
                    Toast.makeText(this, "News Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, ManageNews.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Error adding news", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!validateAll()) {
                Toast.makeText(this, "Please make sure there are no errors", Toast.LENGTH_SHORT).show();
            } else {
                //edited news
                int nId = Integer.parseInt(mTvNewsID.getText().toString().trim());
                String nHeader = mEtHeader.getText().toString().trim();
                String nLink = mEtLink.getText().toString().trim();
                String nContent = mEtContent.getText().toString().trim();
                BitmapDrawable bd = (BitmapDrawable) mIvNewsImage.getDrawable();
                Bitmap newsImageBitmap = bd.getBitmap();

                if (db.updateNews(nId, nHeader, newsImageBitmap, nLink, nContent)) {
                    Toast.makeText(this, "News Edited Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, ManageNews.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Error Editing news", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void goBackToViewingAllNews(View view) {
        Intent i = new Intent(this, ManageNews.class);
        startActivity(i);
    }

    boolean validateHeader() {
        String val = mEtHeader.getText().toString().trim();
        //check if empty or if it has numbers
        if (val.isEmpty()) {
            mEtHeader.setError("Header cannot be empty");
            return false;
        } else if (val.length() < 10 || val.length() > 30) {
            mEtHeader.setError("News Header must have more than 10 and less than 30 characters");
            return false;
        } else {
            mEtHeader.setError(null);
            return true;
        }
    }

    boolean validateAll() {
        if (!validateImage() || !validateHeader() || !validateLink() || !validateContent()) {
            return false;
        } else {
            return true;
        }
    }

    boolean validateLink() {
        String val = mEtLink.getText().toString().trim();
        if (val.isEmpty()) {
            mEtLink.setError("Link cannot be empty");
            return false;
        } else {
            mEtLink.setError(null);
            return true;
        }
    }

    boolean validateContent() {
        String val = mEtContent.getText().toString().trim();
        if (val.isEmpty()) {
            mEtContent.setError("News Content cannot be empty");
            return false;
        } else {
            mEtContent.setError(null);
            return true;
        }
    }

    boolean validateImage() {
        if (mIvNewsImage.getDrawable()==null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }
}