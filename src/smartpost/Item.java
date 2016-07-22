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
    private double size, weight; // size unit cm³ | weigth unit g
    private int packageID, ID; 
    private boolean breakable;  
    
    public Item(int ID, String N, double S, double W, boolean B) {
        this.ID = ID;
        name = N;
        weight = W;
        size = S;
        breakable = B;
        System.out.printf("%d, %s, %f, %f, %b", ID, name, (float)S, (float)W, B);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public boolean getBreakable() {
        return breakable;
    }
    
    public double getSize() {
        return size;
    }
    
    public double getWeight() {
        return weight;
    }
}


