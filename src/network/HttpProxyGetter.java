/*
 * Copyright 2014 bruce.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package network;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author bruce
 */
public class HttpProxyGetter {
    public List<HttpHost> getFreeProxies(){
        
        List<HttpHost> proxies = new ArrayList<>();
        
        try {
            HttpClientAdaptor httpClientAdaptor = new HttpClientAdaptor();
            String jsonContent = httpClientAdaptor.doGet("http://letushide.com/export/json/http,all,cn/");
            
            JSONArray jsonArray = new JSONArray(jsonContent);
            
            for(int i = 0 ; i < jsonArray.length() ;i++){
                String host = jsonArray.getJSONObject(i).getString("host");
                int port = jsonArray.getJSONObject(i).getInt("port");
                proxies.add(new HttpHost(host, port));
                //System.out.println(host+":"+port);
            }
            
            //System.out.println(jsonContent);
            
            return proxies;
        } catch (JSONException ex) {
            Logger.getLogger(HttpProxyGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
