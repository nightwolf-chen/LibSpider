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
import db.ConnectionManager;
import db.OnlineDatabaseAccessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import object.Book;
import object.User;
import object.UserLibInfo;

/**
 *
 * @author bruce
 */
public class UserDataSource {

    private String userid;
    private UserLibInfo info;

    public UserDataSource(String userid) {
        try {
            this.userid = userid;

            User user = new User();
            user.setUserid(userid);
            
            List<Book> books = new ArrayList<Book>();

            ConnectionManager cMgr = new ConnectionManager();
            Connection con = cMgr.getConnection();
            Statement stmtUser = OnlineDatabaseAccessor.createStatement(con);
            Statement stmtBook = OnlineDatabaseAccessor.createStatement(con);
            ResultSet borrowSet = stmtUser.executeQuery("select * from user_book where userid='" + userid + "'");

            while (borrowSet.next()) {

                int bookid = borrowSet.getInt("bookid");
                ResultSet bookSet = stmtBook.executeQuery("select * from books where bookid="+bookid);
               
                if (bookSet.next()) {
                    Book aBook = Book.getBookFromResultSet(bookSet);
                    books.add(aBook);
                }
            }
            
            this.info = new UserLibInfo(user, books);
        } catch (SQLException ex) {
            Logger.getLogger(UserDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getFavoriteCategory(){
        
        int[] categoryCount = new int[27]; 
        int index = 26;
        int max = 0;
        
        for(Book book : this.info.getBorrowList()){
            
            String value = book.getClassValue();
            char valueChr = value.charAt(0);
            
            if(valueChr >= 'A' && valueChr <= 'Z'){
                categoryCount[valueChr - 'A']++;
            }else{
                categoryCount[26]++;
            }
            
        }
        
        for(int i = 0 ; i < 27 ;i++){
            
            if(categoryCount[i] > max){
                max = categoryCount[i];
                index = i;
            }
            
        }
        
        char result = '0';
        
        if(index < 26){
            result = (char) ('A' + index);
        }
        
        return ""+result;
    }
    
    public String getUserid() {
        return userid;
    }

    public UserLibInfo getInfo() {
        return info;
    }
    
    public static void main(String[] args){
        UserDataSource source = new UserDataSource("20101003712");
//        System.out.println(source.info.getBorrowList());
//        System.out.println(source.info.getBorrowList().size());
        System.out.println(source.getFavoriteCategory());
    }

}
