/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timotei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import javafx.stage.Stage;

import javafx.util.Duration;

/**
 *
 * @author k9751
 */
public class FXMLDocumentController implements Initializable {

    XMLparse xml = new XMLparse();
    DBHandler dbh;
    private ArrayList<ArrayList> DCtempList = new ArrayList<>();
    private ArrayList DCadditionalTermList = new ArrayList();
    private ArrayList<String> DCattrList = new ArrayList<>();
    private static ArrayList<String> DCcityList = new ArrayList<>();

    private FadeTransition fadeIn = new FadeTransition(Duration.millis(3000));
    private FadeTransition fadeIn2 = new FadeTransition(Duration.millis(3000));

    private String tempS = "";

    @FXML
    private Button createPackageButton;
    @FXML
    static private WebView webViewScreen;
    @FXML
    private Button addSmartPostButton;
    @FXML
    static private ComboBox<String> smartPostCombo;
    @FXML
    private Button startYesButton;
    @FXML
    private Button startNoButton;
    @FXML
    private Pane startPane;
    @FXML
    private Button deleteMarkers;
    @FXML
    private Button deletePackageButton;
    @FXML
    private Button sendDelivery;
    @FXML
    private static ComboBox<itemPackage> deliveryCombo;
    @FXML
    private AnchorPane sendingErrorPane;
    @FXML
    private TextField pathColorLabel;
    @FXML
    private ListView<TimoteiMan> timoteiListView;
    @FXML
    private Label sendingErrorLabel;
    @FXML
    private TextField locationNameField;
    @FXML
    private TextField infoTextField;
    @FXML
    private TextField markerColorField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField latitudeField;
    @FXML
    private TextField longitudeField;
    @FXML
    private Button addLocationButton;
    @FXML
    private AnchorPane addLocationPane;
    @FXML
    private Pane addLocationErrorPane;
    @FXML
    static private TextArea logTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        dbh = getDbh();   
        webViewScreen.setVisible(false);
        
        webViewScreen.getEngine().load(getClass().getResource("index.html").toExternalForm());
        
        clearDBtest();

        fadeIn.setNode(sendingErrorPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(2);
        fadeIn.setAutoReverse(true);
        
        fadeIn2.setNode(addLocationErrorPane);
        fadeIn2.setFromValue(0.0);
        fadeIn2.setToValue(1.0);
        fadeIn2.setCycleCount(2);
        fadeIn2.setAutoReverse(true);
        
    }
    
    private void clearDBtest() {
        /* If there is no smartPosts in database start the clear start. */
        try {
        dbh = getDbh();
        DCattrList.add("smartPostID");
        ArrayList A = dbh.readFromdb("smartPost", DCattrList, null);
        
        if (A.get(0) == null) {
            clearDBstart();
        }
        } catch (IndexOutOfBoundsException ex) {
            clearDBstart();
        }
    }

    @FXML
    private void startPackageCreator(ActionEvent event) {
        startingPackageCreator();
    }
    
