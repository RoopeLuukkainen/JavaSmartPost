/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TIMOTEI;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javafx.stage.Stage;

import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author k9751
 */
public class PackageCreatorFXMLController implements Initializable {
    DBHandler dbh;
    smartPostObject sp = new smartPostObject();
    Item I = null;
    
    enum btype {normal, breaker, protective, special}
    
    private int deliveryClassRadio = 1, dID = 0, itype = 0, addItemCount;
    private boolean tempBool;
    
    private FadeTransition fadeIn = new FadeTransition(Duration.millis(2000));
    
    private ArrayList<Item> PCitemList = new ArrayList<>(); //List of all items
    private ArrayList<ArrayList> PCtempList = new ArrayList<>(); 
    private ArrayList<Integer> PCidList = new ArrayList<>();
    private ArrayList<itemPackage> PCpackageList = new ArrayList<>(); // List of all packages
    private ArrayList<String> PCattrList = new ArrayList<>();   //List for dbreader attributes
    private ArrayList<Object> PCadditionalTermList = new ArrayList<>(); // List for dbreader additionalTerms
    private ArrayList<String> itemTypes = new ArrayList<>(); //List of 4 different itemTypes
    
    @FXML
    private Button addItemButton;
    @FXML
    private ComboBox<Item> itemCombo;
    @FXML
    private ToggleButton breakableToggle;
    @FXML
    private Button createItemButton;
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
    static private ComboBox<itemPackage> packageCombo;
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
    @FXML
    private RadioButton radioButton1;
    @FXML
    private RadioButton radioButton2;
    @FXML
    private RadioButton radioButton3;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private GridPane backgroundPane;
    @FXML
    private AnchorPane questionPane;
    @FXML
    private TextField packageSizeField;
    @FXML
    private TextField itemAmountField;
    @FXML
    private TextField percentField;
    @FXML
    private Button itemTypeButton;
    @FXML
    private AnchorPane intPane;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        radioButton1.selectedProperty().setValue(true);
        questionPane.setVisible(false);
        intPane.setVisible(false);
        percentField.disableProperty().setValue(true);
        
        dbh = getDbh();
        
        PCattrList.addAll(Arrays.asList(
            "itemTypeID", "itemName", "itemSize", "itemWeight", "breakable", "btype", "percent"));
        PCtempList = dbh.readFromdb("itemTypeView", PCattrList, null);
        
        PCattrList.clear();
        
        for (ArrayList A : PCtempList) {
            createItemObject(A);
        }
        
        PCtempList.clear();

        PCattrList.addAll(Arrays.asList(
                "packageID", "deliveryID", "packageSize", "from_ID", "to_ID", 
                "from_smartPost", "to_smartPost", "deliveryType"));
        PCtempList = dbh.readFromdb("packageView", PCattrList, null);
        
        PCattrList.clear();
        
        for (ArrayList A : PCtempList) {
            createPackageObject((int)A.get(0), (int)A.get(1), 
            Float.parseFloat(A.get(2).toString()),(int)A.get(3), (int)A.get(4),
            (String)A.get(5), (String)A.get(6), (int)A.get(7));
        }
        PCtempList.clear();
        
        itemInfoLabel.setText(
                "Tavallinen esine, ei riko eikä suojaa muita esineitä.\n"
                + "Paketissa voi olla yksi tai useampi esine.");
        
        addCityToList();
        
