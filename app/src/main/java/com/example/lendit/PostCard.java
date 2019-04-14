package com.example.lendit;

public class PostCard {

    private String imgURL;
    private String postTitle;
    private String personName;
    private String building;
    private String profileImgURL;
    private String deposit;
    private String description;

    // Constructor for lend
    PostCard(String img, String item, String person, String building, String profile, String dep, String desc){
        this.imgURL = img;
        this.postTitle = item;
        this.personName = person;
        this.building = building;
        this.profileImgURL = profile;
        this.deposit = dep;
        this.description = desc;
    }

    // Constructor for ask
    PostCard(String item, String person, String building, String profile, String desc){
        this.postTitle = item;
        this.personName = person;
        this.building = building;
        this.profileImgURL = profile;
        this.description = desc;
        // default ask image
        this.imgURL = "gs://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";
        // default deposit amount - will never be accessed
        this.deposit = "0";
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String itemName) {
        this.postTitle = itemName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String b) {
        this.building = b;
    }

    public String getProfileImg() {
        return profileImgURL;
    }

    public void setProfileImgURL(String img) {
        this.profileImgURL = img;
    }
    public String getDeposit() {
        return this.deposit;
    }

    public void setDeposit(String d) {
        this.deposit = d;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
