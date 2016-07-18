/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author k9751
 */
public class PackageCreatorFXMLController implements Initializable {
    dbHandler dbh;
    
    @FXML
    private Button addItemButton;
    @FXML
    private ComboBox<String> itemCombo;
    @FXML
    private TextField itemSizeLabel;
    @FXML
    private TextField itemWeightLabel;
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
    private ComboBox<String> startSmartPostCombo;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> destinationCityCombo;
    @FXML
    private ComboBox<String> destinationSmartPostCombo;
    @FXML
    private Button createPackageButton;
    @FXML
    private TextField itemNameLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void addItemAction(ActionEvent event) {
        
    }

    @FXML
    private void createItemAction(ActionEvent event) {        
        String itemName = itemNameLabel.getText();
        String[] size = itemSizeLabel.getText().split("x");
        int itemSize = Integer.parseInt(size[0]) * Integer.parseInt(size[1]) * Integer.parseInt(size[2]);
        int itemWeight = Integer.parseInt(itemWeightLabel.getText());        
        boolean breakable = breakableToggle.isSelected();
        
        dbh = getDbh();
        dbh.addItemToDB(itemName, itemSize, itemWeight, breakable);
        
        itemCombo.getItems().add(itemName);
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        
    }

    @FXML
    private void createPackageAction(ActionEvent event) {
    }
    
    private dbHandler getDbh() {
        dbh = dbHandler.getInstance();
        return dbh;
    }
}
