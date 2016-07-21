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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author k9751
 */
public class PackageCreatorFXMLController implements Initializable {
    dbHandler dbh;
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<ArrayList> tempList = new ArrayList<>();
    private ArrayList<itemPackage> packageList = new ArrayList<>();
    private ArrayList<String> attrList = new ArrayList<>();
    private ArrayList<String> additionalTermList = new ArrayList<>();
    
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
    private TableColumn<?, ?> spNameColumn1;
    @FXML
    private TableColumn<?, ?> spAddressColumn1;
    @FXML
    private TableColumn<?, ?> spNameColumn2;
    @FXML
    private TableColumn<?, ?> spAddressColumn2;
    @FXML
    private TableView<String> startTable;
    @FXML
    private TableView<?> destinationTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        dbh = getDbh();
        attrList.addAll(Arrays.asList("itemID", "itemName", "itemSize", "itemWeight", "breakable"));
        tempList = dbh.readFromdb("item", attrList, null);
        
        attrList.clear();
        
        for (ArrayList A : tempList) {
            createItemObject(A);
        }
        
        addCitiesToList();
    }    

    @FXML
    private void addItemAction(ActionEvent event) {
        
    }

    @FXML
    private void createItemAction(ActionEvent event) {        
        String itemName = itemNameField.getText();
        
        double itemSize = Double.parseDouble(itemWidthField.getText()) 
                * Double.parseDouble(itemHeightField.getText()) 
                * Double.parseDouble(itemDepthField.getText());
        
        int itemWeight = Integer.parseInt(itemWeightField.getText());        
        boolean breakable = breakableToggle.isSelected();
        
        dbh = getDbh();
        dbh.addItemToDB(itemName, itemSize, itemWeight, breakable);
       
        attrList.addAll(Arrays.asList("itemID", "itemName", "itemSize", "itemWeight", "breakable"));
        
        additionalTermList.addAll(Arrays.asList(
                " ORDER BY itemID DESC LIMIT ?", "1")); //Used 2 terms because 
        
        tempList = dbh.readFromdb("item", attrList, additionalTermList);
        
        System.out.println(tempList);
        System.out.println(tempList.get(0));
        
        createItemObject(tempList.get(0));
        tempList.clear();
        attrList.clear();
        additionalTermList.clear();
    }
    
    private void createItemObject(ArrayList A) {
//        System.out.println(A);
//        System.out.println(Integer.parseInt(A.get(0).toString()) + "|" + (String)A.get(1) + "|" +
//                Double.parseDouble(A.get(2).toString()) + "|" + Integer.parseInt(A.get(3).toString()) + "|" + Boolean.parseBoolean(A.get(4).toString()));

        itemCombo.getItems().add(new Item(Integer.parseInt(A.get(0).toString()),
                (String)A.get(1), Double.parseDouble(A.get(2).toString()),
                Integer.parseInt(A.get(3).toString()),
                Boolean.parseBoolean(A.get(4).toString())));
    }
    

    @FXML
    private void createPackageAction(ActionEvent event) {
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        Stage packageCreator = (Stage) cancelButton.getScene().getWindow();
        packageCreator.close();
    }
    
    private dbHandler getDbh() {
        dbh = dbHandler.getInstance();
        return dbh;
    }

    private void addCitiesToList() {
        startCityCombo.getItems().addAll(FXMLDocumentController.getCityList());
        destinationCityCombo.getItems().addAll(FXMLDocumentController.getCityList());   
    }

    @FXML
    private void refreshTableView(ActionEvent event) {
        //OPETTELE KÄYTTÄMÄÄN TABLEVIEWIÄ!!!
    }
}
