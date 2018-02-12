/*
 * Summer 2016
 * Object-Oriented Programming -course
 * Delivery -class
 */

package timotei;

/**
 *
 * @author Roope Luukkainen
 */
public abstract class deliveryClass {    
    public deliveryClass() {}
}

class deliveryClass1 extends deliveryClass {
    private int distanceLimit;
    
    public deliveryClass1(int dl) {
        super();
        distanceLimit = dl;
    }
}

class deliveryClass2 extends deliveryClass {
    private int amountOfTimoteiMen;
    private int emptySizeLimit; //percent
    
    public deliveryClass2(int m, int el) {
        super();
        amountOfTimoteiMen = m;
        emptySizeLimit = el;
    }
}

class deliveryClass3 extends deliveryClass {
    public deliveryClass3() {
        super();
    }
}
