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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruce
 */
public class Book implements DBPersitance,Comparable<Book>{

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
    private int bookid;
    private int borrow_count;
    private String classValue;
    private String bookURL;
    private String imageURL;

    public Book(String bookName, String author) {
        this.bookName = bookName;
        this.author = author;
    }

    public Book() {

    }

    public String getBookURL() {
        return bookURL;
    }

    public void setBookURL(String bookURL) {
        this.bookURL = bookURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    
    public String getClassValue() {
        return classValue;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public void setBorrow_count(int borrow_count) {
        this.borrow_count = borrow_count;
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
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

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBorrow_count() {
        return borrow_count;
    }
    
    

    @Override
    public void saveToDB() {

        try {

            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);

            if (this.exists()) {
                String sql = "update books set borrow_count=borrow_count+1 where bookname='" + bookName
                        + "' and author='" + author + "'";
                OnlineDatabaseAccessor.update(stmt, sql);
            } else {
                String sql = "insert into books(bookname,author,topic"
                        + ",publisher,categorycode,acquirecode,lang) values('" + bookName + "','" + author
                        + "','" + topic + "','" + publisher
                        + "','" + categoryCode + "','" + acquireCode + "','" + lang + "')";

                OnlineDatabaseAccessor.insert(stmt, sql);

                System.out.println(sql);
            }

            stmt.close();
            stmt = null;
            con.close();
            con = null;
        } catch (SQLException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public boolean exists() {
        try {
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            ResultSet rs = stmt.executeQuery("select * from books where bookname='" + bookName
                    + "' and author='" + author + "'");

            boolean r = false;
            if (rs.next()) {
                r = true;
            } else {
                r = false;
            }

            rs.close();
            stmt.close();
            con.close();
            
            return r;
        } catch (SQLException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.bookName.hashCode();
        hash = 67 * hash + this.author.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if (!this.bookName.equals(other.bookName)) {
            return false;
        }
        if (!this.author.equals(other.author)) {
            return false;
        }
        return true;
    }

    
    
    public static Book getBookFromResultSet(ResultSet rs) {
        Book aBook = new Book();

        try {
            aBook.bookName = rs.getString("bookname");
            aBook.author = rs.getString("author");
            aBook.publisher = rs.getString("publisher");
            aBook.topic = rs.getString("topic");
            aBook.categoryCode = rs.getString("categorycode");
            aBook.acquireCode = rs.getString("acquirecode");
            aBook.lang = rs.getString("lang");
            aBook.borrow_count = rs.getInt("borrow_count");
            aBook.bookid = rs.getInt("bookid");
            aBook.classValue = rs.getString("classvalue");
            aBook.bookURL = rs.getString("bookurl");
            aBook.imageURL = rs.getString("imageurl");
        } catch (SQLException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }

        return aBook;
    }

    public int getBookid() {
        return bookid;
    }
    

    @Override
    public int compareTo(Book o) {
        if(this.borrow_count > o.borrow_count){
            return -1;
        }else if(this.borrow_count < o.borrow_count){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Book{" + "bookName=" + bookName + ", author=" + author + ", publishTime=" + publishTime + ", lang=" + lang + ", topic=" + topic + ", publisher=" + publisher + ", categoryCode=" + categoryCode + ", acquireCode=" + acquireCode + ", bookid=" + bookid + ", borrow_count=" + borrow_count + ", classValue=" + classValue + ", bookURL=" + bookURL + ", imageURL=" + imageURL + '}';
    }

    
//    
//    public Map<String,String> toMap(){
//        Map<String,String> bookMap = new HashMap<String,String>();
//        bookMap.put("bookname", this.bookName);
//        bookMap.put("author", this.author);
//        bookMap.put("topic", this.topic);
//        bookMap.put("acquirecode", this.acquireCode);
//        bookMap.put("publisher", this.publisher);
//        bookMap.put("lang", this.lang);
//        return bookMap;
//    }
}
