package com.example.myapplication;

public class newsClass {
    private String title;
    private String link;
    private String keywords;
    private String author;
    private String description;
    private String content;
    private String date;
    private String imageUrl;
    private String catagory;
    private String country;
    private String language;

    public newsClass(String title, String link, String keywords, String author, String description, String content, String date, String imageUrl, String catagory, String country, String language) {
        this.title = title;
        this.link = link;
        this.keywords = keywords;
        this.author = author;
        this.description = description;
        this.content = content;
        this.date = date;
        this.imageUrl = imageUrl;
        this.catagory = catagory;
        this.country = country;
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
