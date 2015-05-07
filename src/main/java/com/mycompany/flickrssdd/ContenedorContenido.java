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
public class ContenedorContenido {
    protected String title;
    protected String flickrEnum;

    public ContenedorContenido(String title, String flickrEnum) {
        this.title = title;
        this.flickrEnum = flickrEnum;
    }
    
    @Override
    public String toString() {
        return title;
    }
}
