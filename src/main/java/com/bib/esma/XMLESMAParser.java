package com.bib.esma;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.File;

import java.io.IOException;

public class XMLESMAParser {
    private static XPathExpression xmlRefData;
    private static XPathExpression isinExpr;
    private static Source xslRemAttr;
    private static Source xslMakeList;
    public  XMLESMAParser() {
        ClassLoader classLoader = getClass().getClassLoader();
        xslRemAttr = new StreamSource(new File(classLoader.getResource("rem_attrib.xsl").getFile()));
        xslMakeList = new StreamSource(new File(classLoader.getResource("keep_isin2.xsl").getFile()));

    }


    public static void processEsmaXml (UrlList urlList)
            throws ParserConfigurationException,TransformerConfigurationException,TransformerException,
            IOException,  XPathExpressionException, org.xml.sax.SAXException {
        String xmlInFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml();
        String xmlOutFilePath = urlList.getFilePath() + File.separator + urlList.getFileXmlNoAttr();
        File xmlInFile = new File(xmlInFilePath);
        System.out.println("Process XML file " + xmlInFilePath);
        //attributes cleanup
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslRemAttr);
        Source text = new StreamSource(xmlInFile);
        transformer.transform(text, new StreamResult(xmlOutFilePath));
        xmlInFile.delete();
        //secod coversion
        String isinFilePath = urlList.getFilePath() + File.separator + urlList.getFileXmlNoAttr().replaceAll("DLTINS","ISIN");
        Transformer trans_isin = factory.newTransformer(xslMakeList);
        Source isinlist = new StreamSource(xmlOutFilePath);
        trans_isin.transform(isinlist,new StreamResult(isinFilePath));

        // XPath preparation
    //    XPath xpath = XPathFactory.newInstance().newXPath();
        //xmlRefData = xpath.compile("/BizData/Pyld/Document/FinInstrmRptgRefDataRpt/RefData");
        //isinExpr = xpath.compile("./FinInstrmGnlAttrbts/Id/text()");
    //    xmlRefData = xpath.compile("/BizData/Pyld/Document/FinInstrmRptgRefDataDltaRpt/FinInstrm");
    //    isinExpr = xpath.compile("./ModfdRcrd/FinInstrmGnlAttrbts/Id/text()");

        //Reading XML document
    //    File inXmlFile = new File(xmlOutFilePath);
    //    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    //    Document document = builder.parse(inXmlFile);
    //    System.out.println("Document parsed");
     //   processDocument(document);

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
