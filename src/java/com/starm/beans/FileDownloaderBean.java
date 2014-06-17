/*
 * Copyright (C) 2014 Wasif Altaf <malikalamgirian@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.starm.beans;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Provides functionality required for file downloading 
 * 
 * @author Wasif Altaf
 */
public class FileDownloaderBean {

    /**
     * Creates a new instance of FileDownloaderBean
     */
    public FileDownloaderBean() {
    }

    /**
     * Force the file to be downloaded through the user agent
     * 
     * @param filePath the folder path where file exists
     * @param fileNameAndExtension name and extension of the file
     * @return the final status of download process
     */
    public boolean downloadFile(String filePath, String fileNameAndExtension) {
        boolean downloadSuccess = false;
        Path downloadFilePath;
        byte[] contents;

        try {            
            System.out.println("Downloading file : " +  filePath + File.separator + fileNameAndExtension);
            
            // get file contents
            downloadFilePath = FileSystems.getDefault().getPath(filePath, fileNameAndExtension);
            contents = Files.readAllBytes(downloadFilePath);
            
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            // setup parameters
            ec.responseReset();
            ec.setResponseContentType(ec.getMimeType(filePath + File.separator + fileNameAndExtension));
            ec.setResponseContentLength(contents.length); 
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileNameAndExtension + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

            // write contents
            OutputStream output = ec.getResponseOutputStream();
            output.write(contents);
            
            // cleanup
            contents = null;

            System.out.println("Downloaded file : " +  filePath + File.separator + fileNameAndExtension);            
            
            downloadSuccess = true;
        } catch (Exception e) {
            System.err.println("Exception in downloadFile() : " + e.getMessage());
            System.err.println("Could not download : " + filePath + File.separator + fileNameAndExtension);

            e.printStackTrace();
        }

        return downloadSuccess;
    }

}
