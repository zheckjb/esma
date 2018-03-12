package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class XmlPath {
    private static List<String> xmlPathArr = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(XmlPath.class);
    private static String searchXmlPath;

    public XmlPath (String fileType) {
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("config.properties")) {
            props.load(in);
            String filePath = "search."+fileType.toUpperCase();

            searchXmlPath = props.getProperty(filePath);
            logger.info("XML search path: "+searchXmlPath);
        } catch (IOException e) {
            logger.error("Unable to read config.properties");
        }
    }

    public void remElement(String value) {
        int i = xmlPathArr.size();
        if (i > 0) {
            xmlPathArr.remove(i-1);
        }

    }

    public void addElement(String value) {
        xmlPathArr.add(value);
    }

    public boolean compare(String xmlPath) {
        return xmlPath.equals(searchXmlPath);
    }
    @Override
    public String toString(){
        String result = null;
        for (String value : xmlPathArr) {
            if (result != null) {
                result = String.format("%s/%s",result,value);
            } else {
                result = value;
            }
        }
        return result;
    }
}
