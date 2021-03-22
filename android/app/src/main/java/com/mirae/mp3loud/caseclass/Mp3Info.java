package com.mirae.mp3loud.caseclass;

public class Mp3Info {
    public static final int NOT_TAKEN_YET = 0;
    public static final int TAKEN = 1;

    private int state;
    private String genre;
    private String title;
    private String artist;
    private String image;
    private int playedTimes;
    private boolean like;

    public Mp3Info() {
        this(NOT_TAKEN_YET, "", "", "", "", false, 0);
    }

    public Mp3Info(int state, String genre, String title, String artist, String image, boolean like, int playedTimes) {
        this.state = state;
        this.genre = genre.trim();
        this.title = title.trim();
        this.artist = artist.trim();
        this.image = image.trim();
        this.like = like;
        this.playedTimes = playedTimes;
        this.like = false;
    }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPlayedTimes() {
        return playedTimes;
    }

    public void setPlayedTimes(int playedTimes) { this.playedTimes = playedTimes; }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
