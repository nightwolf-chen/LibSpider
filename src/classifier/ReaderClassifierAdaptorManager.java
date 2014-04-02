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
import object.Book;
import weka.core.SerializationHelper;

/**
 *
 * @author bruce
 */
public class ReaderClassifierAdaptorManager {

    private ReaderClassifierAdaptor classifierAdaptor;
    private boolean isBuilt = false;
    public static final String defaultSerialPath = "ZeroRBinary.binary";

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

        Book book = dataItem.getBook();
        String classValue = dataItem.getClassValue();
        if (classValue != null) {
            this.classifierAdaptor.updateData(book, classValue);
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

    public Book getABook(ResultSet rs) {
        try {

            Book book = new Book("", "");
            book.setAuthor(rs.getString("author"));
            book.setBookName(rs.getString("bookname"));
            book.setTopic(rs.getString("topic"));
            book.setPublisher(rs.getString("publisher"));
            book.setCategoryCode(rs.getString("categorycode"));
            book.setAcquireCode(rs.getString("acquirecode"));
            book.setLang(rs.getString("lang"));

            return book;
        } catch (SQLException ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void train() throws SQLException {
        ConnectionManager conMgr = new ConnectionManager();
        Connection connection = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(connection);
        ResultSet rs = stmt.executeQuery("select * from books");
        ReaderClassifierAdaptorManager manager = new ReaderClassifierAdaptorManager();

        final int saveCount = 100;
        int count = saveCount;
        while (rs.next()) {

            Book book = this.getABook(rs);
            String classVlaue = ReaderClassifierAdaptor.transferCategorycodeToClass(book.getCategoryCode());

            System.out.println(classVlaue);

            DataSetItem dataItem = new DataSetItem(book);
            dataItem.setClassValue(classVlaue);
            manager.trainClassfierWithSingleData(dataItem);

            if (count-- <= 0) {
                count = saveCount;
                manager.serializeToFile();
            }
        }

        manager.classifierAdaptor.saveDataToArff("data.arff");
        try {
            manager.classifierAdaptor.buildClassifier();
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        rs.close();
        stmt.close();
        connection.close();
    }

    public void testClassifier() throws SQLException {
        ConnectionManager conMgr = new ConnectionManager();
        Connection connection = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(connection);
        ResultSet rs = stmt.executeQuery("select * from books");
        ReaderClassifierAdaptorManager manager = new ReaderClassifierAdaptorManager();

        rs.next();

        Book book = manager.getABook(rs);
        System.out.println(manager.classify(book));

        rs.close();
        stmt.close();
        connection.close();
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public String classify(Book book) {
        try {
            return this.classifierAdaptor.classifyReader(book);
        } catch (Exception ex) {
            Logger.getLogger(ReaderClassifierAdaptorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static void main(String[] args) throws SQLException, Exception {
        ReaderClassifierAdaptorManager mgr = new ReaderClassifierAdaptorManager();
        
//        mgr.train();
        mgr.testClassifier();
    }

}
