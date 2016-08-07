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
  public abstract class Item {
    protected String name;
    protected double size, weight; // size unit cm³ | weigth unit g
    protected int packageID, ID, percent; 
    protected boolean breakable;  

    public Item(int ID, String N, double S, double W, boolean B) {
        this.ID = ID;
        name = N;
        size = S;
        weight = W;
        breakable = B;
                                            //System.out.printf("Item: %d, %s, %f, %f, %b\n", ID, name, (float)S, (float)W, B);
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
    
    public String getType() {
        return typeToFinnish(((this.getClass().getName()).replace("timotei.", "")).toLowerCase());
    } //className: 'timotei.Normal' -> normal
    
    private String typeToFinnish(String s) {
        if (s.equals("normal")) {
            s = "tavallinen";
        } else if (s.equals("breaker")) {
            s = "särkevä";
        } else if (s.equals("protective")) {
            s = "suojaava";
        } else {
            s = "erikoinen";
        }
        return s;
    }
}

class Normal extends Item { 
    public Normal(int ID, String N, double S, double W, boolean B) {
        super(ID, N, S, W, B);
                                                   // System.out.println("N: " + ID +" " +N + " " + S + " " + W + " " + B);
    }
}

class Breaker extends Item {
    private int canBreak; // percent of propability to break other item.
    
    public Breaker(int ID, String N, double S, double W, boolean B, int C) {
        super(ID, N, S, W, B);
        canBreak = C;
    }
    
    public int getCanBreak() {
        return canBreak;
    }
}

class Protective extends Item {
    private int protection; // amount of not break propability increased as percent. 
    
    public Protective(int ID, String N, double S, double W, boolean B, int P) {
        super(ID, N, S, W, B);
        protection = P;
    }
}

class Special extends Item {
    private int onlyOne; //percent of propability to break if not only item in package.

    public Special(int ID, String N, double S, double W, boolean B, int O) {
        super(ID, N, S, W, B);
        onlyOne = O;
    }

    public int getOnlyOne() {
        return onlyOne;
    }
}
