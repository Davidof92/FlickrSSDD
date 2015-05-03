package com.mycompany.flickrssdd;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.tags.Tag;
import com.flickr4java.flickr.uploader.Uploader;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jvnet.substance.SubstanceLookAndFeel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author USUARIO100
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    protected Collection<Photoset> photosets;

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        //System.out.println(System.getProperty("java.io.tmpdir"));
        JFrame.setDefaultLookAndFeelDecorated(true); //que nos permite dejar a Substance la decoracion ( por asi decirlo) 
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.OfficeBlue2007Skin");
        updateAlbums();
    }

    private void updateAlbums() {
        try {
            RequestContext requestContext = RequestContext.getRequestContext();
            requestContext.setAuth(SingleFlickr.getInstance().getAuth().getAuth());

            Photosets list = SingleFlickr.getInstance().getFlickr().getPhotosetsInterface()
                    .getList(SingleFlickr.getInstance().getAuth().getUserID());
            photosets = list.getPhotosets();
            this.jComboBox1.removeAllItems();
            this.jComboBox1.addItem("-----------------------------------------------------------------------------------------------------------------------------");
            photosets.stream().forEach((ps) -> {
                ContenedorAlbum contenedor = new ContenedorAlbum(ps.getId(), ps.getTitle());
                this.jComboBox1.addItem(contenedor);
            });

            jComboBox1.addItemListener((ItemEvent e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (jComboBox1.getSelectedIndex() > 0) {
                        System.out.println("SELECTED");
                        System.out.println("Leemos imágenes del album");
                        ContenedorAlbum album = (ContenedorAlbum) jComboBox1.getSelectedItem();
                        System.out.println(album);
                        imgs.removeAll();

                        PhotosetsInterface photosetsInterface = SingleFlickr.getInstance().getFlickr()
                                .getPhotosetsInterface();
                        try {
                            RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                            PhotoList<Photo> photoList = photosetsInterface.getPhotos(album.getId(),
                                    new HashSet<>(), 0, 0, 0);
                            DefaultListModel modelo = new DefaultListModel();
                            photoList.stream().forEach((pl) -> {
                                ContenedorFoto cf = new ContenedorFoto(pl.getTitle(), pl);
                                modelo.addElement(cf);
                            });
                            imgs.setModel(modelo);
                            //fl.getPhotosetsInterface().getPhotosetCount("");
                        } catch (FlickrException ex) {
                            Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        imgs.removeAll();
                        DefaultListModel modelo = new DefaultListModel();
                        imgs.setModel(modelo);
                    }
                }
            });
        } catch (FlickrException ex) {
            Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        imgs = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Seleccionar álbum");

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Subir nuevo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        imgs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imgsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(imgs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, 534, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SubirNuevoAlbum sna = new SubirNuevoAlbum(this, true);
        sna.setVisible(true);
        updateAlbums();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void imgsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imgsMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int index = imgs.locationToIndex(evt.getPoint());
            System.out.println(index);
            ContenedorFoto cf = (ContenedorFoto) imgs.getModel().getElementAt(index);
            System.out.println(cf);
            try {
                RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                Photo photo = SingleFlickr.getInstance().getFlickr().getPhotosInterface().getPhoto(cf.getImagen().getId());
                //Photo pp = fl.getPhotosInterface().getInfo(cf.getImagen().getId(), null); //, null)
                //Descargar foto para verla y luego pasar el PATH
                URL url = new URL(photo.getLargeUrl());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream is = conn.getInputStream();

                String fileName = System.getProperty("java.io.tmpdir") + photo.getTitle() + "." + photo.getOriginalFormat();
                OutputStream os = new FileOutputStream(fileName);

                byte[] b = new byte[8388608];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                JDialog1 editarImagen = new JDialog1(this, true, photo, fileName);
                editarImagen.setVisible(true);
                if (editarImagen.save()) {
                    RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                    SingleFlickr.getInstance().getFlickr().getPhotosInterface().setMeta(photo.getId(), editarImagen.obtenerTitulo(), editarImagen.obtenerDescripcion());
                    photo.getTags().stream().forEach(tag -> {
                        try {
                            RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                            SingleFlickr.getInstance().getFlickr().getPhotosInterface().removeTag(tag.getId());
                        } catch (FlickrException ex) {
                            Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                    SingleFlickr.getInstance().getFlickr().getPhotosInterface().addTags(photo.getId(), editarImagen.obtenerTags().split(","));
                    /*Collection<String> col = new LinkedList<>();
                     col.addAll(Arrays.asList(editarImagen.obtenerTags().split(",")));
                     Tag t = new Tag();
                     photo.setTags(col);*/ //photo.setTags(Arrays.asList(editarImagen.obtenerTags().split(",")));
                }
            } catch (FlickrException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_imgsMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList imgs;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
