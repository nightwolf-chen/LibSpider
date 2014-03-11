/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import network.HttpClientAdaptor;
import object.BookBorrowHistory;
import object.User;
import util.PageParserBorrowList;
import util.PageParserUserInfo;

/**
 *
 * @author bruce
 */
public class LibSpider {

    public static void main(String[] args) throws MalformedURLException, IOException {
        // TODO code application logic here
        String userid = "20101003714";

        HttpClientAdaptor httpClient = new HttpClientAdaptor();
        PageParserUserInfo userInfoPaser = new PageParserUserInfo(userid, httpClient);

        Map<String, String> userInfo = userInfoPaser.parserPageForData();

        User user = new User(userid,
                userInfo.get(PageParserUserInfo.kName),
                userInfo.get(PageParserUserInfo.kCollege),
                userInfo.get(PageParserUserInfo.kMajor));
       
        String userBorrowListUrl = userInfo.get(PageParserUserInfo.kBorrowHistoryUrl);
        
        PageParserBorrowList borrowListPaser = new PageParserBorrowList(userBorrowListUrl,userid, httpClient);
        ArrayList<Object> list =  borrowListPaser.parserPageForRepeatedData();
        
        BookBorrowHistory bookBorrowHistory = (BookBorrowHistory)list.get(0);
        System.out.println(userInfo);
        System.out.println(list.size());
        System.out.println(bookBorrowHistory.getaBook().getBookName());
    }

}
