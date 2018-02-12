/*
 * Summer 2016
 * Object-Oriented Programming -course
 * XML parser -class
 */

package timotei;

import java.io.IOException;
import java.io.StringReader;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.ComboBox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Roope Luukkainen
 */
public class XMLparse {
    private Document doc;
    private String city, name, availability, address, code;
    private float lat, lng;

    public XMLparse() {}
    
    public void XMLparser(String XMLcontent) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder;
        
            dbBuilder = dbFactory.newDocumentBuilder();
        
        
            doc = dbBuilder.parse(new InputSource(new StringReader(XMLcontent)));
            doc.getDocumentElement().normalize();
            
            parseSmartPostData();
        
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLparse.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Document builder error! :(");
        }
    }

    private void parseSmartPostData() {
        /* Parses XML content by finding smartPost infos by tagnames below.
        Calls database Handler to add cities and smartPosts to database. */
        NodeList nodes = doc.getElementsByTagName("place");
        
        DBHandler dbh = DBHandler.getInstance();
        
        ComboBox<String> Combo = null;
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element)node;
            
            code = getValue("code", e);
            city = getValue("city", e).toUpperCase();
            address = getValue("address", e);
            availability = getValue("availability", e);
            name = getValue("postoffice", e);
            lat = Float.parseFloat(getValue("lat", e));
            lng = Float.parseFloat(getValue("lng", e));
            
//            System.out.println(code +" "+ city +" "+ address);
            dbh.writeCityTodb(code, city);
            
            dbh.addSmartPostTodb(name, availability, address, code, lat, lng, null);            
        }
    }
    
    private String getValue(String tag, Element e) {
        return (e.getElementsByTagName(tag).item(0)).getTextContent();
    }
}
