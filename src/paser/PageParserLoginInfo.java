/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package paser;

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
    
    public PageParserLoginInfo(String userid,HttpClientAdaptor httpClient) {
        super(httpClient);
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }
    
    
    
    @Override
    public Map<String, String> parserPageForData() {
        
        String urlStr = "http://lib.gdufs.edu.cn/bor3.php";
        String regex = "<meta http-equiv=\"refresh\" content=\"0.1;url=(.*?)\"";
      
        PatternTool patternTool = new PatternTool();
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("userid", userid));
        String pageContent = this.httpClient.doPost(urlStr, parameters);
        
         if(pageContent == null){
            return null;
        }
        
        String userPageUrl = patternTool.findStringPattern(regex, pageContent, 1);
        
        if(userPageUrl == null){
            return null;
        }
        
        Map<String,String> data = new HashMap<String,String>();
        
        data.put(PageParserLoginInfo.kUserPageUrl, userPageUrl);
        
        return data;
    }

    @Override
    public ArrayList<Object> parserPageForRepeatedData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
