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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.HttpClientAdaptor;
import network.ProxiedHttpClientAdaptor;
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

        HttpClientAdaptor httpClient = new ProxiedHttpClientAdaptor();
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

        if (list == null) {
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

        String Year = "2010";
        final int collegeNum = 15;
        List<String> collegeCodes = new ArrayList<>();
        int[] collegeStudentNums = {1616, 1350, 1719, 3000, 800, 1026, 1165, 1400, 400, 1700, 880, 240, 850, 140, 150};
        for (int i = 1; i <= 15; i++) {
            String code = null;
            if (i < 10) {
                code = "0" + String.valueOf(i) + "0";
            } else {
                code = String.valueOf(i) + "0";
            }
            collegeCodes.add(code);
        }

        int count = 0;
        for (int collegeIndex = 0; collegeIndex < collegeNum; collegeIndex++) {

            int collegeStudentNum = 5000;
            String collegeCode = collegeCodes.get(collegeIndex);

            for (int studentCode = 0; studentCode < collegeStudentNum; studentCode++) {

                String useridStr = this.generateStudentid(Year, collegeCode, String.valueOf(studentCode));

                if (this.userExists(useridStr)) {
                    continue;
                }

                SingleUserCrawlResult aUserResult = this.crawlDataForUser(useridStr);
                if (aUserResult != null) {
                    ConcurrencyManager.dbOperationExecutor.execute(new DatabaseOperatorSave(aUserResult));
                }
                count++;
            }
        }

        System.out.println("count:" + count);

}

    private String generateStudentid(String year, String collegeCode, String studentCode) {

        int zeroToAdd = 4 - studentCode.length();
        String studentid = year + collegeCode;

        while (zeroToAdd-- > 0) {
            studentid += "0";
        }

        return studentid + studentCode;
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
