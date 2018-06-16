package interfaz;


import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import maps.java.Elevation;
import maps.java.Geocoding;
import maps.java.MapsJava;
import maps.java.Places;
import maps.java.Route;
import maps.java.StaticMaps;
import maps.java.StreetView;
import maps.java.ShowMaps;
import org.jsoup.Jsoup;


public class MainFrame extends javax.swing.JFrame {

    public MainFrame() {
        initComponents();
        capturarEventos();
    }

    private EventsStatusBar ObjStatusBar;
    
    private Geocoding ObjGeocoding=new Geocoding();
    //private Elevation ObjElevation=new Elevation();
    //private ShowMaps ObjShowMaps=new ShowMaps();
    //private Route ObjRoute=new Route();
    //private StreetView ObjStreetView=new StreetView();
    private StaticMaps ObjStaticMaps=new StaticMaps();
    private Places ObjPlaces=new Places();
    
    
     private void capturarEventos(){
        ObjStatusBar=new EventsStatusBar(this.jPanel5);
        recorrerComponentes(jTabbedPane1.getComponents());
        recorrerComponentes(jPanel1.getComponents());
        recorrerComponentes(jPanel2.getComponents());
        recorrerComponentes(jPanel3.getComponents());
        recorrerComponentes(jPanel4.getComponents());
        recorrerComponentes(jPanel6.getComponents());
        recorrerComponentes(jPanel7.getComponents());
        recorrerComponentes(jPanel8.getComponents());
        recorrerComponentes(jPanel9.getComponents());
    }
     
    /*
     se encarga de redondear los decimales de las direcciones
     */ 
    double redondeoDosDecimales(double d) {
        return Math.rint(d*1000)/1000;
    }
    private void recorrerComponentes(Component[] componentes){
        for(int i=0; i<componentes.length;i++){ 
            componentes[i].addMouseListener(ObjStatusBar);
        }
    }
    
    /*
    actualiza las propiedades que iingreso el usuario
    */
    private void actualizarPropiedades(){
        JText_Conexion.setText(String.valueOf(MapsJava.getConnectTimeout()));
        JText_idioma.setText(MapsJava.getLanguage());
        JText_Region.setText(MapsJava.getRegion());
        
        if(MapsJava.getSensor()==true){
            JCombo_Sensor.setSelectedIndex(1);
        }else{
            JCombo_Sensor.setSelectedIndex(0);
        }
        JText_Clave.setText(MapsJava.getKey());
    }
    
    /*
    pega texto
    */
    private void pegarTexto() throws ClassNotFoundException, UnsupportedFlavorException, IOException{
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = cb.getContents(this);
        DataFlavor dataFlavorStringJava = new DataFlavor("application/x-java-serialized-object; class=java.lang.String");
        if (t.isDataFlavorSupported(dataFlavorStringJava)) {
           String claveApi = (String) t.getTransferData(dataFlavorStringJava);
           JText_Clave.setText(claveApi);
        }
    }
    /*
    se encarga de validar la clave api del usuario con el mettodo de maps.java check api key
    */
    private void comprobarClaveApi(){
        String status=MapsJava.APIkeyCheck(JText_Clave.getText());
        if("OK".equals(status)){
            this.JLabel_Clave.setText("Válida");
        }else{
            this.JLabel_Clave.setText("No Válida");
        }
    }
    
    /*
    compruba que la conexion de la llave api con la plataforma sea valida 
    */
    private void comprobarStatus(JLabel label){
         label.setText(MapsJava.getLastRequestStatus());
    }
    
    /*
    carga la jlist  donde se almacenan los datos
    */
    private void cargarJList(ArrayList<String> arrayList, JList jlist){
        DefaultListModel listModel = new DefaultListModel();
        for(int i=0; i<arrayList.size(); i++) {
            listModel.add(i, arrayList.get(i));
        }
        jlist.setModel(listModel);
    }
    
    /*
    permite seleccionar los datos almacenados en la jlist
    */
    private void seleccionarItemList(){
        String itemSelecionado=(String)this.jList_CI_DirEncon.getSelectedValue();
        this.JText_CI_DireEnc.setText(itemSelecionado);
    }
    
