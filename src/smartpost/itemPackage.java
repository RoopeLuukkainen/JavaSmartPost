/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

/**
 *
 * @author k9751
 */
public class itemPackage {
    private String fromSP, toSP;
    private int packageID, deliveryID, fromID, toID;
    private double size;
    
    public itemPackage(int pID, int dID, double s, int fromID, int toID, String fromSP, String toSP) {
        packageID = pID;
        deliveryID = dID;
        size = s;
        this.fromID = fromID;
        this.toID = toID;
        this.fromSP = fromSP;
        this.toSP = toSP;
    }

    public double getSize() {
        return size;
    }
    
//    @Override
//    public String toString() {
//        return
//    }
}
