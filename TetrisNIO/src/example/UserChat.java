package example;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Enzo_Criska
 */
public class UserChat {
    private String NameID;
    private String password;
    private SelectionKey skey;
    private boolean status = false;
    
    public UserChat(String id, String pass){
        this.NameID = id;
        this.password = pass;
    }
    
    public void setKey(SelectionKey skey){
        this.skey = skey;
    }
    
    public SelectionKey getKey(){
        return skey;
    }
    
    public String getID(){
        return NameID;
    }
    
    public String getPass(){
        return password;
    }
    
    public void login(){
        status = true;
    }
    
    public void logout(){
        status = false;
    }
    
    public boolean getStatus(){
        return status;
    }
}
