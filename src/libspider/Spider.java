/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import concurrent.ConcurrencyManager;
import db.ConnectionManager;
import db.OnlineDatabaseAccessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.HttpClientAdaptor;
import object.Book;
import object.BookBorrowHistory;
import object.User;
import paser.PageParserBookDetail;
import paser.PageParserBorrowList;
import paser.PageParserUserInfo;

/**
 *
 * @author bruce
 */
public class Spider implements Runnable {

    public SingleUserCrawlResult crawlDataForUser(String userid) {

        HttpClientAdaptor httpClient = new HttpClientAdaptor();
        PageParserUserInfo userInfoPaser = new PageParserUserInfo(userid, httpClient);

        Map<String, String> userInfo = userInfoPaser.parserPageForData();

        if (userInfo == null) {
            return null;
        }

        User user = new User(userid,
                userInfo.get(PageParserUserInfo.kName),
                userInfo.get(PageParserUserInfo.kCollege),
                userInfo.get(PageParserUserInfo.kMajor));

        String userBorrowListUrl = userInfo.get(PageParserUserInfo.kBorrowHistoryUrl);

        if (userBorrowListUrl == null) {
            System.out.println("userBorrowListUrl is null! userid =" + userid);
            return null;
        }

        PageParserBorrowList borrowListPaser = new PageParserBorrowList(userBorrowListUrl, userid, httpClient);
        ArrayList<Object> list = borrowListPaser.parserPageForRepeatedData();
        
        if(list == null){
            return null;
        }
        
        for (Object obj : list) {

            BookBorrowHistory borrowHistory = (BookBorrowHistory) obj;
            Book aBook = borrowHistory.getaBook();
            PageParserBookDetail bookDetailParser = new PageParserBookDetail(aBook.getLinkUrl(), httpClient);
            Map<String, String> bookDetailData = bookDetailParser.parserPageForData();

            if (bookDetailData != null) {
                aBook.setLang(bookDetailData.get(PageParserBookDetail.kLang));
                aBook.setPublisher(bookDetailData.get(PageParserBookDetail.kPublisher));
                aBook.setTopic(bookDetailData.get(PageParserBookDetail.kTopic));
                aBook.setAcquireCode(bookDetailData.get(PageParserBookDetail.kAcquireCode));
                aBook.setCategoryCode(bookDetailData.get(PageParserBookDetail.kCategoryCode));
            }
            System.out.println(borrowHistory.getaBook().getBookName());
        }

        return new SingleUserCrawlResult(user, list);
    }

    public void crawlForAllPossibleUserAndSaveToDB() {
        long startUserid = 1003700;
        long endUserid = 1009999;

//        startUserid = 1003714;
//        endUserid = 1003714;
        for (long userid = startUserid; userid <= endUserid; userid++) {

            String useridStr = "2010" + String.valueOf(userid);

            if (this.userExists(useridStr)) {
                continue;
            }

            SingleUserCrawlResult aUserResult = this.crawlDataForUser(useridStr);
            if (aUserResult != null) {
                ConcurrencyManager.dbOperationExecutor.execute(new DatabaseOperatorSave(aUserResult));
            }
        }

    }

    private boolean userExists(String userid) {

        try {
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            String sql = "select * from lib_user where userid = '" + userid + "'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return true;
            }

            rs.close();
            stmt.close();
            con.close();
            rs = null;
            stmt = null;
            con = null;

            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public void run() {
        crawlForAllPossibleUserAndSaveToDB();
    }

}