    private void rellenarPeticiones(){
        String[][] peticiones=MapsJava.getStockRequest();
        String[] columnas=new String[6];
        columnas[0]="Número";columnas[1]="Hora";columnas[2]="Status";columnas[3]="URL";columnas[4]="Información";columnas[5]="Excepción";
        TableModel tableModel=new DefaultTableModel(peticiones, columnas);
       this.jTable_Peticiones.setModel(tableModel);
    }
    
    /*
    se encarga de mostrar el mapa para esto usa la variable dirreción mapa que se encarga de 
    guardar el dato de la dirección que dijito el usuario
    */
    private void mostrarMapa(String direccion) throws IOException, URISyntaxException{
        String direccionMapa=ObjShowMaps.getURLMap(direccion);
        Desktop.getDesktop().browse(new URI(direccionMapa));
    }
    private void mostrarMapa(Double latitud, Double longitud) throws URISyntaxException, IOException{
        String direccionMapa=ObjShowMaps.getURLMap(latitud,longitud);
        Desktop.getDesktop().browse(new URI(direccionMapa));
    }
    
    /*
    este metodo lo que hace es hacer geocoding coordenadas geograficas
    */
    private void CodiGeografica() throws UnsupportedEncodingException, MalformedURLException{
        if(!this.JText_CD_Direc.getText().isEmpty()){
            JText_CD_DireEnc.setText("");
            Point2D.Double resultado=ObjGeocoding.getCoordinates(this.JText_CD_Direc.getText());
            JText_CD_Lati.setText(String.valueOf(resultado.x));
            JText_CD_Long.setText(String.valueOf(resultado.y));
            JText_CD_DireEnc.setText(String.valueOf(ObjGeocoding.getAddressFound()));
            JText_CD_CodigPost.setText(ObjGeocoding.getPostalcode());
            JText_CD_Resolucion.setText(ObjGeocoding.getPostalcode());
        }
    }
    
    /*
    cambia el string de la direccion de texto del lugar que ingreso el usuario por
    sus respectivas coordenadas
    */
       private void CodiGeograficaInver() throws UnsupportedEncodingException, MalformedURLException{
        if(!this.JText_CI_Lati.getText().isEmpty() && !this.JText_CI_Long.getText().isEmpty()){
            JText_CI_DireEnc.setText("");
            DefaultListModel model = new DefaultListModel(); jList_CI_DirEncon.setModel(model);
            ArrayList<String> resultado=ObjGeocoding.getAddress(Double.valueOf(this.JText_CI_Lati.getText()),
                    Double.valueOf(this.JText_CI_Long.getText()));
            if(resultado.size()>0){
                JText_CI_DireEnc.setText(resultado.get(0));
            }
            JText_CI_CodigPost.setText(ObjGeocoding.getPostalcode());
            cargarJList(resultado,jList_CI_DirEncon);
        }
    }
    
    private void crearMapa() throws MalformedURLException, UnsupportedEncodingException{
         if(!JText_ME_Direccion.getText().isEmpty()){
             this.JLabel_ME_Imagen.setText("");
             Image imagenMapa=ObjStaticMaps.getStaticMap(JText_ME_Direccion.getText(),
                     Integer.valueOf(JText_ME_Zoom.getText()),new Dimension(500,500),
                     Integer.valueOf(JText_ME_Escala.getText()),this.seleccionarFormato(),
                     this.seleccionarTipoMapa());
            if(imagenMapa!=null){
                ImageIcon imgIcon=new ImageIcon(imagenMapa);
                Icon iconImage=(Icon)imgIcon;
                JLabel_ME_Imagen.setIcon(iconImage);
            }
         }
     }
     private class MyTableModel extends DefaultTableModel {

         public MyTableModel(Object[][] data, Object[] columnNames) {
             super(data, columnNames);
         }

