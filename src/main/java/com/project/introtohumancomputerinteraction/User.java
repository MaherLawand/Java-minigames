package com.project.introtohumancomputerinteraction;

import javafx.scene.image.Image;

public class User {

    private int userid;
    private String username;

    private Image profile_picture;
    private boolean level1_completed;
    private boolean level2_completed;
    private boolean level3_completed;
    private boolean level4_completed;
    private boolean level5_completed;
    private int total_score;
    private int level1_highScore;
    private int level2_highScore;
    private int level3_highScore;
    private int level4_highScore;
    private int level5_highScore;

    public User(int userid, String username, Image profile_picture, boolean level1_completed, boolean level2_completed, boolean level3_completed, boolean level4_completed, boolean level5_completed, int total_score, int level1_highScore, int level2_highScore, int level3_highScore, int level4_highScore, int level5_highScore) {
        this.userid = userid;
        this.username = username;
        this.profile_picture = profile_picture;
        this.level1_completed = level1_completed;
        this.level2_completed = level2_completed;
        this.level3_completed = level3_completed;
        this.level4_completed = level4_completed;
        this.level5_completed = level5_completed;
        this.total_score = total_score;
        this.level1_highScore = level1_highScore;
        this.level2_highScore = level2_highScore;
        this.level3_highScore = level3_highScore;
        this.level4_highScore = level4_highScore;
        this.level5_highScore = level5_highScore;
    }

    public User() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getLevel1_completed() {
        return level1_completed;
    }

    public void setLevel1_completed(boolean level1_completed) {
        this.level1_completed = level1_completed;
    }

    public boolean getLevel2_completed() {
        return level2_completed;
    }

    public void setLevel2_completed(boolean level2_completed) {
        this.level2_completed = level2_completed;
    }

    public boolean getLevel3_completed() {
        return level3_completed;
    }

    public void setLevel3_completed(boolean level3_completed) {
        this.level3_completed = level3_completed;
    }

    public boolean getLevel4_completed() {
        return level4_completed;
    }

    public void setLevel4_completed(boolean level4_completed) {
        this.level4_completed = level4_completed;
    }

    public boolean getLevel5_completed() {
        return level5_completed;
    }

    public void setLevel5_completed(boolean level5_completed) {
        this.level5_completed = level5_completed;
    }

    public Image getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(Image profile_picture) {
        this.profile_picture = profile_picture;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }
    public int getLevel1_highScore() {
        return level1_highScore;
    }

    public void setLevel1_highScore(int level1_highScore) {
        this.level1_highScore = level1_highScore;
    }

    public int getLevel2_highScore() {
        return level2_highScore;
    }

    public void setLevel2_highScore(int level2_highScore) {
        this.level2_highScore = level2_highScore;
    }

    public int getLevel3_highScore() {
        return level3_highScore;
    }

    public void setLevel3_highScore(int level3_highScore) {
        this.level3_highScore = level3_highScore;
    }

    public int getLevel4_highScore() {
        return level4_highScore;
    }

    public void setLevel4_highScore(int level4_highScore) {
        this.level4_highScore = level4_highScore;
    }

    public int getLevel5_highScore() {
        return level5_highScore;
    }

    public void setLevel5_highScore(int level5_highScore) {
        this.level5_highScore = level5_highScore;
    }

}
