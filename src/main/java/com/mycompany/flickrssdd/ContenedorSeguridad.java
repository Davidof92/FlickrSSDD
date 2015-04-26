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
public class ContenedorSeguridad {
    String title;
    String flickrEnum;

    public ContenedorSeguridad(String title, String flickrEnum) {
        this.title = title;
        this.flickrEnum = flickrEnum;
    }
    
    @Override
    public String toString() {
        return title;
    }
}
