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
package db;

import classifier.ReaderClassifierAdaptorManager;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import object.Book;

/**
 *
 * @author bruce
 */
public class DatabaseRefactor {

    public void refactorUsers() throws SQLException {

        Connection con = new ConnectionManager().getConnection();
        Statement stmt1 = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt1.executeQuery("select * from lib_user");

        Statement stmt2 = OnlineDatabaseAccessor.createStatement(con);
        while (rs.next()) {
            String userid = rs.getString("userid");
            String username = rs.getString("name");
            String college = rs.getString("college");
            String major = rs.getString("major");

            stmt2.execute("insert into users values('" + userid + "','" + username + "','" + college + "','" + major + "')");

        }

        stmt2.execute(" delete from users where username='null'");

        stmt2.close();
        rs.close();
        stmt1.close();
        con.close();
    }

    public void refactorBooks() throws SQLException {
        Connection con = new ConnectionManager().getConnection();
        Statement stmt1 = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt1.executeQuery("select * from lib_book");

        Statement stmt2 = OnlineDatabaseAccessor.createStatement(con);

        while (rs.next()) {
            String bookname = rs.getString("bookname").replaceAll("'", "''");
            String author = rs.getString("author").replaceAll("'", "''");
            String topic = rs.getString("topic").replaceAll("'", "''");
            String publisher = rs.getString("publisher").replaceAll("'", "''");
            String categorycode = rs.getString("categorycode").replaceAll("'", "''");
            String acquirecode = rs.getString("acquirecode").replaceAll("'", "''");
            String lang = rs.getString("lang").replaceAll("'", "''");

            ResultSet rs2 = stmt2.executeQuery("select * from books where bookname='" + bookname
                    + "' and author='" + author + "'");

            if (rs2.next()) {
                stmt2.execute("update books set borrow_count=borrow_count+1 where bookname='" + bookname + "' and author='" + author + "'");
            } else {
                stmt2.execute("insert into books values('" + bookname + "','" + author + "','"
                        + publisher + "','" + topic + "','" + categorycode + "','" + acquirecode
                        + "','" + lang + "',1)");
            }
        }

        stmt2.close();
        stmt1.close();
        con.close();
    }

    public void refactorUserBook() throws SQLException {
        Connection con = new ConnectionManager().getConnection();

        Statement stmt1 = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt1.executeQuery("select * from lib_borrowlist");

        Statement stmt2 = OnlineDatabaseAccessor.createStatement(con);
        Statement stmt3 = OnlineDatabaseAccessor.createStatement(con);

        while (rs.next()) {
            String userid = "";
            String bookname = "";
            String author = "";
            int id = rs.getInt("user_int_id");
            int bookId = rs.getInt("book_id");

            ResultSet userRs = stmt2.executeQuery("select * from lib_user where id=" + id + "");
            if (userRs.next()) {
                userid = userRs.getString("userid");
            }
            userRs.close();

            ResultSet bookRs = stmt3.executeQuery("select * from lib_book where bookid=" + bookId + "");
            if (bookRs.next()) {
                bookname = bookRs.getString("bookname").replaceAll("'", "''");
                author = bookRs.getString("author").replaceAll("'", "''");
            }
            bookRs.close();

            stmt2.execute("insert into user_book values('" + userid + "','" + bookname + "','" + author + "')");
        }
    }

    public void refactorBookid() throws SQLException {
        Connection con = new ConnectionManager().getConnection();

        Statement stmt1 = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt1.executeQuery("select * from books");

        PreparedStatement stmt2 = (PreparedStatement) con.prepareStatement("update user_book set bookid=? where bookname=? and author=?");

        int count = 0;
        while (rs.next()) {

            Book aBook = Book.getBookFromResultSet(rs);


            stmt2.setString(1, String.valueOf(aBook.getBookid()));
            stmt2.setString(2, aBook.getBookName());
            stmt2.setString(3, aBook.getAuthor());

            stmt2.execute();

            System.out.println("..." + count++);

        }

        con.close();
    }

    public void refactorClassValue() throws SQLException {

        ReaderClassifierAdaptorManager cMgr = new ReaderClassifierAdaptorManager();

        Connection con = new ConnectionManager().getConnection();

        Statement stmt1 = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = stmt1.executeQuery("select * from books");

        PreparedStatement stmt2 = (PreparedStatement) con.prepareStatement("update books set classvalue=? where bookname=? and author=?");

        int count = 0;
        while (rs.next()) {

            Book aBook = Book.getBookFromResultSet(rs);

            String classValue =cMgr.classify(aBook);
            System.out.println(classValue);
            stmt2.setString(1, classValue);
            stmt2.setString(2, aBook.getBookName());
            stmt2.setString(3, aBook.getAuthor());

            stmt2.execute();

            System.out.println("..." + count++);

        }

        con.close();
    }

    public static void main(String[] args) throws SQLException {
        DatabaseRefactor dr = new DatabaseRefactor();
        dr.refactorClassValue();
    }
}
