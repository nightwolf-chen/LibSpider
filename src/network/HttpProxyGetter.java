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
import org.nirvawolf.douban.util.SerializatioinHelper;
import org.nirvawolf.douban.util.TimeTool;

/**
 *
 * @author bruce
 */
public class HttpProxyGetter {

    private final int UpdateTimeGap = 1000 * 60 * 30;

    private final String proxySavingPath = "proxies.list";

    public HttpHost getAProxy() {

        List<HttpHost> proxies = this.getAvailableProxies();

        for (HttpHost proxy : proxies) {
            if (this.isProxyReachable(proxy)) {
                return proxy;
            }
        }

        return null;
    }

    private boolean isProxyReachable(HttpHost proxy) {
        HttpClientAdaptor clientAdaptor = new ProxiedHttpClientAdaptor(proxy);
        String result = clientAdaptor.doGet("http://www.baidu.com");
        return (result != null) ? true : false;
    }

    public List<HttpHost> getAvailableProxies() {

        this.doUpdate();

        List<HttpHost> proxies = new ArrayList<HttpHost>();
        List<HttpProxyRecord> proxyRecords = this.loadExistingProxyRecords();

        for (HttpProxyRecord proxyRecord : proxyRecords) {
            String host = proxyRecord.host;
            int port = Integer.valueOf(proxyRecord.port);
            HttpHost proxy = new HttpHost(host, port);
            proxies.add(proxy);
        }

        return proxies;
    }

    private void doUpdate() {

        try {
            
            if (this.shouldCheckUpdate()) {
                this.updateProxies();
            }

        } catch (JSONException ex) {
            Logger.getLogger(HttpProxyGetter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updateProxies() throws JSONException {

        System.out.println("Update proxy list...");

        String currentTimeStr = new TimeTool().getCurrentTime();
        HttpClientAdaptor httpClientAdaptor = new HttpClientAdaptor();
        List<HttpProxyRecord> newProxyRecords = new ArrayList<HttpProxyRecord>();

        String jsonContent = httpClientAdaptor.doGet("http://letushide.com/export/json/http,all,cn/");
        JSONArray jsonArray = new JSONArray(jsonContent);

        for (int i = 0; i < jsonArray.length(); i++) {

            String host = jsonArray.getJSONObject(i).getString("host");
            int port = jsonArray.getJSONObject(i).getInt("port");
            String portStr = String.valueOf(port);

            HttpProxyRecord newProxyRecord = new HttpProxyRecord();
            newProxyRecord.host = host;
            newProxyRecord.port = portStr;
            newProxyRecord.updatetime = currentTimeStr;

            newProxyRecords.add(newProxyRecord);
        }

        httpClientAdaptor.close();

        this.saveProxyRecords(newProxyRecords);
    }

    private boolean shouldCheckUpdate() {

        List<HttpProxyRecord> proxyRecords = this.loadExistingProxyRecords();

        if(proxyRecords == null){
            return true;
        }
        
        for (HttpProxyRecord proxyRecord : proxyRecords) {

            TimeTool tTool = new TimeTool();
            String lastUpdateTime = proxyRecord.updatetime;
            String currentTime = tTool.getCurrentTime();

            long timeGap = tTool.calculateDiscance(lastUpdateTime, currentTime);
            if (timeGap >= 0 && timeGap < this.UpdateTimeGap) {
                return false;
            }

        }

        return true;
    }

    private List<HttpProxyRecord> loadExistingProxyRecords() {
        List<HttpProxyRecord> proxyRecords = (List<HttpProxyRecord>) SerializatioinHelper
                .restoreObjectFromFile(this.proxySavingPath);
        return proxyRecords;
    }

    private void saveProxyRecords(List<HttpProxyRecord> proxyRecords) {
        SerializatioinHelper.serializeToFile(proxyRecords, this.proxySavingPath);
    }
    
    public static void main(String[] args){
            
        HttpHost aProxy = new HttpProxyGetter().getAProxy();
        System.out.println(aProxy.getHostName()+":"+aProxy.getPort());
    }
}
