package interfaz;

import java.awt.Component;
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

public class ventanaPrincipal extends javax.swing.JFrame {
    
    public ventanaPrincipal() {
        initComponents();
        capturarEventos();
    }

    private EventsStatusBar ObjStatusBar;
  
    
    private Geocoding ObjGeocoding=new Geocoding();
    private ShowMaps ObjShowMaps=new ShowMaps();
    private StaticMaps ObjStaticMaps=new StaticMaps();
    private Places ObjPlaces=new Places();
    
    private void capturarEventos(){
        ObjStatusBar=new EventsStatusBar(this.jPanel5);
        recorrerComponentes(jTabbedPane1.getComponents());
        recorrerComponentes(jPanel4.getComponents());
        recorrerComponentes(jPanel8.getComponents());
        recorrerComponentes(jPanel9.getComponents());
    }
    
    double redondeoDosDecimales(double d) {
        return Math.rint(d*1000)/1000;
    }
    
    private void recorrerComponentes(Component[] componentes){
        for(int i=0; i<componentes.length;i++){ 
            componentes[i].addMouseListener(ObjStatusBar);
        }
    }
    
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
    
    private void pegarTexto() throws ClassNotFoundException, UnsupportedFlavorException, IOException{
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = cb.getContents(this);
        DataFlavor dataFlavorStringJava = new DataFlavor("application/x-java-serialized-object; class=java.lang.String");
        if (t.isDataFlavorSupported(dataFlavorStringJava)) {
           String claveApi = (String) t.getTransferData(dataFlavorStringJava);
           JText_Clave.setText(claveApi);
        }
    }
    
    private void comprobarClaveApi(){
        String status=MapsJava.APIkeyCheck(JText_Clave.getText());
        if("OK".equals(status)){
            this.JLabel_Clave.setText("Válida");
        }else{
            this.JLabel_Clave.setText("No Válida");
        }
    }
    
    private void comprobarStatus(JLabel label){
         label.setText(MapsJava.getLastRequestStatus());
    }
    
    private void mostrarMapa(String direccion) throws IOException, URISyntaxException{
        String direccionMapa=ObjShowMaps.getURLMap(direccion);
        Desktop.getDesktop().browse(new URI(direccionMapa));
    }
    
    private void mostrarMapa(Double latitud, Double longitud) throws URISyntaxException, IOException{
        String direccionMapa=ObjShowMaps.getURLMap(latitud,longitud);
        Desktop.getDesktop().browse(new URI(direccionMapa));
    }
    

    private StaticMaps.Format seleccionarFormato(){
        StaticMaps.Format formato= StaticMaps.Format.png;
        switch(JCombo_ME_Formato.getSelectedItem().toString()){
            case "png":
                formato= StaticMaps.Format.png;
                break;
            case "png32":
                formato= StaticMaps.Format.png32;
                break;
            case "gif":
                formato= StaticMaps.Format.gif;
                break;
            case "jpg":
                formato= StaticMaps.Format.jpg;
                break;
            case "jpg_baseline":
                formato= StaticMaps.Format.jpg_baseline;
                break;
        }
        return formato;
    }
    
    private StaticMaps.Maptype seleccionarTipoMapa(){
        StaticMaps.Maptype tipoMapa= StaticMaps.Maptype.roadmap;
        switch(JCombo_ME_TipoMapa.getSelectedItem().toString()){
            case "roadmap":
                tipoMapa= StaticMaps.Maptype.roadmap;
                break;
            case "satellite":
                tipoMapa= StaticMaps.Maptype.satellite;
                break;
            case "hybrid":
                tipoMapa= StaticMaps.Maptype.hybrid;
                break;
            case "terrain":
                tipoMapa= StaticMaps.Maptype.terrain;
                break;
        }
        return tipoMapa;
    }

 
    
