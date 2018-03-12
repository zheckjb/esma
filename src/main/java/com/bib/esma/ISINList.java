package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ISINList {
    private static final Logger logger = Logger.getLogger(ISINList.class);
    private static String isinFile;
    //private static List<String> isinList =  new ArrayList<>();
    private static HashSet<String> isinSet = new HashSet<String>();
    public ISINList (){
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("config.properties")) {
            props.load(in);
            String isinFileName = props.getProperty("isin.file");
            String isinFilePath = props.getProperty("isin.path");
            isinFile = isinFilePath + File.separator + isinFileName;
            logger.info("ISIN file: "+isinFile);
        } catch (IOException e) {
            logger.error("Unable to read config.properties");
        }
    }

    public void addIsin(String value) {
        isinSet.add(value);
    }

    public void saveIsinFile() {
        logger.info("ISIN Records found: "+isinSet.size());

        try {
            File file = new File(isinFile);
            FileWriter fileHandle = new FileWriter(file);
            for (Object value : isinSet){
                fileHandle.append(String.format("%s \r\n", value));
            }
            fileHandle.flush();
            fileHandle.close();
            logger.info("File saved");
        } catch ( IOException e) {
            logger.error("Unable to write to");
        }

    }

}
