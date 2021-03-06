/*
 * Summer 2016
 * Object-Oriented Programming -course
 * Database handler -class
 */
package timotei;

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
 * @author Roope Luukkainen
 */
public class DBHandler {

    static private DBHandler dbh = null;

    private ArrayList tempList = null, innerList = null;

    private final String protocol = "jdbc:sqlite:", dbName = "harkkakanta.db";

    Connection conn = null;
    PreparedStatement query;
    ResultSet rs = null;

    private DBHandler() {
        tempList = new ArrayList();
        innerList = new ArrayList();

        String sDriverName = "org.sqlite.JDBC";
        try {
            Class.forName(sDriverName);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ClassNotFound EXCEPTION!!!!");
            FXMLDocumentController.writeLog("##### ClassNotFound EXCEPTION #####");

        }

    }

    static public DBHandler getInstance() {
        /* Returns instance to dbHandler object. Uses singleton design pattern */

        if (dbh == null) {
            dbh = new DBHandler();
        }
        return dbh;
    }
    
    public void writeCityTodb(String pcode, String city) {
        /*Adds city to database to table "city", postalcode is primary key*/
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
            System.err.println("#####" + ex.getMessage() + "#####");
            try {
                conn.rollback();
                System.err.println("Adding city transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Adding city "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addSmartPostTodb(String postOffice, String avaibility, String address,
            String postalCode, float lat, float lng, String color) {
        /* Adds all SmartPosts to database table "smartPost" */

        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);
            
            if (color == null) {
            query = conn.prepareStatement("INSERT INTO smartPost"
                    + "('smartPostName', 'info', "
                    + "'streetAddress', 'postalCode', 'latitude', 'longitude')"
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            
            } else {
                query = conn.prepareStatement("INSERT INTO smartPost"
                        + "('smartPostName', 'info',"
                        + "'streetAddress', 'postalCode', 'latitude', 'longitude',"
                        + "colorOnMap)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)");
                
                query.setString(7, color);
                FXMLDocumentController.writeLog("Lisätään uusi kohde " + 
                        postOffice + " tietokantaan ja kartalle.");
            }
            
            query.setString(1, postOffice);
            query.setString(2, avaibility);//info text
            query.setString(3, address);
            query.setString(4, postalCode);
            query.setFloat(5, lat);
            query.setFloat(6, lng);


            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Adding SmartPost transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Adding SmartPost "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addBreakTypeToDB(String t, int p) {
    /* Add breakType to database to table "breakTypes" if breakType is new */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO breakTypes"
                    + "('btype', 'percent') VALUES (?, ?)");

            query.setString(1, t.toLowerCase());
            query.setInt(2, p);
            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add breakType transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add breakType "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addPackageDeliveryToDB (int deliveryType, int fromSP, int toSP) {
        /* Adds packageDelivery to database table "packageDelivery" */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO packageDelivery"
                    + "('deliveryType', 'fromSp', 'toSP') VALUES (?, ?, ?)");

            query.setInt(1, deliveryType);
            query.setInt(2, fromSP);
            query.setInt(3, toSP);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add packageDelivery transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add packageDelivery "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addPackageToDB(int deliveryID, double size) {
        /* Add packages to database table "package" */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO package"
                    + "('deliveryID', 'packageSize') VALUES (?, ?)");

            query.setInt(1, deliveryID);
            query.setDouble(2, size);

            query.execute();

            conn.commit();
            FXMLDocumentController.writeLog("Uusi paketti lisätty.");

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add package transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add package "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }    

    public void addItemTypeToDB(String N, double S, int W, boolean B, int T) {
        /* Add itemType to database table "itemType" */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO itemType"
                    + "('itemName', 'itemSize', 'itemWeight', 'breakable', 'breakType')"
                    + "VALUES (?, ?, ?, ?, ?)");

            query.setString(1, N);
            query.setDouble(2, S);
            query.setInt(3, W);
            query.setBoolean(4, B);
            query.setInt(5, T);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add itemType transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add itemType "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
                
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addItemToDB(int T, int P) {
        /* Add all items which are in package to database table "itemInPackage" */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO itemInPackage"
                    + "('itemTypeID', 'packageID')"
                    + "VALUES (?, ?)");

            query.setInt(1, T);
            query.setInt(2, P);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add item transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add item "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void addDeliversToDB(int dID, int tID, double D) {
        /* Add TimoteiManID & packageDeliveryID pair to database table "delivers" */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            query = conn.prepareStatement("INSERT INTO delivers"
                    + "('deliveryID', 'TIMOTEI_ID', 'distance')"
                    + "VALUES (?, ?, ?)");

            query.setInt(1, dID);
            query.setInt(2, tID);
            query.setDouble(3, D);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Add delivers transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Add delivers "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }

    public ArrayList readFromdb(String table, ArrayList<String> attrList, 
            ArrayList additionalTerms) {
    /* Read given attributes from given table. Returns ArrayList of values. If 
        there is multiple attributes given, each database row is ArrayList which 
        are then stored to returning ArrayList. */

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
                
                if (additionalTerms.get(1) != null) {
                    query.setObject(1, additionalTerms.get(1));
                }
                
                if (additionalTerms.size() == 3)
                    query.setObject(2, additionalTerms.get(2));
                                                            
            } else {              
                query = conn.prepareStatement(queryS);  
            }
                                                            
            rs = query.executeQuery();

            if (attrList.isEmpty()) {
                while (rs.next()) {
                    tempList.add(rs.getObject(attr));
                }

            } else {
                int i;
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
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("DB reading error.");
            FXMLDocumentController.writeLog("##### Database reading ERROR #####");
            try {
                conn.rollback();
                System.err.println("Reading from DB transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Reading from database"
                        + "transaction is rolled back! #####");
            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");

            } finally {
                innerList.clear();
                CloseDB();

            }
        } catch (IndexOutOfBoundsException ex) {}
        return tempList;
    }
    
