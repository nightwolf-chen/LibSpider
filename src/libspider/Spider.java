/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import java.util.ArrayList;
import java.util.Map;
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


public class Spider {
    
    public SingleUserCrawlResult crawlDataForUser(String userid){
        
        HttpClientAdaptor httpClient = new HttpClientAdaptor();
        PageParserUserInfo userInfoPaser = new PageParserUserInfo(userid, httpClient);

        Map<String, String> userInfo = userInfoPaser.parserPageForData();
        
        if(userInfo == null){
            return null;
        }

        User user = new User(userid,
                userInfo.get(PageParserUserInfo.kName),
                userInfo.get(PageParserUserInfo.kCollege),
                userInfo.get(PageParserUserInfo.kMajor));
       
        String userBorrowListUrl = userInfo.get(PageParserUserInfo.kBorrowHistoryUrl);
        
        PageParserBorrowList borrowListPaser = new PageParserBorrowList(userBorrowListUrl,userid, httpClient);
        ArrayList<Object> list =  borrowListPaser.parserPageForRepeatedData();
        
        
        for(Object obj : list){
            
            BookBorrowHistory borrowHistory = (BookBorrowHistory)obj;
            Book aBook = borrowHistory.getaBook();
            PageParserBookDetail bookDetailParser = new PageParserBookDetail(aBook.getLinkUrl(), httpClient);
            Map<String,String> bookDetailData = bookDetailParser.parserPageForData();
            
            aBook.setLang(bookDetailData.get(PageParserBookDetail.kLang));
            aBook.setPublisher(bookDetailData.get(PageParserBookDetail.kPublisher));
            aBook.setTopic(bookDetailData.get(PageParserBookDetail.kTopic));
            aBook.setAcquireCode(bookDetailData.get(PageParserBookDetail.kAcquireCode));
            aBook.setCategoryCode(bookDetailData.get(PageParserBookDetail.kCategoryCode));
            
            System.out.println(borrowHistory.getaBook().getBookName());
        }
        
        return new SingleUserCrawlResult(user, list);
    }
    
    public void crawlForAllPossibleUserAndSaveToDB(){
          long startUserid = 1000000;
          long endUserid   = 1020000;
          
//          startUserid = 1003712;
//          endUserid = 1003712;
//          
//          for(long userid = startUserid ; userid <= endUserid ;userid++){
//              String useridStr = "2010" + String.valueOf(userid);
//              SingleUserCrawlResult aUserResult = this.crawlDataForUser(useridStr);
//              aUserResult.saveToDB();
//          }
          
             SingleUserCrawlResult aUserResult = this.crawlDataForUser("20101003713");
             aUserResult.saveToDB();
    }
    
}