    private void startingPackageCreator() {
        /* Start packageCreator scene. */
        try {
            Stage packageCreator = new Stage();
            Parent page = FXMLLoader.load(getClass().getResource("PackageCreatorFXML.fxml"));

            Scene scene = new Scene(page);

            packageCreator.setScene(scene);
            packageCreator.show();

        } catch (IOException ex) {
            System.err.println("Error while opening packagecreator.");
            writeLog("##### Error while opening packagecreator. #####");
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void addSmartPostAction(ActionEvent event) {
        /* Add smartPosts to webView based on selected city. Uses google maps javascrip API*/
        
        /*webViewScreen.getEngine().executeScript("document.goToLocation(
        'Skinnarilankatu 34, 53850 Lappeenranta', 'avaiblity', 'color')");*/
        tempS = " WHERE cityName = ?;";
        DCadditionalTermList.addAll(Arrays.asList(tempS, smartPostCombo.
                valueProperty().getValue().toUpperCase()));

        dbh = getDbh();
        DCattrList.addAll(Arrays.asList("smartPostID", "latitude", "longitude", 
                "smartPostName", "colorOnMap", "postalCode", 
                "cityName", "streetAddress", "info"));
        DCtempList = dbh.readFromdb("smartPostView", DCattrList, DCadditionalTermList);
        
        DCadditionalTermList.clear();
        DCattrList.clear();

        for (ArrayList A : DCtempList) {
            for (smartPostObject SP : smartPostObject.getSpList()) {
                if (SP.getID() == (int)A.get(0))
                    SP.setDrawn(true);
            }
            
            //goToLocation(lat, lng, info, color)
            String script = String.format("document.goToLocation('%s', '%s', '%s', '%s')",
                        String.valueOf(A.get(1)), String.valueOf(A.get(2)),
                        (String)A.get(8), ((String)A.get(4)).toLowerCase());

            webViewScreen.getEngine().executeScript(script);
            writeLog("SmartPost automaatit piirretty kohteeseen: " + (String)A.get(6));
        }
    }

    private void loadSmartPostXMLaction() {
        /* Read XML text from URL below and calls XML parser. */
        try {
            URL url = new URL("http://smartpost.ee/fi_apt.xml");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            String content = "";

            while ((line = br.readLine()) != null) {
                content += line + "\n";
            }

            xml.XMLparser(content);

            br.close();

            addCityToCombo();

        } catch (MalformedURLException ex) {
            System.err.println("Can not load SmartPosts! :(");
            writeLog("##### Can not load SmartPosts! #####");
        } catch (IOException ex) {
            System.err.println("XML IOError! :(");
            writeLog("##### XML IOError! #####");
        }
    }
    
    private void listSmartPost() {
        /* Call smartPost object constructor and add them to list. */
        smartPostObject sp = new smartPostObject();
        
        dbh = getDbh();
        DCattrList.addAll(Arrays.asList("smartPostID", "longitude", "latitude",
                "smartPostName", "colorOnMap", "postalCode",
                "cityName", "streetAddress", "info"));
        DCtempList = dbh.readFromdb("smartPostView", DCattrList, null);

        DCattrList.clear();
        
//SmartPost constructor(id, color, code, city, address, info, name, lat, lng, drawn)
        for (ArrayList A : DCtempList) {
            sp.addToSpList(new smartPostObject((int) A.get(0), (String) A.get(4),
                    (String)A.get(5), (String) A.get(6), (String) A.get(7),
                    (String) A.get(8),(String) A.get(3), 
                    Float.parseFloat(A.get(1).toString()),
                    Float.parseFloat(A.get(2).toString()), false));
        }
    }

    @FXML
    private void continueAction(ActionEvent event) {
        /* When at the beginning pressed no database clearing (Ei).
        Continue from current database situation. */
        startPane.setVisible(false);
        webViewScreen.setVisible(true);
        
        addCityToCombo();
        listSmartPost();
        refreshTimoteiMenListView();
    }
    
    private void clearDBstart() {
        /* Clear databse and read XML again. */
        startPane.setVisible(false);
        webViewScreen.setVisible(true);        

        dbh.clearDB();
        loadSmartPostXMLaction();
        listSmartPost();
        refreshTimoteiMenListView();

        startingPackageCreator();
    }
    

    @FXML
    private void clearDBaction(ActionEvent event) {
        /* When at the beginning pressed yes database clearing (Kyllä).
         Start from the begining. */
        clearDBstart();
    }

    private void addCityToCombo() {
        /* Add citynames to combobox */
        
        DCattrList.add("cityName");
        DCcityList = dbh.readFromdb("city", DCattrList, null);
        DCattrList.clear();

        Collections.sort(DCcityList);
        
        for (Object o : DCcityList) {                   
            String s = o.toString().substring(0, 1).toUpperCase()
                    + o.toString().substring(1).toLowerCase();

            if (!(smartPostCombo.getItems().contains(s))) {
                smartPostCombo.getItems().add(s);
            }
        }
    }

    static public ArrayList getCityList() {
        DCcityList.clear();
        DCcityList.addAll(smartPostCombo.getItems());        
        return DCcityList;
    }

    @FXML
    private void deleteMarkersAction(ActionEvent event) {
        /* Delete drawn markers from webView. */
        
        webViewScreen.getEngine().executeScript("document.deleteMarkers()");
        
        for (smartPostObject SP : smartPostObject.getSpList()) {           
            SP.setDrawn(false);
        }
    }

    @FXML
    private void deletePackageAction(ActionEvent event) {
        /* Button action to delete package. */
        
        try {
            int pID = deliveryCombo.valueProperty().getValue().getPackageID();
            deleteItemsAndPackage(pID);
            
        } catch (NullPointerException ex) {
            writeLog("Ei valittuna poistettavaa pakettia.");
        }
    }
    
    private void deleteItemsAndPackage(int ID) {
        /* Delete items from selected package and package and its delivery itself. */
        
        dbh = getDbh();
        DCattrList.addAll(Arrays.asList("deliveryID", "packageID"));
        DCadditionalTermList.addAll(Arrays.asList(
                " WHERE packageID = ?", ID));
        DCtempList = dbh.readFromdb("packageView", DCattrList, DCadditionalTermList);
        
        int dID = (int)(DCtempList.get(0).get(0));
        int pID = (int)(DCtempList.get(0).get(1));
        
        DCattrList.clear();
        DCadditionalTermList.clear();
        DCtempList.clear();        
        
        dbh.deleteFromDB("itemInPackage", "packageID", pID);
        dbh.deleteFromDB("package", "deliveryID", dID);
        dbh.deleteFromDB("packageDelivery", "deliveryID", dID);        
        
        ComboBox C = PackageCreatorFXMLController.getPackageCombo();
        
        if (C.getItems().contains(deliveryCombo.getSelectionModel().getSelectedItem())) {
            C.getItems().remove(deliveryCombo.getSelectionModel().getSelectedItem());
        }
        
        writeLog("Paketti: " + 
                deliveryCombo.getSelectionModel().getSelectedItem().toString()
                + " poistettu.");
        
        deliveryCombo.getItems().remove(deliveryCombo.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void sendDeliveryAction(ActionEvent event) {
        /* Send package(delivery) and calls path drawer from google API
        Afterwards delete sent package and writes log distance.*/
        
//document.createPath(ArrayList{from LAT, from LNG, to LAT, to LNG}, 'colour', (int)deliveryClass);
        try {
            itemPackage P = deliveryCombo.valueProperty().getValue();
            TimoteiMan T = timoteiListView.getSelectionModel().getSelectedItem();
            
            if (checkDrawn(P.getFromID(), P.getToID())) {
                DCattrList.addAll(Arrays.asList("smartPostID", "latitude", "longitude"));
                DCadditionalTermList.addAll(Arrays.asList(
                        " WHERE smartPostID = ? OR smartPostID = ?", P.getFromID(), P.getToID()));

                dbh = getDbh();      
                DCtempList = dbh.readFromdb("smartPost" , DCattrList, DCadditionalTermList);

                DCattrList.clear();
                DCadditionalTermList.clear();

                ArrayList A = new ArrayList();
                if ((int)DCtempList.get(0).get(0) == P.getFromID()) {
                    A.add((double)DCtempList.get(0).get(1));
                    A.add((double)DCtempList.get(0).get(2));
                    A.add((double)DCtempList.get(1).get(1));
                    A.add((double)DCtempList.get(1).get(2));

                } else {
                    A.add((double) DCtempList.get(1).get(1));
                    A.add((double) DCtempList.get(1).get(2));
                    A.add((double) DCtempList.get(0).get(1));
                    A.add((double) DCtempList.get(0).get(2));
                }
                String color = "black"; //Also default colour of Google Maps
                if (!(pathColorLabel.getText().isEmpty())) {
                    color = pathColorLabel.getText();
                }
                int deliveryClass = Integer.parseInt(P.getDeliveryClass().getClass()
                                    .toString().replace("class timotei.deliveryClass", ""));                

                double l = (double)(webViewScreen.getEngine().executeScript(
                        "document.createPath("+ A + ", '" + color + "', " + deliveryClass + ")"));

                DCtempList.clear();
                A.clear();
                
                T.increaseStressLevel(deliveryClass * 10);
                
                refreshTimoteiMenListView();
                
                dbh.addDeliversToDB(P.getPackageID(), T.getId() , l);
                
                writeLog("Paketti lähetetty: " + P.toString()+ "\n"
                        + "Matkan pituus on " + l + " km.");
                
                String tMan = "Pakettia kuljettaa " + T.getFirstName() 
                        + " " + T.getFamilyName();
                
                if (deliveryClass == 2) {
                    writeLog(tMan + " ja Pikku Apulainen auttaa.");
                
                } else {
                    writeLog(tMan);
                }
                
                checkBreaking(T, P, deliveryClass);
            }
        
        } catch (NullPointerException ex) {
            sendingErrorLabel.setText("Valitse ensin paketti ja TIMOTEI mies");
            sendingErrorPane.setVisible(true);
            fadeIn.playFromStart();            
        }
    }
    
    private void checkBreaking(TimoteiMan T, itemPackage P, int dClass) {
        /* Check if items in package break or not. Based on breakTypes and 
        percents of them. Very simplyfied math used. */
        
        int randInt;
        double weight = 0, breakPercent = 0;
        String s = "";
        
        Random rand = new Random();
        dbh = getDbh();
        DCattrList.addAll(Arrays.asList(
                "itemName", "breakType", "percent", "breakable", "itemWeight"));
        
        DCadditionalTermList.addAll(Arrays.asList(
                " WHERE packageID = ?", P.getPackageID()));
        
        DCtempList = dbh.readFromdb("inPackagesView", DCattrList, DCadditionalTermList);
        
        DCattrList.clear();
        DCadditionalTermList.clear();
        
        for (ArrayList A : DCtempList) { // First loop to get overall breaking percent.
            weight += (double)A.get(4);
            if (((String)A.get(1)).equals("breaker")) {
                breakPercent += (int)A.get(2);
            
            } else if (((String)A.get(1)).equals("protective")) {
                breakPercent -= (int)A.get(2);
            }            
        }
        
        if (dClass == 2 && 10 / 9 < P.getSize() / P.checkPackageSpace()) {
            breakPercent += 25;
        }

        if (dClass == 3 || T.getStressLevel() > T.getStressLimit()) {
            breakPercent += 25;
            s += String.format("%s %s suoritti stressinpurku "
                    + "toimenpiteen", T.getFirstName(), T.getFamilyName());
            
            breakPercent -= (weight/1000); //1 kg lowers break chance by 1%.

            if (T.getAction3() != null && breakPercent >= 100) {
                s += T.getAction3() + "\n";

            } else if (T.getAction2() != null && breakPercent >= 50) {
                s += T.getAction2() + "\n";

            } else {
                s += T.getAction1() + "\n";
            }
        }
        
        for (ArrayList B : DCtempList) { // Second loop to test each item's breaking.
            
            if (DCtempList.size() != 1 && ((String)B.get(1)).equals("special")) {
                breakPercent += 50;
                randInt = rand.nextInt(100);
                
                if (randInt < breakPercent) {
                    s += "Esine " + B.get(0) + " särkyi matkalla.\n";
                }
                breakPercent -= 50;
            
            } else if ((boolean) B.get(3)) {                
                randInt = rand.nextInt(100);
                
                if (randInt < breakPercent) {
                    s += "Esine " + B.get(0) + " särkyi matkalla.\n";
                }
            }
        }
        
        if (s.equals("")) {
            s = "Kaikki esineet pysyivät ehjänä.";
        }
        
        DCtempList.clear();
        
        writeLog(s);
    }
    
    private boolean checkDrawn(int fromID, int toID) {
        /* Check if smartPost is drawn to webView or not. Reaturns boolean. */
        
        int i = 0;
        for (smartPostObject SP : smartPostObject.getSpList()) {            
            if (SP.getID() == fromID && SP.getDrawn()) {
                i++; 
            
            } else if (SP.getID() == toID && SP.getDrawn()) {
                i++;
            }
            
            if (i == 2) {
                break;
            }
        }
        
        if (!(i == 2)) {
            sendingErrorLabel.setText("Piirrä ensin SmartPostit kartalle.");
            sendingErrorPane.setVisible(true);
            fadeIn.playFromStart();  
            return false;
        }
        return true;
    }
    
    private boolean checkAddingLocation() {
        /* Check if own location can be added to map. True if all text fields 
        are filled and coordinates can be parsed to float.*/
        
        for (Object O : addLocationPane.getChildren()) {
            TextField T = (TextField) O;
            if (T.getText().isEmpty()) {
                addLocationErrorPane.setVisible(true);
                fadeIn2.playFromStart();                
                return false;
            }
        }
        
        try {
            Float.parseFloat(latitudeField.getText());
            Float.parseFloat(longitudeField.getText());        
        
        } catch (NumberFormatException ex) {
            addLocationErrorPane.setVisible(true);
            fadeIn2.playFromStart();
            return false;
        }
        return true;
    }

    @FXML
    private void addLocationAction(ActionEvent event) {
        /* Add user given location to map if checker return true. 
        Also if there is new city or postal code add them to batabase.*/
        if (checkAddingLocation()) {
            
            smartPostObject sp = new smartPostObject();
            String c = cityField.getText().substring(0, 1).toUpperCase()
                    + cityField.getText().substring(1).toLowerCase();
            
            dbh = getDbh();
            dbh.writeCityTodb(zipCodeField.getText(), c.toUpperCase());
            
            dbh.addSmartPostTodb(locationNameField.getText(), infoTextField.getText(),
                                addressField.getText(), zipCodeField.getText(), 
                                Float.parseFloat(latitudeField.getText()),
                                Float.parseFloat(longitudeField.getText()),
                                markerColorField.getText());

            String Script = String.format("document.goToOwnLocation('%s, %s %s', '%s', '%s')",
                    addressField.getText(), zipCodeField.getText(), cityField.getText(),
                    infoTextField.getText(), markerColorField.getText());

            webViewScreen.getEngine().executeScript(Script);
            
            DCattrList.addAll(Arrays.asList("smartPostID"));
            DCadditionalTermList.addAll(Arrays.asList(
                    " ORDER BY smartPostID DESC LIMIT ?", 1)); //dbHandler requires 2 terms.
            ArrayList<Object> A = dbh.readFromdb(
                    "smartPost", DCattrList, DCadditionalTermList);
            
    //SmartPost constructor(id, color, code, city, address, info, name, lat, lng, drawn)
            sp.addToSpList(new smartPostObject((int) A.get(0),
                    markerColorField.getText(), zipCodeField.getText(),
                    c.toUpperCase(), addressField.getText(), 
                    infoTextField.getText(), locationNameField.getText(),
                    Float.parseFloat(latitudeField.getText()),
                    Float.parseFloat(longitudeField.getText()), true));

            DCattrList.clear();
            DCadditionalTermList.clear();
            A.clear();
            
            if (!(smartPostCombo.getItems().contains(c))) {
                smartPostCombo.getItems().add(c);
            }
            
            writeLog("Oma kohde: " + locationNameField.getText() + " lisätty kartalle.");

            cityField.clear();
            locationNameField.clear();
            infoTextField.clear();
            addressField.clear();
            zipCodeField.clear();
            latitudeField.clear();
            longitudeField.clear();
            markerColorField.clear();
        }
        
        
    }

    private void refreshTimoteiMenListView() {
        /* Refresh timoteiMen showing on listView */
        timoteiListView.getItems().clear();
        dbh = getDbh();
        DCattrList.addAll(Arrays.asList(
                "TIMOTEI_ID", "familyName", "firstName",
                "stressLimit", "stressLevel", "action1", "action2", "action3"));        
        DCtempList = dbh.readFromdb("timoteiManView", DCattrList, null);
        
        for (ArrayList A : DCtempList) {

    /*TimoteiMan constructor(int id, String familyName, String firstName, 
            int limit, int level, String a1, String a2, String a3)*/
            timoteiListView.getItems().add(new TimoteiMan((int)A.get(0), 
                (String)A.get(1), (String)A.get(2), (int)A.get(3), (int)A.get(4),
                (String)A.get(5), (String)A.get(6), (String)A.get(7)));
        }
        
        DCattrList.clear();
        DCtempList.clear();
    }
    
    static public double getRouteLenght(ArrayList A) {
        double lenght = (double) webViewScreen.getEngine().
                executeScript("document.routeLenght(" + A + ")");
        return lenght;
    }
    
    static public void writeLog(String s) {
        logTextArea.setText(logTextArea.getText() + "\n" + s);
    }

    public static ComboBox getDeliveryCombo() {
        return deliveryCombo;
    }
    
    private DBHandler getDbh() {
        /* Return instance to database handler object. */
        dbh = DBHandler.getInstance();
        return dbh;
    }

}