        fadeIn.setNode(intPane);    // for number format exception
        fadeIn.setFromValue(1.0);   // visible
        fadeIn.setToValue(0.0);     // invisible
        fadeIn.setCycleCount(1);    //-1 infinite loop AND 1 is default
        fadeIn.setAutoReverse(false); //true goes also back to beginning
        

    }    

    @FXML
    private void addItemAction(ActionEvent event) {        
        try {
            if (itemAmountField.getText().isEmpty()) {
                addItemCount = 1;
            } else {
                addItemCount = Integer.parseInt(itemAmountField.getText());
            }
            
            itemPackage P = packageCombo.valueProperty().getValue();
            Item It = itemCombo.valueProperty().getValue();
            
            Double size = P.checkPackageSpace();
            
            if (size == -2.0) {
                size = P.getSize();
            }
            
            if (size - (It.getSize()/1000)*addItemCount < 0) {
                packageInfoLabel.setText("Esine(et) ei(vät) mahdu pakettiin.");
                
            } else {
                addItemToPackage(addItemCount, It.ID, P.getPackageID());
            }
            
        } catch (NumberFormatException ex) {
            intPane.setVisible(true);
            fadeIn.playFromStart();
        
        } catch (NullPointerException e) {
            packageInfoLabel.setText("Valitse aluksi lisättävä esine\n"
                    + "ja sitten paketti, johon esine lisätään.");
    
        } finally {
            itemAmountField.setText("");            
        }
        
        ComboBox C = FXMLDocumentController.getDeliveryCombo();
        C.getItems().clear();
        for (itemPackage p : packageCombo.getItems()) {
            if (!(C.getItems().contains(p))) {
                C.getItems().add(p);
            }            
        }
    }
    
    private void addItemToPackage(int i, int Type, int Package) {
        dbh = getDbh();
        while (i-- > 0) {            
            dbh.addItemToDB(Type, Package);
        }
        refreshPackageInfo();
    }

    @FXML
    private void createItemTypeAction(ActionEvent event) {        
        String itemName = itemNameField.getText();
        
        try {
            double itemSize = Double.parseDouble(itemWidthField.getText()
                    .replaceAll(",", ".").replaceAll(" ", "")) 
                * Double.parseDouble(itemHeightField.getText()
                        .replaceAll(",", ".").replaceAll(" ", "")) 
                * Double.parseDouble(itemDepthField.getText()
                        .replaceAll(",", ".").replaceAll(" ", ""));
            
            int itemWeight = Integer.parseInt(itemWeightField.getText());

            boolean breakable = breakableToggle.isSelected();

            dbh = getDbh();
            int tempPercent;
            if (percentField.getText().isEmpty()) {
                tempPercent = 100;
                
            } else {
                    tempPercent = Integer.parseInt(percentField.getText());
            }  
            
            int breakType = checkBreakType(
                    typeToEnglish(itemTypeButton.getText().toLowerCase()), tempPercent);
            
            dbh.addItemTypeToDB(itemName, itemSize, itemWeight, breakable, breakType);        
            

            PCattrList.addAll(Arrays.asList(
                    "itemTypeID", "itemName", "itemSize", "itemWeight", "breakable",
                    "btype", "percent"));

            PCadditionalTermList.addAll(Arrays.asList(
                    " ORDER BY itemTypeID DESC LIMIT ?", 1)); //dbHandler requires 2 terms.

            PCtempList = dbh.readFromdb("itemTypeView", PCattrList, PCadditionalTermList);

            createItemObject(PCtempList.get(0));
            PCtempList.clear();
            PCattrList.clear();
            PCadditionalTermList.clear();

            itemNameField.setText(""); 
            itemWeightField.setText("");
            itemHeightField.setText("");
            itemDepthField.setText("");
            itemWidthField.setText("");
            percentField.setText("");
            // inside TRY because only if creating succeess fields are emptied.
        
        } catch (NumberFormatException ex) {
            
            itemInfoLabel.setText("Anna mitat ja paino lukuarvoina.\n"
                    + "Välimerkki desimaaliluvussa voi olla '.' tai ','.\n"
                    + "Prosentti kokonaislukuna tai tyhjänä (=100%)"); 
            //fadeNumber.playFromStart();
        }
    }
    
    /*item constructor:
int ID, String Name, double Size, double Weight, boolean Breakable, int Percent */
    private void createItemObject(ArrayList A) {
        tempBool = (int)A.get(4) == 1;
        
        btype b = btype.valueOf((String)A.get(5));//A.get(5) value is breaktype.
        switch (b) {
            case normal:
                I = new Normal(((int) A.get(0)), (String) A.get(1), (double) A.get(2),
                        (double) A.get(3), tempBool);
                PCitemList.add((Normal) I);
                break;
                
            case breaker:
                I = new Breaker(((int) A.get(0)), (String) A.get(1), (double) A.get(2),
                        (double) A.get(3), tempBool, (int)A.get(6));
                PCitemList.add((Breaker) I);
                break;
                
            case protective:
                I = new Protective(((int) A.get(0)), (String) A.get(1), (double) A.get(2),
                        (double) A.get(3), tempBool, (int) A.get(6));
                PCitemList.add((Protective) I);
                break;
                
            case special:
                I = new Special(((int) A.get(0)), (String) A.get(1), (double) A.get(2),
                        (double) A.get(3), tempBool, (int) A.get(6));
                PCitemList.add((Special) I);
                break;
                
            default:
                System.err.println("Unsupported item type or null.");
                break;
        }

        itemCombo.getItems().add(PCitemList.get(PCitemList.size()-1));
    }
