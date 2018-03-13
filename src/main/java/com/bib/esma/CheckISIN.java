package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class CheckISIN {
    private static final Logger logger = Logger.getLogger(CheckISIN.class);
    private static String isinInFile;
    private static String isinOutFile;
    private static Map<String,ISINList> isinListMap = new HashMap<>();


    public CheckISIN(){
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("config.properties")) {
            props.load(in);
            String isinFilePath = props.getProperty("isin.path");
            String isinInFileName = props.getProperty("isin.in.file");
            String isinOutFileName = props.getProperty("isin.out.file");
            isinInFile = isinFilePath + File.separator + isinInFileName;
            isinOutFile = isinFilePath + File.separator + isinOutFileName;
            logger.info("ISIN In file: "+isinInFile);
            logger.info("ISIN Out file: "+isinOutFile);
        } catch (IOException e) {
            logger.error("Unable to read config.properties");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(isinInFile));
            String fline;
            while ((fline = reader.readLine()) != null) {
                String[] value = fline.split("#");
                ISINList sm = new ISINList();
                if (isinListMap.containsKey(value[1])) {
                    logger.info("Duplicate ISIN record found:  " + value[0] + " " + value[1]);
                    sm = isinListMap.get(value[1]);
                    sm.addSmId(value[0]);
                } else {
                sm.addSmId(value[0]);
                sm.setSmTicker(value[1]);
                if (value[3].equalsIgnoreCase("yes")) {
                    sm.setSmStatus(true);
                } else {
                    sm.setSmStatus(false);
                }
                isinListMap.put(value[1],sm);
                }
            }
            logger.info("ISIN List loaded: "+isinListMap.size());
        } catch (IOException e) {
            logger.error("Unable to read from: "+isinInFile);
        }
    }

    public void addIsin(String value) {
        if (isinListMap.containsKey(value)) {
            ISINList sm = isinListMap.get(value);
            sm.setSmStatus(true);
            isinListMap.put(value,sm);
            logger.debug(String.format("SM %s with ISIN %s updated with YES",sm.getSmId(0),value));
        }
    }



    public void remIsin(String value) {
        if (isinListMap.containsKey(value)) {
            ISINList sm = isinListMap.get(value);
            sm.setSmStatus(false);
            isinListMap.put(value,sm);
            logger.debug(String.format("SM %s with ISIN %s updated with NO",sm.getSmId(0),value));
        }
    }

    public boolean matchIsin (String value){
        return isinListMap.containsKey(value);
    }

    public boolean needTicker(String value) {
        return isinListMap.containsKey(value) && isinListMap.get(value).isSmShare();
    }

    public void saveIsinList() {
        logger.info("ISIN Records found: "+isinListMap.size());
        try {
            File file = new File(isinOutFile);
            FileWriter fileHandle = new FileWriter(file);
            for (Map.Entry<String, ISINList> value : isinListMap.entrySet()) {
                if (value.getValue().smIdLen() > 1) {
                    for (int i = 0; i < value.getValue().smIdLen(); i ++) {
                        fileHandle.append(String.format("%s#%s#%s \r\n", value.getValue().getSmId(i), value.getKey(), value.getValue().getSmStatus()));
                    }
                } else {
                    fileHandle.append(String.format("%s#%s#%s \r\n", value.getValue().getSmId(0), value.getKey(), value.getValue().getSmStatus()));
                }
            }
            fileHandle.flush();
            fileHandle.close();
        } catch ( IOException e) {
            logger.error("Unable to write to "+isinOutFile);
        }

    }


}
