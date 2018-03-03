package com.bib.esma;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnpackZip {
    private static final int BUFFER_SIZE = 4096;

    public void unZipIt(UrlList urlList) throws IOException {
        String[] filesExtracted;
        String savedFilePath = urlList.getFilePath() + File.separator + urlList.getFileName();
        //get the zip file content
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(savedFilePath));
        //get the zipped file list entry
        ZipEntry ze = inputStream.getNextEntry();
        while(ze!=null){
            if (!ze.isDirectory()) {
                String fileName = urlList.getFilePath() + File.separator + ze.getName();
                File newFile = new File(fileName);
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
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
        File f = new File(savedFilePath);
        boolean delete = f.delete();
        if (!delete) {
            throw new RuntimeException("Failed to delete file "+ savedFilePath);
        }
        System.out.println("Done");


    }
}
