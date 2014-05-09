/*
 * Copyright 2014 nightwolf.
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
package db;

import com.mysql.jdbc.Connection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author nightwolf
 */

/*
{
'name':'中国古典诗歌英译理论研究',
'url':'http://book.douban.com/subject/1974063/',
'image':'http://img3.douban.com/mpic/s5804824.jpg'
}
 */
public class URLDataImportor {

    private String fileName = "url.txt";

    private int readStringFromFile() throws FileNotFoundException, IOException, SQLException {

        Connection con = new ConnectionManager().getConnection();
        PreparedStatement pstmt = con.prepareStatement("update books set bookurl=? , imageurl=? where bookname=?");

        File dataFile = new File(this.fileName);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(dataFile));
        BufferedReader br = new BufferedReader(isr);

        String content = "";
        String tmp = "";

        br.readLine();

        boolean first = true;
        int count = 0;

        while (true) {

            if (first) {
                first = false;
            } else {
                br.readLine();
            }

            String mark = br.readLine();

            if (mark.equals("]}")) {
                break;
            }

            String bookName = this.getContent(br.readLine());
            String bookURL = this.getContent(br.readLine());
            String imageURL = this.getContent(br.readLine());

            pstmt.setString(1, bookURL);
            pstmt.setString(2, imageURL);
            pstmt.setString(3, bookName);
            pstmt.execute();
            
            System.out.println(bookName+"|"+bookURL+"|"+imageURL);

            count++;

            br.readLine();

        }

        con.close();
        pstmt.close();

        return count;
    }

    private String getContent(String formateStr) {
        
        
        
        String[] strs = formateStr.split(":");
        
        String target = "";
        
        if(strs.length > 2){
            target += strs[1];
            target += ":";
            target += strs[2];
        }else{
            target += strs[1];
        }
//        System.err.println(target);

        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(target);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public int doImport() throws JSONException, FileNotFoundException, IOException, SQLException {
      return this.readStringFromFile();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, JSONException, SQLException {
        URLDataImportor importor = new URLDataImportor();
        importor.doImport();
    }
}
