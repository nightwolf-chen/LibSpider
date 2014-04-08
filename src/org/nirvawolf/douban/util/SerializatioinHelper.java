/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nirvawolf.douban.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruce
 */
public class SerializatioinHelper {

    public static void serializeToFile(Object obj, String filename) {

        try {
            
            File file = new File(filename);

            if (!file.exists()) {
                file.createNewFile();
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);

            oos.close();
           
        } catch (IOException ex) {

        }

    }

    public static Object restoreObjectFromFile(String filename) {

        try {
            
            File file = new File(filename);
            
            if (!file.exists()) {
                return null;
            }
            
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object obj = ois.readObject();
            
            return obj;
        } catch (IOException ex) {
            Logger.getLogger(SerializatioinHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SerializatioinHelper.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return null;
    }
    
    public static void main(String[] args){
        
        String str = "Hello world!";
        
        System.out.println("Before serialize:"+str);
        
        SerializatioinHelper.serializeToFile(str, "hello");
        
        String str2 = (String)SerializatioinHelper.restoreObjectFromFile("hello");
        
        System.out.println("Restore:"+str2);
    }
}
