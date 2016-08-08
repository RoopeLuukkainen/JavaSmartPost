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
public class TimoteiMan {
    private String familyName, firstName, action1, action2, action3;
    private int id, stressLimit, stressLevel;
    DBHandler dbh = null;
    
    public TimoteiMan(int id, String fa, String fi, int li, int le, 
                                String a1, String a2, String a3) {
        this.id = id;
        familyName = fa;
        firstName = fi;
        stressLimit = li;
        stressLevel = le;
        action1 = a1;
        action2 = a2;
        action3 = a3;
    }
    
    @Override
    public String toString() {        
        return String.format("%s %s (%d/%d)", 
                firstName, familyName, stressLevel, stressLimit);
    }
    
    public void increaseStressLevel(int inc) {
        dbh = DBHandler.getInstance();
        if (inc != 30) {
            stressLevel += inc;
        } else {
            stressLevel = 0;
        }
//String table, String attr, Object newValue, String whereAttr, Object whereValue
        dbh.updateDB("TIMOTEI_man", "stressLevel", stressLevel, "TIMOTEI_ID", id);
    }

    public int getId() {
        return id;
    }

    public String getAction1() {
        return action1;
    }

    public String getAction2() {
        return action2;
    }

    public String getAction3() {
        return action3;
    }

    public int getStressLimit() {
        return stressLimit;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFirstName() {
        return firstName;
    }
    
    
}