/*item package constructor:
int pID, int dID, double s, int fromID, int toID, String fromSP, String toSP */
    private void createPackageObject(int id, int did, double ps, int fromID, 
                                     int toID, String fromSP, String toSP, int dc) {
        
        PCpackageList.add(new itemPackage(id, did, ps, fromID, toID, fromSP, toSP, dc));        
        packageCombo.getItems().add(PCpackageList.get(PCpackageList.size() - 1));
    }    

    @FXML
    private void createPackageAction(ActionEvent event) {        
        smartPostObject fromSP = startList.getSelectionModel().getSelectedItem();
        smartPostObject toSP = destinationList.getSelectionModel().getSelectedItem();
        
        double routeLenght = 0.0;
        
        if (deliveryClassRadio == 1) {
            ArrayList A = new ArrayList();
            A.addAll(Arrays.asList(fromSP.getLatitude(), fromSP.getLongitude(),
                                    toSP.getLatitude(), toSP.getLongitude()));

            routeLenght = FXMLDocumentController.getRouteLenght(A);
        }
        
        if (routeLenght > 150) {
            packageInfoLabel.setText("Reitin pituus on yli 150km, ole hyvä ja \n"
                                    + "vaihda paketin kuljetusluokkaa.");
        } else {        
            if (fromSP == null || toSP == null) {
                packageInfoLabel.setText("Valitse ensin lähtö- ja kohdeautomaatti.");

            } else {
                questionPane.setVisible(true);
                backgroundPane.disableProperty().set(true);
            }
        }
    }
    
    @FXML
    private void yesAction(ActionEvent event) {
        questionPane.setVisible(false);
        backgroundPane.disableProperty().set(false);
        dbh = getDbh();

        System.out.println("StartList: " + startList.getSelectionModel().getSelectedItem());
        System.out.println("DestinationList: " + destinationList.getSelectionModel().getSelectedItem());

        smartPostObject fromSP = startList.getSelectionModel().getSelectedItem();
        smartPostObject toSP = destinationList.getSelectionModel().getSelectedItem();

         if (fromSP.equals(toSP)) {
            packageInfoLabel.setText("Et voi lähettää ja vastaanottaa\n"
                    + "pakettia samasta automaatista.");

        } else {
            packageInfoLabel.setText("");
            try {
                double ps = Double.parseDouble(packageSizeField.getText()
                        .replaceAll(",", ".").replaceAll(" ", ""));

                dbh.addPackageDeliveryToDB(deliveryClassRadio, fromSP.getID(), toSP.getID());

                PCattrList.addAll(Arrays.asList("deliveryID"));
                PCadditionalTermList.addAll(Arrays.asList(
                        " ORDER BY deliveryID DESC LIMIT ?", 1)); //dbHandler requires 2 terms.
                PCidList = dbh.readFromdb(
                        "packageDelivery", PCattrList, PCadditionalTermList);

                dID = (int) ((PCidList.get(0)));
                dbh.addPackageToDB(dID, ps);

                PCidList.clear();
                PCattrList.clear();

                PCattrList.addAll(Arrays.asList("packageID"));
                PCidList = dbh.readFromdb(
                        "package", PCattrList, PCadditionalTermList);

                /*item package constructor:
                 int pID, int dID, double s, int fromID, int toID, String fromSP, String toSP */
                createPackageObject((int) PCidList.get(0), dID, ps,
                        fromSP.getID(), toSP.getID(), fromSP.getName(), toSP.getName(),
                        deliveryClassRadio);

                packageSizeField.clear();

            } catch (NullPointerException ex) {
                packageInfoLabel.setText("Valitse ensin paketin lähtö- \n"
                        + "ja kohdeautomaatit yläpuolelta.");

            } catch (NumberFormatException ex) {
                packageInfoLabel.setText("Anna tilavuus lukuarvona.\n"
                        + "Välimerkki desimaaliluvussa voi olla '.' tai ','.");
                packageSizeField.clear();

            } finally {
                PCidList.clear();
                PCattrList.clear();
                PCadditionalTermList.clear();

            }
        }
    }

    @FXML
    private void noAction(ActionEvent event) {
        questionPane.setVisible(false);
        backgroundPane.disableProperty().set(false);
    }
    
    private int checkBreakType(String t, int p) {
        dbh = getDbh();
        int breakTypeID  = 0;
        PCattrList.addAll(Arrays.asList("breakTypeID","btype"));
        PCadditionalTermList.addAll(Arrays.asList(" WHERE percent = ?", p));
        
        PCtempList = dbh.readFromdb("breakTypes", PCattrList, PCadditionalTermList);
        PCadditionalTermList.clear();
        
        for (ArrayList A : PCtempList) {
            if (t.toLowerCase().equals(((String)A.get(1)).toLowerCase())) {                                                        
                PCattrList.clear();
                PCtempList.clear();                                                                    
                return ((int)A.get(0));    
                //Returns breakTypeID if percent-type pair already exists.      
            }
        }
        // Creates new pair if percent-type pair is new.
        dbh.addBreakTypeToDB(t, p);

        PCadditionalTermList.addAll(Arrays.asList(
                " ORDER BY breakTypeID DESC LIMIT ?", 1)); //dbHandler requires 2 terms.
                                            //Uses same attributeList as checker.
        PCtempList = dbh.readFromdb("breakTypes", PCattrList, PCadditionalTermList);
        breakTypeID = (int)PCtempList.get(0).get(0);
        
        PCtempList.clear();
        PCattrList.clear();
        PCadditionalTermList.clear();
        
        return breakTypeID; //Returns newest breakTypeID, which is just created one.
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
        try {
            L.getItems().clear();
            for (smartPostObject SP : sp.getSpList()) {

                if (SP.getCity().equals(C.valueProperty().getValue().toUpperCase())) {
                    L.getItems().add(SP);
                }
            }
        } catch (NullPointerException ex) {}
    }

    @FXML
    private void refreshPackageInfoLabel(ActionEvent event) {
        refreshPackageInfo();             
    }
    
    private void refreshPackageInfo() {
        itemPackage P = packageCombo.valueProperty().getValue();

        try {
            dbh = getDbh();
            PCattrList.addAll(Arrays.asList("from_smartPost", "to_smartPost"));
            PCadditionalTermList.addAll(Arrays.asList(
                    " WHERE packageID = ?", P.getPackageID()));
            PCtempList = dbh.readFromdb("packageView", PCattrList, PCadditionalTermList);

            PCattrList.clear();
            PCadditionalTermList.clear();
            
            String s = String.format("Lähtöautomaatti: %s\n"
                    + "Kohdeautomaatti: %s\nTilavuus jäljellä/yhteensä: ",
                    (String) PCtempList.get(0).get(0), (String) PCtempList.get(0).get(1));

            Double size = P.checkPackageSpace();

            if (size == -2) {
                s += String.format("%1$.2f/%1$.2f dm³ \n", P.getSize());
            } else {
                s += String.format("%.2f/%.2f dm³\n", size, P.getSize());
                s += parsePackageInfo(P.getPackageID());
            }

            PCtempList.clear();
            packageInfoLabel.setText(s);
            
        } catch (NullPointerException ex) {} 
        // Catching ex when deleting items from database.     
    }
    
    private String parsePackageInfo(int ID) {
        int i = 0;
        String temp = "";
        String s = "Paketissa olevat esineet: ";
        dbh = getDbh();
        PCattrList.addAll(Arrays.asList(
                "itemName", "breakType", "itemSize", "itemWeight"));
        PCadditionalTermList.addAll(Arrays.asList(" WHERE packageID = ?", ID));
        PCtempList = dbh.readFromdb("InPackagesView", PCattrList, PCadditionalTermList);

        PCattrList.clear();
        PCadditionalTermList.clear();

        for (ArrayList A : PCtempList) {        
            if (i == 0) {
                i++;
                temp = stringCombiner(A);                
                
            } else if (temp.equals(stringCombiner(A))) {
                i++;
                
            } else {    
                    s += "\n" + ++i + "x " + temp;
                    temp = stringCombiner(A);
                    i = 0;
            }
        }
        if (i == 0)
            i++;
        
        s += "\n" + i + "x " + temp;
        
        return s;
    }
    
    private String stringCombiner(ArrayList A) {
        String s = String.format("%s: %s | %.2f cm³ | %.2f g",
                A.get(0), A.get(1), A.get(2), A.get(3));
        return s;
    }
    
