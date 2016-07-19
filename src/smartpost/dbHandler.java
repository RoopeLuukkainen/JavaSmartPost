/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author k9751
 */
public class dbHandler {
    static private dbHandler dbh = null;
    
    private ArrayList tempList = null;
    private Object[] tempArray;
    
    private final String protocol = "jdbc:sqlite:", dbName = "harkkakanta.db";
    
    Connection conn = null;
    PreparedStatement query;
    ResultSet rs = null;
    
    private dbHandler() {
        tempList = new ArrayList();
        tempArray = new String[6];
        
        String sDriverName = "org.sqlite.JDBC";
        try {
            Class.forName(sDriverName);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ClassNotFound EXCEPTION!!!!");
        
        } 
        
        
    }
    
    static public dbHandler getInstance() {
    /* Returns instance to dbHandler object. Uses singleton design pattern */
        
        if (dbh == null)
            dbh = new dbHandler();
        return dbh;
    }

//    public void writeTodb() {
//        
//        try {
//            conn = DriverManager.getConnection(protocol + dbName);
//        
//            query = conn.prepareStatement("INSERT INTO deliveryClass"
//                            + "('deliveryType', 'speed', 'amountOfTimoteiMen')\n" 
//                            + "VALUES (2, 70, 2)");
//            query.execute();
//            System.out.println("WRITE 1");
//            query.close();
//            
//            conn.close();
//        
//        } catch (SQLException ex) {
//            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("write error 1");
//            
//        } 
//    }
    
    void writeSmartPostTodb(String postOffice, String avaibility, String address,
                      String postalCode, String city, String lat, String lng) {
        /* Adds all SmartPosts to database table "smartPost" */
        
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);
            
            query = conn.prepareStatement("INSERT INTO smartPost"
                    + "('smartPostName', 'openingTime', 'closingTime', "
                    + "'streetAddress', 'postalCode', 'city', 'latitude', 'longitude')"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            
            query.setString(1, postOffice);
            query.setString(2, avaibility);// MUUUUTAAA
            query.setString(3, avaibility);// TO DATE MUOTOON!!
            query.setString(4, address);
            query.setString(5, postalCode);
            query.setString(6, city);
            query.setFloat(7, Float.parseFloat(lat));
            query.setFloat(8, Float.parseFloat(lng));
            
            query.execute();

            conn.commit();
                        
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Transaction is rolled back!");
                
            } catch (SQLException ex1) {                
                System.err.println(ex1.getMessage());
            }
            
        } finally {
            CloseDB();
        }
    }
    
    public void addItemToDB(String N, double S, int W, boolean B) {
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO item"
                    + "('itemName', 'itemSize', 'itemWeight', 'breakable')"
                    + "VALUES (?, ?, ?, ?)");

            query.setString(1, N);
            query.setDouble(2, S);
            query.setInt(3, W);
            query.setBoolean(4, B);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add item transaction is rolled back!");

            } catch (SQLException ex1) {                
                System.err.println(ex1.getMessage());
            }

        } finally {
            CloseDB();
        }
    }
    
    public ArrayList readFromdb(String table, ArrayList<String> attrList, String whereTerm) { //String attr, String attr2, String attr3
    /* Read given attributes from given table. Returns ArrayList of values if 
        there is multiple attributes given each row is as array in ArrayList. */
        
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);
            tempList.clear();
            
            String attr = attrList.get(0);
            String queryS = "SELECT " + attr;
            attrList.remove(0); // removing first attr makes query building easier.
            
            if (!attrList.isEmpty()) {
                for (String attribute : attrList) {
                    queryS += String.format(", %s", attribute);
                }
            }
            queryS += String.format("FROM %s", table);
            

            if (!whereTerm.equals("")) {
                queryS += (" WHERE " + whereTerm);
            }
            
            System.out.println(queryS);
            query = conn.prepareStatement(queryS);
            
            rs = query.executeQuery();
            
            if (attrList.isEmpty()) {
                while (rs.next()) {
                    tempList.add(rs.getObject(attr));
                }
            
            } else {
                int i = 0;
                attrList.add(attr); //Adding attr back makes Array handling easier.                
                while (rs.next()) {
                    i = 0;
                    for (String S : attrList) {
                        tempArray[i] = rs.getObject(attrList.get(i++));
                    }
                    
                    System.out.println(tempArray[0] + " " + tempArray[1]);
                    tempList.add(tempArray.clone());
                }
            }
            
            
            conn.commit();
            attrList.clear();
            
        } catch (SQLException ex) {
            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("DB reading error.");
            try {
                conn.rollback();
                System.err.println("Reading from DB transaction is rolled back!");
            } catch (SQLException ex1) {                
                System.err.println(ex1.getMessage());
            
            } finally {
                tempArray = null;
                CloseDB();
                 
            }
        }
        return tempList; 
    }
    
    public void CloseDB() {
        try {
            if (!(rs == null) || !(rs.isClosed()))
                rs.close();
            
            if (!query.isClosed())
                    query.close();

            if (!conn.isClosed())
                conn.close();

                        
        } catch (SQLException ex) {
            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
