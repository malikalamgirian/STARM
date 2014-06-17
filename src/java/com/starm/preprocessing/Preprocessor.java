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
package com.starm.preprocessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main preprocessor, utilizes multiple types of preprocessing steps such as <br /><br />
 * 
 * <ul>
 *  <li>Case Normalization</li>
 *  <li>Slash Normalization</li>
 *  <li>POS Tagging</li>
 *  <li>POS Tags based filtration</li>
 *  <li>Token based Cleaning</li>
 *  <li>Lemmatization</li>
 *  <li>Stop word removal</li>
 *  <li>Term base form replacements</li>
 *  <li>POS Tag Removal</li>
 * </ul>
 * 
 * @see com.starm.preprocessing.Cleaner
 * @see com.starm.preprocessing.Lemmatizer
 * @see com.starm.preprocessing.Normalizer
 * @see com.starm.preprocessing.POSTagBasedFilter
 * @see com.starm.preprocessing.PosTagger
 * @see com.starm.preprocessing.StopWordRemover
 * @see com.starm.preprocessing.SynonymReplacer
 * 
 * @author Wasif Altaf
 */
public class Preprocessor {

    /**
     * Preprocesses the input file and saves in the same folder with file name
     * extended with _pp postfix. Considers the input file to be of charset StandardCharsets.ISO_8859_1
     *
     * @param filePath folder path of file to be preprocessed
     * @param fileNameAndExtension file name and extension of the file to be preprocessed
     * 
     * @return true if preprocessing is successful, false if preprocessing failed
     */
    public boolean process(String filePath, String fileNameAndExtension) {
        return process(filePath, fileNameAndExtension, StandardCharsets.ISO_8859_1);
    }

    /**
     * Preprocesses the input file and save in the same folder with file name
     * extended with _pp postfix.
     *
     * @param filePath folder path of file to be preprocessed
     * @param fileNameAndExtension file name and extension of the file to be preprocessed
     * @param charsetOfFile character set of input file
     * 
     * @return true if preprocessing was successful, false if preprocessing failed.
     */
    public boolean process(String filePath, String fileNameAndExtension, Charset charsetOfFile) {
        Date startTime = new Date();
        boolean processSuccessStatus = false;
        List<String> transactions = null;
        HashMap<String, String> uniqueTermsIndex = new HashMap<>();
        Path outputFilePath = null;
        String transaction = null;
        Map synonymReplacementMap;
        Logger logger = Logger.getLogger(Preprocessor.class.getName());

        System.out.println(new Date() + ": Reading all lines...");
        // read all strings
        try {
            transactions = Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), charsetOfFile);

        } catch (IOException ex) {
            Logger.getLogger(Preprocessor.class.getName()).log(Level.SEVERE, null, ex);
            return processSuccessStatus = false;
        }
        System.out.println(new Date() + ": Read all lines...");

        System.out.println(new Date() + ": Printing all characters...");
        CharactersAnalysis.printUniqueCharacters(transactions);
        System.out.println(new Date() + ": Printed all characters...");

        // preprocess each transaction
        for (int i = 0; i < transactions.size(); i++) {
            // get transaction
            transaction = transactions.get(i);

            System.out.println("Preprocessing transaction: " + (i + 1) + " out of " + transactions.size() + " for " + fileNameAndExtension);
            System.out.println("-------------------------------------------");
            System.out.println(new Date() + ": Normalizing: " + transaction);

            // normalize case
            transaction = Normalizer.normalize(transaction);

            // normalize slashes
            transaction = Normalizer.normalizeSlashes(transaction);
            System.out.println(new Date() + ": Normalized: " + transaction);

            System.out.println(new Date() + ": POS Tagging: " + transaction);

            // pos tag
            transaction = PosTagger.posTagString(transaction);
            System.out.println(new Date() + ": POS Tagged: " + transaction);

            System.out.println(new Date() + ": Filtering: " + transaction);

            // apply pos tag based filter
            transaction = POSTagBasedFilter.applyPOSBasedFilter(transaction);
            System.out.println(new Date() + ": Filtered: " + transaction);

            System.out.println(new Date() + ": Cleaning: " + transaction);

            // clean
            transaction = Cleaner.cleanString(transaction, "_");
            System.out.println(new Date() + ": Cleaned: " + transaction);

            System.out.println(new Date() + ": Lemmatizing: " + transaction);

            // lemmatize
            transaction = Lemmatizer.lemmatizeString(transaction, "_");
            System.out.println(new Date() + ": Lemmatized: " + transaction);

            System.out.println(new Date() + ": Removing stop words: " + transaction);

            // remove stop words
            transaction = StopWordRemover.removeStopWords(transaction, "_");
            System.out.println(new Date() + ": Stop words removed: " + transaction);

            // add to unique terms index
            // index contains terms as well as their pos tags
            uniqueTermsIndex.putAll(SynonymReplacer.stringToMap(transaction, " ", "_"));

            System.out.println("-------------------------------------------");

            // update transactions
            transactions.set(i, transaction);
        }

        // build synonym replacement map
        synonymReplacementMap = SynonymReplacer.buildSynonymReplacementMap(uniqueTermsIndex);

        // post-process each transaction 
        // replace synonyms
        // removed pos tags
        for (int i = 0; i < transactions.size(); i++) {
            // get transaction
            transaction = transactions.get(i);

            System.out.println("Post-processing transaction: " + (i + 1) + " out of " + transactions.size() + " for " + fileNameAndExtension);
            System.out.println("-------------------------------------------");

            System.out.println(new Date() + ": Replacing synonyms: " + transaction);

            // synonym replacement
            transaction = SynonymReplacer.applySynonymReplacement(synonymReplacementMap, transaction, " ", "_");
            System.out.println(new Date() + ": Replaced synonyms: " + transaction);

            System.out.println(new Date() + ": Removing posTags: " + transaction);

            // remove posTags replacement
            transaction = PosTagger.removePosTags(transaction, " ", "_");
            System.out.println(new Date() + ": Removed posTags: " + transaction);

            System.out.println("-------------------------------------------");

            // update transactions
            transactions.set(i, transaction);
        }

        // change the name of output file
        fileNameAndExtension = fileNameAndExtension.substring(0, fileNameAndExtension.lastIndexOf(".")) + "_pp.txt";
        outputFilePath = FileSystems.getDefault().getPath(filePath, fileNameAndExtension);
        try {
            Files.deleteIfExists(outputFilePath);
        } catch (IOException ex) {
            Logger.getLogger(Preprocessor.class.getName()).log(Level.SEVERE, null, ex);
            return processSuccessStatus = false;
        }

        System.out.println(new Date() + ": Saving output file: " + fileNameAndExtension);

        // write file
        for (String transactionInHand : transactions) {
            try {
                Files.write(outputFilePath, (transactionInHand + "\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
            } catch (IOException ex) {
                Logger.getLogger(Preprocessor.class.getName()).log(Level.SEVERE, null, ex);
                return processSuccessStatus = false;
            }
        }
        // cleanup
        transactions = null;
        System.gc();

        System.out.println(new Date() + ": Saved output file: " + fileNameAndExtension);

        System.out.println("Process started at : " + startTime.toString() + "\nProcess completed at : " + new Date());

        return processSuccessStatus = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Preprocessor pp = new Preprocessor();
        String filePath = "E:\\wasif\\myMSCS\\thesis\\experimentation\\spatiotemporal";
        String fileNameAndExtension = "angioplasty.org_v4_0_USA_t.txt";

        if (pp.process(filePath, fileNameAndExtension)) {
            System.out.println("Success.");
        } else {
            System.out.println("Failure.");
        }
    }

}
