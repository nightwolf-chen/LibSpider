/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author bruce
 */
public class HttpClientAdaptor {

    
    protected  CloseableHttpClient httpclient = HttpClients.createDefault();
    private final HttpClientContext localContext = HttpClientContext.create();
   
    public HttpClientAdaptor() {
        CookieStore cookieStore = new BasicCookieStore();
        localContext.setCookieStore(cookieStore);
    }

    public String doGet(String url) {
        
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = this.httpclient.execute(httpGet,localContext);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            
            String htmlStr = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                htmlStr += (line + "\n");
            }
            
            return htmlStr;
        } catch (IOException ex) {
            Logger.getLogger(HttpClientAdaptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public String doPost(String url, List<NameValuePair> parameters) {

        try {

            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            CloseableHttpResponse response = this.httpclient.execute(httpPost, localContext);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String htmlStr = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                htmlStr += (line + "\n");
            }
            
            return htmlStr;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpClientAdaptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpClientAdaptor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
