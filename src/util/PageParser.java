/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author bruce
 */
public abstract class PageParser {
    abstract public Map<String,String> parserPageForData();
    abstract public ArrayList<Object> parserPageForRepeatedData();
}
