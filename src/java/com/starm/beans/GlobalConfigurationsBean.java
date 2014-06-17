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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Handles the global configurations
 * 
 * @author Wasif Altaf
 */
public class GlobalConfigurationsBean {
    
    private String localTempFolderPath;
    private String cloudServiceFolderPath;
    
    private String datasetFilePath;
    private String datasetFileNameAndExtension = "angioplasty.org_v4_0_USA.txt";
    private Charset datasetFileCharset = StandardCharsets.UTF_16;
    private boolean datasetFileHasHeaderRow = true;
    
    private float minimumSupport;
    private float leastSupport;
    private float minimumConfidence;
    private float minimumIDFScore;
    
    private String transactionsFilePath;
    private String transactionsFileNameAndExtension = "angioplasty.org_v4_0_USA_t.txt";
    
    private String ppFilePath;
    private String ppFileNameAndExtension = "angioplasty.org_v4_0_USA_t_pp.txt";
    private Charset ppFileCharset = Charset.defaultCharset();
    
    private String tdmFilePath;
    private String tdmFileNameAndExtension = "angioplasty.org_v4_0_USA_t_pp_TDM.csv";
    
    private String rulesFilePath;
    private String rulesFileNameAndExtension = "angioplasty.org_v4_0_USA_t_pp_TDM_rules.txt";
    
    private String temporalDataFilePath;
    private String temporalDataFileNameAndExtension = "temporalDataFile.txt";
    
    private String spatialDataFilePath;
    private String spatialDataFileNameAndExtension = "spatialDataFile.kml";
    
    private String spatioTemporalDataFilePath;
    private String spatioTemporalDataFileNameAndExtension = "spatioTemporalDataFile.kml";
    
    private String simpleYealyFrequenciesDataFilePath;
    private String simpleYealyFrequenciesDataFileNameAndExtension = "simpleYealyFrequenciesDataFile.txt";

    /**
     * Creates a new instance of GlobalConfigurationsBean
     */
    public GlobalConfigurationsBean() {
        // set local temp folder path for saving dataset, transctions,
        // preprocessing and rules related files
        this.setLocalTempFolderPath("e:\\toDel");

        // set cloud service folder path
        this.setCloudServiceFolderPath("e:\\wasif\\sync\\Dropbox\\Dropbox");
    }
    
    public String getDatasetFilePath() {
        return datasetFilePath;
    }
    
    public void setDatasetFilePath(String datasetFilePath) {
        this.datasetFilePath = datasetFilePath;
    }
    
    public String getDatasetFileNameAndExtension() {
        return datasetFileNameAndExtension;
    }
    
    public void setDatasetFileNameAndExtension(String datasetFileNameAndExtension) {
        this.datasetFileNameAndExtension = datasetFileNameAndExtension;
    }
    
    public float getMinimumSupport() {
        return minimumSupport;
    }
    
    public void setMinimumSupport(float minimumSupport) {
        this.minimumSupport = minimumSupport;
    }
    
    public float getLeastSupport() {
        return leastSupport;
    }
    
