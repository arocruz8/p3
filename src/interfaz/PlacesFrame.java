package interfaz;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import maps.java.Places;
import maps.java.ShowMaps;
import maps.java.StaticMaps;

public class PlacesFrame extends javax.swing.JFrame {

    private String referencePlace;
    private Places ObjPlaces=new Places();
    private String[][] datosReview;
    private ArrayList<String> fotosReferencia;
    private ShowMaps ObjShowMaps=new ShowMaps();
    private StaticMaps ObjStaticMaps=new StaticMaps();
    
    private Icon cargarImagen(String url) throws IOException{
        Image imagen;
        imagen=ImageIO.read(new URL(url));
        imagen=imagen.getScaledInstance(35,35,Image.SCALE_FAST);
        ImageIcon imgIcon=new ImageIcon(imagen);
        Icon iconImage=(Icon)imgIcon;
        return iconImage;
    }
            
    private void cargarInfo() throws UnsupportedEncodingException, MalformedURLException, IOException{
        String[] datos=ObjPlaces.getPlacesDetails(this.referencePlace);
        this.JText_DP_Nombre.setText(datos[0]);
        this.JText_DP_Vecindad.setText(datos[1]);
        this.JText_DP_Telefono.setText(datos[2]);
        this.JText_DP_Direccion.setText(datos[3]);
        this.JText_DP_URLGoogle.setText(datos[4]);
        this.JText_DP_Puntuacion.setText(datos[5]);
        this.JText_DP_Web.setText(datos[7]);
        this.JLabel_DP_NombrePr.setText(datos[0]);
        this.JLabel_DP_Icono.setText("");
        this.JLabel_DP_Icono.setIcon(cargarImagen(datos[6]));
         this.jLabel_DP_MapaEst.setText("");
        Image mapaEst=ObjStaticMaps.getStaticMap(datos[3],15,new Dimension(480,80),1, StaticMaps.Format.jpg, StaticMaps.Maptype.roadmap);
        ImageIcon imgIcon=new ImageIcon(mapaEst);
        Icon iconImage=(Icon)imgIcon;   
        this.jLabel_DP_MapaEst.setIcon(iconImage);
        
    }
    
//    private void seleccionarReview(){
//         if(datosReview.length>0){
//             int indice=this.jList_Review.getSelectedIndex();
//             JText_Autor.setText(datosReview[indice][1]);
//             JText_URLAutor.setText(datosReview[indice][3]);
//             jTextArea_Review.setText(datosReview[indice][2]);
//         }
//    }
//        
//    private void cargarReview() throws UnsupportedEncodingException, MalformedURLException, IOException{
//        datosReview=ObjPlaces.getPlaceReview(this.referencePlace);
//        DefaultListModel modelo = new DefaultListModel();
//        if(datosReview.length>0){
//            for(int i=0;i<datosReview.length;i++){
//                modelo.add(i, "Review por: " + datosReview[i][1]);
//            }
//            this.jList_Review.setModel(modelo);
//            this.jList_Review.setSelectedIndex(0);
//            
//        }else{
//            modelo.add(0,"No hay datos disponibles");
//             this.jList_Review.setModel(modelo);
//        }
//    }
//    
//    private void cargarFoto() throws UnsupportedEncodingException, MalformedURLException, IOException{
//        this.fotosReferencia=ObjPlaces.getPhotosReference();
//        DefaultListModel modelo = new DefaultListModel();
//        if(fotosReferencia.size()>0){
//            for(int i=0;i<fotosReferencia.size();i++){
//                modelo.add(i, "Foto " + (i+1));
//            }
//            this.jList_Fotos.setModel(modelo);
//            this.jList_Fotos.setSelectedIndex(0);
//            
//        }else{
//            modelo.add(0,"No hay datos disponibles");
//            this.jList_Fotos.setModel(modelo);
//        }
//    }
//       
//    private void seleccionarFoto() throws MalformedURLException{
//         if(fotosReferencia.size()>0){
//             int indice=this.jList_Fotos.getSelectedIndex();
//             Image imagenAux=ObjPlaces.getPhoto(fotosReferencia.get(indice), 800);
//             ImageIcon imgIcon=new ImageIcon(imagenAux);
//             Icon iconImage=(Icon)imgIcon;   
//             this.jLabe_Imagen.setIcon(iconImage);
//         }
//    }
//            
    public PlacesFrame(String _referencePlace) throws UnsupportedEncodingException {
        initComponents();
        referencePlace=_referencePlace;
        try {
          cargarInfo();  
//          cargarReview();
//          cargarFoto();
        } catch (Exception e) {
        }
    }

