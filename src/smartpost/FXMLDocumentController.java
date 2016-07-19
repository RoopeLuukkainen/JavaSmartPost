/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import javafx.stage.Stage;

/**
 *
 * @author k9751
 */
public class FXMLDocumentController implements Initializable {
    XMLparse xml = new XMLparse();
    dbHandler dbh;
    private ArrayList tempList = new ArrayList();
    private ArrayList<String> attrList = new ArrayList<>();

    private String tempS = "";
    
    private Label label;
    @FXML
    private Button createPackageButton;
    @FXML
    private WebView webViewScreen;
    @FXML
    private Button addSmartPostButton;
    @FXML
    private MenuItem loadXML;
    @FXML
    static private ComboBox<String> smartPostCombo;
    @FXML
    private ProgressBar startProgressBar;
    @FXML
    private Button startYesButton;
    @FXML
    private Button startNoButton;
    @FXML
    private Pane startPane;
    @FXML
    private Button deleteMarkers;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbh = getDbh();
        webViewScreen.setVisible(false);
        webViewScreen.getEngine().load(getClass().getResource("index.html").toExternalForm());
//        webViewScreen.getEngine().executeScript("document.goToLocation('Katuosoite, postinumero postitoimipaikka', 'Aukioloaika', 'väri')");
//        webViewScreen.getEngine().executeScript("document.goToLocation('Skinnarilankatu 34, 53850 Lappeenranta', 'Aukioloaika', 'red')");
    
//        webViewScreen.getEngine().executeScript("document.createPath(ArrayList, 'red', lähetysluokka)");
    }    

    @FXML
    private void startPackageCreator(ActionEvent event) {
        try {
            Stage packageCreator = new Stage();
            Parent page = FXMLLoader.load(getClass().getResource("PackageCreatorFXML.fxml"));
            
            Scene scene = new Scene(page);
            
            packageCreator.setScene(scene);
            packageCreator.show();
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void addSmartPostAction(ActionEvent event) {        
        //webViewScreen.getEngine().executeScript("document.goToLocation('Skinnarilankatu 34, 53850 Lappeenranta', 'Aukioloaika', 'red')");
        tempS = "city = '" + smartPostCombo.valueProperty().getValue().toUpperCase() + "'";
        
        dbh = getDbh();
        attrList.addAll(Arrays.asList("streetAddress", "postalCode", "openingTime", "closingTime", "colorOnMap"));
        tempList = dbh.readFromdb("smartPost", attrList, tempS);
        
        attrList.clear();

//        if (tempList.size() > 11) {
//            tempList.subList(0, 10)
//        }
        int i = 0;
        for (Object o : tempList) {
            String[] list = (String[]) o;
            
//            System.out.println(i++);
//
//            try {
//
//                Thread.sleep(500);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//            }
            

            String script = String.format("document.goToLocation('%s, %s %s', "
                    + "'Aukioloaika', 'red')", list[0], list[1], smartPostCombo.valueProperty().getValue());
            webViewScreen.getEngine().executeScript(script);
        }
        
        
        
    }

    @FXML
    private void loadXMLaction(ActionEvent event) {
        loadSmartPostXMLaction();
    }
    
    private void loadSmartPostXMLaction() {
        dbh = getDbh();
        
        try {
            URL url = new URL("http://smartpost.ee/fi_apt.xml");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String line;
            String content = "";
            
            while((line = br.readLine()) != null) {
                content += line + "\n";
            }
            
            //System.out.println(content);
            xml.XMLparser(content);
            
            br.close();
            
        } catch (MalformedURLException ex) {
            System.err.println("Can not load SmartPosts! :(");
        } catch (IOException ex) {
            System.err.println("XML IOError! :(");
        }
    }
    


    @FXML
    private void continueAction(ActionEvent event) {
        startPane.setVisible(false);
        webViewScreen.setVisible(true);
        addCityToCombo(); 
        
        
    }

    @FXML
    private void noContinueAction(ActionEvent event) {        
        loadSmartPostXMLaction();
        startPane.setVisible(false);
        webViewScreen.setVisible(true);

    }
    
    private dbHandler getDbh() {
        dbh = dbHandler.getInstance();
        return dbh;
    }
    
    private void addCityToCombo() {
        dbh = getDbh();
        attrList.add("city");
        tempList = dbh.readFromdb("smartPost", attrList, "");
        
        attrList.clear();
        
        for (Object o : tempList) {
            String s = o.toString().substring(0, 1).toUpperCase()
                     + o.toString().substring(1).toLowerCase();
            
            
            if (!(smartPostCombo.getItems().contains(s)))
                smartPostCombo.getItems().add(s);
        }
        tempList.clear();
    }
    
    
    static public ComboBox<String> getSmartPostCombo() {
        return smartPostCombo;
    }

    @FXML
    private void deleteMarkersAction(ActionEvent event) {
        webViewScreen.getEngine().executeScript("document.deleteMarkers()");
    }
}
