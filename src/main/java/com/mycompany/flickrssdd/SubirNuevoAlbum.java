/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickrssdd;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author Cristian
 */
public class SubirNuevoAlbum extends javax.swing.JDialog {

    protected Flickr fl;
    AutorizacionesFlickr autorizacionesFlickr;

    //method add defined in the mother object
    public void add(Component c, int x, int y, int sX, int sY) {
        add(c);
        c.setBounds(x, y, sX, sY);
    }

    /**
     * Creates new form SubirNuevoAlbum
     *
     * @param parent
     * @param modal
     */
    public SubirNuevoAlbum(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        autorizacionesFlickr = new AutorizacionesFlickr();
        fl = new Flickr(autorizacionesFlickr.getApi_key(),
                autorizacionesFlickr.getSecret(),
                new REST());
        RequestContext requestContext
                = RequestContext.getRequestContext();
        requestContext.setAuth(autorizacionesFlickr.getAuth());
        JFileChooser fchooser = new JFileChooser();
        fchooser.setMultiSelectionEnabled(true);
        int result = fchooser.showOpenDialog(this);
        List<PhotoTags> pts = new LinkedList<>();
        if (result == JFileChooser.APPROVE_OPTION) {
            CountDownLatch finishUpload = new CountDownLatch(1);
            File[] files = fchooser.getSelectedFiles();
            if (files.length > 0) {
                try {
                    JDialog1 jd = new JDialog1(null, true);
                    for (File f : files) {
                        jd.setImage(f.getAbsolutePath());
                        System.out.println(f.getAbsolutePath());
                        //jd.setVisible(true);
                        //if (jd.save()) {
                        PhotoTags pt = new PhotoTags();
                        pt.file = f;
                        pt.title = jd.obtenerTitulo();
                        if (pt.title.isEmpty()) {
                            pt.title = pt.file.getName();
                        }
                        pt.description = jd.obtenerDescripcion();
                        pt.tags = new LinkedList<>();
                        pt.tags.addAll(Arrays.asList(jd.obtenerTags().split(",")));
                        pts.add(pt);
                        //}
                    }
                    Uploader upd = fl.getUploader();
                    Set<String> tickets = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>()); //new HashSet<>();
                    Runnable task = () -> {
                        pts.parallelStream().forEach((pt) -> {
                            try {
                                UploadMetaData meta = new UploadMetaData();
                                meta.setAsync(true);
                                meta.setTitle(pt.title);
                                meta.setDescription(pt.description);
                                meta.setTags(pt.tags);
                                RequestContext.getRequestContext().setAuth(autorizacionesFlickr.getAuth());
                                String ticket = upd.upload(pt.file, meta);
                                tickets.add(ticket);
                                System.out.println(ticket);
                            } catch (FlickrException ex) {
                                Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    };
                    new Thread(task).start();
                    /*Runnable task2;
                    task2 = new Runnable() {
                    
                    public void run() {*/
                    UploadInterface updInterface = new UploadInterface(autorizacionesFlickr.getApi_key(),
                            autorizacionesFlickr.getSecret(),
                            new REST());
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        
                        @Override
                        public void run() {
                            try {
                                RequestContext.getRequestContext().setAuth(autorizacionesFlickr.getAuth());
                                
                                List<Ticket> checkTickets = updInterface.checkTickets(tickets);
                                Long completed = checkTickets.stream().filter(t -> t.hasCompleted()).count();
                                Long notCompleted = pts.size() - completed;
                                
                                String str = "Progreso: " + completed + " ficheros subidos, " + notCompleted + " pendientes";
                                System.out.println(str);
                                
                                if (completed == pts.size()) {
                                    finishUpload.countDown();
                                    this.cancel();
                                    timer.cancel();
                                }
                            } catch (FlickrException ex) {
                                Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }, new Date(), 3000l);
                    
                    finishUpload.await();
                    try {
                        List<String> ids = new LinkedList<>();
                        updInterface.checkTickets(tickets).stream().forEach(t -> ids.add(t.getPhotoId()));
                        
                        RequestContext.getRequestContext().setAuth(autorizacionesFlickr.getAuth());
                        Photoset photoset = fl.getPhotosetsInterface().create("dfgfdg", "Descripción", ids.get(0));
                        ids.remove(0);
                        ids.parallelStream().forEach(id -> {
                            try {
                                RequestContext.getRequestContext().setAuth(autorizacionesFlickr.getAuth());
                                fl.getPhotosetsInterface().addPhoto(photoset.getId(), id);
                            } catch (FlickrException ex) {
                                Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    } catch (FlickrException ex) {
                        Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                    pts.stream().map((pt) -> {
                        ImagePanel ip = new ImagePanel();
                        ip.cambiarTitulo(pt.title);
                        ip.cambiarDescripcion(pt.description);
                        ip.cambiarTags(pt.tags.toString());
                        ip.cambiarImagen(pt.file.getAbsolutePath());
                        return ip;
                    }).forEach((ip) -> {
                        panel.add(ip);
                    });

                    jScrollPane1.setViewportView(panel);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

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

        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(SubirNuevoAlbum.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SubirNuevoAlbum.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SubirNuevoAlbum.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SubirNuevoAlbum.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SubirNuevoAlbum dialog = new SubirNuevoAlbum(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
