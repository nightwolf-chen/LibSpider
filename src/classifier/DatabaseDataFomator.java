/*
 * Copyright 2014 bruce.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classifier;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import db.ConnectionManager;
import db.OnlineDatabaseAccessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import object.Book;
import object.User;

/**
 *
 * @author bruce
 */
public class DatabaseDataFomator {

    public void insertDataIntoTableDataset() throws SQLException {

        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmtUsers = OnlineDatabaseAccessor.createStatement(con);
        Statement stmtBorrowlist = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rsUsers = stmtUsers.executeQuery("select * from lib_user");

        while (rsUsers.next()) {

            int user_int_id = rsUsers.getInt("id");
            String userid = rsUsers.getString("userid");
            String username = rsUsers.getString("name");
            String college = rsUsers.getString("college");
            String major = rsUsers.getString("major");

            ResultSet rsBorrowList = stmtBorrowlist.executeQuery("select * from lib_borrowlist where user_int_id="
                    + user_int_id + "");

            while (rsBorrowList.next()) {
                int bookId = rsBorrowList.getInt("book_id");
                Book aBook = this.getBookFromDB(bookId);
                User aUser = new User(userid, username, college, major);
                DataSetItem dataItem = this.generateDateSetItem(aUser, aBook);
                if (dataItem.isValid()) {
                    dataItem.saveToDB();
                }
            }
            
            rsBorrowList.close();
            
        }
        
        rsUsers.close();
        stmtUsers.close();
        stmtBorrowlist.close();
        con.close();

    }

    private Book getBookFromDB(int bookId) throws SQLException {

        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt.executeQuery("select * from lib_book where bookid=" + bookId);

        Book aBook = null;
        if (rs.next()) {
            String bookName = rs.getString("bookname");
            String author = rs.getString("author");
            aBook = new Book(bookName, author);
            aBook.setCategoryCode(rs.getString("categorycode"));;
            aBook.setLang(rs.getString("lang"));;
            aBook.setTopic(rs.getString("topic"));
        }
        
        rs.close();
        stmt.close();
        con.close();
        
        return aBook;
    }

    public DataSetItem generateDateSetItem(User aUser, Book aBook) {
        
        BorrowListItem borrowlistItem = new BorrowListItem();
        borrowlistItem.setUserid(aUser.getUserid());
        borrowlistItem.setAuthor(aBook.getAuthor());
        borrowlistItem.setBookname(aBook.getBookName());
        borrowlistItem.setCollege(aUser.getCollege());
        borrowlistItem.setLang(aBook.getLang());
        borrowlistItem.setMajor(aUser.getMajor());
        borrowlistItem.setTopic(aBook.getTopic());
        borrowlistItem.setUsername(aUser.getName());
       
        DataSetItem datasetItem = new DataSetItem(borrowlistItem);
        String classValue = ReaderClassifierAdaptor.transferCategorycodeToClass(aBook.getCategoryCode());
        datasetItem.setClassValue(classValue);
        
        return datasetItem;
    }
    
    

    public static void main(String[] args) {
        try {
            new DatabaseDataFomator().insertDataIntoTableDataset();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDataFomator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
