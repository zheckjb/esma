package com.bib.esma;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Main {
    private static String startDate;
    private static String endDate;
    private static String url;

    public static void main(String[] args){
        System.out.println("Program started");
        new Main().getFiles(args[0]);
    }

    public static void getFiles(String inputString) {
        System.out.println(inputString);
        String[] input = inputString.split("#");
        startDate = setStartDate(input[0]);
        endDate = setEndDate(input[1]);
        System.out.println(startDate);
        System.out.println(endDate);
        url = buildUrl();
        System.out.println(url);
        try{
            makeRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeRequest() throws Exception  {
        URL reqhttp = new URL(url);
        HttpURLConnection con = (HttpURLConnection) reqhttp.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        System.out.println(in.toString());
    }

    private static String setStartDate(String indate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(indate,formatter);
        return String.format("%sT00:00:00Z",date.toString());
    }
    private static String setEndDate(String indate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(indate,formatter);
        return String.format("%sT23:59:00Z",date.toString());
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
