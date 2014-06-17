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

import com.starm.preprocessing.Preprocessor;
import com.starm.preprocessing.tdm.ListOfStringsToBinaryTDMUsing2DMatrix;
import com.starm.preprocessing.tdm.WeightingScheme;
import com.starm.processors.SpatioTemporalDataFileOperations;
import com.starm.processors.SpatioTemporalTransaction;
import com.starm.processors.miners.FPGrowthBasedARMiner;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

/**
 * Backing bean for MineARsUsingFPGrowth UI
 * 
 * @author Wasif Altaf
 */
public class ARMBean {

    private Part file;
    private float minimumSupport;
    private float minimumConfidence;
    private float minimumIDFScore;

    private GlobalConfigurationsBean globalConfigurationsBean;

    /**
     * Performs One Click Mining
     * 
     * @return Success or Failure Case
     */
    public String mine() {
        String failureStatus = "FAILURE";
        String successStatus = "SUCCESS";
        InputStream is = null;
        byte[] fileContents;
        Path pathToSaveFileTo;
        String datasetFilePath = globalConfigurationsBean.getLocalTempFolderPath();
        String datasetFileNameAndExtension;
        SpatioTemporalTransaction[] spatioTemporalTransactions;
        Preprocessor preprocessor;
        ListOfStringsToBinaryTDMUsing2DMatrix tdmGenerator;
        FPGrowthBasedARMiner miner;

        try {
            // read and validate parameters, read and manage uploaded file  
            // perform association rule mining task, save results properly and start analysis

            // validate input
            if (minimumSupport <= 0) {
                FacesContext.getCurrentInstance().addMessage("support",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum support value should be greater than 0.",
                                "Minimum support value should be greater than 0."));

                return failureStatus;
            } else if (minimumConfidence <= 0) {
                FacesContext.getCurrentInstance().addMessage("confidence",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum confidence value should be greater than 0.",
                                "Minimum confidence value should be greater than 0."));

                return failureStatus;
            } else if (minimumIDFScore <= 0) {
                FacesContext.getCurrentInstance().addMessage("idf",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum IDF value should be greater than or equal to 0.",
                                "Minimum IDF value should be greater than or equal to 0."));

