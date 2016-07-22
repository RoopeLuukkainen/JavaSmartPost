/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author k9751
 */
public class PackageCreatorFXMLController implements Initializable {
    DBHandler dbh;
    
    private ArrayList<Item> PCitemList = new ArrayList<>();
    private ArrayList<ArrayList> PCtempList = new ArrayList<>();
    private ArrayList<itemPackage> PCpackageList = new ArrayList<>();
    private ArrayList<String> PCattrList = new ArrayList<>();
    private ArrayList<String> PCadditionalTermList = new ArrayList<>();
    smartPostObject sp = new smartPostObject();
    
    @FXML
    private Button addItemButton;
    @FXML
    private ComboBox<Item> itemCombo;
    @FXML
    private ToggleButton breakableToggle;
    @FXML
    private Button createItemButton;
    @FXML
    private RadioButton RadioButton1;
    @FXML
    private Label deliveryClassInfoLabel;
    @FXML
    private ComboBox<String> startCityCombo;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> destinationCityCombo;
    @FXML
    private Button createPackageButton;
    @FXML
    private ComboBox<itemPackage> packageCombo;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemWidthField;
    @FXML
    private TextField itemWeightField;
    @FXML
    private TextField itemHeightField;
    @FXML
    private TextField itemDepthField;
    @FXML
    private TextArea packageInfoLabel;
    @FXML
    private TextArea itemInfoLabel;
    @FXML
    private ListView<smartPostObject> destinationList;
    @FXML
    private ListView<smartPostObject> startList;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        dbh = getDbh();
        
        PCattrList.addAll(Arrays.asList(
                "itemID", "itemName", "itemSize", "itemWeight", "breakable"));
        PCtempList = dbh.readFromdb("item", PCattrList, null);
        
        PCattrList.clear();
        
        for (ArrayList A : PCtempList) {
            createItemObject(A);
        }
        
        addCityToList();
    }    

    @FXML
    private void addItemAction(ActionEvent event) {
        
    }

    @FXML
    private void createItemAction(ActionEvent event) {        
        String itemName = itemNameField.getText();
        
        try {
            double itemSize = Double.parseDouble(itemWidthField.getText().replaceAll(",", ".")) 
                * Double.parseDouble(itemHeightField.getText().replaceAll(",", ".")) 
                * Double.parseDouble(itemDepthField.getText().replaceAll(",", "."));
            
            int itemWeight = Integer.parseInt(itemWeightField.getText());

        boolean breakable = breakableToggle.isSelected();
        
        dbh = getDbh();
        dbh.addItemToDB(itemName, itemSize, itemWeight, breakable);    
        
        PCattrList.addAll(Arrays.asList(
                "itemID", "itemName", "itemSize", "itemWeight", "breakable"));
        
        PCadditionalTermList.addAll(Arrays.asList(
                " ORDER BY itemID DESC LIMIT ?", "1")); //dbHandler requires 2 terms.
        
        PCtempList = dbh.readFromdb("item", PCattrList, PCadditionalTermList);
        
        createItemObject(PCtempList.get(0));
        PCtempList.clear();
        PCattrList.clear();
        PCadditionalTermList.clear();
        
        itemNameField.setText(""); 
        // inside TRY because only if creating succeess field is emptied.
        
        } catch (NumberFormatException ex) {
            itemInfoLabel.setText("Anna koko ja paino lukuarvoina.\n" +
                            "Välimerkki desimaaliluvussa voi olla '.' tai ','.");
        }
        itemWeightField.setText("");
        itemHeightField.setText("");
        itemDepthField.setText("");
        itemWidthField.setText("");
    }
    
    private void createItemObject(ArrayList A) {
        itemCombo.getItems().add(new Item(Integer.parseInt(A.get(0).toString()),
                (String)A.get(1), Double.parseDouble(A.get(2).toString()),
                Double.parseDouble(A.get(3).toString()),
                Boolean.parseBoolean(A.get(4).toString())));        
    }
    

    @FXML
    private void createPackageAction(ActionEvent event) {
        dbh = getDbh();
//        dbh.addPackageDeliveryToDB(deliveryType, fromSP, toSP);
//        
//        dbh.addPackageToDB(deliveryID, packageSize);

        PCattrList.addAll(Arrays.asList(
                "itemID", "itemName", "itemSize", "itemWeight", "breakable"));

        PCadditionalTermList.addAll(Arrays.asList(
                " ORDER BY itemID DESC LIMIT ?", "1")); //dbHandler requires 2 terms.

        PCtempList = dbh.readFromdb("item", PCattrList, PCadditionalTermList);

        createItemObject(PCtempList.get(0));
        PCtempList.clear();
        PCattrList.clear();
        PCadditionalTermList.clear();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        Stage packageCreator = (Stage) cancelButton.getScene().getWindow();
        packageCreator.close();
    }
    

    private void addCityToList() {
        startCityCombo.getItems().addAll(FXMLDocumentController.getCityList());
        destinationCityCombo.getItems().addAll(FXMLDocumentController.getCityList());   
    }

    @FXML
    private void refreshListView1(ActionEvent event) {
        refreshList(startList, startCityCombo);
    }
    
    @FXML
    private void refreshListView2(ActionEvent event) {
        refreshList(destinationList, destinationCityCombo);
    }
    
    private void refreshList(ListView L, ComboBox<String> C) {
        L.getItems().clear();
        for (smartPostObject SP : sp.getSpList()) {

            if (SP.getCity().equals(C.valueProperty().getValue().toUpperCase())) {
                L.getItems().add(SP);
            }
        }
    }

    @FXML
    private void refreshPackageInfoLabel(ActionEvent event) {
        
    }

    @FXML
    private void refreshItemInfoLabel(ActionEvent event) {
        String s = "Ei särkyvä";
        Item I = itemCombo.valueProperty().getValue(); 
        
        if (I.getBreakable()) { s = "Särkyvä"; }
        
        itemInfoLabel.setText(String.format("Esineen tiedot:\n"
                + "Koko = %.2f dm³ / litraa\n"
                + "Paino = %.2f kg\n"
                + "%s", I.getSize()/1000, I.getWeight()/1000, s));
    }
    
    private DBHandler getDbh() {
        dbh = DBHandler.getInstance();
        return dbh;
    }
}
