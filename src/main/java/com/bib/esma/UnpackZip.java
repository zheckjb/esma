package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnpackZip {
    private static final int BUFFER_SIZE = 4096;
    private static final Logger logger = Logger.getLogger(UnpackZip.class);

    public void unZipIt(UrlList urlList) throws IOException {
        //get the zip file content
        String savedFilePath = urlList.getFilePath() + File.separator + urlList.getFileName();
        logger.info("Process to unpack file: "+savedFilePath);
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(savedFilePath));
        //get the zipped file list entry
        ZipEntry ze = inputStream.getNextEntry();
        while(ze!=null){
            if (!ze.isDirectory()) {
                String fileName = urlList.getFilePath() + File.separator + ze.getName();
                File newFile = new File(fileName);
                logger.info("Unpacking file: " + ze.getName());
                FileOutputStream outputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                urlList.setFileXml(ze.getName());
            }
            ze = inputStream.getNextEntry();
        }
        inputStream.closeEntry();
        inputStream.close();
        logger.info("Removing zip fle: "+savedFilePath);
        File f = new File(savedFilePath);
        boolean delete = f.delete();
        if (!delete) {
            logger.error("Failed to delete file "+ savedFilePath);
        }
    }
}
