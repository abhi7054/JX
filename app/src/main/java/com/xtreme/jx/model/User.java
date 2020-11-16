package com.xtreme.jx.model;

import java.util.Date;

public class User {

    private String docId;
    private String username = "";
    private String email = "";
    private String image = "";
    private String name = "";
    private Date timestamp;

    public User() {

    }

    public User(String username, String email, String image, String name, Date timestamp) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
