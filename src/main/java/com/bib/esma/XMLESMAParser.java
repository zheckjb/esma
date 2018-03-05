package com.bib.esma;

import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class XMLESMAParser {
    private static XPathExpression xmlRefData;
    private static XPathExpression isinExpr;
    private static ClassLoader classLoader;
    public XMLESMAParser() {
       classLoader = getClass().getClassLoader();

    }


    public static void processEsmaXml (UrlList urlList)
            throws ParserConfigurationException,TransformerConfigurationException,TransformerException,
            IOException, SAXException, XPathExpressionException, org.xml.sax.SAXException {
        String xmlFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml();
        System.out.println("Process XML file " + xmlFilePath);

        //attributes cleanup

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslRemAttr = new StreamSource(new File(classLoader.getResource("rem_attrib.xsl").getFile()));
        Transformer transformer = factory.newTransformer(xslRemAttr);
        Source text = new StreamSource(new File(xmlFilePath));
        transformer.transform(text, new StreamResult("abc.xml"));
        urlList.setFileXmlNoAttr("abc.xml");
        String txmlFile = urlList.getFilePath() + File.separator + urlList.getFileXmlNoAttr();
        // XPath preparation
        XPath xpath = XPathFactory.newInstance().newXPath();
        //xmlRefData = xpath.compile("/BizData/Pyld/Document/FinInstrmRptgRefDataRpt/RefData");
        //isinExpr = xpath.compile("./FinInstrmGnlAttrbts/Id/text()");
        xmlRefData = xpath.compile("/BizData/Pyld/Document/FinInstrmRptgRefDataDltaRpt/FinInstrm");
        isinExpr = xpath.compile("./ModfdRcrd/FinInstrmGnlAttrbts/Id/text()");

        //Reading XML document
        File inXmlFile = new File(txmlFile);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inXmlFile);
        //StreamSource stylesource = new StreamSource(stylesheet);
        //Transformer transformer = Factory.newTransformer(stylesource);
        System.out.println("Document parsed");
        processDocument(document);
        
    }
    private static void processDocument(Document document) throws XPathExpressionException {
        NodeList docNodeList = (NodeList) xmlRefData.evaluate(document, XPathConstants.NODESET);
        int r = docNodeList.getLength();
        System.out.println(r);
        for (int i = 0; i < docNodeList.getLength(); i++) {
            Node docNode = docNodeList.item(i);
            processDocNode(docNode);
        }
    }

    private static void processDocNode(Node docNode) throws XPathExpressionException {
        String isinNr = (String) isinExpr.evaluate(docNode, XPathConstants.STRING);
        System.out.println("Isin " + isinNr);
    }

}
