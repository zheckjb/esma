package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadFromHttp {
    private static final int BUFFER_SIZE = 4096;
    private static final Logger logger = Logger.getLogger(DownloadFromHttp.class);

    public void downloadFile(UrlList urlList) throws IOException {
        URL url = new URL(urlList.getFileUrl());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            //String saveFilePath = saveDir + File.separator + fileName;
            String saveFilePath = urlList.getFilePath() + File.separator + urlList.getFileName();
            logger.info("Process download to: "+saveFilePath);
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            logger.info("File downloaded");
        } else {
            logger.error("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
}
