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
package com.starm.processors;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Provides functionality for spatio-temporal dataset file (USA dataset file) operations
 * 
 * @author Wasif Altaf
 */
public class SpatioTemporalDataFileOperations {

    /**
     * Reads all spatio-temporal transactions from given file
     * 
     * @param filePath folder where spatio-temporal transactions file exists
     * @param fileNameAndExtension file name and extenstion for spatio-temporal transactions file
     * @return array of SpatioTemporalTransaction<i>s</i> read
     */
    public static SpatioTemporalTransaction[] readAllTransactions(String filePath, String fileNameAndExtension) {
        List<String> allLines;
        SpatioTemporalTransaction[] transactions = null;
        SpatioTemporalTransaction transaction;
        
        try {
            // read all lines and remove first line
            allLines = Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), StandardCharsets.UTF_16);
            allLines.remove(0);

            // convert transactions
            transactions = new SpatioTemporalTransaction[allLines.size()];

            for (int i = 0; i < allLines.size(); i++) {
                String[] parts = allLines.get(i).split("\t");

                transaction = new SpatioTemporalTransaction();

                transaction.setId(Integer.parseInt(parts[0]));
                transaction.setTimeStamp(parts[1]);
                transaction.setLocation(parts[2]);
                transaction.setTransaction(parts[3]);
                transaction.setTopic(parts[4]);

                transactions[i] = transaction;
            }

        } catch (Exception e) {
            System.err.println("Exception in readAllTransactions.");
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Saves the transaction parts (posts) only, not the ID, location, time stamp or topic.
     * 
     * @param transactions SpatioTemporalTransactionS from which the transaction text is to be saved
     * @param outputFilePath folder path for output file path
     * @param outputFileNameAndExtension file name and extension of output file path
     * @return true if saving was successful, false if saving failed
     */
    public static boolean saveAllTransactions(final SpatioTemporalTransaction[] transactions,
            String outputFilePath, String outputFileNameAndExtension) {
        boolean processSuccessful = false;
        Path outputFile;
        String transactionsTextToWrite = "";

        try {            
            // prepare transactions text
            for (SpatioTemporalTransaction transaction : transactions) {
                transactionsTextToWrite += transaction.getTransaction() + "\n";                
            }
            
            if (transactions.length > 0) {
                transactionsTextToWrite = transactionsTextToWrite.substring(0, transactionsTextToWrite.lastIndexOf("\n"));
            }
            
            // create empty file
            outputFile = FileSystems.getDefault().getPath(outputFilePath, outputFileNameAndExtension);
            Files.deleteIfExists(outputFile);
            outputFile = Files.createFile(outputFile);
            
            // write transactions
            Files.write(outputFile, Charset.defaultCharset().encode(transactionsTextToWrite).array(), StandardOpenOption.APPEND);
            
            processSuccessful = true;
        } catch (Exception e) {
            System.err.println("Exception in saveAllTransactions.");
            System.err.println("outputFilePath : " + outputFilePath);
            System.err.println("outputFileNameAndExtension : " + outputFileNameAndExtension);
            
            e.printStackTrace();
        }

        return processSuccessful;
    }

}
