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
package com.starm.preprocessing.tdm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Methods to load list of strings from file and convert the list of strings
 * to binary term document matrix. <br /><br />
 *
 * Algorithm.<br /><br />
 *
 * <ol>
 * 
 * <li>Read the input file and Convert it to the List of Strings i.e. LS.
 * </li>
 * 
 * <li>Use List of Strings LS to create Sorted list of all unique terms,
 * called terms index i.e. TI
 * 
 * <ol>
 * 
 * <li>Foreach String based Transaction contained in List of Strings</li>
 * <ol>
 * <li>Tokenize Using Space Character.</li>
 * 
 * <li>Save Tokenized String as a List of Tokens or Words as Strings i.e.
 * BagOfWords.</li>
 * 
 * <li>Add Tokens to TI List if TI List does not already contain them. </li>
 * 
 * <li>Use TI List to create comma separated TI </li>
 * </ol>
 * 
 * </ol>
 * 
 * </li>
 * 
 * <li>Foreach transaction containing a list of tokens stored as BagOfWords in
 * 2.1.2.
 * <ol>
 * 
 * <li> Make a copy of comma separated TI i.e. TIFT.. Terms Index for Transaction</li>
 * <li>Foreach Word W contained in BagOfWords.</li>
 * 
 * <ol>
 * <li>Find and Replace the exact word W with 1 in comma separated TI. //
 * the word should have a comma before and after it.</li>
 * </ol>
 * 
 * <li>Replace all the non 1 words with 0.</li>
 * 
 * </ol>
 * </li>
 * 
 * <li>Create Final Binary TDM as comma separated values file.
 * 
 * <ol>
 * <li>Save the file in same path provided for the input file, with _TDM suffix in filename. </li>
 * </ol>
 * 
 * </li>
 * 
 * </ol>
 *
 * @author Wasif Altaf
 */
