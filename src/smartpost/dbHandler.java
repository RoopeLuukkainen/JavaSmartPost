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

    private ArrayList tempList = null, innerList = null;

    private final String protocol = "jdbc:sqlite:", dbName = "harkkakanta.db";

    Connection conn = null;
    PreparedStatement query;
    ResultSet rs = null;

    private dbHandler() {
        tempList = new ArrayList();
        innerList = new ArrayList();

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

        if (dbh == null) {
            dbh = new dbHandler();
        }
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
    
    public void writeCityTodb(String pcode, String city) {
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO city" +
                    "('postalCode', 'cityName')"+ "VALUES (?, ?)");

            query.setString(1, pcode);
            query.setString(2, city);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Adding city transaction is rolled back!");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addSmartPostTodb(String postOffice, String avaibility, String address,
            String postalCode, String lat, String lng) {
        /* Adds all SmartPosts to database table "smartPost" */

        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO smartPost"
                    + "('smartPostName', 'openingTime', 'closingTime', "
                    + "'streetAddress', 'postalCode', 'latitude', 'longitude')"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");

            query.setString(1, postOffice);
            query.setString(2, avaibility);// MUUUUTAAA
            query.setString(3, avaibility);// TO DATE MUOTOON!!
            query.setString(4, address);
            query.setString(5, postalCode);
            query.setFloat(6, Float.parseFloat(lat));
            query.setFloat(7, Float.parseFloat(lng));

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Adding SmartPost transaction is rolled back!");

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

    public ArrayList readFromdb(String table, ArrayList<String> attrList, ArrayList additionalTerms) {
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
            queryS += String.format(" FROM %s", table);

            if (additionalTerms != null) {
                queryS += additionalTerms.get(0);  
                                                            
                query = conn.prepareStatement(queryS);
                query.setObject(1, additionalTerms.get(1));
                                                            System.out.println("IF: " + queryS);
            } else {              
                query = conn.prepareStatement(queryS);  
                                                            System.out.println("ELSE: " +queryS);
            }

            rs = query.executeQuery();

            if (attrList.isEmpty()) {
                while (rs.next()) {
                    tempList.add(rs.getObject(attr));
                                                            //System.out.println(rs.getObject(attr));
                }

            } else {
                int i = 0;
                attrList.add(0, attr); //Adding attr back makes Array handling easier.                
                while (rs.next()) {
                    innerList.clear();
                    i = 0;
                    for (String S : attrList) {
                        innerList.add(i++, rs.getObject(S));
                    }
                    tempList.add(innerList.clone());
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
                innerList.clear();
                CloseDB();

            }
        }
        return tempList;
    }

    public void CloseDB() {
        try {
            if (!(rs == null)) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }

            if (!query.isClosed()) {
                query.close();
            }

            if (!conn.isClosed()) {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(dbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearDB() {
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);
            
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, null, "%", null);
            
            String table = null;
            while (rs.next()) {
                table = rs.getString(3);
                System.out.println(table);
                if (table.contains("View"))
                    continue;
                query = conn.prepareStatement("DELETE FROM " + rs.getString(3) + ";");
                
                query.execute();
                query.close();
            }
            
            rs.close();
            
            conn.commit();
            conn.close();
            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
