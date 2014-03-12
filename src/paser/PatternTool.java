/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bruce
 */
/*
 Pattern pattern = Pattern.compile("<meta http-equiv=\"refresh\" content=\"0.1;url=(.*?)\"");
 Matcher matcher = pattern.matcher(content);
            
 String userPageUrl = null;
            
 if (matcher.find()) {
 userPageUrl = matcher.group(1);
 }*/
public class PatternTool {

    public String findStringPattern(String regex, String source, int groupIndex) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        String stringPattern = null;

        while (matcher.find()) {
            stringPattern = matcher.group(groupIndex);
            if(stringPattern != null){
                break;
            }
        }
        
        return stringPattern;
    }
 
}
