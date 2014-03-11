/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package object;

/**
 *
 * @author bruce
 */
public class User {
    private String userid;
    private String name;
    private String college;
    private String major;

    public User(String userid, String name, String college, String major) {
        this.userid = userid;
        this.name = name;
        this.college = college;
        this.major = major;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getCollege() {
        return college;
    }

    public String getMajor() {
        return major;
    }
    
    
}
