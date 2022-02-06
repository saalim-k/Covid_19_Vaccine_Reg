package com.example.covid19vaccinereg;

public class Question {
    private int answerId;
    private String answerCorrect;

    //set the constructor for the questions
    public Question(int answerId, String answerCorrect) {
        this.answerId = answerId;
        this.answerCorrect = answerCorrect;
    }
    //getter to get the answer
    public int getAnswerId() {
        return answerId;
    }
    //check if answer is correct
    public String isAnswerCorrect() {
        return answerCorrect;
    }
    //setter to set the correct answer
    public void setAnswerCorrect(String answerCorrect) {
        this.answerCorrect = answerCorrect;
    }



}