    public void updateDB(String table, String attr, Object newValue,
            String whereAttr, Object whereValue) {
        /*Update given attribute in given table with new given value
        using where attribute and value to find right attribute.*/
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            String queryS = "UPDATE " + table + " SET " + attr + 
                    " = ? WHERE " + whereAttr + " = ?";
            query = conn.prepareStatement(queryS);

            query.setObject(1, newValue);
            query.setObject(2, whereValue);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Update transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Update "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
            }

        } finally {
            CloseDB();
        }
    }
    
    public void deleteFromDB(String table, String whereAttr, Object whereValue) {
        /* Delete given attribute from database based on whereValue from given table. */
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);

            String queryS = "DELETE FROM " + table + 
                    " WHERE " + whereAttr + " = ?";
            query = conn.prepareStatement(queryS);

            query.setObject(1, whereValue);

            query.execute();

            conn.commit();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            try {
                conn.rollback();
                System.err.println("Delete transaction is rolled back!");
                FXMLDocumentController.writeLog("##### ERROR: Delete "
                        + "transaction is rolled back! #####");

            } catch (SQLException ex1) {
                System.err.println(ex1.getMessage());
                FXMLDocumentController.writeLog("#####" + ex1.getMessage() + "#####");
                
            }

        } finally {
            CloseDB();
        }
    }

    public void CloseDB() {
        /*Closes all streams and connections.*/
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
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            FXMLDocumentController.writeLog("#####" + ex.getMessage() + "#####");
        }
    }

    void clearDB() {
        /* Clears all tables from database excluding views and ones specified below.*/
        try {
            conn = DriverManager.getConnection(protocol + dbName);
            conn.setAutoCommit(false);
            
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, null, "%", null);
            
            String table = null;
            while (rs.next()) {
                table = rs.getString(3);
                System.out.println(table);
            // Some tables remain same all the time they don't need to be cleared.
                if (table.contains("View") || table.contains("deliveryClass") || 
                        table.contains("breakTypes") || table.contains("TIMOTEI_man")
                        || table.contains("stressActions")) 

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
            FXMLDocumentController.writeLog("#####" + ex.getMessage() + "#####");
        }
    }
}
