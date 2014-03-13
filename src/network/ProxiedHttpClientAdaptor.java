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

import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

/**
 *
 * @author bruce
 */
public class ProxiedHttpClientAdaptor extends HttpClientAdaptor {

    public ProxiedHttpClientAdaptor() {
        super();
        HttpProxyGetter proxyGetter = new HttpProxyGetter();
        List<HttpHost> proxies = proxyGetter.getFreeProxies();

        int randomIndex = (int) ((Math.random() * 1000) % proxies.size());
        HttpHost proxy = proxies.get(randomIndex);

        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        this.httpclient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();
    }

}
