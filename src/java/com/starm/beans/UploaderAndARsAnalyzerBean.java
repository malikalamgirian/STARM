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
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

/**
 * UI backing bean to support files uploading and analysis for UIs such as UploadAndAnalyseAssociationRules
 * 
 * @author Wasif Altaf
 */
public class UploaderAndARsAnalyzerBean {

    private Part stDatasetFile;
    private Part ppDatasetFile;
    private Part associationRulesFile;

    private GlobalConfigurationsBean globalConfigurationsBean;

    /**
     * Uploads the dataset, preprocessed dataset and association rules files
     * 
     * @return if uploading is successful, a value suggesting the UI to show SUCCESS case interface is returned, 
     * otherwise a value suggesting the UI to show FAILURE case interface is returned.
     */
    public String uploadAndAnalyse() {
        String failureStatus = "FAILURE";
        String successStatus = "SUCCESS";
        
        try {
            // save the uploaded files            
            if (upload(this.stDatasetFile, globalConfigurationsBean.getDatasetFilePath(),
                    globalConfigurationsBean.getDatasetFileNameAndExtension(), true) == false) {
                
                FacesContext.getCurrentInstance().addMessage("stDatasetFile",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not save " + this.stDatasetFile.getSubmittedFileName() + ". Try again later.",
                                "Could not save " + this.stDatasetFile.getSubmittedFileName() + ". Try again later."));

                return failureStatus;
            } else if (upload(ppDatasetFile, globalConfigurationsBean.getPpFilePath(),
                    globalConfigurationsBean.getPpFileNameAndExtension(), true) == false) {
                
                FacesContext.getCurrentInstance().addMessage("ppDatasetFile",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not save " + this.ppDatasetFile.getSubmittedFileName() + ". Try again later.",
                                "Could not save " + this.ppDatasetFile.getSubmittedFileName() + ". Try again later."));

                return failureStatus;
            } else if (upload(this.associationRulesFile, globalConfigurationsBean.getRulesFilePath(),
                    globalConfigurationsBean.getRulesFileNameAndExtension(), true) == false) {
                
                FacesContext.getCurrentInstance().addMessage("associationRulesFile",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not save " + this.associationRulesFile.getSubmittedFileName() + ". Try again later.",
                                "Could not save " + this.associationRulesFile.getSubmittedFileName() + ". Try again later."));

                return failureStatus;
            }

        } catch (Exception e) {
            System.err.println("Exception in uploadAndAnalyse() : " + e.getMessage());

            FacesContext.getCurrentInstance().addMessage("",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Could not analyse successfully at the moment. Please try later.",
                            "Could not analyse successfully at the moment. Please try later."));
            e.printStackTrace();

            return failureStatus;
        }

        return successStatus;
    }

    /**
     * Looks up bean using the beanName parameter
     * 
     * @param <T> type of the bean to be returned
     * @param beanName name of the bean to be looked up
     * @return returns the looked up bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();

        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class
        );
    }

    /**
     * Convenience method to support the filePart uploading to a specified location 
     * 
     * @param filePart the file part uploaded and to be saved 
     * @param outputFilePath the folder where to save the file
     * @param outputFileNameAndExtension the name and extension with which to save the file
     * @param deleteIfExists whether the file should be deleted if it already exists
     * @return true if file saved successfully, false if file could not be saved
     */
     public boolean upload(Part filePart, String outputFilePath,
            String outputFileNameAndExtension, boolean deleteIfExists) {
        boolean uploadSuccessfull = false;
        
        byte[] fileContents;
        InputStream is;
        Path pathToSaveFileTo;

        try {
            System.out.println("Uploading " + filePart.getSubmittedFileName()
                    + " to " + outputFilePath
                    + File.separator + outputFileNameAndExtension);
            
            // read uploaded filePart            
            fileContents = new byte[(int) filePart.getSize()];
            is = filePart.getInputStream();
            is.read(fileContents);

            // save the read filePart
            pathToSaveFileTo = FileSystems.getDefault().getPath(outputFilePath, outputFileNameAndExtension);
            
            if (deleteIfExists) {
                Files.deleteIfExists(pathToSaveFileTo);
            }
            
            pathToSaveFileTo = Files.write(pathToSaveFileTo, fileContents, StandardOpenOption.CREATE);
            
            // perform cleanup
            is.close();
            fileContents = null;

            System.out.println("Uploaded " + filePart.getSubmittedFileName()
                    + " to " + outputFilePath
                    + File.separator + outputFileNameAndExtension);
            
            uploadSuccessfull = true;
        } catch (Exception e) {
            System.err.println("Exception in upload() : " + e.getMessage());

            System.err.println("Could not upload " + filePart.getSubmittedFileName()
                    + " to " + outputFilePath
                    + File.separator + outputFileNameAndExtension);
            
            e.printStackTrace();
        }

        return uploadSuccessfull;
    }

   
    
    /**
     * Creates a new instance of ARMBean
     */
    public UploaderAndARsAnalyzerBean() {
        globalConfigurationsBean = findBean("globalConfigurationsBean");
    }

    public Part getStDatasetFile() {
        return stDatasetFile;
    }

    public void setStDatasetFile(Part stDatasetFile) {
        this.stDatasetFile = stDatasetFile;
    }

    public Part getPpDatasetFile() {
        return ppDatasetFile;
    }

    public void setPpDatasetFile(Part ppDatasetFile) {
        this.ppDatasetFile = ppDatasetFile;
    }

    public Part getAssociationRulesFile() {
        return associationRulesFile;
    }

    public void setAssociationRulesFile(Part associationRulesFile) {
        this.associationRulesFile = associationRulesFile;
    }

    public GlobalConfigurationsBean getGlobalConfigurationsBean() {
        return globalConfigurationsBean;
    }

    public void setGlobalConfigurationsBean(GlobalConfigurationsBean globalConfigurationsBean) {
        this.globalConfigurationsBean = globalConfigurationsBean;
    }

}
