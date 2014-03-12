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
public class User implements DBPersitance{
    private String userid;
    private String name;
    private String college;
    private String major;

    public User(String userid, String name, String college, String major) {
        this.userid = userid;
        this.name = name;
        this.college = college;
        this.major = major;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getCollege() {
        return college;
    }

    public String getMajor() {
        return major;
    }

    @Override
    public void saveToDB() {
        
        try {
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            boolean isSuccess = OnlineDatabaseAccessor.insert(stmt, "insert into lib_user(userid,name,college,major) "
                    + "values('"+userid+"','"+name+"','"+college+"','"+major+"')");
            
            stmt.close();
            stmt=null;
            con.close();
            con=null;
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
