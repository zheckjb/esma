package com.bib.esma;


import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLESMAParser extends DefaultHandler{
    private static final Logger logger = Logger.getLogger(XMLESMAParser.class);
    private static XmlPath xmlPath = new XmlPath("DLTINS");
    private static ISINList isinList = new ISINList();
    private static Boolean flagISIN = false;
    private static String fileType;


    @Override
    public void startDocument() throws SAXException {
        logger.info("Start parse XML...");


    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        xmlPath.addElement(qName);
        if (xmlPath.compare(xmlPath.toString())){
            flagISIN = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (flagISIN) {
            isinList.addIsin(new String(ch, start, length));
            flagISIN = false;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
       xmlPath.remElement(qName);
    }

    @Override
    public void endDocument() {
        isinList.saveIsinFile();
    }
}
