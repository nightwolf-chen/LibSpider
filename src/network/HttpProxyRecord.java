/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.Serializable;

/**
 *
 * @author bruce
 */
public class HttpProxyRecord implements Serializable{
    public String host;
    public String port;
    public String updatetime;
}
