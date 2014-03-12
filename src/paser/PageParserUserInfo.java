/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.HttpClientAdaptor;

/**
 *
 * @author bruce
 */
public class PageParserUserInfo extends PageParser {

    public static final String kUserid = "kUserid";
    public static final String kName = "kName";
    public static final String kCollege = "kCollege";
    public static final String kMajor = "kMajor";
    public static final String kBorrowHistoryUrl = "kBorrowHistoryUrl";

    private String userid;
  
    public PageParserUserInfo(String userid, HttpClientAdaptor httpClient) {
        super(httpClient);
        this.userid = userid;
    }

    @Override
    public Map<String, String> parserPageForData() {

        Map<String, String> data = new HashMap<>();
        data.put(kUserid, this.userid);

        PageParserLoginInfo loginParser = new PageParserLoginInfo(this.userid, this.httpClient);
        Map<String, String> tmpData = loginParser.parserPageForData();
        String urlStr = tmpData.get(PageParserLoginInfo.kUserPageUrl);
        
        if(urlStr == null){
            return null;
        }
        
        String regex = "<td class=td2 align=left> \n"
                + "        借阅历史列表 \n"
                + "  </td> \n"
                + "    <td class=td1><a href=\"javascript:replacePage\\('(.*?)'\\);\">(.*?)</a></td>";

        PatternTool patternTool = new PatternTool();
        String pageContent = this.httpClient.doGet(urlStr);
        String userBookListUrl = patternTool.findStringPattern(regex, pageContent, 1);
        data.put(kBorrowHistoryUrl, userBookListUrl);

        String userInfoUpdateUrl = null;
        String regex2 = "<a class=\"blue\" href=\"javascript:replacePage\\('(.*?)'\\);\" title=\"Addresses\"> \n"
                + "       更新地址</a>";
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher2 = pattern2.matcher(pageContent);
        if (matcher2.find()) {
            userInfoUpdateUrl = matcher2.group(1);
        }

        if (userInfoUpdateUrl != null) {
            String updatePageContent = this.httpClient.doGet(userInfoUpdateUrl);
            String name = null;
            String college = null;
            String major = null;
            String regex3 = "<td class=td2> <input size=30 name=\"F0(.*?)\" value=\"(.*?)\"></td> ";
            Pattern pattern3 = Pattern.compile(regex3);
            Matcher matcher3 = pattern3.matcher(updatePageContent);
            while (matcher3.find()) {
                
                if( matcher3.group(1).equals("3") ){
                    name = matcher3.group(2);
                }else if(matcher3.group(1).equals("4")){
                    college = matcher3.group(2);
                }else if(matcher3.group(1).equals("5")){
                    major = matcher3.group(2);
                }
            }

            data.put(kName, name);
            data.put(kCollege, college);
            data.put(kMajor, major);
        }

        return data;
    }

    @Override
    public ArrayList<Object> parserPageForRepeatedData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
