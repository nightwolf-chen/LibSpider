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

            OnlineDatabaseAccessor.insert(stmt, "insert into user_book values('"+userid
                    +"','"+aBook.getBookName()+"','"+aBook.getAuthor()+"')");

            stmt.close();
            stmt = null;
            con.close();
            con = null;
        } catch (SQLException ex) {
            Logger.getLogger(BookBorrowHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
