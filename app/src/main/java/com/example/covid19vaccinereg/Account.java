package com.example.covid19vaccinereg;

public class Account {
    int id,role,isRegistered;
    String username, password;

    public Account(int id, String username, String password, int role, int isRegistered) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isRegistered = isRegistered;
    }

    public Account(int id, String username,int role, int isRegistered) {
        this.id = id;
        this.role = role;
        this.isRegistered = isRegistered;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public int getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(int isRegistered) {
        this.isRegistered = isRegistered;
    }
}