                return failureStatus;
            }

            // read uploaded file
            datasetFileNameAndExtension = file.getSubmittedFileName();
            fileContents = new byte[(int) file.getSize()];
            is = file.getInputStream();
            is.read(fileContents);

            // save the read file
            pathToSaveFileTo = FileSystems.getDefault().getPath(datasetFilePath, datasetFileNameAndExtension);
            Files.deleteIfExists(pathToSaveFileTo);
            pathToSaveFileTo = Files.write(pathToSaveFileTo, fileContents, StandardOpenOption.CREATE);

            // save configurations
            globalConfigurationsBean.setDatasetFilePath(datasetFilePath);
            globalConfigurationsBean.setDatasetFileNameAndExtension(datasetFileNameAndExtension);

            globalConfigurationsBean.setMinimumSupport(minimumSupport);
            globalConfigurationsBean.setMinimumConfidence(minimumConfidence);
            globalConfigurationsBean.setMinimumIDFScore(minimumIDFScore);

            // perform transactions extraction, preprocessing, term-by-document matrix generation, association rule extraction
            // extract and save transactions from spatio temporal dataset
            spatioTemporalTransactions = SpatioTemporalDataFileOperations.readAllTransactions(datasetFilePath, datasetFileNameAndExtension);

            // if saving transactions file successful, then preprocess file and perform other steps
            if (SpatioTemporalDataFileOperations.saveAllTransactions(spatioTemporalTransactions,
                    globalConfigurationsBean.getTransactionsFilePath(), globalConfigurationsBean.getTransactionsFileNameAndExtension())) {

                // preprocess transactions
                preprocessor = new Preprocessor();

                // if preprocessing successful, construct term by document matrix
                if (preprocessor.process(globalConfigurationsBean.getTransactionsFilePath(),
                        globalConfigurationsBean.getTransactionsFileNameAndExtension())) {

                    // construct term by document matrix
                    tdmGenerator = new ListOfStringsToBinaryTDMUsing2DMatrix();

                    // if term by document matrix generation successful, then perform association rule extraction
                    if (tdmGenerator.convertTextFileToBinaryCSVBasedTDM(globalConfigurationsBean.getPpFilePath(),
                            globalConfigurationsBean.getPpFileNameAndExtension(), WeightingScheme.IDF,
                            this.minimumIDFScore, 4f, 2)) {

                        // mine ARs
                        miner = new FPGrowthBasedARMiner(this.minimumSupport,
                                this.minimumConfidence,
                                globalConfigurationsBean.getTdmFilePath(),
                                globalConfigurationsBean.getTdmFileNameAndExtension(),
                                globalConfigurationsBean.getRulesFilePath(),
                                globalConfigurationsBean.getRulesFileNameAndExtension());

                        // if mining successful, return success status
                        if (miner.mine()) {
                            // this will return success status 

                        } else {
                            FacesContext.getCurrentInstance().addMessage("",
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                            "Could not mine ARs from TDM.",
                                            "Could not mine ARs from TDM."));

                            return failureStatus;
                        }

                    } else {
                        FacesContext.getCurrentInstance().addMessage("",
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                        "Could not convert preprocessed transactions to TDM.",
                                        "Could not convert preprocessed transactions to TDM."));

                        return failureStatus;
                    }

                } else {
                    FacesContext.getCurrentInstance().addMessage("",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Could not preprocess transactions.",
                                    "Could not preprocess transactions."));

                    return failureStatus;
                }

            } else {
                FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not save transactions extracted from stTransactions.",
                                "Could not save transactions extracted from stTransactions."));

                return failureStatus;
            }

        } catch (Exception e) {
            System.err.println("Exception in mine() : " + e.getMessage());

            FacesContext.getCurrentInstance().addMessage("",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Could not perform mining due to some issue at the moment. Please try later.",
                            "Could not perform mining due to some issue at the moment. Please try later."));
            e.printStackTrace();

            return failureStatus;
        }

        return successStatus;
    }

    /**
     * Does 1) Creates transactions file, from st Transactions 2) Preprocesses
     * transactions 3) Saves the preprocessed transactions
     *
     * @return stays on the same page i.e. always returns the failure case
     */
    public String preprocessDataset() {

        String failureStatus = "FAILURE";
        String successStatus = "FAILURE";

        InputStream is = null;
        byte[] fileContents;
        Path pathToSaveFileTo;
        String datasetFilePath = globalConfigurationsBean.getLocalTempFolderPath();
        String datasetFileNameAndExtension;
        SpatioTemporalTransaction[] spatioTemporalTransactions;
        Preprocessor preprocessor;

        try {
            //  read and manage uploaded file  
            // read uploaded file
            datasetFileNameAndExtension = file.getSubmittedFileName();
            fileContents = new byte[(int) file.getSize()];
            is = file.getInputStream();
            is.read(fileContents);

            // save the read file
            pathToSaveFileTo = FileSystems.getDefault().getPath(datasetFilePath, datasetFileNameAndExtension);
            Files.deleteIfExists(pathToSaveFileTo);
            pathToSaveFileTo = Files.write(pathToSaveFileTo, fileContents, StandardOpenOption.CREATE);

            // clean up file handles
            is.close();
            fileContents = null;
            
            // save configurations
            globalConfigurationsBean.setDatasetFilePath(datasetFilePath);
            globalConfigurationsBean.setDatasetFileNameAndExtension(datasetFileNameAndExtension);
            
            // perform transactions extraction, preprocessing, 
            spatioTemporalTransactions = SpatioTemporalDataFileOperations.readAllTransactions(datasetFilePath, datasetFileNameAndExtension);

            // if saving transactions file successful, then preprocess file and perform other steps
            if (SpatioTemporalDataFileOperations.saveAllTransactions(spatioTemporalTransactions,
                    globalConfigurationsBean.getTransactionsFilePath(), 
                    globalConfigurationsBean.getTransactionsFileNameAndExtension())) {

                // preprocess transactions
                preprocessor = new Preprocessor();

                // if preprocessing successful, construct term by document matrix
                if (preprocessor.process(globalConfigurationsBean.getTransactionsFilePath(),
                        globalConfigurationsBean.getTransactionsFileNameAndExtension())) {

                    // this is the success case
                    // add success message
                    FacesContext.getCurrentInstance().addMessage("",
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                    "Transactions preprocessed successfully.",
                                    "Transactions preprocessed successfully."));

                } else {
                    FacesContext.getCurrentInstance().addMessage("",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Could not save the preprocessed transactions.",
                                    "Could not save the preprocessed transactions."));

                    return failureStatus;
                }

            } else {
                FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not save transactions extracted from stTransactions.",
                                "Could not save transactions extracted from stTransactions."));

                return failureStatus;
            }

        } catch (Exception e) {
            System.err.println("Exception in preprocessDataset() : " + e.getMessage());

            FacesContext.getCurrentInstance().addMessage("",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Could not perform dataset preprocessing due to some issue at the moment. Please try later.",
                            "Could not perform dataset preprocessing due to some issue at the moment. Please try later."));
            e.printStackTrace();

            return failureStatus;
        }

        return successStatus;
    }

    /**
     * Does 1) Creates TDM from Preprocessed Transactions Data File. Assumes
     * that Preprocessing has been performed successfully.
     *
     * @return stays on the same page i.e always returns the failure case
     */
    public String createTDM() {
        String failureStatus = "FAILURE";
        String successStatus = "FAILURE";

        String ppFileNameAndExtension;
        byte[] fileContents;
        InputStream is;
        ListOfStringsToBinaryTDMUsing2DMatrix tdmGenerator;
        Path pathToSaveFileTo;

        try {

            // validate minimumIDFScore
            if (minimumIDFScore <= 0) {
                FacesContext.getCurrentInstance().addMessage("idf",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum IDF value should be greater than or equal to 0.",
                                "Minimum IDF value should be greater than or equal to 0."));

                return failureStatus;
            }

            // save idf score
            globalConfigurationsBean.setMinimumIDFScore(minimumIDFScore);
            
            // read and manage uploaded proprocessed file  
            // read uploaded file
            fileContents = new byte[(int) file.getSize()];
            is = file.getInputStream();
            is.read(fileContents);

            // save the read file
            pathToSaveFileTo = FileSystems.getDefault().getPath(globalConfigurationsBean.getPpFilePath(), 
                    globalConfigurationsBean.getPpFileNameAndExtension());
            Files.deleteIfExists(pathToSaveFileTo);
            pathToSaveFileTo = Files.write(pathToSaveFileTo, fileContents, StandardOpenOption.CREATE);
              
            // clean up file handles
            is.close();
            fileContents = null;

            // construct term-by-document-matrix
            tdmGenerator = new ListOfStringsToBinaryTDMUsing2DMatrix();

            // if term by document matrix generation successful
            if (tdmGenerator.convertTextFileToBinaryCSVBasedTDM(globalConfigurationsBean.getPpFilePath(),
                    globalConfigurationsBean.getPpFileNameAndExtension(), WeightingScheme.IDF,
                    this.minimumIDFScore, 4f, 2)) {

                // this is the success case, so add success message
                FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "TDM created successfully.",
                                "TDM created successfully."));

            } else {
                FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not convert preprocessed transactions to TDM.",
                                "Could not convert preprocessed transactions to TDM."));

                return failureStatus;
            }

        } catch (Exception e) {
            System.err.println("Exception in createTDM() : " + e.getMessage());

            FacesContext.getCurrentInstance().addMessage("",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Could not create TDM due to some issue at the moment. Please try later.",
                            "Could not create TDM due to some issue at the moment. Please try later."));

            e.printStackTrace();

            return failureStatus;
        }

        return successStatus;
    }

    /**
     * Extracts association rules from the uploaded term-by-document matrix 
     * 
     * @return stays on the same page i.e. always returns the failure case 
     */
    public String extractARs() {
        String failureStatus = "FAILURE";
        String successStatus = "FAILURE";

        String tdmFileNameAndExtension;
        byte[] fileContents;
        InputStream is;
        Path pathToSaveFileTo;
        
        FPGrowthBasedARMiner miner;

        try {
            // read and validate parameters  
            // perform association rule mining task, save results properly and start analysis

            // validate input
            if (minimumSupport <= 0) {
                FacesContext.getCurrentInstance().addMessage("support",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum support value should be greater than 0.",
                                "Minimum support value should be greater than 0."));

                return failureStatus;
            } else if (minimumConfidence <= 0) {
                FacesContext.getCurrentInstance().addMessage("confidence",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Minimum confidence value should be greater than 0.",
                                "Minimum confidence value should be greater than 0."));

                return failureStatus;
            }

            globalConfigurationsBean.setMinimumSupport(minimumSupport);
            globalConfigurationsBean.setMinimumConfidence(minimumConfidence);
            
            // read and manage uploaded tdm file  
            // read uploaded file
            fileContents = new byte[(int) file.getSize()];
            is = file.getInputStream();
            is.read(fileContents);

            // save the read file
            pathToSaveFileTo = FileSystems.getDefault().getPath(globalConfigurationsBean.getTdmFilePath(), 
                    globalConfigurationsBean.getTdmFileNameAndExtension());
            Files.deleteIfExists(pathToSaveFileTo);
            pathToSaveFileTo = Files.write(pathToSaveFileTo, fileContents, StandardOpenOption.CREATE);
            
            // clean up file handles
            is.close();
            fileContents = null;

            // mine ARs
            miner = new FPGrowthBasedARMiner(this.minimumSupport,
                    this.minimumConfidence,
                    globalConfigurationsBean.getTdmFilePath(),
                    globalConfigurationsBean.getTdmFileNameAndExtension(),
                    globalConfigurationsBean.getRulesFilePath(),
                    globalConfigurationsBean.getRulesFileNameAndExtension());

            // if mining successful, return success status
            if (miner.mine()){
                
               // success status 
               FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Association rules mined successfully.",
                                "Association rules mined successfully."));

            } else {
                FacesContext.getCurrentInstance().addMessage("",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Could not mine ARs from TDM.",
                                "Could not mine ARs from TDM."));

                return failureStatus;
            }

        } catch (Exception e) {
            System.err.println("Exception in extractARs() : " + e.getMessage());

            FacesContext.getCurrentInstance().addMessage("",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Could not extract ARs due to some issue at the moment. Please try later.",
                            "Could not extract ARs due to some issue at the moment. Please try later."));
            e.printStackTrace();

            return failureStatus;
        }

        return successStatus;

    }
    
    /**
     * Handles the success case for analyse association rules button
     * 
     * @return the success status
     */
    public String analyseAssociationRules(){
            String successStatus = "SUCCESS";
            
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
     * Creates a new instance of ARMBean
     */
    public ARMBean() {
        // lookup configurations bean
        globalConfigurationsBean = findBean("globalConfigurationsBean");
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public float getMinimumSupport() {
        return minimumSupport;
    }

    public void setMinimumSupport(float minimumSupport) {
        this.minimumSupport = minimumSupport;
    }

    public float getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(float minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public float getMinimumIDFScore() {
        return minimumIDFScore;
    }

    public void setMinimumIDFScore(float minimumIDFScore) {
        this.minimumIDFScore = minimumIDFScore;
    }

}