//    private double checkPackageSpace(int pID, double size) {
//        dbh = getDbh();
//        PCattrList.addAll(Arrays.asList("itemSize", "itemWeight"));
//        PCadditionalTermList.addAll(Arrays.asList(" WHERE packageID = ?", pID));
//        
//        ArrayList<ArrayList> items = dbh.readFromdb(
//                "inPackagesView", PCattrList, PCadditionalTermList);
//        
//        PCattrList.clear();
//        PCadditionalTermList.clear();
//        
//        if (items.isEmpty()) return -2.0; // = no items in package.
//        
//        for (ArrayList A : items) {
//            size -= ((double)A.get(0))/1000;
//        }        
//        items.clear();
//        
//        if (size < 0) return -1;
//        
//        return size;
//    }

    @FXML
    private void refreshItemInfoLabel(ActionEvent event) {
        String s = "Ei särkyvä";
        Item I = itemCombo.valueProperty().getValue(); 
        
        if (I.getBreakable()) { s = "Särkyvä"; }
        
        itemInfoLabel.setText(String.format("Esineen tiedot:\n"
                + "Koko = %.2f dm³ / litraa\n"
                + "Paino = %.2f kg\n"
                + "%s | %s", I.getSize()/1000, I.getWeight()/1000, s, I.getType()));
    }
    
    private DBHandler getDbh() {
        dbh = DBHandler.getInstance();
        return dbh;
    }

    @FXML
    private void selectRadioAction1(ActionEvent event) {
        radioButton2.selectedProperty().setValue(false);
        radioButton3.selectedProperty().setValue(false);
        deliveryClassRadio = 1;
        deliveryClassInfoLabel.setText("\tNopein pakettiluokka.\n"
                + "Matkan pituusrajoitus on 150km.\n"
                + "Särkyvät esineet todennäköisesti särkyvät.");
    }

    @FXML
    private void selectRadioAction2(ActionEvent event) {
        radioButton1.selectedProperty().setValue(false);
        radioButton3.selectedProperty().setValue(false);
        deliveryClassRadio = 2;
        deliveryClassInfoLabel.setText("\tTurvallisin pakettiluokka.\n"
                + "Paketissa ei saa olla yli 10% tyhjää tilaa\n"
                + "tai muuten särkyvät esineet eivät\n"
                + "todennäköisemmin pysy ehjänä.");
    }

    @FXML
    private void selectRadioAction3(ActionEvent event) {
        radioButton1.selectedProperty().setValue(false);
        radioButton2.selectedProperty().setValue(false);
        deliveryClassRadio = 3;
        deliveryClassInfoLabel.setText("\tHitain pakettiluokka.\n"
                + "Särkyvät esineet luultavimmin särkymään\n"
                + "paino ja suuri paketin koko voivat auttaa.");
    }

        

    @FXML
    private void changeTypeAction(ActionEvent event) {
        if (itemTypes.isEmpty()) {
            itemTypes.addAll(Arrays.asList(
                    "Tavallinen", "Särkevä", "Suojaava", "Erikoinen"));
        }
        
        //Changes text to next type on the typeList.
        if (itype == 3) {
            itype = 0;
            itemTypeButton.setText(itemTypes.get(0)); 
            itemInfoLabel.setText("Tavallinen esine, ei riko eikä suojaa muita esineitä.\n"
                    + "Paketissa voi olla yksi tai useampi esine.\n");
            percentField.disableProperty().setValue(true);
        } else {
            percentField.disableProperty().setValue(false);
            itemTypeButton.setText(itemTypes.get(++itype));
            itemInfoLabel.setText("Särkevä esine voi rikkoa muita esineitä.\n"
                    + "Suojaava esine voi estää rikkoutumisen.\n"
                    + "Erikoisesine voi särkyä, jos paketissa on muita esineitä. \n"
                    + "Ominaisuuksien todennäköisyyttä voi säätää.");
        }
    }
    
    private String typeToEnglish(String s) {
        if (s.equals("tavallinen"))
            s = "normal";
        else if (s.equals("särkevä"))
            s = "breaker";
        else if (s.equals("suojaava"))
            s = "protective";
        else 
            s = "special";        
        return s;
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
    
    public static ComboBox getPackageCombo() {
        return packageCombo;
    }
}