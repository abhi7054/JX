package com.xtreme.jx.model;

import java.io.Serializable;
import java.util.Date;

public class ComicReview implements Serializable {

    private String comicId;
    private String userId;
    private int rate;
    private String comicTitle;
    private String comicImage;
    private String userName;
    private String userImage;
    private Date timestamp;

    public ComicReview() {

    }

    public ComicReview(String comicId, String userId, int rate, String comicTitle, String comicImage, String userName, String userImage, Date timestamp) {
        this.comicId = comicId;
        this.userId = userId;
        this.rate = rate;
        this.comicTitle = comicTitle;
        this.comicImage = comicImage;
        this.userName = userName;
        this.userImage = userImage;
        this.timestamp = timestamp;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComicTitle() {
        return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    public String getComicImage() {
        return comicImage;
    }

    public void setComicImage(String comicImage) {
        this.comicImage = comicImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
