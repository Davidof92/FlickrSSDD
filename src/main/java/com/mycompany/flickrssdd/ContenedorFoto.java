/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickrssdd;

import com.flickr4java.flickr.photos.Photo;

/**
 *
 * @author USUARIO100
 */
public class ContenedorFoto {
    
    public String title;
    public Photo imagen;

    public ContenedorFoto(String title, Photo imagen) {
        this.title = title;
        this.imagen = imagen;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Photo getImagen() {
        return imagen;
    }

    public void setImagen(Photo imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return title;
    }
    
}
