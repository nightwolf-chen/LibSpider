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

package util;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.HttpClientAdaptor;
import object.Book;
import object.BookBorrowHistory;

/**
 *
 * @author bruce
 */
public class PageParserBorrowList extends PageParser{
    
    private final String url;
    private final String userid;
    
    public PageParserBorrowList(String url,String userid,HttpClientAdaptor httpClient) {
        super(httpClient);
        this.url = url;
        this.userid = userid;
    }

    @Override
    public Map<String, String> parserPageForData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Object> parserPageForRepeatedData() {
        ArrayList<Object> list = new ArrayList<>();
        
        String pageContent = this.httpClient.doGet(this.url);
        
        String regex = " <tr> \n" +
        "  <td class=td1 id=centered valign=top><A HREF=(.*?)>(.*?)</A></td> \n" +
        "  <td class=td1 valign=top><a href=\"(.*?)\" target=_blank>(.*?)</a></td> \n" +
        "  <td class=td1 valign=top><a href=\"(.*?)\" target=_blank>(.*?)</a></td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" + //7
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        "  <td class=td1 valign=top>(.*?)</td> \n" +
        " </tr> ";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pageContent);
        
        while(matcher.find()){
            String borrowInfoUrl = matcher.group(1);
            String bookDetailUrl = matcher.group(3);
            String author = matcher.group(4);
            String bookName = matcher.group(6);
            String publishTime = matcher.group(7);
            String shouldReturnTime = matcher.group(8) + matcher.group(9);
            String actuelReturnTime = matcher.group(10) + matcher.group(11);
            String location = matcher.group(13);
            
            Book aBook = new Book(bookName, author, publishTime, location, bookDetailUrl);
            BookBorrowHistory borrowHistory = new BookBorrowHistory(userid, aBook, shouldReturnTime, actuelReturnTime);
            list.add(borrowHistory);
        }
        
        
        return list;
    }
    
}