    private PlacesFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JLabel_DP_NombrePr = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        JText_DP_Nombre = new javax.swing.JTextField();
        JText_DP_Vecindad = new javax.swing.JTextField();
        JText_DP_Direccion = new javax.swing.JTextField();
        JText_DP_Telefono = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        JText_DP_URLGoogle = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        JText_DP_Puntuacion = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        JText_DP_Web = new javax.swing.JTextField();
        jLabel_DP_MapaEst = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        JLabel_DP_Icono = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JLabel_DP_NombrePr.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        JLabel_DP_NombrePr.setText("NOMBRE");

        jLabel2.setText("Nombre:");

        jLabel3.setText("Ciudad");

        jLabel4.setText("Dirección:");

        jLabel5.setText("Teléfono:");

        JText_DP_Nombre.setText("jTextField1");

        JText_DP_Vecindad.setText("jTextField2");

        JText_DP_Direccion.setText("jTextField3");

        JText_DP_Telefono.setText("jTextField4");

        jLabel6.setText("Url google");

        JText_DP_URLGoogle.setText("jTextField5");

        jLabel7.setText("Puntuación (/5):");

        JText_DP_Puntuacion.setText("jTextField6");

        jLabel8.setText("Página web:");

        JText_DP_Web.setText("jTextField7");
        JText_DP_Web.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JText_DP_WebActionPerformed(evt);
            }
        });

        jLabel_DP_MapaEst.setText("jLabel9");

        jButton1.setText("Abrir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Abrir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Abrir");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        JLabel_DP_Icono.setText("icono");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(JLabel_DP_NombrePr)
                            .addComponent(jLabel8))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(JText_DP_Vecindad, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JText_DP_Telefono, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JText_DP_Puntuacion, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(JText_DP_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton3))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(JText_DP_URLGoogle, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton2))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(JText_DP_Web, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton1)))
                                    .addComponent(JText_DP_Nombre)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(JLabel_DP_Icono)))
                        .addGap(0, 74, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel_DP_MapaEst, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel_DP_NombrePr)
                    .addComponent(JLabel_DP_Icono))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(JText_DP_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(JText_DP_Vecindad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(JText_DP_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(JText_DP_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(JText_DP_URLGoogle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(JText_DP_Puntuacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(JText_DP_Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addGap(20, 20, 20)
                .addComponent(jLabel_DP_MapaEst, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JText_DP_WebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JText_DP_WebActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JText_DP_WebActionPerformed

     private void abrirWeb(String url) throws URISyntaxException, IOException{
        Desktop.getDesktop().browse(new URI(url));
    }
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       if(!JText_DP_URLGoogle.getText().isEmpty() && !"NO DATA".equals(JText_DP_URLGoogle.getText())){
            try {
                abrirWeb(JText_DP_URLGoogle.getText());
            } catch (Exception ex) {
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!JText_DP_Web.getText().isEmpty() && !"NO DATA".equals(JText_DP_Web.getText())){
            try {
                abrirWeb(JText_DP_Web.getText());
            } catch (Exception ex) {
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(!JText_DP_Direccion.getText().isEmpty()){
        try {
            String direccionMapa = ObjShowMaps.getURLMap(JText_DP_Direccion.getText());
            Desktop.getDesktop().browse(new URI(direccionMapa));
        } catch (Exception ex) {
            }
        }  
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(PlacesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlacesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlacesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlacesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlacesFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel_DP_Icono;
    private javax.swing.JLabel JLabel_DP_NombrePr;
    private javax.swing.JTextField JText_DP_Direccion;
    private javax.swing.JTextField JText_DP_Nombre;
    private javax.swing.JTextField JText_DP_Puntuacion;
    private javax.swing.JTextField JText_DP_Telefono;
    private javax.swing.JTextField JText_DP_URLGoogle;
    private javax.swing.JTextField JText_DP_Vecindad;
    private javax.swing.JTextField JText_DP_Web;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel_DP_MapaEst;
    // End of variables declaration//GEN-END:variables
}
