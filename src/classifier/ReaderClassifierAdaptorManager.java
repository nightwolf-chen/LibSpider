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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.trees.J48;
import weka.core.SerializationHelper;

/**
 *
 * @author bruce
 */
public class ReaderClassifierAdaptorManager {

    private ReaderClassifierAdaptor classifierAdaptor;
    private  boolean isBuilt = false;
    public static final String defaultSerialPath = "J48TreeBinary.binary";

    public ReaderClassifierAdaptorManager() {

        try {
            classifierAdaptor = (ReaderClassifierAdaptor) SerializationHelper.read(ReaderClassifierAdaptorManager.defaultSerialPath);
            isBuilt = true;
        } catch (Exception ex) {
            classifierAdaptor = new ReaderClassifierAdaptor();
            isBuilt = false;
        }

    }

    public void trainClassifierWithDataSet(List<DataSetItem> dataset) {

        for (DataSetItem dataItem : dataset) {

            this.trainClassfierWithSingleData(dataItem);
        }

    }

    public void trainClassfierWithSingleData(DataSetItem dataItem) {

        BorrowListItem borrowListItem = dataItem.getListitem();
        String classValue = dataItem.getClassValue();
        if (classValue != null) {
            this.classifierAdaptor.updateData(borrowListItem, classValue);
        } else {
            System.out.println("No classvalue cannot used to train!");
        }

    }

    public void serializeToFile() {
        try {
            SerializationHelper.write(ReaderClassifierAdaptorManager.defaultSerialPath, classifierAdaptor);
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void visualzie() {
        try {
            classifierAdaptor.buildClassifier();
//            J48 j48 =  classifierAdaptor.getM_Classifier();
//            TreeClassifierVisualizer vs = new TreeClassifierVisualizer(j48);
//            vs.visualize();
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BorrowListItem getABorrowListItem(ResultSet rs) {
        try {
            BorrowListItem borrowItem = new BorrowListItem();
            borrowItem.setAuthor(rs.getString("author"));
            borrowItem.setBookname(rs.getString("bookname"));
            borrowItem.setCollege(rs.getString("college"));
            borrowItem.setLang(rs.getString("lang"));
            borrowItem.setMajor(rs.getString("major"));
            borrowItem.setTopic(rs.getString("topic"));
            borrowItem.setUserid(rs.getString("userid"));
            borrowItem.setUsername(rs.getString("username"));

            return borrowItem;
        } catch (SQLException ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void train() throws SQLException {
        ConnectionManager conMgr = new ConnectionManager();
        Connection connection = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(connection);
        ResultSet rs = stmt.executeQuery("select * from dataset");
        ReaderClassifierAdaptorManager manager = new ReaderClassifierAdaptorManager();

        final int saveCount = 100;
        int count = saveCount;
        while (rs.next()) {
            BorrowListItem borrowItem = new BorrowListItem();
            borrowItem.setAuthor(rs.getString("author"));
            borrowItem.setBookname(rs.getString("bookname"));
            borrowItem.setCollege(rs.getString("college"));
            borrowItem.setLang(rs.getString("lang"));
            borrowItem.setMajor(rs.getString("major"));
            borrowItem.setTopic(rs.getString("topic"));
            borrowItem.setUserid(rs.getString("userid"));
            borrowItem.setUsername(rs.getString("username"));
            String classVlaue = rs.getString("category");

            System.out.println(classVlaue);

            DataSetItem dataItem = new DataSetItem(borrowItem);
            dataItem.setClassValue(classVlaue);
            manager.trainClassfierWithSingleData(dataItem);

            if (count-- <= 0) {
                count = saveCount;
                manager.serializeToFile();
            }
        }

        manager.classifierAdaptor.saveDataToArff("lib_data.arff");
        try {
            manager.classifierAdaptor.buildClassifier();
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        rs.close();
        stmt.close();
        connection.close();
    }

    public boolean isIsBuilt() {
        return isBuilt;
    }
    
    

    public String classify(BorrowListItem item){
        try {
            return  this.classifierAdaptor.classifyReader(item);
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            return "";
    }
    public static void main(String[] args) throws SQLException, Exception {

        ConnectionManager conMgr = new ConnectionManager();
        Connection connection = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(connection);
        ResultSet rs = stmt.executeQuery("select * from dataset");
        ReaderClassifierAdaptorManager manager = new ReaderClassifierAdaptorManager();
        
        rs.next();
        
        BorrowListItem item = manager.getABorrowListItem(rs);
        System.out.println(manager.classify(item));
       
    }

}
