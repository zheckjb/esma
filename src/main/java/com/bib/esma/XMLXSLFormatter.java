package com.bib.esma;

import org.apache.log4j.Logger;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class XMLXSLFormatter {
    private static String TMP_PFX = "TMP";
    private static String ISIN_PFX = "ISIN";
    private static final Logger logger = Logger.getLogger(XMLXSLFormatter.class);


    public void transformXml (UrlList urlList)  {
        ClassLoader classLoader = getClass().getClassLoader();
        Source xslRemAttr = new StreamSource(new File(classLoader.getResource("rem_attrib.xsl").getFile()));
        String xslName = urlList.getFileType().toLowerCase()+".xsl'";
        Source xslMakeList = new StreamSource(new File(classLoader.getResource(xslName).getFile()));

        String xmlInFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml();
        String xmlTmpFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml().replaceAll(urlList.getFileType(),TMP_PFX);
        String xmlOutFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml().replaceAll(urlList.getFileType(),ISIN_PFX);
        try {
            logger.info("Process XSL transformation from rem_attrib.xsl on " + xmlInFilePath);
            doTransform(xmlInFilePath,xmlTmpFilePath,xslRemAttr);
        } catch (TransformerException e) {
            logger.error("Unable to process XSL transformation");
        }

        try {
            logger.info("Process XSL transformation from dltins.xsl on " + xmlTmpFilePath);
            doTransform(xmlTmpFilePath,xmlOutFilePath,xslMakeList);
        } catch (TransformerException e) {
            logger.error("Unable to process XSL transformation");
        }

    }

    private void doTransform(String inFileName,String outFileName,Source xslFileSrc) throws TransformerException {
        //Open file with File, as we need to delete source file after
        File xmlInFile = new File(inFileName);
        Source xmlInSource = new StreamSource(xmlInFile);
        TransformerFactory xmlFactory = TransformerFactory.newInstance();
        Transformer xmlTrf = xmlFactory.newTransformer(xslFileSrc);
        //Process attributes removing
        xmlTrf.transform(xmlInSource,new StreamResult(outFileName));
        //Remove source file
        logger.info("Removing initial xml file: "+ outFileName);
        boolean delete = xmlInFile.delete();
        if (!delete) {
            logger.error("Failed to delete file "+ outFileName);
        }
    }
}
