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
package recommendation;

import classifier.ReaderClassifierAdaptor;
import java.util.HashMap;
import java.util.Map;
import object.Book;
import object.UserLibInfo;

/**
 *
 * @author bruce
 */
public class UserSimilarity {

    private UserLibInfo userInfoA;
    private UserLibInfo userInfoB;
    private double similarity;
    
    String[] classValues = ReaderClassifierAdaptor.getClassValues();

    public UserSimilarity(UserLibInfo userInfoA, UserLibInfo userInfoB) {
        this.userInfoA = userInfoA;
        this.userInfoB = userInfoB;
        this.similarity = this.caculateSimilarity();
    }

    private double caculateSimilarity() {

        Map<String, Integer> userA = this.getInfoMap(userInfoA);
        Map<String, Integer> userB = this.getInfoMap(userInfoB);

        int intersection = 0;
        int union = 0;
        for (String str : classValues) {
            
            int a = userA.get(str);
            int b = userB.get(str);
            
            intersection += Math.min(a, b);
            union += Math.max(a, b);
        }
        
        if(union <= 0){
            return 0;
        }
        
        double result = (double)intersection / (double)union;
        return result;
    }

    private Map<String, Integer> getInfoMap(UserLibInfo userInfo) {

        Map<String, Integer> userInfoMap = new HashMap<String,Integer>();
        
        for (String str : classValues) {
            userInfoMap.put(str, 0);
        }

        for (Book book : userInfo.getBorrowList()) {
            
            String classValue = book.getClassValue();
            Integer lastCount = userInfoMap.get(classValue);
            userInfoMap.put(classValue, lastCount + 1);
        }

        return userInfoMap;
    }

    public double getSimilarity() {
        return similarity;
    }
    
    
    
    public static void main(String[] args){
        UserDataSource a = new UserDataSource("20101003713");
        UserDataSource b = new UserDataSource("20101003712");
        UserSimilarity similarity = new UserSimilarity(a.getInfo(), b.getInfo());
        System.out.println(similarity.getSimilarity());
    }
    
}
