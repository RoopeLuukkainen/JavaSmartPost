/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package timotei;

import java.util.ArrayList;

/**
 *
 * @author k9751
 */
public class smartPostObject {
    static private ArrayList <smartPostObject> spList = new ArrayList<>();
    
    private String color, postalCode, city, address, availability, name;
    private float latitude, longitude;
    private boolean drawn;
    private int ID;
    
    public smartPostObject(){}
    
    public smartPostObject(int ID, String co, String p, String c, String a, 
            String av, String n, float lat, float lng, boolean d) {        
        try {
            name = n.split(",")[1]; /* XML format is following
            <postoffice>Pakettiautomaatti, Kauppakeskus Sello</postoffice> 
            doublechecks if there isn't , so program will not crash. */
        } catch (IndexOutOfBoundsException ex) {
            name = n;
        }
        color = co;
        this.ID = ID;
        postalCode = p;
        city = c;
        address = a;
        availability = av;
        latitude = lat;
        longitude = lng;
        drawn = d;
        
    }
    
    static public ArrayList<smartPostObject> getSpList() {
        return spList;
    }
    
    public void addToSpList(smartPostObject sp) {
        spList.add(sp);
    }
    
    public void removeFromSpList(smartPostObject sp) {
        spList.remove(sp);
    }
    
    public String getCity() {
        return city;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean getDrawn() {
        return drawn;
    }
    
    public void setDrawn(boolean b) {
        drawn = b;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
    
    @Override
    public String toString() {
        return name + " | " + address;
    }
}
