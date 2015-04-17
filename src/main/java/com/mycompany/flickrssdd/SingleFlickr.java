/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickrssdd;

import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;

/**
 *
 * @author Cristian
 */
public class SingleFlickr {
    protected static SingleFlickr instance;
    protected AutorizacionesFlickr auth;
    protected SingleFlickr() {
        auth = new AutorizacionesFlickr();
    }
    public AutorizacionesFlickr getAuth() {
        return auth;
    }
    public static SingleFlickr getInstance() {
        if(SingleFlickr.instance == null) {
            SingleFlickr.instance = new SingleFlickr();
        }
        return SingleFlickr.instance;
    }
}
