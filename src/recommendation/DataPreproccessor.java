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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruce
 */
public class DataPreproccessor {

    public void preCaculateSimilarities() throws SQLException {

        List<UserDataSource> userSources = new ArrayList<UserDataSource>();
        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(con);
        Statement stmtIn = OnlineDatabaseAccessor.createStatement(con);
        ResultSet rs = OnlineDatabaseAccessor.select(stmt, "select * from users");

        System.out.println("Tying to get user data...");
        while (rs.next()) {
            String userid = rs.getString("userid");
            userSources.add(new UserDataSource(userid));
            System.out.println("user:"+userid+" data loaded.");
        }
        System.out.println("User data all loaded");

        System.out.println("totol:" + userSources.size());

        for (int i = 0; i < userSources.size(); i++) {

            UserDataSource sourceA = userSources.get(i);

            for (int j = i + 1; j < userSources.size(); j++) {

                UserDataSource sourceB = userSources.get(j);
                
                String useridA = sourceA.getUserid();
                String useridB = sourceB.getUserid();
                if (this.exists(useridA, useridB)) {
                    continue;
                }

                System.out.println("Proccess:(" + useridA + "," + useridB + ")...");

                UserSimilarity similarity = new UserSimilarity(sourceA.getInfo(), sourceB.getInfo());
                double value = similarity.getSimilarity();

                OnlineDatabaseAccessor.insert(stmtIn, "insert into user_user values('"
                        + useridA + "','" + useridB + "'," + value + ")");

                OnlineDatabaseAccessor.insert(stmtIn, "insert into user_user values('"
                        + useridB + "','" + useridA + "'," + value + ")");

                System.out.println("Proccess:(" + useridA + "," + useridB + ") done!");
            }
        }

        rs.close();
        stmt.close();
        con.close();
    }

    private boolean exists(String useridA, String useridB) {

        try {
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            ResultSet rs = OnlineDatabaseAccessor.select(stmt, "select * from user_user where userid_a='"
                    + useridA + "' and userid_b='" + useridB + "'");

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
            Logger.getLogger(DataPreproccessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static void main(String[] args) throws SQLException {
        new DataPreproccessor().preCaculateSimilarities();
    }
}
