package com.example.covid19vaccinereg;

import android.graphics.Bitmap;

public class News {
    String newsHeader, newsLink, newsText;
    int newsId;
    Bitmap newsImage;

    public News(int newsId, String newsHeader, String newsLink, String newsText, Bitmap newsImage) {
        this.newsId=newsId;
        this.newsHeader = newsHeader;
        this.newsLink = newsLink;
        this.newsText = newsText;
        this.newsImage = newsImage;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getNewsHeader() {
        return newsHeader;
    }

    public void setNewsHeader(String newsHeader) {
        this.newsHeader = newsHeader;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public Bitmap getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.newsImage = newsImage;
    }
}
