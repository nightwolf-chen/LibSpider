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
package user;

import classifier.ReaderClassifierAdaptor;
import classifier.ReaderClassifierAdaptorManager;
import java.util.HashMap;
import java.util.Map;
import object.Book;
import object.UserLibInfo;

/**
 *
 * @author bruce
 */
public class UserSimilarity {

    UserLibInfo userInfoA;
    UserLibInfo userInfoB;
    String[] classValues = ReaderClassifierAdaptor.getClassValues();

    public UserSimilarity(UserLibInfo userInfoA, UserLibInfo userInfoB) {
        this.userInfoA = userInfoA;
        this.userInfoB = userInfoB;
    }

    public double caculateSimilarity() {

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
        
        double result = (double)intersection / (double)union;
        return result;
    }

    private Map<String, Integer> getInfoMap(UserLibInfo userInfo) {

        Map<String, Integer> userInfoMap = new HashMap<>();
        ReaderClassifierAdaptorManager cMgr = new ReaderClassifierAdaptorManager();

        for (String str : classValues) {
            userInfoMap.put(str, 0);
        }

        for (Object obj : userInfo.getBorrowList()) {
            Book book = (Book) obj;
            String classValue = cMgr.classify(book);
            Integer lastCount = userInfoMap.get(classValue);
            userInfoMap.put(classValue, lastCount + 1);
        }

        return userInfoMap;
    }
    
    public static void main(String[] args){
        UserDataSource a = new UserDataSource("20101003713");
        UserDataSource b = new UserDataSource("20101003714");
        UserSimilarity similarity = new UserSimilarity(a.getInfo(), b.getInfo());
        System.out.println(similarity.caculateSimilarity());
    }
    
}
