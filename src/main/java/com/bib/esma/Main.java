package com.bib.esma;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;



public class Main {
    private static final String DAY_START = "T00:00:00Z";
    private static final String DAY_END = "T23:59:59Z";

    private static String ERR_NO_ARGS = "No arguments found: expected <start date>#<end date>";
    private static String PATH_FOR_FILES = "C:\\Users\\JavaCourses1\\Java\\esma";
    private static String FILES_TYPE = "DLTINS";
    private static String startDate;
    private static String endDate;
    private static String url;
    private static List<UrlList> linksArray= new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Main.class);
    //https://www.mkyong.com/logging/log4j-hello-world-example/
    //maven repackage dependency jar
    public static void main(String[] args){
        System.out.println("Program started");
        if (args.length < 1) {
            System.out.println(ERR_NO_ARGS);
        } else {
            new Main().getFiles(args[0]);
        }
    }

    public static void getFiles(String inputString) {
        String type = "";
        if (inputString == null) {
            System.out.println(ERR_NO_ARGS);
        } else {
            String[] input = inputString.split("#");
            if (input.length < 2) {
                System.out.println(ERR_NO_ARGS);
            } else {
                startDate = buildDate(input[0],DAY_START);
                endDate = buildDate(input[1],DAY_END);
                logger.info("Start date: "+startDate+" End date: "+endDate);
                if (input.length == 3 && input[2] != null) {
                    FILES_TYPE = input[2];
                }
                if (input.length == 4 && input[3] != null) {
                    PATH_FOR_FILES = input[3];
                }
                url = buildUrl();
                System.out.println(url);
                try{
                    String response = makeRequest();
                    getLinksbyJson(response,FILES_TYPE);
                    if (!linksArray.isEmpty()) {
                        for (UrlList urlList : linksArray) {
                            //Thread thread = new Thread(getFiles::downloadFiles(arr.getFileUrl(),arr.getFileName()));
                            //thread.start();
                            downloadFiles(urlList);
                            System.out.println("XML "+urlList.getFileXml());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private static void downloadFiles (UrlList urlList) {
        DownloadFromHttp getFile = new DownloadFromHttp();
        UnpackZip zipFile = new UnpackZip();
        XMLESMAParser xmlFile = new XMLESMAParser();
        try {
            getFile.downloadFile(urlList);
            zipFile.unZipIt(urlList);
            xmlFile.processEsmaXml(urlList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }  catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    private static void getLinksbyJson(String jsonText, String type) {
        JSONObject jsonObj = new JSONObject(jsonText);
        JSONObject jsonResp = jsonObj.getJSONObject("response");
        int jsonArrayNum = jsonResp.getInt("numFound");
        System.out.println("Number of elements "+jsonArrayNum);
        JSONArray jsonArray = jsonResp.getJSONArray("docs");
        for(int i = 0; i < jsonArray.length(); i++) {
            UrlList filesList = new UrlList();
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            String fileType = jsonElement.getString("file_type");
            if(fileType.equals(type)) {
                System.out.printf("Download link: %s file name: %s \n",jsonElement.getString("download_link"),jsonElement.getString("file_name"));
                filesList.setFileName(jsonElement.getString("file_name"));
                filesList.setFileUrl(jsonElement.getString("download_link"));
                filesList.setFilePath(PATH_FOR_FILES);
                linksArray.add(filesList);
            }
        }
    }



    private static String makeRequest() throws Exception  {
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
}
