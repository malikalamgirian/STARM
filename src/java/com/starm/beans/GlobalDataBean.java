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

import com.starm.processors.AssociationRuleScrapper;
import com.starm.processors.KMLWriter;
import com.starm.processors.SpatioTemporalTransaction;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.context.FacesContext;

/**
 * Handles the data related operations to support the UI
 * 
 * @author Wasif Altaf
 */
public class GlobalDataBean {

    private String[] rules;
    private GlobalConfigurationsBean globalConfigurationsBean;
    private FileDownloaderBean fileDownloaderBean;

    /**
     * Creates a new instance of GlobalDataBean
     */
    public GlobalDataBean() {
        globalConfigurationsBean = findBean("globalConfigurationsBean");
        fileDownloaderBean = findBean("fileDownloaderBean");
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
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    /**
     * Reads the association rules, and prepares them for user agent
     * 
     * @return the association rules as array of Strings, or status messages if rules could not be found or loaded
     */
    public String[] getRules() {
        try {
            // read rules using global configurations bean
            Path rulesFilePath = FileSystems.getDefault().getPath(globalConfigurationsBean.getRulesFilePath(),
                    globalConfigurationsBean.getRulesFileNameAndExtension());

            this.rules = Files.readAllLines(rulesFilePath).toArray(new String[0]);

        } catch (Exception ex) {
            Logger.getLogger(GlobalDataBean.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Exception in getRules() : " + ex.getMessage());
            return new String[]{"No rules found.", "Sorry, couldn't get them."};
        }

        return rules;
    }

    
    public void setRules(String[] rules) {
        this.rules = rules;
    }

    /**
     * Updates the spatial, temporal, and spatio-temporal data files
     * 
     * @param rule the rule for which the files should be updates
     * @return string representation suggesting the UI agent to stay on the same page
     */
    public String updateDataFiles(String rule) {
        String[] transactionsSupportingRule;
        SpatioTemporalTransaction[] stTransactionsSupportingRule;

        try {
            // fetch spatio temporal transactions supporting the rule
            transactionsSupportingRule = AssociationRuleScrapper.retrieveSpatioTemporalTransactions(rule,
                    globalConfigurationsBean.getPpFilePath(),
                    globalConfigurationsBean.getPpFileNameAndExtension(),
                    globalConfigurationsBean.getPpFileCharset(),
                    globalConfigurationsBean.getDatasetFilePath(),
                    globalConfigurationsBean.getDatasetFileNameAndExtension(),
                    globalConfigurationsBean.getDatasetFileCharset(),
                    globalConfigurationsBean.isDatasetFileHasHeaderRow());
            
            // parse transactionsSupportingRule for KML generation
            stTransactionsSupportingRule = SpatioTemporalTransaction.parse(transactionsSupportingRule);

            // update data files according to the rule input
            createTemporalDataFile(rule, transactionsSupportingRule);
            createSpatialDataFile(rule, stTransactionsSupportingRule);
            createSpatioTemporalDataFile(rule, stTransactionsSupportingRule);
            createSimpleYearlyFrequenciesDataFile(rule, stTransactionsSupportingRule);

        } catch (Exception e) {
            System.err.println("Exception in updateDataFiles(): " + e.getMessage());
            System.err.println("Could not update data files for :" + rule);
            e.printStackTrace();
        }
        
        return "";
    }

    /**
     * Temporal data file generator
     * 
     * @param rule
     * @param transactions
     * @return true if file creation is successful, false if file creation is failure.
     */
    private boolean createTemporalDataFile(String rule, String[] transactions) {
        boolean processSuccessful = false;
        Path path;
        
        try {
            // creates a file with rule at first line and 
            // spatio temporal transactions supporting that rule in following lines
            path = FileSystems.getDefault().getPath(globalConfigurationsBean.getTemporalDataFilePath(),
                    globalConfigurationsBean.getTemporalDataFileNameAndExtension());
            Files.deleteIfExists(path);
            
            // prepare rule for first line
            rule += "\n";            
            
            // attach all remaining transactions below rule
            for (String t : transactions) {
                t += "\n";
                
                rule += t;
            }
            
            // strip off last '\n'
            rule = rule.substring(0, rule.lastIndexOf("\n"));
            
            // write to disk
            path = Files.write(path, rule.getBytes(), StandardOpenOption.CREATE);
            
            processSuccessful = true;
        } catch (Exception e) {
            System.err.println("Exception in createTemporalDataFile(): " + e.getMessage());
            System.err.println("Could not create temporal data file for :" + rule);
            e.printStackTrace();
        }
        
        return processSuccessful;
    }

    /**
     * Creates the spatial data file i.e. KML file
     * 
     * @param rule
     * @param transactions
     * @return true if file creation is successful, false if file creation is failure.
     */
    private boolean createSpatialDataFile(String rule, SpatioTemporalTransaction[] transactions) {
        
        boolean processSuccessful = false;
        
        try {
            // creates a kml file with placemarks grouped by location             
            processSuccessful = KMLWriter.write(transactions, globalConfigurationsBean.getSpatialDataFilePath(), 
                    globalConfigurationsBean.getSpatialDataFileNameAndExtension(), true);

        } catch (Exception e) {
            System.err.println("Exception in createSpatialDataFile(): " + e.getMessage());
            System.err.println("Could not create spatial data file for :" + rule);
            e.printStackTrace();
        }
        
        return processSuccessful;
    }
    
    /**
     * Creates spatio-temporal data file i.e. KML file
     * 
     * @param rule
     * @param transactions
     * @return true if file creation is successful, false if file creation is failure.
     */
    private boolean createSpatioTemporalDataFile(String rule, SpatioTemporalTransaction[] transactions) {        
        boolean processSuccessful = false;
        
        try {
            // creates a kml file with placemarks grouped by location             
            processSuccessful = KMLWriter.write(transactions, globalConfigurationsBean.getSpatioTemporalDataFilePath(), 
                    globalConfigurationsBean.getSpatioTemporalDataFileNameAndExtension(), false);

        } catch (Exception e) {
            System.err.println("Exception in createSpatioTemporalDataFile(): " + e.getMessage());
            System.err.println("Could not create spatio temporal data file for :" + rule);
            e.printStackTrace();
        }
        
        return processSuccessful;
    }
    
    /**
     * Creates simple yearly frequencies data file 
     * 
     * @param rule
     * @param transactions
     * @return true if file creation is successful, false if file creation is failure.
     */
    private boolean createSimpleYearlyFrequenciesDataFile(String rule, SpatioTemporalTransaction[] transactions) {        
        boolean processSuccessful = false;
        Path path;
        
        try {
            // create a data file with rule at first line and 
            // YYYY,frequency in following lines
            path = FileSystems.getDefault().getPath(globalConfigurationsBean.getSimpleYealyFrequenciesDataFilePath(),
                    globalConfigurationsBean.getSimpleYealyFrequenciesDataFileNameAndExtension());
            Files.deleteIfExists(path);
            
            // prepare data for writing
            rule += "\n";
            
            Map<String, List<SpatioTemporalTransaction>> grouped = Arrays.asList(transactions).stream().collect(Collectors.groupingBy(SpatioTemporalTransaction::getTimeStampYear));

            Stream<String> sortedKeys = grouped.keySet().stream().sorted();
            
            for (Iterator it = sortedKeys.iterator(); it.hasNext();) {
                String year = (String) it.next();
                int frequency = grouped.get(year).size();
                
                rule += year + "," + frequency + "\n";
            }            
                        
            // strip off last '\n'
            rule = rule.substring(0, rule.lastIndexOf("\n"));
                   
            // write 
            path = Files.write(path, rule.getBytes(), StandardOpenOption.CREATE);
            
            processSuccessful = true;
        } catch (Exception e) {
            System.err.println("Exception in createSimpleYearlyFrequenciesDataFile(): " + e.getMessage());
            System.err.println("Could not create simple yearly data file for : " + rule);
            e.printStackTrace();
        }
        
        return processSuccessful;
    }
    
    /**
     * Initiates downloading the spatial data file
     * @see com.starm.beans.FileDownloaderBean
     * 
     * @return a string value suggesting the UI agent to stay on the same page
     */
    public String openSpatialDatasetInGE(){
        String valueToReturn = "";
        
        try {
            fileDownloaderBean.downloadFile(globalConfigurationsBean.getSpatialDataFilePath(),
                    globalConfigurationsBean.getSpatialDataFileNameAndExtension());
            
        } catch (Exception e) {
            System.err.println("Exception in openSpatialDatasetInGE(): " + e.getMessage());
            System.err.println("Could not open spaial dataset in GE.");
            e.printStackTrace();
        }
        
        return valueToReturn;    
    }
    
    /**
     * Initiates downloading the spatio-temporal data file 
     * @see com.starm.beans.FileDownloaderBean
     * 
     * @return a string value suggesting the UI agent to stay on the same page
     */
    public String openSpatioTemporalDatasetInGE(){
        String valueToReturn = "";
        
        try {
            fileDownloaderBean.downloadFile(globalConfigurationsBean.getSpatioTemporalDataFilePath(),
                    globalConfigurationsBean.getSpatioTemporalDataFileNameAndExtension());
            
        } catch (Exception e) {
            System.err.println("Exception in openSpatioTemporalDatasetInGE(): " + e.getMessage());
            System.err.println("Could not open spatio temporal dataset in GE.");
            e.printStackTrace();
        }
        
        return valueToReturn;    
    }
    
}
