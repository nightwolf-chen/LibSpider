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
public class BookBorrowHistory {
    private String userid;
    private Book aBook;
    private String shouldReturnTime;
    private String actrulReturnTime;

    public String getUserid() {
        return userid;
    }

    public Book getaBook() {
        return aBook;
    }

    public String getShouldReturnTime() {
        return shouldReturnTime;
    }

    public String getActrulReturnTime() {
        return actrulReturnTime;
    }

    public BookBorrowHistory(String userid, Book aBook, String shouldReturnTime, String actrulReturnTime) {
        this.userid = userid;
        this.aBook = aBook;
        this.shouldReturnTime = shouldReturnTime;
        this.actrulReturnTime = actrulReturnTime;
    }
    
}
