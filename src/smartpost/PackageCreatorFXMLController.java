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
    private ArrayList<itemPackage> packageList = new ArrayList<>();
    private ArrayList<String> attrList = new ArrayList<>();
    
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbh = getDbh();
        attrList.addAll(Arrays.asList("itemID", "itemName", "itemSize", "itemWeight", "breakable"));
        itemList = dbh.readFromdb("item", attrList, "");
        
        for (Object o : itemList) {
            String[] list = (String[]) o;
            itemCombo.getItems().add(new Item(list[0], list[1], list[2], list[3], list[4]));
        } ////// KORJAAAAAA !!!!!!!!!!!!!!!
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
        
        itemCombo.getItems().add(new Item(itemName, itemSize, itemWeight, breakable));
    }
    
    private void createItemObject() {
        itemCombo.getItems().addAll(itemList);
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
}
