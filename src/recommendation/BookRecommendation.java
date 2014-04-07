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
package recommendation;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import db.ConnectionManager;
import db.OnlineDatabaseAccessor;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import libspider.Spider;
import object.Book;
import object.User;
import object.UserLibInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author bruce
 */
public class BookRecommendation {

    private User user = new User();
    private UserLibInfo infoA;

    public BookRecommendation(String userid) throws SQLException {
        this.user.setUserid(userid);
        if (!user.exists()) {
            preproccess();
        } else {
            infoA = new UserDataSource(userid).getInfo();
        }
    }

    private void preproccess() throws SQLException {

        Spider spider = new Spider();
        this.infoA = spider.crawlDataForUser(user.getUserid());
        if (infoA != null) {
            infoA.saveToDB();
        } else {
            throw new NullPointerException();
        }

        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmtOut = OnlineDatabaseAccessor.createStatement(con);
        Statement stmtIn = OnlineDatabaseAccessor.createStatement(con);

        ResultSet userRs = stmtOut.executeQuery("select * from users");

        while (userRs.next()) {
            String useridB = userRs.getString("userid");
            if (useridB.equals(this.user.getUserid())) {

                UserDataSource sourceB = new UserDataSource(useridB);
                UserSimilarity similarity = new UserSimilarity(infoA, sourceB.getInfo());
                double value = similarity.getSimilarity();

                OnlineDatabaseAccessor.insert(stmtIn, "insert into user_user values('"
                        + user.getUserid() + "','" + useridB + "'," + value + ")");

                OnlineDatabaseAccessor.insert(stmtIn, "insert into user_user values('"
                        + useridB + "','" + user.getUserid() + "'," + value + ")");
            }
        }

        userRs.close();
        stmtIn.close();
        stmtOut.close();
        con.close();

    }

    public List<Book> getRecommendation() throws SQLException {

        List<Book> books = new ArrayList<Book>();

        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmtOut = OnlineDatabaseAccessor.createStatement(con);
        
        String sql = "select * from user_user where userid_a='"
                +user.getUserid()+"' order by similarity desc limit 5";
        
        ResultSet rs = OnlineDatabaseAccessor.select(stmtOut, sql);

        while (rs.next()) {
            System.out.println(rs.getString("userid_a")+"|"+rs.getString("userid_b")+"="+rs.getDouble("similarity"));
            UserDataSource source = new UserDataSource(rs.getString("userid_b"));
            UserLibInfo info = source.getInfo();
            Collections.sort(info.getBorrowList());
            books.addAll(info.getBorrowList());
        }
        
        //删除用户已经借阅过的图书
        books.removeAll(this.infoA.getBorrowList());
        
        return books;
    }
    
    public String toJson() throws SQLException, JSONException{
        List<Book> books = this.getRecommendation();
        
        JSONObject object = new JSONObject();
        object.accumulate("userid", this.user.getUserid()); 
        object.accumulate("books", books);
            
        return object.toString();
    }

    public static void main(String[] args) throws Exception {

        BookRecommendation bookRecommendation = new BookRecommendation("20100100001");
        System.out.println(bookRecommendation.infoA.getBorrowList().size());
        System.out.println(bookRecommendation.getRecommendation().size());
        List<Book> books = bookRecommendation.getRecommendation();
        
        for(Book book:bookRecommendation.infoA.getBorrowList()){
            System.out.println(book);
        }
        
        System.out.println("------------------------");
        
        for(Book book:books){
            System.out.println(book);
        }
        System.out.println(bookRecommendation.toJson());
    }
}