public class ListOfStringsToBinaryTDMUsing2DMatrix {

    
    /**
     * Converts the text file to binary CSV based term-by-document matrix, 
     * 
     * <ol>
     * <li>Reads all lines from input file</li>
     * <li>Creates unique terms index from the list of bag of words created from lines read</li>
     * <li>Converts list of bag of words into 2-dimensional array</li>
     * <li>Marks items and transactions for pruning in 2-dimensional array</li>
     * <li>Prunes items and transactions</li>
     * <li>Saves the index and 2-dimensional array</li>
     * </ol>
     * 
     * @param filePath input folder path
     * @param fileNameAndExtension input file name and extension
     * @param weightingScheme weighting scheme to be used for term-by-document matrix
     * @param pruneValueLowerLimit lower bound for prune value
     * @param pruneValueUpperLimit upper bound for prune value
     * @param minimumNumberOfItems minimum number of items required by a transaction
     * 
     * @return true if term-by-document matrix has been saved successfully, false if 
     * term-by-document matrix could not be saved successfully.
     */
    public boolean convertTextFileToBinaryCSVBasedTDM(String filePath,
            String fileNameAndExtension,
            WeightingScheme weightingScheme,
            float pruneValueLowerLimit,
            float pruneValueUpperLimit,
            int minimumNumberOfItems) {
        boolean processCompletedSuccessfully = false;
        List<String> allTranscations = null;
        List<List<String>> listOfBagOfWords = null;
        float[][] frequenciesTable = null;
        List<String> uniqueTermsIndex = null;
        String commaSeparatedUniqueTermsIndex = null;
        String commaSeparatedTDMFrequenciesTable = null;
        String csvFileNameAndExtension = null;
        Path pathOfCSVFileToCreate = null;

        try {
            // real all transactions
            allTranscations = this.readAllLinesUsingFilePathAndName(filePath, fileNameAndExtension);
            listOfBagOfWords = this.convertListOfStringsIntoListOfBagOfWords(allTranscations);

            // clean up allTransactions
            allTranscations = null;
            System.gc();

            uniqueTermsIndex = createUniqueTermsIndexAsListOfStrings(listOfBagOfWords);

            frequenciesTable
                    = this.convertListOfBagOfWordsIntoBinaryTDMAs2DArray(listOfBagOfWords,
                            uniqueTermsIndex,
                            weightingScheme);

            // mark for pruning
            frequenciesTable = this.prune2DArrayAndUniqueTermsIndexAsList(frequenciesTable,
                    uniqueTermsIndex,
                    pruneValueLowerLimit,
                    pruneValueUpperLimit,
                    minimumNumberOfItems);

            /*
             * convert uniqueTermsIndex and frequenciesTable into strings and save
             */
            commaSeparatedUniqueTermsIndex = this.convertUniqueTermsIndexListIntoCommaSeparatedTermsIndex(uniqueTermsIndex);
            commaSeparatedUniqueTermsIndex += "\n";

            // change the name of output file and write the title index
            csvFileNameAndExtension = fileNameAndExtension.substring(0, fileNameAndExtension.lastIndexOf(".")) + "_TDM.csv";

            // write title index to the file
            pathOfCSVFileToCreate = FileSystems.getDefault().getPath(filePath, csvFileNameAndExtension);
            Files.deleteIfExists(pathOfCSVFileToCreate);
            pathOfCSVFileToCreate = Files.write(pathOfCSVFileToCreate, commaSeparatedUniqueTermsIndex.getBytes(),
                    StandardOpenOption.CREATE);

            // carry out pruning and save 
            processCompletedSuccessfully = this.save2DArrayToOutputFileAsCSV(frequenciesTable, pathOfCSVFileToCreate);

            // clean up extra lists
            listOfBagOfWords = null;
            frequenciesTable = null;
            commaSeparatedTDMFrequenciesTable = null;
            commaSeparatedUniqueTermsIndex = null;

            System.gc();

        } catch (IOException ex) {
            Logger.getLogger(ListOfStringsToBinaryTDMUsing2DMatrix.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Failed. Could not complete the process.");
        }

        return processCompletedSuccessfully;
    }

    
    /**
     * Reads all lines as list of Strings
     * 
     * @param filePath path of the folder
     * @param fileNameAndExtension file name and extension
     * @return list of strings contained in file
     * @throws IOException throws IOException in case lines could not be read successfully from the provided path
     */
    public List<String> readAllLinesUsingFilePathAndName(String filePath, String fileNameAndExtension) throws IOException {
        System.out.println(new Date().toString() + ": Reading All Lines from : " + fileNameAndExtension);
        return Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), Charset.defaultCharset());
    }

    /**
     * Converts list of strings into list of bag of words
     * 
     * @param listOfStrings list of strings to be converted into bag of words
     * @return list of bags of words 
     */
    public List<List<String>> convertListOfStringsIntoListOfBagOfWords(List<String> listOfStrings) {
        System.out.println(new Date().toString() + ": Converting list of strings in tokenized strings...");
        List<List<String>> listOfBagOfWordsToReturn = new ArrayList<List<String>>();
        String[] tokens = null;

        for (String transaction : listOfStrings) {
            tokens = transaction.split(" ");

            List<String> tokensContainedInTransaction = new ArrayList<>();
            for (String token : tokens) {
                if (token.equals("") == false) {
                    tokensContainedInTransaction.add(token);
                }
            }

            listOfBagOfWordsToReturn.add(tokensContainedInTransaction);
        }
        System.gc();

        System.out.println(new Date().toString() + ": Done converting list of strings in tokenized strings.");

        return listOfBagOfWordsToReturn;
    }

    /**
     * Converts list of bag of words into comma separated binary values term-by-document matrix, as a String.
     * <br />
     * <br />
     * 
     * This method has slower performance.
     * 
     * @param listOfBagOfWords the list of bag or words to be converted into term-by-document matrix
     * @return term-by-document matrix as a string 
     * @deprecated deprecated due to lower performance, 
     * use com.starm.preprocessing.tdm.ListOfStringsToBinaryTDMUsing2DMatrix.convertListOfBagOfWordsIntoBinaryTDMAs2DArray() instead
     */
    public String convertListOfBagOfWordsIntoCommaSeparatedValuedBinaryTDM(List<List<String>> listOfBagOfWords) {
        System.out.println(new Date().toString() + ": Converting list of tokenized strings into binary TDM...");
        String commaSeparatedValuedTermDocumentMatrixToReturn = "";
        String commaSeparatedTermsIndex = "";

        // create commaSeparatedTermsIndex
        commaSeparatedTermsIndex = this.createUniqueTermsCommaSeparatedIndex(listOfBagOfWords);

        System.out.println(commaSeparatedTermsIndex);

        // set TDM title as commaSeparatedTermsIndex
        commaSeparatedValuedTermDocumentMatrixToReturn = commaSeparatedTermsIndex + "\n";

        // create binary frequencies based term document matrix
        for (Iterator<List<String>> it = listOfBagOfWords.iterator(); it.hasNext();) {
            List<String> bagOfWords = it.next();
            String binaryTermFrequenciesForThisDocument = commaSeparatedTermsIndex;

            // update binary frequencies 
            for (String term : bagOfWords) {
                if (binaryTermFrequenciesForThisDocument.startsWith(term + ",")) {
                    binaryTermFrequenciesForThisDocument
                            = binaryTermFrequenciesForThisDocument.replace(term + ",", 1 + ",");
                } else if (binaryTermFrequenciesForThisDocument.contains("," + term + ",")) {
                    binaryTermFrequenciesForThisDocument
                            = binaryTermFrequenciesForThisDocument.replace("," + term + ",", "," + 1 + ",");
                } else if (binaryTermFrequenciesForThisDocument.endsWith("," + term)) {
                    binaryTermFrequenciesForThisDocument
                            = binaryTermFrequenciesForThisDocument.replace("," + term, "," + 1);
                }
            }

            // remove the bagOfWords in hand for performance
            it.remove();

            // replace missing words with 0
            binaryTermFrequenciesForThisDocument
                    = binaryTermFrequenciesForThisDocument.replaceAll("[a-zA-Z]+", "0");

            System.out.println(binaryTermFrequenciesForThisDocument);

            // concatenate the updated frequencies for this transaction
            commaSeparatedValuedTermDocumentMatrixToReturn += binaryTermFrequenciesForThisDocument + "\n";
        }
        System.gc();

        System.out.println(new Date().toString() + ": Done converting list of tokenized strings into binary TDM...");

        return commaSeparatedValuedTermDocumentMatrixToReturn;
    }

    /**
     * Calculates IDF score 
     * 
     * @param listOfBagOfWords list of bags of words from which to find IDF score 
     * @param term word for which IDF score is to be found
     * @return the IDF score value
     */
    public float calculateIDF(List<List<String>> listOfBagOfWords, String term) {
        float idfToReturn = 0;
        float documentsContainingTheTerm = 0;
        float totalNumberOfDocuments = listOfBagOfWords.size();

        for (int i = 0; i < totalNumberOfDocuments; i++) {
            List<String> bagOfWords = listOfBagOfWords.get(i);

            if (bagOfWords.contains(term)) {
                ++documentsContainingTheTerm;
            }
        }
        // IDF calculation 
        idfToReturn = (float) Math.log10(totalNumberOfDocuments / documentsContainingTheTerm);

        return idfToReturn;
    }

    /**
     * Converts list of bag of words into term-by-document matrix as 2D array. 
     * 
     * @param listOfBagOfWords list of bag of words which are to be converted into term-by-document matrix
     * @param uniqueTermsIndexAsList list of unique terms contained in list of bag of words
     * @param weightingSchemeToUse weighting scheme to be used for calculating term-by-document matrix values
     * @return a 2-dimensional array containing the term-by-document matrix
     */
    public float[][] convertListOfBagOfWordsIntoBinaryTDMAs2DArray(List<List<String>> listOfBagOfWords,
            List<String> uniqueTermsIndexAsList, WeightingScheme weightingSchemeToUse) {
        System.out.println(new Date().toString() + ": Converting list of tokenized strings into binary TDM as 2D Array...");
        float[][] binaryTermDocumentMatrixToReturn = new float[listOfBagOfWords.size()][uniqueTermsIndexAsList.size()];
        HashMap<String, Float> idfCalculationsMap = new HashMap(listOfBagOfWords.size());

        // for each of the bag of words
        for (int i = 0; i < listOfBagOfWords.size(); i++) {
            List<String> bagOfWords = listOfBagOfWords.get(i);
            System.out.println(new Date().toString() + ": Converting transaction : " + i);

            // update frequencies 
            for (String term : bagOfWords) {
                // find the index of term in uniqueTermsIndexAsList
                int indexOfTermInUniqueTermsIndex = uniqueTermsIndexAsList.indexOf(term);

                // update frequency
                if (weightingSchemeToUse == WeightingScheme.BINARY) {
                    binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] = 1;
                } else if (weightingSchemeToUse == WeightingScheme.TF) {
                    ++binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex];
                } else if (weightingSchemeToUse == WeightingScheme.TFIDF) {
                    if (idfCalculationsMap.containsKey(term)) {
                        binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] = Collections.frequency(bagOfWords, term) * idfCalculationsMap.get(term);
                    } else {
                        binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] = calculateIDF(listOfBagOfWords, term);
                        idfCalculationsMap.put(term, binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex]);
                        binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] *= Collections.frequency(bagOfWords, term);
                    }
                } else if (weightingSchemeToUse == WeightingScheme.IDF) {
                    if (idfCalculationsMap.containsKey(term)) {
                        binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] = idfCalculationsMap.get(term);
                    } else {
                        binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex] = calculateIDF(listOfBagOfWords, term);
                        idfCalculationsMap.put(term, binaryTermDocumentMatrixToReturn[i][indexOfTermInUniqueTermsIndex]);
                    }
                }
            }
        }
        System.gc();

        System.out.println(new Date().toString() + ": Done converting list of tokenized strings into binary TDM as 2D Array...");
        System.out.println(new Date().toString() + ": Size of 2D Array : " + binaryTermDocumentMatrixToReturn.length);

        // printing values if weighting was IDF
        if (weightingSchemeToUse == WeightingScheme.IDF) {
            System.out.println("Printing idf scores. ");

            for (Map.Entry<String, Float> entry : idfCalculationsMap.entrySet()) {
                String key = entry.getKey();
                float value = entry.getValue();

                System.out.println(key + "\t" + value);
            }

            System.out.println("\nNumber of Words\tOccurence Frequency");
            long distinctScores = idfCalculationsMap.values().stream().sorted().distinct().count();
            Map<Float, List<Float>> dvMap = idfCalculationsMap.values().stream().sorted().sequential().collect(Collectors.groupingBy(Float::floatValue));
            dvMap.entrySet().stream().peek(e -> System.out.println(e.getValue().size() + "\t" + Math.round(1.0f / (Math.pow(10, e.getKey()) / listOfBagOfWords.size())) + " times.")).count();
        }
        
        return binaryTermDocumentMatrixToReturn;
    }

    /**
     * Creates sorted unique terms index as list of Strings
     * 
     * @param transactions list of bag of words from which to generate unique terms index
     * @return the sorted list of unique terms
     */
    public List<String> createUniqueTermsIndexAsListOfStrings(List<List<String>> transactions) {
        System.out.println(new Date().toString() + ": Creating unique terms index as list of string using tokenized transactions...");
        List<String> uniqueTermsIndex = new ArrayList<>();

        for (Iterator<List<String>> it = transactions.iterator(); it.hasNext();) {
            List<String> transaction = it.next();
            for (String token : transaction) {
                // if uniqueTermsIndex does not contain token, then add it
                if (uniqueTermsIndex.contains(token) == false) {
                    uniqueTermsIndex.add(token);
                }
            }
        }

        // Sort uniqueTermsIndex
        Collections.sort(uniqueTermsIndex);
        System.out.println(new Date().toString() + ": Size of Index : " + uniqueTermsIndex.size());

        System.gc();

        System.out.println(new Date().toString() + ": Done creating unique terms index as list of strings.");
        return uniqueTermsIndex;
    }

    /**
     * Creates unique terms comma separated index as String
     * <br />
     * <br />
     * 
     * @param transactions list of bag of words 
     * @return the unique terms index as String consisting of comma separated values 
     * @deprecated deprecated due to lower performance, use convertUniqueTermsIndexListIntoCommaSeparatedTermsIndex() instead
     */
    public String createUniqueTermsCommaSeparatedIndex(List<List<String>> transactions) {
        System.out.println(new Date().toString() + ": Creating unique terms comma separated index using tokenized transactions...");
        String commaSeparatedTermsIndexToReturn = "";
        List<String> uniqueTermsIndex = new ArrayList<>();

        for (Iterator<List<String>> it = transactions.iterator(); it.hasNext();) {
            List<String> transaction = it.next();
            for (String token : transaction) {
                // if uniqueTermsIndex does not contain token, then add it
                if (uniqueTermsIndex.contains(token) == false) {
                    uniqueTermsIndex.add(token);
                }
            }
        }

        // Sort uniqueTermsIndex
        Collections.sort(uniqueTermsIndex);
        System.out.println(new Date().toString() + ": Size of Index : " + uniqueTermsIndex.size());

        // Convert uniqueTermsIndex to commaSeparatedTermsIndexToReturn
        for (Iterator<String> u = uniqueTermsIndex.iterator(); u.hasNext();) {
            String term = u.next();

            if (u.hasNext()) {
                commaSeparatedTermsIndexToReturn += term + ",";
            } else {
                commaSeparatedTermsIndexToReturn += term;
            }
            u.remove();
        }

        System.gc();

        System.out.println(new Date().toString() + ": Done creating unique terms comma separated index.");

        return commaSeparatedTermsIndexToReturn;
    }

    /**
     * Converts the unique terms index list into comma separated unique terms index 
     * 
     * @param uniqueTermsIndex list of unique terms 
     * @return the string containing comma separated unique terms index
     */
    public String convertUniqueTermsIndexListIntoCommaSeparatedTermsIndex(List<String> uniqueTermsIndex) {
        System.out.println(new Date().toString() + ": Converting unique terms sorted list into comma separated index...");
        String commaSeparatedTermsIndexToReturn = "";

        // Convert uniqueTermsIndex to commaSeparatedTermsIndexToReturn
        for (Iterator<String> u = uniqueTermsIndex.iterator(); u.hasNext();) {
            String term = u.next();

            if (u.hasNext()) {
                commaSeparatedTermsIndexToReturn += term + ",";
            } else {
                commaSeparatedTermsIndexToReturn += term;
            }
        }
        
        System.out.println(new Date().toString() + ": Done converting unique terms sorted list into comma separated index...");
        System.out.println("Index:" + commaSeparatedTermsIndexToReturn);
        
        return commaSeparatedTermsIndexToReturn;
    }

    /**
     * Saves 2D Array to output CSV file 
     * 
     * <br />
     * <br />
     * 
     * Also performs the pruning. Transaction pruning, as well as term pruning. 
     * 
     * @param twoDimensionalArrayToConvert 2-dimensional array to save
     * @param pathOfOutputFileToSaveTermFrequencies path of output file
     * @return true if file saves, false if file could not be saved
     */
    public boolean save2DArrayToOutputFileAsCSV(float[][] twoDimensionalArrayToConvert,
            Path pathOfOutputFileToSaveTermFrequencies) {

        System.out.println(new Date().toString() + ": Saving 2D Array to output file...\n2D Array Contains: "
                + twoDimensionalArrayToConvert.length + " rows.");
        boolean processCompleted = false;
        String commaSeparatedTermFrequencies = null;

        for (int i = 0; i < twoDimensionalArrayToConvert.length; i++) {
            System.out.println(new Date().toString() + ": Converting row number " + i);

            // transaction pruning
            if (twoDimensionalArrayToConvert[i][0] == -2) {
                System.out.println(new Date().toString() + ": skipping row number " + i);
                continue;
            }
            commaSeparatedTermFrequencies = "";

            for (int j = 0; j < twoDimensionalArrayToConvert[i].length; j++) {
                // item pruning
                if (twoDimensionalArrayToConvert[i][j] == -1) {
                    continue;
                }

                if (twoDimensionalArrayToConvert[i][j] == 0) {
                    commaSeparatedTermFrequencies += ",";
                } else {
                    commaSeparatedTermFrequencies += "y,";
                }
            }
            System.out.println(new Date().toString() + ": Converted row number " + i);
            // start new line for each of new line
            commaSeparatedTermFrequencies = commaSeparatedTermFrequencies.substring(0, commaSeparatedTermFrequencies.lastIndexOf(","));
            commaSeparatedTermFrequencies += "\n";

            // call method to append the update
            ListOfStringsToBinaryTDMUsing2DMatrix
                    .appendContentVector(pathOfOutputFileToSaveTermFrequencies, commaSeparatedTermFrequencies);
        }
        
        processCompleted = true;
        System.out.println(new Date().toString() + ": Done Saving 2D Array to Output file...");
        System.gc();

        return processCompleted;
    }

    /**
     * Performs synchronized append operation to output file
     * 
     * @param outputFilePath path of output file
     * @param contentVectorToAppend string to append
     * @return true if append successful, false if append failed
     */
    public static synchronized Boolean appendContentVector(Path outputFilePath, String contentVectorToAppend) {

        System.out.println(new Date() + " : Appending content vector.");

        Boolean toReturn = false;

        try {
            Files.write(outputFilePath, contentVectorToAppend.getBytes(), StandardOpenOption.APPEND);

            toReturn = true;
        } catch (IOException e) {
            System.err.println("Couldn't append " + contentVectorToAppend + " to output file.");
            e.printStackTrace();
        }

        System.out.println(new Date() + " : Appended.");

        return toReturn;
    }

    /**
     * Marks items for pruning in 2D Array by using the term prune values provided by user
     * 
     * <ol>
     * 
     * <li>Marks -1 for those terms which have value less
     * than termPruneMinValue or greater than termPruneMaxValue</li> 
     * 
     * <li>Deletes -1ed terms from uniqueTermsIndexAsList. </li>
     * 
     * <li>Places -2 as first item of
     * those transactions which should be pruned out completely.</li>
     * 
     * </ol>
     * 
     * @param twoDimensionalArrayToPrune 2-dimensional array to mark items for pruning
     * @param uniqueTermsIndexAsList unique terms index as list of strings
     * @param termPruneMinValue lower bound of term prune value
     * @param termPruneMaxValue upper bound of term prune value
     * @param minimumNumberOfItemsInTransaction minimum number of terms in transaction 
     * 
     * @return Returns float[][] with -1 for those terms which have value less
     * than termPruneMinValue or greater than termPruneMaxValue. Also deletes
     * -1ed terms from uniqueTermsIndexAsList. Also places -2 as first item of
     * those transactions which should be pruned out completely.
     */
    public float[][] prune2DArrayAndUniqueTermsIndexAsList(float[][] twoDimensionalArrayToPrune,
            List<String> uniqueTermsIndexAsList, float termPruneMinValue, float termPruneMaxValue,
            float minimumNumberOfItemsInTransaction) {
        System.out.println(new Date().toString() + ": Started marking for pruning ... ");
        float[][] twoDimensionalArrayToReturn = null;
        List<Integer> indexesOfTermsToPruneOut = new ArrayList<>();
        List<Integer> indexesOfTransactionsToSkip = new ArrayList<>();
        int numberOfTermsPresent = 0;

        for (int i = 0; i < twoDimensionalArrayToPrune.length; i++) {
            float[] transaction = twoDimensionalArrayToPrune[i];
            numberOfTermsPresent = 0;

            for (int j = 0; j < transaction.length; j++) {
                // term prune preprocessing
                if (transaction[j] != 0
                        && (transaction[j] < termPruneMinValue || transaction[j] > termPruneMaxValue)) {
                    transaction[j] = -1;
                    if (!indexesOfTermsToPruneOut.contains(j)) {
                        indexesOfTermsToPruneOut.add(j);
                        System.out.println(new Date().toString() + ": Marked term for pruning : " + uniqueTermsIndexAsList.get(j));
                    }
                }

                // transaction prune preprocessing
                if (transaction[j] >= termPruneMinValue || transaction[j] <= termPruneMaxValue) {
                    ++numberOfTermsPresent;
                }
            }

            // transaction prune preprocessing
            if (numberOfTermsPresent < minimumNumberOfItemsInTransaction) {
                indexesOfTransactionsToSkip.add(i);
                System.out.println(new Date().toString() + ": Marked transaction for skipping : " + i);
            }
        }

        //sort indexesOfTermsToPruneOut
        Collections.sort(indexesOfTermsToPruneOut);

        // mark for pruning out item in all transactions
        for (Integer itemToPruneOut : indexesOfTermsToPruneOut) {
            for (int i = 0; i < twoDimensionalArrayToPrune.length; i++) {
                twoDimensionalArrayToPrune[i][itemToPruneOut] = -1;
            }
        }

        for (int i = 0; i < twoDimensionalArrayToPrune.length; i++) {
            if (indexesOfTransactionsToSkip.contains(i)) {
                twoDimensionalArrayToPrune[i][0] = -2;
            }
        }

        // update uniqueTermsIndexAsList        
        Collections.reverse(indexesOfTermsToPruneOut);
        for (Integer indexOfUniqueTermToRemove : indexesOfTermsToPruneOut) {
            System.out.println(new Date().toString() + " Removed from index : " + uniqueTermsIndexAsList.remove((int) indexOfUniqueTermToRemove).toString());
        }
        System.out.println(new Date().toString() + ": Size of remaining uniqueTermsIndexList : " + uniqueTermsIndexAsList.size());

        System.out.println(new Date().toString() + ": Completed marking for pruning ... ");

        return twoDimensionalArrayToReturn = twoDimensionalArrayToPrune;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Date startTime = new Date();
        String filePath = "E:\\wasif\\myMSCS\\thesis\\experimentation\\spatiotemporal";
        String fileNameAndExtension = "angioplasty.org_v4_0_USA_t_pp.txt";
        boolean status = false;

        ListOfStringsToBinaryTDMUsing2DMatrix instance = new ListOfStringsToBinaryTDMUsing2DMatrix();
        status = instance.convertTextFileToBinaryCSVBasedTDM(filePath, fileNameAndExtension, WeightingScheme.IDF, 2.0f, 4f, 2);
        if (status) {
            System.out.println("Success.");
        } else {
            System.out.println("Failed.");
        }

        System.out.println("Started at : " + startTime.toString() + "\nCompleted at : " + new Date());
    }

}
