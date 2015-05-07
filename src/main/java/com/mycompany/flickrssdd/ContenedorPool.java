/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickrssdd;

/**
 *
 * @author Cristian
 */
public class ContenedorPool {
    protected String title;
    protected String id;

    public ContenedorPool(String title, String id) {
        this.title = title;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return title;
    }
}