      @Override
      public Class<?> getColumnClass(int columnIndex) {
                    Class<?> clazz = Object.class;
      Object aux = getValueAt(0, columnIndex);
       if (aux != null) {
        clazz = aux.getClass();
       }

       return clazz;
      }

     }
    private void rellenarPlaces(String[][] resultadoPlaces) throws MalformedURLException, IOException{
        this.JLabel_Pl_Status.setText(MapsJava.getLastRequestStatus());
        if(resultadoPlaces.length>0){
            String[] columnas=new String[6];
            columnas[0]="Place";columnas[1]="Dirección";columnas[2]="Latitud";columnas[3]="Longitud";columnas[4]="Tipo";columnas[5]="Referencia";
            Object[][] obj=new Object[resultadoPlaces.length][resultadoPlaces[0].length];
            for(int i=0; i<obj.length;i++){
                obj[i][0]=resultadoPlaces[i][0].toString();
                obj[i][1]=resultadoPlaces[i][1].toString();
                obj[i][2]=resultadoPlaces[i][2].toString();
                obj[i][3]=resultadoPlaces[i][3].toString();
                Image imageCargada;
                imageCargada=ImageIO.read(new URL(resultadoPlaces[i][4]));
                imageCargada=imageCargada.getScaledInstance(20,20,Image.SCALE_FAST);
                obj[i][4]=new ImageIcon(imageCargada);
                obj[i][5]=resultadoPlaces[i][5].toString();
            }
            TableModel tableModel=new MyTableModel(obj, columnas);
            this.jTable_Pl_places.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.jTable_Pl_places.setModel(tableModel);
            this.jTable_Pl_places.setRowSelectionInterval(0, 0);
            seleccionarReferencia();
        }
    }
    private void borrarTable(JTable jtable){
        jtable.setModel(new DefaultTableModel());
    }
    
    /*
    se encarga de buscar el lugar en especifico que digita el usuario, las cordenadas se transforman
    y permite usar la geolocalización, posteriormente el lugar se guarda en unarreglo con la información
    del mismo permitiendo que se una a jlist para mostrar sus datos
    */
    private void places() throws UnsupportedEncodingException, MalformedURLException, IOException{
        if(!JText_Pl_Direccion.getText().isEmpty()){
            borrarTable(jTable_Pl_places);
            Point2D.Double latLong=ObjGeocoding.getCoordinates(JText_Pl_Direccion.getText());
            if(latLong.x!=0.0 && latLong.y!=0.0){
                String keyword=null;
                if(!JText_Pl_Keyword.getText().isEmpty()){
                    keyword=JText_Pl_Keyword.getText();
                }
                String place=null;
                if(!JText_Pl_Place.getText().isEmpty()){
                    place=JText_Pl_Place.getText();
                }
                ArrayList<String> types=new ArrayList<>();
                if(!"Sin tipo".equals(JCombo_Pl_TipoPlace.getSelectedItem().toString())){
                    types.add(JCombo_Pl_TipoPlace.getSelectedItem().toString());
                }
                Places.Rankby rankby= Places.Rankby.prominence;
                if(!"Importancia".equals(JCombo_Pl_Orden.getSelectedItem().toString())){
                    rankby=Places.Rankby.distance;
                }
                int radio=Integer.valueOf(JText_Pl_Radio.getText());
                String[][] resultado=ObjPlaces.getPlaces(latLong.x, latLong.y,radio,
                        keyword, place,rankby,types);
                rellenarPlaces(resultado);
            }
        }
        
    }
    
    /*
    abre una ventana nueva con la informaci+on del lugar que se busca
    */
    private void abrirFramePlaces(String referenciaPlace) throws UnsupportedEncodingException{
        if(!referenciaPlace.isEmpty()){
            for(UIManager.LookAndFeelInfo laf:UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(laf.getName()))
                    try {
                    UIManager.setLookAndFeel(laf.getClassName());
                } catch (Exception ex) {
                }
            }
            PlacesFrame mainF=new PlacesFrame(referenciaPlace);
            mainF.setVisible(true);
      
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
      private void formWindowOpened(java.awt.event.WindowEvent evt) {                                  
        this.JText_CD_Direc.requestFocus();
    }                                 

    private void JText_CD_MostrarActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        if(!JText_CD_Lati.getText().isEmpty() && !JText_CD_Long.getText().isEmpty()){
            try {
                this.mostrarMapa(Double.valueOf(JText_CD_Lati.getText()),Double.valueOf(JText_CD_Long.getText()));
            } catch (Exception ex) {
        }  
        }
        
    }                                                