    public void setLeastSupport(float leastSupport) {
        this.leastSupport = leastSupport;
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
    
    public String getTransactionsFilePath() {
        return transactionsFilePath;
    }
    
    public void setTransactionsFilePath(String transactionsFilePath) {
        this.transactionsFilePath = transactionsFilePath;
    }
    
    public String getTransactionsFileNameAndExtension() {
        return transactionsFileNameAndExtension;
    }
    
    public void setTransactionsFileNameAndExtension(String transactionsFileNameAndExtension) {
        this.transactionsFileNameAndExtension = transactionsFileNameAndExtension;
    }
    
    public String getPpFilePath() {
        return ppFilePath;
    }
    
    public void setPpFilePath(String ppFilePath) {
        this.ppFilePath = ppFilePath;
    }
    
    public String getPpFileNameAndExtension() {
        return ppFileNameAndExtension;
    }
    
    public void setPpFileNameAndExtension(String ppFileNameAndExtension) {
        this.ppFileNameAndExtension = ppFileNameAndExtension;
    }
    
    public String getTdmFilePath() {
        return tdmFilePath;
    }
    
    public void setTdmFilePath(String tdmFilePath) {
        this.tdmFilePath = tdmFilePath;
    }
    
    public String getTdmFileNameAndExtension() {
        return tdmFileNameAndExtension;
    }
    
    public void setTdmFileNameAndExtension(String tdmFileNameAndExtension) {
        this.tdmFileNameAndExtension = tdmFileNameAndExtension;
    }
    
    public String getRulesFilePath() {
        return rulesFilePath;
    }
    
    public void setRulesFilePath(String rulesFilePath) {
        this.rulesFilePath = rulesFilePath;
    }
    
    public String getRulesFileNameAndExtension() {
        return rulesFileNameAndExtension;
    }
    
    public void setRulesFileNameAndExtension(String rulesFileNameAndExtension) {
        this.rulesFileNameAndExtension = rulesFileNameAndExtension;
    }
    
    public Charset getDatasetFileCharset() {
        return datasetFileCharset;
    }
    
    public void setDatasetFileCharset(Charset datasetFileCharset) {
        this.datasetFileCharset = datasetFileCharset;
    }
    
    public boolean isDatasetFileHasHeaderRow() {
        return datasetFileHasHeaderRow;
    }
    
    public void setDatasetFileHasHeaderRow(boolean datasetFileHasHeaderRow) {
        this.datasetFileHasHeaderRow = datasetFileHasHeaderRow;
    }
    
    public Charset getPpFileCharset() {
        return ppFileCharset;
    }
    
    public void setPpFileCharset(Charset ppFileCharset) {
        this.ppFileCharset = ppFileCharset;
    }
    
    public String getTemporalDataFilePath() {
        return temporalDataFilePath;
    }
    
    public void setTemporalDataFilePath(String temporalDataFilePath) {
        this.temporalDataFilePath = temporalDataFilePath;
    }
    
    public String getTemporalDataFileNameAndExtension() {
        return temporalDataFileNameAndExtension;
    }
    
    public void setTemporalDataFileNameAndExtension(String temporalDataFileNameAndExtension) {
        this.temporalDataFileNameAndExtension = temporalDataFileNameAndExtension;
    }
    
    public String getSpatialDataFilePath() {
        return spatialDataFilePath;
    }
    
    public void setSpatialDataFilePath(String spatialDataFilePath) {
        this.spatialDataFilePath = spatialDataFilePath;
    }
    
    public String getSpatialDataFileNameAndExtension() {
        return spatialDataFileNameAndExtension;
    }
    
    public void setSpatialDataFileNameAndExtension(String spatialDataFileNameAndExtension) {
        this.spatialDataFileNameAndExtension = spatialDataFileNameAndExtension;
    }
    
    public String getSpatioTemporalDataFilePath() {
        return spatioTemporalDataFilePath;
    }
    
    public void setSpatioTemporalDataFilePath(String spatioTemporalDataFilePath) {
        this.spatioTemporalDataFilePath = spatioTemporalDataFilePath;
    }
    
    public String getSpatioTemporalDataFileNameAndExtension() {
        return spatioTemporalDataFileNameAndExtension;
    }
    
    public void setSpatioTemporalDataFileNameAndExtension(String spatioTemporalDataFileNameAndExtension) {
        this.spatioTemporalDataFileNameAndExtension = spatioTemporalDataFileNameAndExtension;
    }
    
    public String getSimpleYealyFrequenciesDataFilePath() {
        return simpleYealyFrequenciesDataFilePath;
    }
    
    public void setSimpleYealyFrequenciesDataFilePath(String simpleYealyFrequenciesDataFilePath) {
        this.simpleYealyFrequenciesDataFilePath = simpleYealyFrequenciesDataFilePath;
    }
    
    public String getSimpleYealyFrequenciesDataFileNameAndExtension() {
        return simpleYealyFrequenciesDataFileNameAndExtension;
    }
    
    public void setSimpleYealyFrequenciesDataFileNameAndExtension(String simpleYealyFrequenciesDataFileNameAndExtension) {
        this.simpleYealyFrequenciesDataFileNameAndExtension = simpleYealyFrequenciesDataFileNameAndExtension;
    }
    
    public String getLocalTempFolderPath() {
        return localTempFolderPath;
    }
    
    public void setLocalTempFolderPath(String localTempFolderPath) {
        this.localTempFolderPath = localTempFolderPath;

        // update other related path configurations
        this.setDatasetFilePath(localTempFolderPath);
        this.setTransactionsFilePath(localTempFolderPath);
        this.setPpFilePath(localTempFolderPath);
        this.setTdmFilePath(localTempFolderPath);
        this.setRulesFilePath(localTempFolderPath);
        
    }
    
    public String getCloudServiceFolderPath() {
        return cloudServiceFolderPath;
    }
    
    public void setCloudServiceFolderPath(String cloudServiceFolderPath) {
        this.cloudServiceFolderPath = cloudServiceFolderPath;

        // update data files related file folder paths
        this.setTemporalDataFilePath(cloudServiceFolderPath);
        this.setSpatialDataFilePath(cloudServiceFolderPath);
        this.setSpatioTemporalDataFilePath(cloudServiceFolderPath);
        this.setSimpleYealyFrequenciesDataFilePath(cloudServiceFolderPath);
    }
    
}
