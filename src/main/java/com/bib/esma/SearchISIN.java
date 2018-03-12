package com.bib.esma;

import org.apache.log4j.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;


public class SearchISIN {
    private static final Logger logger = Logger.getLogger(SearchISIN.class);
    private static String fileType;

    public void parseXml (UrlList urlList) {
        String xmlInFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml();
        setFileType(urlList);
        try {
            File inFile = new File(xmlInFilePath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLESMAParser saxp = new XMLESMAParser(this);

            parser.parse(inFile, saxp);
            boolean delete = inFile.delete();
            if (!delete) {
                logger.error("Failed to delete file "+ xmlInFilePath);
            }
        } catch (Exception e) {
            logger.error("Parse failed");
        }
    }
    public String getFileType () {
        return fileType;
    }
    private static void setFileType (UrlList urlList){
       fileType = urlList.getFileType();
    }
}