    private void guardarCambios(){
         MapsJava.setConnectTimeout(Integer.valueOf(JText_Conexion.getText()));
         MapsJava.setLanguage(JText_idioma.getText());
         MapsJava.setRegion(JText_Region.getText());
         if("true".equals(JCombo_Sensor.getSelectedItem().toString())){
             MapsJava.setSensor(true);
         }else{
             MapsJava.setSensor(false);
         }
         MapsJava.setKey(JText_Clave.getText());
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable_Pl_places = new javax.swing.JTable();
        JButton_ME_Buscar1 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        JText_Pl_Direccion = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        JSlider_Pl_Radio = new javax.swing.JSlider();
        JText_Pl_Radio = new javax.swing.JTextField();
        JText_Pl_Place = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        JText_Pl_Keyword = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        JCombo_Pl_Orden = new javax.swing.JComboBox();
        JCombo_Pl_TipoPlace = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        JLabel_Pl_Status = new javax.swing.JLabel();
        JText_Pl_Referencia = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        JText_Region = new javax.swing.JTextField();
        JText_idioma = new javax.swing.JTextField();
        JText_Conexion = new javax.swing.JTextField();
        JText_Clave = new javax.swing.JTextField();
        JCombo_Sensor = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        JLabel_Clave = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        JButton_ME_Buscar = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        JText_ME_Direccion = new javax.swing.JTextField();
        JSlider_ME_Escala = new javax.swing.JSlider();
        jLabel29 = new javax.swing.JLabel();
        JText_ME_Escala = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        JCombo_ME_Formato = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        JCombo_ME_TipoMapa = new javax.swing.JComboBox();
        JSlider_ME_Zoom = new javax.swing.JSlider();
        jLabel32 = new javax.swing.JLabel();
        JText_ME_Zoom = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        JLabel_ME_Imagen = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        JButton_ME_Buscar4 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        JText_ME_Direccion2 = new javax.swing.JTextField();
        JSlider_ME_Escala2 = new javax.swing.JSlider();
        jLabel46 = new javax.swing.JLabel();
        JText_ME_Escala2 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        JCombo_ME_Formato2 = new javax.swing.JComboBox();
        jLabel55 = new javax.swing.JLabel();
        JCombo_ME_TipoMapa2 = new javax.swing.JComboBox();
        JSlider_ME_Zoom2 = new javax.swing.JSlider();
        jLabel56 = new javax.swing.JLabel();
        JText_ME_Zoom2 = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        JLabel_ME_Imagen2 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable_Pl_places2 = new javax.swing.JTable();
        JButton_ME_Buscar5 = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        JText_Pl_Direccion2 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        JSlider_Pl_Radio2 = new javax.swing.JSlider();
        JText_Pl_Radio2 = new javax.swing.JTextField();
        JText_Pl_Place2 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        JText_Pl_Keyword2 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        JCombo_Pl_Orden2 = new javax.swing.JComboBox();
        JCombo_Pl_TipoPlace2 = new javax.swing.JComboBox();
        jLabel67 = new javax.swing.JLabel();
        JLabel_Pl_Status2 = new javax.swing.JLabel();
        JText_Pl_Referencia2 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jButton18 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        JText_Region2 = new javax.swing.JTextField();
        JText_idioma2 = new javax.swing.JTextField();
        JText_Conexion2 = new javax.swing.JTextField();
        JText_Clave2 = new javax.swing.JTextField();
        JCombo_Sensor2 = new javax.swing.JComboBox();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        JLabel_Clave2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();

        jTable_Pl_places.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Place", "Dirección", "Latitud", "Longitud", "Tipo", "Referencia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Pl_places.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_Pl_placesMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTable_Pl_places);

