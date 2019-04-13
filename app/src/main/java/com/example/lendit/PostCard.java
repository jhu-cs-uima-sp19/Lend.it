package com.example.lendit;

public class PostCard {

    private String imgURL;
    private String itemName;
    private String personName;

        PostCard (String img, String item, String person){
            imgURL = img;
            itemName = item;
            personName = person;
        }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

}
