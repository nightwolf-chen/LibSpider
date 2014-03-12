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
public class BookBorrowHistory implements DBPersitance {

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

    @Override
    public void saveToDB() {
        try {
            
            String user_int_id = null;
            String bookid = null;
            this.aBook.saveToDB();
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            ResultSet rs = OnlineDatabaseAccessor.select(stmt, "select id from lib_user where userid='"+userid+"'");
            
            if(rs.next()){
                user_int_id = rs.getString("id");
            }
            
            rs = OnlineDatabaseAccessor.select(stmt, "select bookid from lib_book where bookname='"+aBook.getBookName()
                    +"' and author='"+aBook.getAuthor()+"'");
            
            if(rs.next()){
                bookid = rs.getString("bookid");
            }
            
            if(bookid != null && user_int_id != null){
                OnlineDatabaseAccessor.insert(stmt, "insert into lib_borrowlist(user_int_id,book_id) values("+user_int_id
                        +","+bookid+")"); 
            }
            
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
            con.close();
            con = null;
        } catch (SQLException ex) {
            Logger.getLogger(BookBorrowHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
