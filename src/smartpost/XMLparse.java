/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package smartpost;

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
 * @author k9751
 */
public class XMLparse {
    private Document doc;
    private String city = null;

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
        NodeList nodes = doc.getElementsByTagName("place");
        
        dbHandler dbh = dbHandler.getInstance();
        
        ComboBox<String> Combo = null;
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Element e = (Element)node;
            
            city = getValue("city", e).toUpperCase();
            
            dbh.writeSmartPostTodb(getValue("postoffice", e), getValue("availability", e),
            getValue("address", e), getValue("code", e),city ,
            getValue("lat", e), getValue("lng", e));
            
            Combo = FXMLDocumentController.getSmartPostCombo();
            
            if (!(Combo.getItems().contains(city.toUpperCase()))) {
                    Combo.getItems().add(city.toUpperCase());
            }
            
//            System.out.printf("%s %s\n"
//                    + "%s %s %s\n"
//                    + "lat: %s lng: %s\n\n", getValue("postoffice", e), getValue("availability", e),
//            getValue("address", e), getValue("code", e), getValue("city", e),
//            getValue("lat", e), getValue("lng", e));
        }
    }
    
    private String getValue(String tag, Element e) {
        return (e.getElementsByTagName(tag).item(0)).getTextContent();
    }
}
