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
public abstract class deliveryClass {
    private int speed;
    
    public deliveryClass(int s) {
        speed = s;
    }
}

class deliveryClass1 extends deliveryClass {
    private int distanceLimit;
    
    public deliveryClass1(int s, int dl) {
        super(s);
        distanceLimit = dl;
    }
}

class deliveryClass2 extends deliveryClass {
    private int amountOfTimoteiMen;
    private double emptySizeLimit;
    
    public deliveryClass2(int s, int m, double el) {
        super(s);
        amountOfTimoteiMen = m;
        emptySizeLimit = el;
    }
}

class deliveryClass3 extends deliveryClass {
    String stressAction;
    public deliveryClass3(int s, String sa) {
        super(s);
        stressAction = sa;
    }
}