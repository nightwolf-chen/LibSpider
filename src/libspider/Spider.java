/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import db.ConnectionManager;
import db.OnlineDatabaseAccessor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.HttpClientAdaptor;
import network.HttpProxyGetter;
import network.ProxiedHttpClientAdaptor;
import object.Book;
import object.BookBorrowHistory;
import object.User;
import org.apache.http.HttpHost;
import paser.PageParserBookDetail;
import paser.PageParserBorrowList;
import paser.PageParserUserInfo;

/**
 *
 * @author bruce
 */
public class Spider implements Runnable {

    private final String studentNumFilePath = "student.num";

    public SingleUserCrawlResult crawlDataForUser(String userid) {

        HttpHost proxy = new HttpProxyGetter().getARandomProxy();
        HttpClientAdaptor httpClient = new ProxiedHttpClientAdaptor(proxy);

        System.out.println("Begin to crawl " + userid + "userinfo");
        PageParserUserInfo userInfoPaser = new PageParserUserInfo(userid, httpClient);
        System.out.println("Userinfo successfully crawled");
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

        System.out.println("Begin to crawl " + userid + "borrowlist");
        PageParserBorrowList borrowListPaser = new PageParserBorrowList(userBorrowListUrl, userid, httpClient);
        ArrayList<Object> list = borrowListPaser.parserPageForRepeatedData();

        if (list == null) {
            System.out.println("Borrowlist crawl failed...");
            return null;
        } else {
            System.out.println("Borrowlist crawl success...");
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

        //关闭网络连接释放资源
        try {
            httpClient.getHttpclient().close();
        } catch (IOException ex) {
            Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new SingleUserCrawlResult(user, list);
    }

    public void crawlForAllPossibleUserAndSaveToDB() {

        String Year = "2010";
        final int collegeNum = 15;
        List<String> collegeCodes = this.generateCollegeCodes(collegeNum);

        ConnectionManager conMgr = new ConnectionManager();
        Connection con = conMgr.getConnection();
        Statement stmt = OnlineDatabaseAccessor.createStatement(con);
       

        for (int collegeIndex = 0; collegeIndex < collegeNum; collegeIndex++) {

            final int collegeStudentNum = 9999;
            String collegeCode = collegeCodes.get(collegeIndex);
            int alreadyCrawCount = this.getUserCountAlreadyCrawled(collegeCode);
            final int numToCraw = 10;
            int targetNum = numToCraw - alreadyCrawCount;
            int tCount = 0;
            int studentCode = 0;
            boolean isRandomStudentCode = true;

            while (targetNum > 0) {

                if (isRandomStudentCode) {
                    studentCode = (int) (Math.random() * 9999);
                } else {
                    if (++studentCode > collegeStudentNum) {
                        break;
                    }
                }

                String useridStr = this.generateStudentid(Year, collegeCode, String.valueOf(studentCode));
                System.out.println(useridStr + " to crawl...");
                if (this.userExists(useridStr)) {
                    System.out.println(useridStr + " already in database abort to crawl...");
                    continue;
                }

                SingleUserCrawlResult aUserResult = this.crawlDataForUser(useridStr);

                if (aUserResult != null) {
                    aUserResult.saveToDB();
                    isRandomStudentCode = false;
                    studentCode -= numToCraw;
                    targetNum--;
                    tCount++;
                    OnlineDatabaseAccessor.update(stmt, "update college_stu_count set studentcount=" + String.valueOf(alreadyCrawCount + tCount)
                            + " where collegecode='" + collegeCode + "'");
                }

            }//end of while

        }//end of for

    }

    private String generateStudentid(String year, String collegeCode, String studentCode) {

        int zeroToAdd = 4 - studentCode.length();
        String studentid = year + collegeCode;
        while (zeroToAdd-- > 0) {
            studentid += "0";
        }
        return studentid + studentCode;
    }

    private List<String> generateCollegeCodes(int collegeNum) {

        List<String> collegeCodes = new ArrayList<>();
        for (int i = 1; i <= collegeNum; i++) {
            String code = null;
            if (i < 10) {
                code = "0" + String.valueOf(i) + "0";
            } else {
                code = String.valueOf(i) + "0";
            }
            collegeCodes.add(code);
        }
        return collegeCodes;

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

    private int getUserCountAlreadyCrawled(String collegeCode) {
        try {
            ConnectionManager conMgr = new ConnectionManager();
            Connection con = conMgr.getConnection();
            Statement stmt = OnlineDatabaseAccessor.createStatement(con);
            String sql = "select * from college_stu_count where collegecode = '" + collegeCode + "'";
            ResultSet rs = stmt.executeQuery(sql);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("studentcount");
            } else {
                OnlineDatabaseAccessor.insert(stmt, "insert into college_stu_count values('" + collegeCode + "',0)");
            }

            rs.close();
            stmt.close();
            con.close();
            rs = null;
            stmt = null;
            con = null;

            return count;
        } catch (SQLException ex) {
            Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public void run() {
        crawlForAllPossibleUserAndSaveToDB();
    }

}
