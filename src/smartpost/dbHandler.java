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
    
    private final String protocol = "jdbc:sqlite:", dbName = "harkkakanta.db";
    
    Connection conn = null;
    PreparedStatement query;
    ResultSet rs = null;
    
    private dbHandler() {
        tempList = new ArrayList();
        
        try {
            conn.setAutoCommit(false);
            
        } catch (SQLException ex) {
            System.err.println("Failed to set autocommit to false.");
        }
        
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
            CloseDB();
            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Transaction is rolled back!");
            } catch (SQLException ex1) {                
                System.err.println(ex1.getMessage());
            }
        }
    }
    
    
    public ArrayList readFromdb(String table, String attr, String attr2) {
    /* Read given attributes from given table. Returns ArrayList of values */
        
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            if (attr2 != null) {
                attr2 = ", " + attr2;
            }
            
            query = conn.prepareStatement("SELECT "+ attr + attr2 + 
                                            " FROM "+ table +"");
            
            rs = query.executeQuery();
                while (rs.next()) {
                    tempList.add(rs.getString(attr));
                }
                
            conn.commit();
            CloseDB();
            
                    
        } catch (SQLException ex) {
            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Read error 1");
            try {
                conn.rollback();
                System.err.println("Transaction is rolled back!");
            } catch (SQLException ex1) {                
                System.err.println(ex1.getMessage());
            }
        }
        
        return tempList;       
    }
    
    public void CloseDB() {
        try {
            if (!(rs == null) || rs.isClosed())
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
