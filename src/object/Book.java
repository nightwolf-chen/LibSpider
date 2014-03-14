/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package object;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import db.ConnectionManager;
import db.DBPersitance;
import db.OnlineDatabaseAccessor;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruce
 */
public class Book implements DBPersitance{
    
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

    @Override
    public void saveToDB() {
        
        try {
            
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            String sql = "insert into lib_book(bookname,author,publishtime,location,linkurl,topic"
                    + ",publisher,categorycode,acquirecode,lang) values('"+bookName+"','"+author+
                    "','"+publishTime+"','"+location+"','"+linkUrl+"','"+topic+"','"+publisher+
                    "','"+categoryCode+"','"+acquireCode+"','"+lang+"')";
            
            
            boolean isSuccess = OnlineDatabaseAccessor.insert(stmt, sql);
            
            System.out.println(sql);
            if(isSuccess){
                 System.out.println("success...");
            }
            
            stmt.close();
            stmt=null;
            con.close();
            con=null;
        } catch (SQLException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
