package com.bib.esma;

import org.apache.log4j.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;


public class SearchISIN {
    private static final Logger logger = Logger.getLogger(SearchISIN.class);

    public void parseXml (UrlList urlList) {
        String xmlInFilePath = urlList.getFilePath() + File.separator + urlList.getFileXml();

        try {
            File inFile = new File(xmlInFilePath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLESMAParser saxp = new XMLESMAParser();

            parser.parse(inFile, saxp);
        } catch (Exception e) {
            logger.error("Parse failed");
        }
    }
    public String getFileType (UrlList url) {
        return url.getFileType();
    }
}
