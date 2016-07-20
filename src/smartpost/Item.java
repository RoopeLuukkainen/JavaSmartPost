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
public class Item {
    private String name;
    private double size; // size unit cm³
    private int packageID, ID, weight; // weigth unit g
    private boolean breakable;  
    
    public Item(int ID, String N, double S, int W, boolean B) {
        this.ID = ID;
        name = N;
        weight = W;
        size = S;
        breakable = B;
        System.out.printf("%d, %s, %f, %d, %b", ID, name, (float)S, W, B);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
