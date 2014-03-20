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
import db.ConnectionManager;
import db.DBPersitance;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruce
 */
public class DataSetItem implements DBPersitance{
    private BorrowListItem listitem ;
    private String categoryClass;

    public DataSetItem(BorrowListItem listitem) {
        this.listitem = listitem;
    }

    public String getCategoryClass() {
        return categoryClass;
    }

    public void setCategoryClass(String categoryClass) {
        this.categoryClass = categoryClass;
    }
    
    public boolean isValid(){
    
        if(categoryClass != null && this.listitem.isValid()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void saveToDB() {
        try {
            
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            PreparedStatement pStmt = con.prepareStatement("insert into dataset values(?,?,?,?,?,?,?,?,?)");
            
            pStmt.setString(1, this.listitem.userid);
            pStmt.setString(2, this.listitem.username);
            pStmt.setString(3, this.listitem.major);
            pStmt.setString(4, this.listitem.college);
            pStmt.setString(5, this.listitem.bookname);
            pStmt.setString(6, this.listitem.author);
            pStmt.setString(7, this.listitem.topic);
            pStmt.setString(8, this.listitem.lang);
            pStmt.setString(9, categoryClass);
            pStmt.execute();
            
            System.out.println("Inserted:("+this.listitem.username+","+this.listitem.bookname+")");
            
            pStmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataSetItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
