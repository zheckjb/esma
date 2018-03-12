package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ISINList {
    private static final Logger logger = Logger.getLogger(ISINList.class);
    private static String isinFile;
    private static List<String> isinList =  new ArrayList<>();

    public ISINList (){
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(classLoader.getResource("config.properties").getFile()));
            String isinFileName = props.getProperty("isin.file");
            String isinFilePath = props.getProperty("isin.path");
            isinFile = isinFilePath + File.separator + isinFileName;
            logger.info("ISIN file: "+isinFile);
        } catch (IOException e) {
            logger.error("Unable to read config.properties");
        }
    }

    public void addIsin(String value) {
        isinList.add(value);
    }

    public void saveIsinFile() {
        logger.info("ISIN Records found: "+isinList.size());
    }

}
