/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package paser;

import java.util.ArrayList;
import java.util.Map;
import network.HttpClientAdaptor;

/**
 *
 * @author bruce
 */
public abstract class PageParser {
    
    protected final HttpClientAdaptor httpClient;
    
    public PageParser(HttpClientAdaptor httpClient){
        this.httpClient = httpClient;
    }
    
    abstract public Map<String,String> parserPageForData();
    abstract public ArrayList<Object> parserPageForRepeatedData();
}
