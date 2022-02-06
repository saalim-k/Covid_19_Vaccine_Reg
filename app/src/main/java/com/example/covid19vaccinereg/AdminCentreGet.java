package com.example.covid19vaccinereg;

public class AdminCentreGet {
    //Define variables in the class.
    public String states,centre1,centre2,centre3,centre4,centre5,centre6;
    private boolean expandable;

    //Initialize the dataset.
    public AdminCentreGet(String states, String centre1, String centre2, String centre3, String centre4, String centre5, String centre6) {
        this.states = states;
        this.centre1 = centre1;
        this.centre2 = centre2;
        this.centre3 = centre3;
        this.centre4 = centre4;
        this.centre5 = centre5;
        this.centre6 = centre6;
        this.expandable = false;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getStates() {
        return states;
    }

    public String getCentre1() {
        return centre1;
    }

    public String getCentre2() {
        return centre2;
    }

    public String getCentre3() {
        return centre3;
    }

    public String getCentre4() {
        return centre4;
    }

    public String getCentre5() {
        return centre5;
    }

    public String getCentre6() {
        return centre6;
    }


    public void setStates(String states) {
        this.states = states;
    }

    public void setCentre1(String centre1) {
        this.centre1 = centre1;
    }

    public void setCentre2(String centre2) {
        this.centre2 = centre2;
    }

    public void setCentre3(String centre3) {
        this.centre3 = centre3;
    }

    public void setCentre4(String centre4) {
        this.centre4 = centre4;
    }

    public void setCentre5(String centre5) {
        this.centre5 = centre5;
    }

    public void setCentre6(String centre6) {
        this.centre6 = centre6;
    }



    @Override
    public String toString() {
        return "AdminCentreGet{" +
                "states='" + states + '\'' +
                ", centre1='" + centre1 + '\'' +
                ", centre2='" + centre2 + '\'' +
                ", centre3='" + centre3 + '\'' +
                ", centre4='" + centre4 + '\'' +
                ", centre5='" + centre5 + '\'' +
                ", centre6='" + centre6 + '\'' +
                '}';
    }
}
