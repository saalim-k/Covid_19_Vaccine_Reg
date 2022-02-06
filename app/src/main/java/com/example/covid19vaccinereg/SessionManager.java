package com.example.covid19vaccinereg;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    //Initialixe variable
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    //Create constructor
    public SessionManager (Context context){
        sharedPreferences = context.getSharedPreferences("AppKey",0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    //Create set login method
    public void setLogin(boolean Login){
        editor.putBoolean("KEY_LOGIN",Login);
        editor.commit();
    }

    //create login get method
    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN",false);
    }

    //create set username method
    public void setUsername(String username){
        editor.putString("KEY_USERNAME",username);
        editor.commit();
    }

    //create get username method
    public String getUsername(){
        return sharedPreferences.getString("KEY_USERNAME","");
    }

    //create get id method
    public  int getID(){
        return sharedPreferences.getInt("KEY_ID",0);
    }

    //create set id method
    public  void setID(int id){
        editor.putInt("KEY_ID",id);
        editor.commit();
    }
}
