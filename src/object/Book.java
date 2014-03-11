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

    public Book(String bookName, String author, String publishTime, String location,String url) {
        this.bookName = bookName;
        this.author = author;
        this.publishTime = publishTime;
        this.location = location;
        this.linkUrl = url;
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
    
}
