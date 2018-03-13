package com.bib.esma;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Properties;


public class Esma {
    private static final String DAY_START = "T00:00:00Z";
    private static final String DAY_END = "T23:59:59Z";
    private static final String ERR_NO_ARGS = "No arguments found: expected <start date>#<end date>";
    private static final String MSG_END =  "Finish";
    private static final String PROC_TYPE = "SEARCH";
    private static String workingPath;
    private static String searchFileType;
    private static String processType;
    private static String startDate;
    private static String endDate;
    private static String url;
    private static CheckISIN isinList;
    private static List<UrlList> linksArray= new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Esma.class);

    private Esma(){
        //loadProps();
    }

    public static void main(String[] args){
        new Esma().getFiles(args[0]);
    }

    public String getFiles(String inputString) {
        loadProps();
        logger.info("Program started");
        String[] input = inputString.split("#");
            if (input.length < 2) {
                logger.error(ERR_NO_ARGS);
                return ERR_NO_ARGS;
            } else {
                startDate = buildDate(input[0],DAY_START);
                endDate = buildDate(input[1],DAY_END);
                logger.info("Input parameters: "+inputString);
                logger.info("Start date: "+startDate+" End date: "+endDate);
                if (input.length == 3 && input[2] != null) {
                    searchFileType = input[2].toUpperCase();
                }
                if(input.length == 4 && input[3] != null){
                    processType = input[3].toUpperCase();
                }
                url = buildUrl();
                logger.info(url);
                try{
                    String response = makeRequest();
                    getLinksbyJson(response,searchFileType);
                    if (!linksArray.isEmpty()) {
                        isinList = new CheckISIN();
                        for (UrlList urlList : linksArray) {
                            //Thread thread = new Thread(getFiles::processFile(urlList));
                            //thread.start();
                            processFile(urlList);
                        }
                        isinList.saveIsinList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MSG_END;
            }


    }

    public static void testXmlSearch () {
            UrlList urlList = new UrlList();
            urlList.setFilePath("C:\\esma");
            urlList.setFileXml("DLTINS_test.xml");
            SearchISIN searchISIN = new SearchISIN();
            CheckISIN isinList = new CheckISIN();
            searchISIN.parseXml(urlList,isinList);
    }

    private static void processFile (UrlList urlList) {
        DownloadFromHttp getFile = new DownloadFromHttp();
        UnpackZip zipFile = new UnpackZip();
        XMLXSLFormatter xmlFile = new XMLXSLFormatter();
        SearchISIN isinSearch = new SearchISIN();
        try {
            getFile.downloadFile(urlList);
            zipFile.unZipIt(urlList);
            if (processType == null || processType.equals(PROC_TYPE) ) {
                isinSearch.parseXml(urlList,isinList);
            } else {
                xmlFile.transformXml(urlList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void getLinksbyJson(String jsonText, String type) {
        JSONObject jsonObj = new JSONObject(jsonText);
        JSONObject jsonResp = jsonObj.getJSONObject("response");
        int jsonArrayNum = jsonResp.getInt("numFound");
        logger.info("Number of elements received: "+jsonArrayNum);
        JSONArray jsonArray = jsonResp.getJSONArray("docs");
        for(int i = 0; i < jsonArray.length(); i++) {
            UrlList filesList = new UrlList();
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            String fileType = jsonElement.getString("file_type");
            if(fileType.equals(type)) {
                logger.info(String.format("File name: %s from link: %s",jsonElement.getString("file_name"),jsonElement.getString("download_link")));
                filesList.setFileName(jsonElement.getString("file_name"));
                filesList.setFileUrl(jsonElement.getString("download_link"));
                filesList.setFilePath(workingPath);
                filesList.setFileType(fileType);
                linksArray.add(filesList);
            }
        }
    }



    private static String makeRequest() throws Exception  {
        System.setProperty("java.net.useSystemProxies", "true");
        StringBuilder response = new StringBuilder();
        URL reqhttp = new URL(url);
        HttpURLConnection con = (HttpURLConnection) reqhttp.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }else {
            throw new RuntimeException("Failed to download");
        }
        return response.toString();
    }

    private static String buildDate(String indate, String addon) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(indate,formatter);
        return String.format("%s%s",date.toString(),addon);
    }

    private static String buildUrl() {
        StringBuilder http = new StringBuilder();
        http.append("https://registers.esma.europa.eu/solr/esma_registers_firds_files/select?");
        http.append("q=*&");
        http.append("fq=publication_date:%5B");
        http.append(startDate);
        http.append("%20TO%20");
        http.append(endDate);
        http.append("%5D&wt=json&indent=true&start=0&rows=100");

        return http.toString();

    }
    private void loadProps() {
        ClassLoader classLoader = getClass().getClassLoader();
        Properties props = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("config.properties")) {
            props.load(in);
            searchFileType = props.getProperty("default.type").toUpperCase();
            logger.info("Default type is "+searchFileType);
            workingPath = props.getProperty("working.path");
            logger.info("Working path: "+workingPath);
            String isinFileName = props.getProperty("isin.file");
            String isinFilePath = props.getProperty("isin.path");
            String isinFile = isinFilePath + File.separator + isinFileName;
            Path isinPath = Paths.get(isinFile);
            Files.deleteIfExists(isinPath);

        } catch (IOException e) {
            logger.error("Unable to read config.properties");
            e.printStackTrace();
        }
        //Just for info
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        logger.info("Current relative path is: " + s);
    }
}
