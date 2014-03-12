/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libspider;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author bruce
 */
public class LibSpider {

    public static void main(String[] args) throws MalformedURLException, IOException {
        // TODO code application logic here
//        for(int i = 0 ; i < ConcurrencyManager.nThreads ;i++){
//            ConcurrencyManager.mainEventExecutor.execute(new Spider());
//        }
//  
            new Spider().run();
    }

}
