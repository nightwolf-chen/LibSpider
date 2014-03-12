/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package object;

/**
 *
 * @author bruce
 */
public class Book {
    
    private String bookName;
    private String author;
    private String publishTime;
    private String location;
    private String linkUrl;
    private String lang;
    private String topic;
    private String publisher;
    private String categoryCode;
    private String acquireCode;
    
    public Book(String bookName, String author) {
        this.bookName = bookName;
        this.author = author;
    }

    
    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getLocation() {
        return location;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getLang() {
        return lang;
    }

    public String getTopic() {
        return topic;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getAcquireCode() {
        return acquireCode;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public void setAcquireCode(String acquireCode) {
        this.acquireCode = acquireCode;
    }
    
}
