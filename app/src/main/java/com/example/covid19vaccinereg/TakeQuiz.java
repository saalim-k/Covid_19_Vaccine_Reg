package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TakeQuiz extends AppCompatActivity {
    private RadioGroup answerOpt1,answerOpt2,answerOpt3,answerOpt4,answerOpt5;
    private TextView txtQuestion1,txtQuestion2,txtQuestion3,txtQuestion4,txtQuestion5;
    private int currentQuestionIndex = 0;
    Dialog successDialog;
    DatabaseManager db;
    SessionManager sessionManager;

    //create question objects from the Question class.
    private Question[] questionBank = new Question[]{
            new Question(R.string.questions_1, "True"),
            new Question(R.string.questions_2, "False"),
            new Question(R.string.questions_3, "True"),
            new Question(R.string.questions_4,"True"),
            new Question(R.string.questions_5,"False")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        db = new DatabaseManager(this);
        sessionManager = new SessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //instantiate the elements using id resource
        answerOpt1 = findViewById(R.id.radioGroupAnswer1);
        answerOpt2=findViewById(R.id.radioGroupAnswer2);
        answerOpt3=findViewById(R.id.radioGroupAnswer3);
        answerOpt4=findViewById(R.id.radioGroupAnswer4);
        answerOpt5=findViewById(R.id.radioGroupAnswer5);
        txtQuestion1 = findViewById(R.id.tvQuestion1);
        txtQuestion2 = findViewById(R.id.tvQuestions2);
        txtQuestion3 = findViewById(R.id.tvQuestions3);
        txtQuestion4 = findViewById(R.id.tvQuestions4);
        txtQuestion5 = findViewById(R.id.tvQuestions5);
        //set the text for the questions from the getter using the question class
        txtQuestion1.setText(questionBank[0].getAnswerId());
        txtQuestion2.setText(questionBank[1].getAnswerId());
        txtQuestion3.setText(questionBank[2].getAnswerId());
        txtQuestion4.setText(questionBank[3].getAnswerId());
        txtQuestion5.setText(questionBank[4].getAnswerId());
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
                Intent intent = new Intent(this, SignIn.class);
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

    //when submit button is clicked
    public void submitAnswer(View view){
        //validate if all questions have been answered.
        if(answerOpt1.getCheckedRadioButtonId() == -1 || answerOpt1.getCheckedRadioButtonId() == -1 || answerOpt2.getCheckedRadioButtonId() == -1 || answerOpt3.getCheckedRadioButtonId() == -1 || answerOpt4.getCheckedRadioButtonId() == -1 || answerOpt5.getCheckedRadioButtonId() == -1)
        {
            //toast message if questions have not been answered
            Toast.makeText(this, R.string.ans_all_ques, Toast.LENGTH_SHORT).show();
        }
        else {
            //if all questions have been answered
            //check save the answers to strings
            String answerQ1 = ((RadioButton) findViewById(answerOpt1.getCheckedRadioButtonId())).getText().toString().trim();
            String answerQ2 = ((RadioButton) findViewById(answerOpt2.getCheckedRadioButtonId())).getText().toString().trim();
            String answerQ3 = ((RadioButton) findViewById(answerOpt3.getCheckedRadioButtonId())).getText().toString().trim();
            String answerQ4 = ((RadioButton) findViewById(answerOpt4.getCheckedRadioButtonId())).getText().toString().trim();
            String answerQ5 = ((RadioButton) findViewById(answerOpt5.getCheckedRadioButtonId())).getText().toString().trim();

            //check if the answers have been answered correctly
            String answer1 = questionBank[0].isAnswerCorrect();
            String answer2 = questionBank[1].isAnswerCorrect();
            String answer3 = questionBank[2].isAnswerCorrect();
            String answer4 = questionBank[3].isAnswerCorrect();
            String answer5 = questionBank[4].isAnswerCorrect();

            //if all questions have been answered correctly
            if (answerQ1.equalsIgnoreCase(answer1) && answerQ2.equalsIgnoreCase(answer2) && answerQ3.equalsIgnoreCase(answer3) && answerQ4.equalsIgnoreCase(answer4) && answerQ5.equalsIgnoreCase(answer5)) {
                Toast.makeText(this, R.string.correct_ans, Toast.LENGTH_SHORT).show();
                //create a dialog box and connect it to success_dialog.xml and display it.
                successDialog = new Dialog(this);
                successDialog.setContentView(R.layout.success_dialog);
                successDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                successDialog.show();
                //instantiate the buttons inside the dialog box using id resource
                //set onclick listeners for the buttons inside the dialog box.
                Button btnReg = successDialog.findViewById(R.id.btnRegNow);
                Button btnHome = successDialog.findViewById(R.id.btnGoHome);
                //if register button is clicked start a new activity and send the user to the registration activity.
                btnReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TakeQuiz.this,Registeration_info.class);
                        startActivity(intent);
                    }
                });
                //if back to home button is clicked start a new activity and send the user to the main activity.
                btnHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TakeQuiz.this,MainActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                // if the questions have not been answered correctly output a toast message informing the user of this.
                Toast.makeText(this, R.string.incorrect_ans, Toast.LENGTH_SHORT).show();
            }
        }
    }
}