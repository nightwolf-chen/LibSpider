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
package paser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.HttpClientAdaptor;
import org.nirvawolf.douban.util.HTMLSpirit;


/**
 *
 * @author bruce
 */
public class PageParserBookDetail extends PageParser {

    public static final String kLang = "作品语种";
    public static final String kPublisher = "出版发行";
    public static final String kBookName = "题名";
    public static final String kTopic = "主题";
    public static final String kCategoryCode = "中图分类号";
    public static final String kAcquireCode = "索书号";

    private final String bookDetailUrl;

    public PageParserBookDetail(String bookDetailUrl, HttpClientAdaptor httpClient) {
        super(httpClient);
        this.bookDetailUrl = bookDetailUrl;
    }

    @Override
    public Map<String, String> parserPageForData() {

        Map<String, String> data = new HashMap<String,String>();

        String pageContent = this.httpClient.doGet(this.bookDetailUrl);
        
        if(pageContent == null){
            return null;
        }
        String regex1 = "<tr> \n"
                + "  <td class=td1 \n"
                + "      id=bold \n"
                + "      width=15% \n"
                + "      valign=top \n"
                + "      nowrap> \n"
                + "    (.*?) \n"
                + "  </td> \n"
                + "  <td class=td1 align=left > \n"
                + "    (.*?) \n"
                + "  </td> \n"
                + " </tr>";
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher1 = pattern1.matcher(pageContent);
        while (matcher1.find()) {
            String name = HTMLSpirit.delHTMLTag(matcher1.group(1)).replaceAll("&nbsp;", "").trim();
            String value = HTMLSpirit.delHTMLTag(matcher1.group(2)).replaceAll("&nbsp;", "").trim();
            data.put(name, value);
        }
        return data;
    }

    @Override
    public ArrayList<Object> parserPageForRepeatedData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
