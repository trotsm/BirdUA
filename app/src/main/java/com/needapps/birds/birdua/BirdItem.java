package com.needapps.birds.birdua;

/**
 * class to create each bird
 */
public class BirdItem {
    private int id;
    private String name;
    private String description; // for DetailActivity
    private int[] photosDetail; // array for slider in DetailActivity
    private int photo; // one photo for RecyclerAdapter
    private int audio; // audio for DetailActivity
    private String moresounds;

    public BirdItem() {
    }

    public BirdItem(int id, String name, String description, int photo, int audio, int[] photosDetail, String moresounds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.setPhoto(photo);
        this.setAudio(audio);
        this.photosDetail = photosDetail;
        this.moresounds = moresounds;
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPhoto() {
        return photo;
    }

    public int getAudio() {
        return audio;
    }

    public int[] getPhotosDetail() {
        return photosDetail;
    }

    public String getMoresounds() { return moresounds; }


    //Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public void setPhotosDetail(int[] photosDetail) {
        this.photosDetail = photosDetail;
    }

    public void setMoresounds(String moresounds) { this.moresounds = moresounds; }

}

