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
package com.starm.processors.miners;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import weka.associations.FPGrowth;
import weka.associations.FPGrowth.AssociationRule;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Provides convenient interface for FPGrowth implemented in Weka 
 * 
 * @author Wasif Altaf
 */
public class FPGrowthBasedARMiner {

    private FPGrowth arMiner;
    private Instances dataset = null;
    private String datasetFilePath;
    private String datasetFileNameAndExtenstion;
    private List<AssociationRule> associationRules;
    private String outputRulesFilePath;
    private String outputRulesFileNameAndExtension;
    private float minimumSupport;
    private float minimumConfidence;
    

    /**
     * For constructing FPGrowthBasedARMiner object by using pre-configured FPGrowth object
     * 
     * @param configuredFPGrowth the pre-configured instance of FPGrowth
     * @param datasetFilePath folder path for dataset file 
     * @param datasetFileNameAndExtenstion file name and extension of dataset file
     * @param outputRulesFilePath folder path for output association rules file
     * @param outputRulesFileNameAndExtension output association rules file name and extension 
     */
    public FPGrowthBasedARMiner(FPGrowth configuredFPGrowth, String datasetFilePath,
            String datasetFileNameAndExtenstion, String outputRulesFilePath, String outputRulesFileNameAndExtension) {
        this.arMiner = configuredFPGrowth;
        this.datasetFilePath = datasetFilePath;
        this.datasetFileNameAndExtenstion = datasetFileNameAndExtenstion;

        this.outputRulesFilePath = outputRulesFilePath;
        this.outputRulesFileNameAndExtension = outputRulesFileNameAndExtension;
    }

    /**
     * For instantiating FPGrowthBasedARMiner using minimum support and minimum confidence values
     * 
     * @param minimumSupport minimum support value
     * @param minimumConfidence minimum confidence value
     * @param datasetFilePath folder path for dataset file 
     * @param datasetFileNameAndExtenstion file name and extension of dataset file
     * @param outputRulesFilePath folder path for output association rules file
     * @param outputRulesFileNameAndExtension output association rules file name and extension 
     */
    public FPGrowthBasedARMiner(float minimumSupport, float minimumConfidence, String datasetFilePath,
            String datasetFileNameAndExtenstion, String outputRulesFilePath, String outputRulesFileNameAndExtension) {
        
        this.minimumSupport = minimumSupport;
        this.minimumConfidence = minimumConfidence;
        
        arMiner = new FPGrowth();
        arMiner.setDelta(0.005f);
        arMiner.setFindAllRulesForSupportLevel(true);
        arMiner.setLowerBoundMinSupport(minimumSupport);
        arMiner.setUpperBoundMinSupport(1.0f);
        arMiner.setMetricType(new SelectedTag(0, FPGrowth.AssociationRule.TAGS_SELECTION));
        arMiner.setMinMetric(minimumConfidence);

        this.datasetFilePath = datasetFilePath;
        this.datasetFileNameAndExtenstion = datasetFileNameAndExtenstion;

        this.outputRulesFilePath = outputRulesFilePath;
        this.outputRulesFileNameAndExtension = outputRulesFileNameAndExtension;
    }

    /**
     * Carries out the actual mining process using FPGrowth algorithm
     * 
     * <br />
     * <br />
     * 
     * <ul>
     * <li>Loads dataset</li>
     * <li>Constructs FP Tree</li>
     * <li>Finds frequent itemsets</li>
     * <li>Builds association rules from itemsets</li>
     * <li>Saves the association rules</li>
     * </ul>
     * 
     * @return true if mining was successful and association rules were written to disk successfully, false otherwise
     */
    public boolean mine() {
        boolean processSuccess = false;
        Path outputFilePath;
        String rules = "";

        try {
            // validate parameters
            if (arMiner == null || datasetFilePath == null || datasetFileNameAndExtenstion == null) {
                return false;
            }

            System.out.println("Loading dataset... ");
            
            // load dataset
            if (datasetFilePath.endsWith("\\")) {
                dataset = DataSource.read(datasetFilePath + datasetFileNameAndExtenstion);
            } else {
                dataset = DataSource.read(datasetFilePath + File.separator + datasetFileNameAndExtenstion);
            }
            
            // test if any data was loaded
            if (dataset == null) {
                System.err.println("Could not load dataset for mining.");
                
                throw new NullPointerException("Could not load dataset, it is null.");
            }
            
            System.out.println("Starting building associations...");

            // start mining
            // build associations
            // get association rules
            arMiner.buildAssociations(dataset);
            this.associationRules = arMiner.getAssociationRules();

            // save association rules
            outputFilePath = FileSystems.getDefault().getPath(outputRulesFilePath, outputRulesFileNameAndExtension);
            Files.deleteIfExists(outputFilePath);

            for (AssociationRule associationRule : associationRules) {
                rules += associationRule.toString().replace("<", "").replace(")>", ")") + "\n";  
                
            }

            System.out.println("Saving association rules...");
            
            Files.write(outputFilePath, rules.getBytes(), StandardOpenOption.CREATE);

            System.out.println("Saved association rules...");
            
            processSuccess = true;

        } catch (Exception e) {
            System.err.println("Exception in mine() : " + e.getMessage());
            
            e.printStackTrace();

            return processSuccess;
        }

        return processSuccess;
    }

}
