/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.HttpClientAdaptor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author bruce
 */
public class PageParserLoginInfo extends PageParser{

    static public final String kUserPageUrl = "_userPageUrlKey_";
    
    private String userid ;
    private final HttpClientAdaptor httpClient ; 
    
    public PageParserLoginInfo(String userid,HttpClientAdaptor httpClient) {
        this.userid = userid;
        this.httpClient = httpClient;
    }

    public String getUserid() {
        return userid;
    }
    
    
    
    @Override
    public Map<String, String> parserPageForData() {
        
        String urlStr = "http://lib.gdufs.edu.cn/bor3.php";
        String regex = "<meta http-equiv=\"refresh\" content=\"0.1;url=(.*?)\"";
      
        PatternTool patternTool = new PatternTool();
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("userid", userid));
        String pageContent = this.httpClient.doPost(urlStr, parameters);
        String userPageUrl = patternTool.findStringPattern(regex, pageContent, 1);
        
        Map<String,String> data = new HashMap<>();
        
        data.put(PageParserLoginInfo.kUserPageUrl, userPageUrl);
        
        return data;
    }

    @Override
    public ArrayList<Object> parserPageForRepeatedData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
