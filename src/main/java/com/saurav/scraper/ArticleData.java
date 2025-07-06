package com.saurav.scraper;


// This is a model class that will be used in another class
public class ArticleData {

    public String title;
    public String content;
    public String imageUrl;
    public String imageLocalPath;
    
    public ArticleData(String title, String content, String imageUrl, String imageLocalPath){

        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.imageLocalPath = imageLocalPath;

    }
    
}
