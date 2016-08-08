/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package timotei;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author k9751
 */
public class itemPackage {
    private String fromSP, toSP, packageString;
    private int packageID, deliveryID, fromID, toID;
    private double size;
    DBHandler dbh = null;
    deliveryClass DC;
    
    private ArrayList<ArrayList> IPtempList = new ArrayList<>();
    private ArrayList<String> IPattrList = new ArrayList<>();   //List for dbreader attributes
    private ArrayList<Object> IPadditionalTermList = new ArrayList<>(); // List for dbreader additionalTerms
    
    public itemPackage(int pID, int dID, double s, int fromID, int toID, String fromSP, String toSP, int dc) {
        packageID = pID;
        deliveryID = dID;
        size = s;
        this.fromID = fromID;
        this.toID = toID;
        this.fromSP = fromSP;
        this.toSP = toSP;
        packageString = "";
        DC = setDeliveryClass(dc);
        
        FXMLDocumentController.writeLog("Paketti: " + this.toString() + " luotu.");
    }

    public double getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return getPackageString();
    }

    private String getPackageString() {
        /* Makes package String more informative for ComboBoxes. */
        if (this.packageString.isEmpty()) {
            dbh = DBHandler.getInstance();

            IPattrList.addAll(Arrays.asList(
                    "from_smartPost", "to_smartPost", "deliveryType"));

            IPadditionalTermList.addAll(Arrays.asList(
                    " WHERE packageID = ?", this.packageID));        

            IPtempList = dbh.readFromdb("packageView", IPattrList, IPadditionalTermList);

            IPattrList.clear();
            IPadditionalTermList.clear();

            packageString = String.format("%s -> %s [%d. luokka]", 
                                    parseName((String)IPtempList.get(0).get(0)),
                                    parseName((String)IPtempList.get(0).get(1)),
                                    IPtempList.get(0).get(2));
            IPtempList.clear();
            
            return packageString;
        
        } else {
            return this.packageString;
        }
    }
    
    private String parseName(String s) {
        String name;
        try {
            name = s.split(",")[1]; /* XML format is following
             <postoffice>Pakettiautomaatti, Kauppakeskus Sello</postoffice> 
             doublechecks if there isn't , so program will not crash. */

        } catch (IndexOutOfBoundsException ex) {
            name = s;
        }
        return name;
    }
    
    public double checkPackageSpace() {
        double emptySize = size;
        dbh = DBHandler.getInstance();
        IPattrList.addAll(Arrays.asList("itemSize", "itemWeight"));
        IPadditionalTermList.addAll(Arrays.asList(" WHERE packageID = ?", packageID));

        IPtempList = dbh.readFromdb(
                "inPackagesView", IPattrList, IPadditionalTermList);

        IPattrList.clear();
        IPadditionalTermList.clear();

        if (IPtempList.isEmpty()) {
            return -2.0; // = no items in package.
        }
        for (ArrayList A : IPtempList) {
            emptySize -= ((double) A.get(0)) / 1000;
        }
        IPtempList.clear();

        if (emptySize < 0) {
            return -1;
        }

        return emptySize;
    }
    
    private deliveryClass setDeliveryClass(int dc) {
        if (dc == 1) {
            DC = new deliveryClass1(150);
        
        } else if (dc == 2) {
            DC = new deliveryClass2(2, 10);
        
        } else {
            DC = new deliveryClass3();
        }
        return DC;
    }
    
    public deliveryClass getDeliveryClass() {
        return DC;
    }
    
    public int getPackageID() {
        return packageID;
    }
    
    public int getFromID() {
        return fromID;
    }
    
    public int getToID() {
        return toID;
    }
}
