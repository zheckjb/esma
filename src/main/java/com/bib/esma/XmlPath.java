package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class XmlPath {
    private static final Logger logger = Logger.getLogger(XmlPath.class);
    private List<String> xmlPathArr = new ArrayList<>();
    private String xmlPath;
    private String searchFULPath;
    private String searchDLTNew;
    private String searchDLTEnd;
    private String searchDLTUpd;
    private String nodeIsin;
    private String nodeTicker;
    private String fileType;

    public XmlPath () {
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("esma.properties")) {
            props.load(in);
            searchFULPath = props.getProperty("search.FULINS");
            searchDLTNew = props.getProperty("search.DLTINS.new");
            searchDLTUpd = props.getProperty("search.DLTINS.upd");
            searchDLTEnd= props.getProperty("search.DLTINS.end");
        } catch (IOException e) {
            logger.error("Unable to read config.properties");
        }
    }

    public void remElement(String value) {
        int i = xmlPathArr.size();
        if (i > 0) {
            xmlPathArr.remove(i-1);
        }
        xmlPath = buildPath();
    }

    public void addElement(String value) {
        xmlPathArr.add(value);
        xmlPath = buildPath();
    }

    public boolean compareFUL(){
        return searchFULPath.equals(xmlPath);
    }

    public boolean compareDLTnew(){
        return searchDLTNew.equals(xmlPath);
    }

    public boolean compareDLTend() {
        return searchDLTEnd.equals(xmlPath);
    }

    public boolean compareDLTupd() {
        return searchDLTUpd.equals(xmlPath);
    }

    public String buildPath() {
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

    @Override
    public String toString(){
        return buildPath();
    }
}