        JButton_ME_Buscar1.setBackground(new java.awt.Color(153, 153, 255));
        JButton_ME_Buscar1.setText("Buscar");
        JButton_ME_Buscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton_ME_Buscar1ActionPerformed(evt);
            }
        });

        jLabel33.setText("Centro de búsqueda (*)");

        jLabel34.setText("Radio (*)");

        JSlider_Pl_Radio.setMaximum(5000);
        JSlider_Pl_Radio.setMinimum(1);
        JSlider_Pl_Radio.setValue(3000);
        JSlider_Pl_Radio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_Pl_RadioStateChanged(evt);
            }
        });

        JText_Pl_Radio.setText("3000");

        jLabel35.setText("Place");

        jLabel36.setText("Keyword");

        jLabel37.setText("Orden resultados");

        JCombo_Pl_Orden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Importancia", "Distancia" }));

        JCombo_Pl_TipoPlace.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sin tipo", "accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "establishment", "finance", "fire_station", "florist", "food", "funeral_home", "furniture_store", "gas_station", "general_contractor", "grocery_or_supermarket", "gym", "hair_care", "hardware_store", "health", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "place_of_worship", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "synagogue", "taxi_stand", "train_station", "travel_agency", "university", "veterinary_care", "zoo" }));

        jLabel38.setText("Tipo place");

        JLabel_Pl_Status.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        JText_Pl_Referencia.setEditable(false);
        JText_Pl_Referencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JText_Pl_ReferenciaActionPerformed(evt);
            }
        });

        jLabel39.setText("Referencia");

        jButton6.setBackground(new java.awt.Color(153, 153, 255));
        jButton6.setText("Ver local");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(146, 178, 206));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(173, 173, 173), new java.awt.Color(224, 224, 224), null, null));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JButton_ME_Buscar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JText_Pl_Direccion)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(JCombo_Pl_TipoPlace, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addComponent(JSlider_Pl_Radio, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JText_Pl_Radio, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JCombo_Pl_Orden, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JText_Pl_Keyword, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(JText_Pl_Place)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel35))
                                .addGap(0, 257, Short.MAX_VALUE))))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(JLabel_Pl_Status))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JText_Pl_Referencia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JText_Pl_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JText_Pl_Radio)
                            .addComponent(JSlider_Pl_Radio, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JText_Pl_Place, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JText_Pl_Keyword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_Pl_Orden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JCombo_Pl_TipoPlace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JLabel_Pl_Status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JButton_ME_Buscar1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JText_Pl_Referencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setToolTipText("");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Región búsquedas");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Idioma resultados");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Tiempo conex (ms)");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Clave API");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Sensor");

        JText_Region.setText("es");

        JText_idioma.setText("es");

        JText_Conexion.setText("300");
        JText_Conexion.setEnabled(false);

        JCombo_Sensor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "false", "true", " " }));

        jButton1.setText("Pegar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Comprobar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(153, 153, 255));
        jButton3.setText("Guardar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Info");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Info");
        jButton5.setToolTipText("");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JText_Conexion, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(JCombo_Sensor, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_Region, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_idioma)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(JLabel_Clave)
                                .addGap(50, 50, 50)
                                .addComponent(jButton2))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(JText_Clave, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(JText_Region, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(JText_idioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(JText_Conexion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(JCombo_Sensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(JText_Clave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(JLabel_Clave))
                .addGap(30, 30, 30)
                .addComponent(jButton3)
                .addContainerGap(162, Short.MAX_VALUE))
        );

        JButton_ME_Buscar.setBackground(new java.awt.Color(153, 153, 255));
        JButton_ME_Buscar.setText("Crear mapa");
        JButton_ME_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton_ME_BuscarActionPerformed(evt);
            }
        });

        jLabel28.setText("Centro del mapa");

        JSlider_ME_Escala.setMaximum(2);
        JSlider_ME_Escala.setMinimum(1);
        JSlider_ME_Escala.setValue(1);
        JSlider_ME_Escala.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_ME_EscalaStateChanged(evt);
            }
        });

        jLabel29.setText("Escala");

        JText_ME_Escala.setText("1");

        jLabel30.setText("Formato");

        JCombo_ME_Formato.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "png", "png32", "gif", "jpg", "jpg_baseline" }));

        jLabel31.setText("Tipo de mapa");

        JCombo_ME_TipoMapa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "roadmap", "satellite", "hybrid", "terrain" }));

        JSlider_ME_Zoom.setMaximum(20);
        JSlider_ME_Zoom.setMinimum(1);
        JSlider_ME_Zoom.setValue(14);
        JSlider_ME_Zoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_ME_ZoomStateChanged(evt);
            }
        });

        jLabel32.setText("Zoom");

        JText_ME_Zoom.setText("14");

        JLabel_ME_Imagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JLabel_ME_Imagen.setText("Mapa estático");
        jScrollPane5.setViewportView(JLabel_ME_Imagen);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JCombo_ME_Formato, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JCombo_ME_TipoMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(JSlider_ME_Escala, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_ME_Escala, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel29))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(JSlider_ME_Zoom, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_ME_Zoom, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel32))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(JText_ME_Direccion)
                    .addComponent(jScrollPane5)
                    .addComponent(JButton_ME_Buscar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JText_ME_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JText_ME_Escala)
                            .addComponent(JSlider_ME_Escala, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JSlider_ME_Zoom, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(JText_ME_Zoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_ME_TipoMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_ME_Formato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JButton_ME_Buscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(353, 401));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane3.setToolTipText("");
        jTabbedPane3.setMinimumSize(new java.awt.Dimension(353, 401));
        jTabbedPane3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane3StateChanged(evt);
            }
        });

        JButton_ME_Buscar4.setBackground(new java.awt.Color(153, 153, 255));
        JButton_ME_Buscar4.setText("Crear mapa");
        JButton_ME_Buscar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton_ME_Buscar4ActionPerformed(evt);
            }
        });

        jLabel45.setText("Centro del mapa");

        JSlider_ME_Escala2.setMaximum(2);
        JSlider_ME_Escala2.setMinimum(1);
        JSlider_ME_Escala2.setValue(1);
        JSlider_ME_Escala2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_ME_Escala2StateChanged(evt);
            }
        });

        jLabel46.setText("Escala");

        JText_ME_Escala2.setText("1");

        jLabel54.setText("Formato");

        JCombo_ME_Formato2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "png", "png32", "gif", "jpg", "jpg_baseline" }));

        jLabel55.setText("Tipo de mapa");

        JCombo_ME_TipoMapa2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "roadmap", "satellite", "hybrid", "terrain" }));

        JSlider_ME_Zoom2.setMaximum(20);
        JSlider_ME_Zoom2.setMinimum(1);
        JSlider_ME_Zoom2.setValue(14);
        JSlider_ME_Zoom2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_ME_Zoom2StateChanged(evt);
            }
        });

        jLabel56.setText("Zoom");

        JText_ME_Zoom2.setText("14");

        JLabel_ME_Imagen2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JLabel_ME_Imagen2.setText("Mapa estático");
        jScrollPane9.setViewportView(JLabel_ME_Imagen2);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JText_ME_Direccion2)
                    .addComponent(JButton_ME_Buscar4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JCombo_ME_Formato2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JCombo_ME_TipoMapa2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(JSlider_ME_Escala2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_ME_Escala2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel46))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel18Layout.createSequentialGroup()
                                        .addComponent(JSlider_ME_Zoom2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_ME_Zoom2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel56))))
                        .addGap(0, 21, Short.MAX_VALUE))
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JText_ME_Direccion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JText_ME_Escala2)
                            .addComponent(JSlider_ME_Escala2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JSlider_ME_Zoom2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(JText_ME_Zoom2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_ME_TipoMapa2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_ME_Formato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JButton_ME_Buscar4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane3.addTab("Mapa estático", jPanel18);

        jTable_Pl_places2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Place", "Dirección", "Latitud", "Longitud", "Tipo", "Referencia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Pl_places2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_Pl_places2MousePressed(evt);
            }
        });
        jScrollPane10.setViewportView(jTable_Pl_places2);

        JButton_ME_Buscar5.setBackground(new java.awt.Color(153, 153, 255));
        JButton_ME_Buscar5.setText("Buscar");
        JButton_ME_Buscar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton_ME_Buscar5ActionPerformed(evt);
            }
        });

        jLabel62.setText("Centro de búsqueda (*)");

        jLabel63.setText("Radio (*)");

        JSlider_Pl_Radio2.setMaximum(5000);
        JSlider_Pl_Radio2.setMinimum(1);
        JSlider_Pl_Radio2.setValue(3000);
        JSlider_Pl_Radio2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSlider_Pl_Radio2StateChanged(evt);
            }
        });

        JText_Pl_Radio2.setText("3000");

        jLabel64.setText("Place");

        jLabel65.setText("Keyword");

        jLabel66.setText("Orden resultados");

        JCombo_Pl_Orden2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Importancia", "Distancia" }));

        JCombo_Pl_TipoPlace2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sin tipo", "accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "establishment", "finance", "fire_station", "florist", "food", "funeral_home", "furniture_store", "gas_station", "general_contractor", "grocery_or_supermarket", "gym", "hair_care", "hardware_store", "health", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "place_of_worship", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "synagogue", "taxi_stand", "train_station", "travel_agency", "university", "veterinary_care", "zoo" }));

        jLabel67.setText("Tipo place");

        JLabel_Pl_Status2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        JText_Pl_Referencia2.setEditable(false);
        JText_Pl_Referencia2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JText_Pl_Referencia2ActionPerformed(evt);
            }
        });

        jLabel68.setText("Referencia");

        jButton18.setBackground(new java.awt.Color(153, 153, 255));
        jButton18.setText("Ver local");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                    .addComponent(JButton_ME_Buscar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JText_Pl_Direccion2)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(JCombo_Pl_TipoPlace2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(JSlider_Pl_Radio2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JText_Pl_Radio2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel66, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JCombo_Pl_Orden2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JText_Pl_Keyword2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(JText_Pl_Place2)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel65)
                                    .addComponent(jLabel64))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel67)
                            .addComponent(JLabel_Pl_Status2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JText_Pl_Referencia2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JText_Pl_Direccion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JText_Pl_Radio2)
                            .addComponent(JSlider_Pl_Radio2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JText_Pl_Place2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JText_Pl_Keyword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JCombo_Pl_Orden2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JCombo_Pl_TipoPlace2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JLabel_Pl_Status2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JButton_ME_Buscar5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JText_Pl_Referencia2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68)
                    .addComponent(jButton18))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Places", jPanel20);

        jPanel16.setToolTipText("");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Región búsquedas");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Idioma resultados");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Tiempo conex (ms)");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Clave API");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Sensor");

        JText_Region2.setText("es");

        JText_idioma2.setText("es");

        JText_Conexion2.setText("300");
        JText_Conexion2.setEnabled(false);

        JCombo_Sensor2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "false", "true", " " }));

        jButton13.setText("Pegar");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Comprobar");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(153, 153, 255));
        jButton15.setText("Guardar");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Info");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText("Info");
        jButton17.setToolTipText("");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JText_Conexion2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(JCombo_Sensor2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_Region2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JText_idioma2)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel21)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(JLabel_Clave2)
                                .addGap(50, 50, 50)
                                .addComponent(jButton14))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(JText_Clave2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(JText_Region2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(JText_idioma2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(JText_Conexion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(JCombo_Sensor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(JText_Clave2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton14)
                    .addComponent(JLabel_Clave2))
                .addGap(30, 30, 30)
                .addComponent(jButton15)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Propiedades", jPanel16);

        jPanel5.setBackground(new java.awt.Color(146, 178, 206));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(173, 173, 173), new java.awt.Color(224, 224, 224), null, null));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(jTabbedPane1.getSelectedIndex()==0){
            actualizarPropiedades();
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            abrirFramePlaces(JText_Pl_Referencia.getText());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void JText_Pl_ReferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JText_Pl_ReferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JText_Pl_ReferenciaActionPerformed

    private void JSlider_Pl_RadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_Pl_RadioStateChanged
        this.JText_Pl_Radio.setText(String.valueOf(JSlider_Pl_Radio.getValue()));
    }//GEN-LAST:event_JSlider_Pl_RadioStateChanged

    private void JButton_ME_Buscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton_ME_Buscar1ActionPerformed
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
    }//GEN-LAST:event_JButton_ME_Buscar1ActionPerformed

     private void seleccionarReferencia(){
        if(jTable_Pl_places.getRowCount()>0){
          this.JText_Pl_Referencia.setText((String)jTable_Pl_places.getValueAt(jTable_Pl_places.getSelectedRow(),5));
        }
    }
    
    private void jTable_Pl_placesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_Pl_placesMousePressed
        seleccionarReferencia();
    }//GEN-LAST:event_jTable_Pl_placesMousePressed

    private void JSlider_ME_ZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_ME_ZoomStateChanged
        this.JText_ME_Zoom.setText(String.valueOf(JSlider_ME_Zoom.getValue()));
    }//GEN-LAST:event_JSlider_ME_ZoomStateChanged

    private void JSlider_ME_EscalaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_ME_EscalaStateChanged
        this.JText_ME_Escala.setText(String.valueOf(JSlider_ME_Escala.getValue()));
    }//GEN-LAST:event_JSlider_ME_EscalaStateChanged

    private void JButton_ME_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton_ME_BuscarActionPerformed
        try {
            this.crearMapa();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_JButton_ME_BuscarActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("https://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1"));
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("http://es.wikipedia.org/wiki/CcTLD"));
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        guardarCambios();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        comprobarClaveApi();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            pegarTexto();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            pegarTexto();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        comprobarClaveApi();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        guardarCambios();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("http://es.wikipedia.org/wiki/CcTLD"));
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("https://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1"));
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void JButton_ME_Buscar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton_ME_Buscar4ActionPerformed
        try {
            this.crearMapa();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_JButton_ME_Buscar4ActionPerformed

    private void JSlider_ME_Escala2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_ME_Escala2StateChanged
        this.JText_ME_Escala.setText(String.valueOf(JSlider_ME_Escala.getValue()));
    }//GEN-LAST:event_JSlider_ME_Escala2StateChanged

    private void JSlider_ME_Zoom2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_ME_Zoom2StateChanged
        this.JText_ME_Zoom.setText(String.valueOf(JSlider_ME_Zoom.getValue()));
    }//GEN-LAST:event_JSlider_ME_Zoom2StateChanged

    private void jTable_Pl_places2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_Pl_places2MousePressed
        seleccionarReferencia();
    }//GEN-LAST:event_jTable_Pl_places2MousePressed

    private void JButton_ME_Buscar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton_ME_Buscar5ActionPerformed
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
    }//GEN-LAST:event_JButton_ME_Buscar5ActionPerformed

    private void JSlider_Pl_Radio2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSlider_Pl_Radio2StateChanged
        this.JText_Pl_Radio.setText(String.valueOf(JSlider_Pl_Radio.getValue()));
    }//GEN-LAST:event_JSlider_Pl_Radio2StateChanged

    private void JText_Pl_Referencia2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JText_Pl_Referencia2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JText_Pl_Referencia2ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        try {
            abrirFramePlaces(JText_Pl_Referencia.getText());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jTabbedPane3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane3StateChanged
        if(jTabbedPane1.getSelectedIndex()==0){
            actualizarPropiedades();
        }
    }//GEN-LAST:event_jTabbedPane3StateChanged

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
            java.util.logging.Logger.getLogger(ventanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ventanaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButton_ME_Buscar;
    private javax.swing.JButton JButton_ME_Buscar1;
    private javax.swing.JButton JButton_ME_Buscar4;
    private javax.swing.JButton JButton_ME_Buscar5;
    private javax.swing.JComboBox JCombo_ME_Formato;
    private javax.swing.JComboBox JCombo_ME_Formato2;
    private javax.swing.JComboBox JCombo_ME_TipoMapa;
    private javax.swing.JComboBox JCombo_ME_TipoMapa2;
    private javax.swing.JComboBox JCombo_Pl_Orden;
    private javax.swing.JComboBox JCombo_Pl_Orden2;
    private javax.swing.JComboBox JCombo_Pl_TipoPlace;
    private javax.swing.JComboBox JCombo_Pl_TipoPlace2;
    private javax.swing.JComboBox JCombo_Sensor;
    private javax.swing.JComboBox JCombo_Sensor2;
    private javax.swing.JLabel JLabel_Clave;
    private javax.swing.JLabel JLabel_Clave2;
    private javax.swing.JLabel JLabel_ME_Imagen;
    private javax.swing.JLabel JLabel_ME_Imagen2;
    private javax.swing.JLabel JLabel_Pl_Status;
    private javax.swing.JLabel JLabel_Pl_Status2;
    private javax.swing.JSlider JSlider_ME_Escala;
    private javax.swing.JSlider JSlider_ME_Escala2;
    private javax.swing.JSlider JSlider_ME_Zoom;
    private javax.swing.JSlider JSlider_ME_Zoom2;
    private javax.swing.JSlider JSlider_Pl_Radio;
    private javax.swing.JSlider JSlider_Pl_Radio2;
    private javax.swing.JTextField JText_Clave;
    private javax.swing.JTextField JText_Clave2;
    private javax.swing.JTextField JText_Conexion;
    private javax.swing.JTextField JText_Conexion2;
    private javax.swing.JTextField JText_ME_Direccion;
    private javax.swing.JTextField JText_ME_Direccion2;
    private javax.swing.JTextField JText_ME_Escala;
    private javax.swing.JTextField JText_ME_Escala2;
    private javax.swing.JTextField JText_ME_Zoom;
    private javax.swing.JTextField JText_ME_Zoom2;
    private javax.swing.JTextField JText_Pl_Direccion;
    private javax.swing.JTextField JText_Pl_Direccion2;
    private javax.swing.JTextField JText_Pl_Keyword;
    private javax.swing.JTextField JText_Pl_Keyword2;
    private javax.swing.JTextField JText_Pl_Place;
    private javax.swing.JTextField JText_Pl_Place2;
    private javax.swing.JTextField JText_Pl_Radio;
    private javax.swing.JTextField JText_Pl_Radio2;
    private javax.swing.JTextField JText_Pl_Referencia;
    private javax.swing.JTextField JText_Pl_Referencia2;
    private javax.swing.JTextField JText_Region;
    private javax.swing.JTextField JText_Region2;
    private javax.swing.JTextField JText_idioma;
    private javax.swing.JTextField JText_idioma2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable_Pl_places;
    private javax.swing.JTable jTable_Pl_places2;
    // End of variables declaration//GEN-END:variables
}