    private void JText_CI_MostrarActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        if(!JText_CI_DireEnc.getText().isEmpty()){
            try {
                this.mostrarMapa(JText_CI_DireEnc.getText());
            } catch (Exception ex) {
        }  
        }
    }                                                

    private void JText_CD_BuscarActionPerformed(java.awt.event.ActionEvent evt) {                                                
        try {
            this.CodiGeografica();
            this.comprobarStatus(JLabel_CD_Status);
            this.elevacionCD(this.JText_CD_Lati,this.JText_CD_Long);
        } catch (Exception ex) {
        }
    }                                               

    private void JText_CI_Buscar1ActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        try {
            CodiGeograficaInver();
            this.comprobarStatus(JLabel_CI_Status);
            this.elevacionCI(this.JText_CI_Lati,this.JText_CI_Long);
        } catch (Exception ex) {
        }
    }                                                

    private void jList_CI_DirEnconValueChanged(javax.swing.event.ListSelectionEvent evt) {                                               
        seleccionarItemList();
    }                                              

    private void jButton_PeticionesActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        rellenarPeticiones();
    }                                                  

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
      guardarCambios();
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            pegarTexto();
        } catch (Exception ex) {
        }
    }                                        

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {                                          
        if(jTabbedPane1.getSelectedIndex()==0){
            actualizarPropiedades();
        }
    }                                         

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {        
            Desktop.getDesktop().browse(new URI("http://es.wikipedia.org/wiki/CcTLD"));
        } catch (Exception ex) {
        }
    }                                        

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {        
            Desktop.getDesktop().browse(new URI("https://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1"));
        } catch (Exception ex) {
        }
    }                                        

    private void jButton_Peticiones1ActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        try {
            crearRuta();
        } catch (Exception ex) {
        }
    }                                                   
   
    private void JText_CD_Buscar1ActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        try {
            cargarStreetView();
        } catch (Exception ex) {
        }
    }                                                

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {                                      
        this.JText_SV_horizontal.setText(String.valueOf(jSlider1.getValue()));
    }                                     

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {                                      
        this.JText_SV_zoom.setText(String.valueOf(jSlider2.getValue()));
    }                                     
    
    private void JButton_ME_Buscar1ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        if(!MapsJava.getKey().isEmpty()){
        try {
            places();
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        }else{
            JOptionPane.showMessageDialog(null,"Esta función requiere clave de desarrollador", 
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }                                                  

    private void JSlider_Pl_RadioStateChanged(javax.swing.event.ChangeEvent evt) {                                              
        this.JText_Pl_Radio.setText(String.valueOf(JSlider_Pl_Radio.getValue()));
    }                                             

    private void seleccionarReferencia(){
        if(jTable_Pl_places.getRowCount()>0){
          this.JText_Pl_Referencia.setText((String)jTable_Pl_places.getValueAt(jTable_Pl_places.getSelectedRow(),5));
        }
    }
    private void jTable_Pl_placesMousePressed(java.awt.event.MouseEvent evt) {                                              
         seleccionarReferencia();
    }                                             

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
             abrirFramePlaces(JText_Pl_Referencia.getText());
        } catch (Exception e) {
        }
    }                                        
  
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        comprobarClaveApi();
    }                                        

    private void JSlider_ME_ZoomStateChanged(javax.swing.event.ChangeEvent evt) {                                             
        this.JText_ME_Zoom.setText(String.valueOf(JSlider_ME_Zoom.getValue()));
    }                                            

    private void JSlider_ME_EscalaStateChanged(javax.swing.event.ChangeEvent evt) {                                               
        this.JText_ME_Escala.setText(String.valueOf(JSlider_ME_Escala.getValue()));
    }                                              

    private void JButton_ME_BuscarActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        try {
            this.crearMapa();
        } catch (Exception ex) {
        }
    }                                              
    
    
    
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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
