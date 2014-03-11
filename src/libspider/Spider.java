/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.HTTPTool;
import object.BookBorrowHistory;
import util.PatternTool;

/**
 *
 * @author bruce
 */


public class Spider {

    public String getUserPageUrl(String userid) {
        
        String urlStr = "http://lib.gdufs.edu.cn/bor3.php";
        String regex = "<meta http-equiv=\"refresh\" content=\"0.1;url=(.*?)\"";
        HTTPTool httpTool = new HTTPTool();
        PatternTool patternTool = new PatternTool();
        String pageContent = httpTool.sendRequest(urlStr, "userid=" + userid, HTTPTool.METHOD.POST);
        String userPageUrl = patternTool.findStringPattern(regex, pageContent, 1);
        
        return userPageUrl;
    }
    
    public String getUserBookListUrl(String userid){
        
        String urlStr = this.getUserPageUrl(userid);
        String regex = "<td class=td2 align=left> \n" +
        "        借阅历史列表 \n" +
        "  </td> \n" +
        "    <td class=td1><a href=\"javascript:replacePage\\('(.*?)'\\);\">(.*?)</a></td>";
        HTTPTool httpTool = new HTTPTool();
        PatternTool patternTool = new PatternTool();
        String pageContent = httpTool.sendRequest(urlStr, "", HTTPTool.METHOD.GET);
        String userBookListUrl = patternTool.findStringPattern(regex, pageContent, 1);
        return userBookListUrl;
    }
    /*
    
  <tr> 
  <td class=td1 id=centered valign=top><A HREF=http://opac.gdufs.edu.cn:8991/F/SMHPEXP6CHVB4R9FFRHU9IUVXMPSMG2BCK66C9P2CVJNXAGNXX-05504?func=BOR-HISTORY-LOAN-EXP&index=0092&loan_number=003065825&adm_library=GWD50>92  </A></td> 
  <td class=td1 valign=top><a href="http://opac.gdufs.edu.cn:8991/F/SMHPEXP6CHVB4R9FFRHU9IUVXMPSMG2BCK66C9P2CVJNXAGNXX-05505?func=direct&doc_number=000271211&format=999&local_base=GWD01" target=_blank>舒庆</a></td> 
  <td class=td1 valign=top><a href="http://opac.gdufs.edu.cn:8991/F/SMHPEXP6CHVB4R9FFRHU9IUVXMPSMG2BCK66C9P2CVJNXAGNXX-05506?func=direct&doc_number=000271211&format=999&local_base=GWD01" target=_blank>超越最好的自己</a></td> 
  <td class=td1 valign=top>2007</td> 
  <td class=td1 valign=top>20101113</td> 
  <td class=td1 valign=top>22:00</td> 
  <td class=td1 valign=top>20101110</td> 
  <td class=td1 valign=top>18:52</td> 
  <td class=td1 valign=top><br></td> 
  <td class=td1 valign=top>南校区中文书外借分馆</td> 
 </tr> 
    
    */
    public ArrayList<BookBorrowHistory> getUserBookBorrowHistories(String userid){
        
        ArrayList<BookBorrowHistory> array = new ArrayList<BookBorrowHistory>();
        String urlStr = this.getUserBookListUrl(userid);
        
        String regex = " <tr> \n" +
        "  <td class=td1 id=centered valign=top><A HREF=(.*?)>(.*?)</A></td> \n" +
        "  <td class=td1 valign=top><a href=\"(.*?)\" target=_blank>(.*?)</a></td> \n" +
        "  <td class=td1 valign=top><a href=\"(.*?)\" target=_blank>(.*?)</a></td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" + //7
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        " </tr> ";
        HTTPTool httpTool = new HTTPTool();
        
        String pageContent = httpTool.sendRequest(urlStr, "", HTTPTool.METHOD.GET);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pageContent);
        
        while(matcher.find()){
            String borrowInfoUrl = matcher.group(1);
            String bookDetailUrl = matcher.group(3);
            String author = matcher.group(4);
            String bookName = matcher.group(6);
            String publishTime = matcher.group(7);
            String shouldReturnTime = matcher.group(8) + matcher.group(9);
            String actuelReturnTime = matcher.group(10) + matcher.group(11);
            String location = matcher.group(13);
            
            System.out.println(author);
            System.out.println(bookName);
        }
        
        
        return null;
    }
}