/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package timotei;

/**
 *
 * @author k9751
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