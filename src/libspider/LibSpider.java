/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import network.HttpClientAdaptor;
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
       PageParserUserInfo userInfoPaser = new PageParserUserInfo(userid,httpClient);
        
       Map<String,String> userInfo = userInfoPaser.parserPageForData();
        
       System.out.print(userInfo);
    }

}
