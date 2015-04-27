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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Cristian
 */
public class SubirNuevoAlbum extends javax.swing.JDialog {

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
        RequestContext requestContext
                = RequestContext.getRequestContext();
        requestContext.setAuth(SingleFlickr.getInstance().getAuth().getAuth());
        JFileChooser fchooser = new JFileChooser();
        fchooser.setMultiSelectionEnabled(true);
        int result = fchooser.showOpenDialog(this);
        List<PhotoTags> pts = new LinkedList<>();
        if (result == JFileChooser.APPROVE_OPTION) {
            CountDownLatch finishUpload = new CountDownLatch(1);
            //CountDownLatch uploading = new CountDownLatch(1);
            File[] files = fchooser.getSelectedFiles();
            if (files.length > 0) {
                try {
                    JdialogPyS pys = new JdialogPyS(null, true);
                    pys.setVisible(true);
                    String seguridad = pys.getSeguridad();
                    String contenido = pys.getContenido();
                    int privacidad = pys.getPrivacidad();

                    JDialog1 jd = new JDialog1(null, true);
                    for (File f : files) {
                        jd.setImage(f.getAbsolutePath());
                        System.out.println(f.getAbsolutePath());
                        jd.setTitle(f.getName());
                        jd.setVisible(true);
                        if (jd.save()) {
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
                        }
                    }
                    Uploader upd = SingleFlickr.getInstance().getFlickr().getUploader();
                    Set<String> tickets = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>()); //new HashSet<>();
                    Runnable task = () -> {
                        uploadImageWithMeta(pts, seguridad, privacidad, upd, tickets, contenido);
                    };
                    new Thread(task).start();
                    UploadInterface updInterface = new UploadInterface(SingleFlickr.getInstance().getAuth().getApi_key(),
                            SingleFlickr.getInstance().getAuth().getSecret(),
                            new REST());
                    uploadTimer(updInterface, tickets, pts, finishUpload);

                    finishUpload.await();

                    CrearAlbumDialog crearAlbum = new CrearAlbumDialog(null, true);
                    crearAlbum.setNombreAlbum(pts.get(0).file.getParentFile().getName());
                    crearAlbum.setVisible(true);

                    addPhotosToAlbum(updInterface, tickets, crearAlbum);

                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(this,
                            "¿Te gustaría agregar estas fotos a un grupo?",
                            "Solo un último paso ...", dialogButton);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        PoolDialog pd = new PoolDialog(null, true);
                        pd.setVisible(true);
                        String poolID = pd.getPoolID();
                        RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                        updInterface.checkTickets(tickets).stream().forEach(t -> {
                            try {
                                RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                                SingleFlickr.getInstance().getFlickr().getPoolsInterface().add(t.getPhotoId(), poolID);
                            } catch (FlickrException ex) {
                                Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }

                    setTitle(crearAlbum.getNombreAlbum());
                    setJScroll(pts);
                } catch (InterruptedException | FlickrException ex) {
                    Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            this.dispose();
        }
    }

    private void loadLoader() {
        final JDialog d = new JDialog();
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.add(new JLabel("Please Wait..."), new GridBagConstraints());
        d.getContentPane().add(p1);
        d.setSize(100, 100);
        d.setLocationRelativeTo(this);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setModal(true);

        Thread t = new Thread() {
            @Override
            public void run() {
                
                for (int x = 0; x <= 100; x += 10) {
                    final int selection = x;
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("long task up to " + selection + "%");
                    } //do swing work on EDT
                    );
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                SwingUtilities.invokeLater(() -> {//do swing work on EDT
                    d.dispose();
                    System.out.println("Do Some Long Task");
                });
            }
        };
        t.start();
        d.setVisible(true);
    }

    private void uploadTimer(UploadInterface updInterface, Set<String> tickets, List<PhotoTags> pts, CountDownLatch finishUpload) {
        final JDialog d = new JDialog();
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.add(new JLabel("Please Wait..."), new GridBagConstraints());
        d.getContentPane().add(p1);
        d.setSize(100, 100);
        d.setLocationRelativeTo(this);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setModal(true);
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());

                    List<Ticket> checkTickets = updInterface.checkTickets(tickets);
                    Long completed = checkTickets.stream().filter(t -> t.hasCompleted()).count();
                    Long notCompleted = pts.size() - completed;

                    String str = "Progreso: " + completed + " ficheros subidos, " + notCompleted + " pendientes";
                    System.out.println(str);

                    if (completed == pts.size()) {
                        finishUpload.countDown();
                        SwingUtilities.invokeLater(() -> {//do swing work on EDT
                            d.dispose();
                            System.out.println("Finish upload");
                        });
                        this.cancel();
                        timer.cancel();
                    }
                } catch (FlickrException ex) {
                    Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, new Date(), 3000l);
        d.setVisible(true);
    }

    private void uploadImageWithMeta(List<PhotoTags> pts, String seguridad, int privacidad, Uploader upd, Set<String> tickets, String contenido) {
        pts.parallelStream().forEach((pt) -> {
            try {
                UploadMetaData meta = new UploadMetaData();
                meta.setAsync(true);
                meta.setSafetyLevel(seguridad);
                meta.setTitle(pt.title);
                meta.setDescription(pt.description);
                meta.setContentType(contenido);
                setPrivacy(privacidad, meta);
                meta.setTags(pt.tags);
                RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                String ticket = upd.upload(pt.file, meta);
                tickets.add(ticket);
                System.out.println(ticket);
            } catch (FlickrException ex) {
                Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void addPhotosToAlbum(UploadInterface updInterface, Set<String> tickets, CrearAlbumDialog crearAlbum) {
        try {
            List<String> ids = new LinkedList<>();
            updInterface.checkTickets(tickets).stream().forEach(t -> ids.add(t.getPhotoId()));

            RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
            Photoset photoset = SingleFlickr.getInstance().getFlickr().getPhotosetsInterface().create(crearAlbum.getNombreAlbum(), crearAlbum.getDescripcionAlbum(), ids.get(0));
            ids.remove(0);
            ids.parallelStream().forEach(id -> {
                try {
                    RequestContext.getRequestContext().setAuth(SingleFlickr.getInstance().getAuth().getAuth());
                    SingleFlickr.getInstance().getFlickr().getPhotosetsInterface().addPhoto(photoset.getId(), id);
                } catch (FlickrException ex) {
                    Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (FlickrException ex) {
            Logger.getLogger(SubirNuevoAlbum.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setJScroll(List<PhotoTags> pts) {
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
    }

    private void setPrivacy(int contenido, UploadMetaData meta) {
        switch (contenido) {
            case Flickr.PRIVACY_LEVEL_FRIENDS:
                meta.setFriendFlag(true);
                break;
            case Flickr.PRIVACY_LEVEL_FAMILY:
                meta.setFamilyFlag(true);
                break;
            case Flickr.PRIVACY_LEVEL_FRIENDS_FAMILY:
                meta.setFamilyFlag(true);
                meta.setFriendFlag(true);
                break;
            case Flickr.PRIVACY_LEVEL_NO_FILTER:
                break;
            case Flickr.PRIVACY_LEVEL_PRIVATE:
                meta.setHidden(true);
                break;
            case Flickr.PRIVACY_LEVEL_PUBLIC:
                meta.setPublicFlag(true);
                break;
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
