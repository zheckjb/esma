package com.bib.esma;


import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLESMAParser extends DefaultHandler{
    private static final Logger logger = Logger.getLogger(XMLESMAParser.class);
    private XmlPath xmlPath;
    private CheckISIN isinList;
    //private String fileType;
    private Boolean flagAddIsin = false;
    private Boolean flagRemIsin = false;

    public XMLESMAParser (CheckISIN value) {
        //logger.info("File type: "+value.getFileType());
        xmlPath = new XmlPath();
        isinList = value;
        //isinList = new CheckISIN();
        //fileType = value.getFileType();
    }

    @Override
    public void startDocument() throws SAXException {
        logger.info("Start parse XML...");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        xmlPath.addElement(qName);
        if (xmlPath.compareFUL() || xmlPath.compareDLTnew()){
            flagAddIsin = true;
        } else if (xmlPath.compareDLTend()) {
            flagRemIsin = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (flagAddIsin) {
            isinList.addIsin(new String(ch, start, length));
            flagAddIsin = false;
        }

        if (flagRemIsin) {
            isinList.remIsin(new String(ch, start, length));
            flagRemIsin = false;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
       xmlPath.remElement(qName);
    }

    @Override
    public void endDocument() {
        //isinList.saveIsinList();
    }
}
