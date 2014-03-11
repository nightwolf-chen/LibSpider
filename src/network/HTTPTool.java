/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import libspider.LibSpider;

/**
 *
 * @author bruce
 */
public class HTTPTool {
    
    public enum METHOD {POST,GET};
    
    
    public String sendRequest(String url,String parameters,METHOD method){
        
        String responseContent = null;
        
        if(method == METHOD.POST){
            responseContent = this.doPost(url, parameters);
        }else if(method == METHOD.GET ){
            responseContent = this.doGet(url, parameters);
        }
        
        return responseContent;
    }
    
    private String doPost(String urlStr,String parameters){
        try {
            URL url = new URL(urlStr);
            URLConnection connection = null;
            try {
                connection = url.openConnection();
            } catch (IOException ex) {
                Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
            }
            connection.setDoOutput(true);
            
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outStreamWriter.write(parameters);
            outStreamWriter.flush();
            outStreamWriter.close();
            
            InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            
            String line = null;
            String content = "";
            while ((line = bReader.readLine()) != null) {
                //System.out.println(line);
                if (line != null) {
                    content += (line + "\n") ;
                }
            }
            
            bReader.close();
            inReader.close();
           
            return content;
        } catch (MalformedURLException ex) {
            Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    private String doGet(String urlStr,String parameters){
        try {
            URL url = new URL(urlStr+"?"+parameters);
            URLConnection connection = null;
            try {
                connection = url.openConnection();
            } catch (IOException ex) {
                Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            
            String line = null;
            String content = "";
            while ((line = bReader.readLine()) != null) {
                //System.out.println(line);
                if (line != null) {
                    content += (line+"\n");
                }
            }
            
            bReader.close();
            inReader.close();
            
            return content;
        } catch (MalformedURLException ex) {
            Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LibSpider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
