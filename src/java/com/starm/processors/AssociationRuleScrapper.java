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

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides functionality for association rule scrapping and searching of association rule supporting
 * transactions.
 * 
 * @author Wasif Altaf
 */
public class AssociationRuleScrapper {

    /**
     * Extracts premise items from association rule
     * 
     * @param associationRule association rule from which to extract premise items
     * @return premise items
     */
    public static String[] extractPremiseItems(String associationRule) {
        String[] itemsToReturn = new String[0];

        try {
            if (associationRule.contains("[")
                    && associationRule.contains("]")) {

                String premise = associationRule.substring(associationRule.indexOf("[") + 1, associationRule.indexOf("]"));

                itemsToReturn = premise.split(", ");

                for (int i = 0; i < itemsToReturn.length; i++) {
                    if (itemsToReturn[i].contains("=")) {
                        itemsToReturn[i] = itemsToReturn[i].substring(0, itemsToReturn[i].indexOf("="));
                    }
                }

            }

        } catch (Exception e) {
            System.err.println("Exception in extractPremiseItems: \n" + e.getMessage());
            e.printStackTrace();
        }

        return itemsToReturn;
    }

    /**
     * Extracts consequence items from association rule
     * 
     * @param associationRule association rule from which to extract consequence items
     * @return consequence items
     */
    public static String[] extractConsequenceItems(String associationRule) {
        String[] itemsToReturn = null;

        try {
            if (associationRule.contains("[")
                    && associationRule.contains("]")) {
                String consequence = associationRule.substring(associationRule.lastIndexOf("[") + 1, associationRule.lastIndexOf("]"));

                itemsToReturn = consequence.split(", ");

                for (int i = 0; i < itemsToReturn.length; i++) {
                    if (itemsToReturn[i].contains("=")) {
                        itemsToReturn[i] = itemsToReturn[i].substring(0, itemsToReturn[i].indexOf("="));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in extractPremiseItems: \n" + e.getMessage());
            e.printStackTrace();
        }

        return itemsToReturn;
    }

    /**
     * Finds negative items from the given items 
     * 
     * @param items positive and/or negative items
     * @return negative items
     */
    public static String[] findNegatedItems(String[] items) {
        List<String> listToReturn = new ArrayList();

        for (String item : items) {
            if (item.startsWith("~")) {
                listToReturn.add(item);
            }
        }

        return listToReturn.toArray(new String[0]);
    }

    /**
     * Finds positive items from the given items 
     * 
     * @param items positive and/or negative items
     * @return positive items
     */
    public static String[] findNormalItems(String[] items) {
        List<String> listToReturn = new ArrayList();

        for (String item : items) {
            if (!item.startsWith("~")) {
                listToReturn.add(item);
            }
        }

        return listToReturn.toArray(new String[0]);
    }

    /**
     * Removes negation symbol from the start of item
     * 
     * @param item item from which to remove negation symbol
     * @param negationSymbol the negation symbol to be removed
     * @return the item with negation symbol removed
     */
    public static String removeNegation(String item, String negationSymbol) {

        if (item.startsWith(negationSymbol)) {
            item = item.substring(negationSymbol.length(), item.length());
        }

        return item;
    }

    /**
     * Removes negation symbol from the start of items
     * 
     * @param items items from which to remove negation symbol
     * @param negationSymbol the negation symbol to be removed
     * @return items with negation symbol removed from start
     */
    public static String[] removeNegation(String[] items, String negationSymbol) {
        for (int i = 0; i < items.length; i++) {
            items[i] = removeNegation(items[i], negationSymbol);
        }

        return items;
    }

    /**
     * Tests whether the array of tokens contains all items to check
     * 
     * @param tokens items to search from
     * @param itemsToCheck items to be searched for presence
     * @return true if all itemsToCheck are found, false if not all itemsToCheck were found in tokens
     */
    public static boolean containsAll(String[] tokens, String[] itemsToCheck) {
        if (itemsToCheck.length > tokens.length) {
            return false;
        }

        return Arrays.asList(tokens).containsAll(Arrays.asList(itemsToCheck));
    }

    /**
     * Tests whether the array of tokens does not contain all of the items to check
     * 
     * @param tokens items to search from
     * @param itemsToCheck items to be searched for absence
     * @return true if all itemsToCheck were absent, false if not all itemsToCheck were absent from tokens
     */
    public static boolean doesNotContainAll(String[] tokens, String[] itemsToCheck) {

        for (String itemToCheck : itemsToCheck) {
            for (String token : tokens) {
                if (itemToCheck.equals(token)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Test whether the given itemsToCheckForPresence are present, and given itemsToCheckForAbsence
     * are absent from the items to search from
     * 
     * @param tokens items to search from
     * @param itemsToCheckForPresence items to be checked for presence 
     * @param itemsToCheckForAbsence items to be for absence 
     * @return if items to check for presence are present in tokens, and the items to
     * check for absence are absent from tokens, then returns true, otherwise returns false
     */
    public static boolean checkPresenceAndAbsenceOfItems(String[] tokens,
            String[] itemsToCheckForPresence,
            String[] itemsToCheckForAbsence) {

        if (containsAll(tokens, itemsToCheckForPresence) == true
                && doesNotContainAll(tokens, itemsToCheckForAbsence) == true) {
            return true;
        }

        return false;
    }

    /**
     * Finds strings which both contain itemsToCheckForPresence and do not contain itemsToCheckForAbsence
     * 
     * @param transactions list of strings from which to perform searching
     * @param itemsToCheckForPresence items to check for presence
     * @param itemsToCheckForAbsence items to check for absence
     * @return strings which both contain itemsToCheckForPresence and do not contain itemsToCheckForAbsence
     */
    public static String[] searchSupportingTransactions(List<String> transactions,
            String[] itemsToCheckForPresence,
            String[] itemsToCheckForAbsence) {

        List<String> transactionsToReturn = new ArrayList<>();
        String[] tokens;

        for (String transaction : transactions) {
            tokens = transaction.split(" ");

            if (checkPresenceAndAbsenceOfItems(tokens,
                    itemsToCheckForPresence, itemsToCheckForAbsence)) {
                transactionsToReturn.add(transaction);
            }
        }

        return transactionsToReturn.toArray(new String[0]);
    }
    
    /**
     * Finds transaction IDs of strings which both contain itemsToCheckForPresence 
     * and do not contain itemsToCheckForAbsence
     * 
     * @param transactions list of transactions from which to perform searching
     * @param itemsToCheckForPresence items to be checked for presence
     * @param itemsToCheckForAbsence items to be checked for absence
     * @return 1-indexed transaction IDs of transactions both containing itemsToCheckForPresence 
     * and not containing itemsToCheckForAbsence
     */
    public static Integer[] searchSupportingTransactionsIds(List<String> transactions,
            String[] itemsToCheckForPresence,
            String[] itemsToCheckForAbsence) {

        List<Integer> transactionsIdsToReturn = new ArrayList<>();
        String[] tokens;

        for (int i = 0; i < transactions.size(); i++) {
            tokens = transactions.get(i).split(" ");

            if (checkPresenceAndAbsenceOfItems(tokens,
                    itemsToCheckForPresence, itemsToCheckForAbsence)) {
                transactionsIdsToReturn.add(i+1);
            }
        }

        return transactionsIdsToReturn.toArray(new Integer[0]);
    }   
    
    /**
     * Finds the transaction IDs for association rule supporting transactions
     * from the input file 
     * 
     * @param filePath folder path for preprocessed dataset file
     * @param fileNameAndExtension file name and extension of preprocessed dataset file
     * @param associationRule association rule for which supporting transactions IDs are to be searched
     * @return 1-indexed transaction IDs of transactions supporting the rule
     */
    public static Integer[] searchSupportingTransactionsIds(String filePath, 
            String fileNameAndExtension, String associationRule) {
        List<String> transactions = null;
        Integer[] transactionsIdsToReturn = null;
        String[] allItems;
        String[] itemsToCheckForPresence;
        String[] itemsToCheckForAbsence;

        try {
            // extract items from association rule
            allItems = join(AssociationRuleScrapper.extractPremiseItems(associationRule), AssociationRuleScrapper.extractConsequenceItems(associationRule));
            itemsToCheckForPresence = findNormalItems(allItems);
            itemsToCheckForAbsence = removeNegation(findNegatedItems(allItems), "~");

            // read all transactions from input file
            transactions = Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), Charset.defaultCharset());

            // perform search
            transactionsIdsToReturn = AssociationRuleScrapper.searchSupportingTransactionsIds(transactions, itemsToCheckForPresence, itemsToCheckForAbsence);

        } catch (Exception e) {
            System.err.println("Exception in searchSupportingTransactions.");
            e.printStackTrace();
        }

        return transactionsIdsToReturn;
    }
    
    /**
     * Finds the transaction IDs for association rule supporting transactions
     * from the input file have the specified Charset
     * 
     * @param associationRule association rule for which to find the supporting transaction IDs
     * @param filePath folder path of preprocessed dataset file
     * @param fileNameAndExtension file name and extension of the preprocessed dataset file
     * @param fileCharset characterset of preprocessed dataset file
     * @return 1-indexed transaction IDs of transactions supporting the given association rule
     */
    public static Integer[] searchSupportingTransactionsIds(String associationRule, String filePath, 
            String fileNameAndExtension, Charset fileCharset) {
        List<String> transactions = null;
        Integer[] transactionsIdsToReturn = null;
        String[] allItems;
        String[] itemsToCheckForPresence;
        String[] itemsToCheckForAbsence;

        try {
            // extract items from association rule
            allItems = join(AssociationRuleScrapper.extractPremiseItems(associationRule), AssociationRuleScrapper.extractConsequenceItems(associationRule));
            itemsToCheckForPresence = findNormalItems(allItems);
            itemsToCheckForAbsence = removeNegation(findNegatedItems(allItems), "~");

            // read all transactions from input file
            transactions = Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), fileCharset);

            // perform search
            transactionsIdsToReturn = AssociationRuleScrapper.searchSupportingTransactionsIds(transactions, itemsToCheckForPresence, itemsToCheckForAbsence);

        } catch (Exception e) {
            System.err.println("Exception in searchSupportingTransactions.");
            e.printStackTrace();
        }

        return transactionsIdsToReturn;
    }

    /**
     * Finds the association rule supporting transactions
     * from the input file
     * 
     * @param filePath folder path for preprocessed dataset file
     * @param fileNameAndExtension file name and extension of preprocessed dataset file
     * @param associationRule association rule for which to find supporting transactions
     * @return array of strings containing the association rule supporting transactions
     */
    public static String[] searchSupportingTransactions(String filePath, 
            String fileNameAndExtension, String associationRule) {
        List<String> transactions = null;
        String[] transactionsToReturn = null;
        String[] allItems;
        String[] itemsToCheckForPresence;
        String[] itemsToCheckForAbsence;

        try {
            // extract items from association rule
            allItems = join(AssociationRuleScrapper.extractPremiseItems(associationRule), AssociationRuleScrapper.extractConsequenceItems(associationRule));
            itemsToCheckForPresence = findNormalItems(allItems);
            itemsToCheckForAbsence = removeNegation(findNegatedItems(allItems), "~");

            // read all transactions from input file
            transactions = Files.readAllLines(FileSystems.getDefault().getPath(filePath, fileNameAndExtension), Charset.defaultCharset());

            // perform search
            transactionsToReturn = AssociationRuleScrapper.searchSupportingTransactions(transactions, itemsToCheckForPresence, itemsToCheckForAbsence);

        } catch (Exception e) {
            System.err.println("Exception in searchSupportingTransactions.");
            e.printStackTrace();
        }

        return transactionsToReturn;
    }
    
    /**
     * Retrieves transaction by ID from unprocessed dataset
     * 
     * @param transactionId transaction id
     * @param filePath folder path for unprocessed dataset
     * @param fileNameAndExtension file name and extension of unprocessed dataset
     * @param fileCharset characterset of unprocessed dataset file
     * @param fileHasHeaderRow whether file has a header row or not
     * @return the transaction for transaction ID, 
     * or null if no transaction existed for that transaction ID 
     */
    public static String retrieveTransactionById(int transactionId, String filePath, 
            String fileNameAndExtension, Charset fileCharset, boolean fileHasHeaderRow){
        String transactionToReturn = null;
        Path path;
        BufferedReader br;
        
        try {
            // prepare reader
            path = FileSystems.getDefault().getPath(filePath, fileNameAndExtension);
            br = Files.newBufferedReader(path, fileCharset);
            
            // read transaction
            if (fileHasHeaderRow) {         
                // skip title read transaction
                transactionToReturn = br.lines().sequential().skip(transactionId).findFirst().get();
            }
            else{
                // read transaction
                transactionToReturn = br.lines().sequential().skip(transactionId - 1).findFirst().get();
            }
            
            br.close();
        } catch (Exception e) {
            System.err.println("Exception in retrieveTransactionById.");
            System.err.println("Could not retrieve transaction for :" + transactionId);
            e.printStackTrace();
        }   
        
        return transactionToReturn;
    }
    
    /**
     * Retrieves transactions by IDs from unprocessed dataset
     * 
     * @param transactionIds transaction IDs of the transactions to be retrieved 
     * from unprocessed dataset
     * @param filePath folder path for unprocessed dataset
     * @param fileNameAndExtension file name and extension of unprocessed dataset
     * @param fileCharset characterset of unprocessed dataset file
     * @param fileHasHeaderRow whether file has a header row or not
     * @return array of transactions for transactionIds, null if no transactions 
     * were found for given transactionIds
     */
    public static String[] retrieveTransactionsByIds(Integer[] transactionIds, String filePath, 
            String fileNameAndExtension, Charset fileCharset, boolean fileHasHeaderRow){
        String[] transactionsToReturn = null;
        Path path;
        List<String> allLines;
                
        try {
            // initialize
            transactionsToReturn = new String[transactionIds.length];
                 
            // read all lines
            path = FileSystems.getDefault().getPath(filePath, fileNameAndExtension);
            allLines = Files.readAllLines(path, fileCharset);
            
            // sort transactionIds
            Arrays.sort(transactionIds);
                        
            if (fileHasHeaderRow) {                
                // retrieve transactions one by one, for title
                for (int i = 0; i < transactionIds.length; i++) {
                    transactionsToReturn[i] = allLines.get(transactionIds[i]);
                }
            }
            else{
                // retrieve transactions one by one, without title
                for (int i = 0; i < transactionIds.length; i++) {
                    transactionsToReturn[i] = allLines.get(transactionIds[i] - 1);
                }
            }
            
            allLines = null;            
        } catch (Exception e) {
            System.err.println("Exception in retrieveTransactionsByIds.");
            e.printStackTrace();
        }   
        
        return transactionsToReturn;
    }
    
    /**
     * Retrieves unprocessed transactions supporting association rule 
     * 
     * @param associationRule association rule for which to find supporting transactions
     * @param ppFilePath folder path for preprocessed dataset file 
     * @param ppFileNameAndExtension file name and extension of preprocessed dataset file
     * @param ppFileCharset characterset of preprocessed dataset file
     * @param stFilePath folder path for unprocessed dataset file
     * @param stFileNameAndExtension file name and extension of unprocessed dataset file
     * @param stFileCharset characterset of unprocessed dataset file
     * @param stFileHasHeaderRow whether the unprocessed dataset file has header row or not
     * @return array of unprocessed transactions, or null if no association rule supporting transactions
     * could be found
     */
    public static String[] retrieveSpatioTemporalTransactions(String associationRule,
            String ppFilePath, String ppFileNameAndExtension, Charset ppFileCharset,
            String stFilePath, String stFileNameAndExtension, Charset stFileCharset, boolean stFileHasHeaderRow){
        String[] stTransactionsToReturn = null;
        Integer[] ppTransactionsIds;
        
        try {
            // search transaction ids of rule supporting transactions from preprocessed (pp) file
            ppTransactionsIds = searchSupportingTransactionsIds(associationRule, ppFilePath, ppFileNameAndExtension, ppFileCharset);
            
            System.out.println("Finding ppTransactionsIds : " + Arrays.toString(ppTransactionsIds) 
                    + " in " + stFilePath + File.separator + stFileNameAndExtension);
            
            // retrieve spatio-temporal transactions from original dataset file
            stTransactionsToReturn = retrieveTransactionsByIds(ppTransactionsIds, stFilePath, stFileNameAndExtension, stFileCharset, stFileHasHeaderRow);
                  
        } catch (Exception e) {
            System.err.println("Exception in retrieveSpatioTemporalTransactions : " + associationRule);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
        return stTransactionsToReturn;
    }

    /**
     * Joins array of strings. 
     * <br />
     * <br />
     * Allows duplicate items to exist.
     * 
     * @param parms string arrays to be joined
     * @return a single array containing all the arrays joined 
     */
    public static String[] join(String[] ... parms) {       
        int size = 0;

        for (String[] array : parms) {
            size += array.length;
        }

        String[] result = new String[size];

        int j = 0;
        for (String[] array : parms) {
            for (String s : array) {
                result[j++] = s;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        String rule = "[~class=y, ~counterpulsation=y, reimburse=y]: 4 ==> [~external=y, iv=y, ~enhance=y]: 4   <conf:(1)> lift:(1776.6) lev:(0) conv:(4) ";
        String negativeItem = "~Hello";
        String ruleToSearchForSupport = "[incorporate=y]: 5 ==> [endothelial=y]: 4   <conf:(0.8)> lift:(157.92) lev:(0.000447) conv:(2.487335)";
        String filePath = "E:\\wasif\\myMSCS\\thesis\\experimentation\\complete\\Ci1";
        String fileNameAndExtension = "complete_pp.txt";
        
        System.out.println("Rule: " + rule);

        System.out.println("Premise Items: ");
        for (String item : AssociationRuleScrapper.extractPremiseItems(rule)) {
            System.out.println("\t" + item);
        }

        System.out.println("\nConsequence Items: ");
        for (String item : AssociationRuleScrapper.extractConsequenceItems(rule)) {
            System.out.println("\t" + item);
        }

        System.out.println("\nMerged:");
        for (String item : join(AssociationRuleScrapper.extractPremiseItems(rule), AssociationRuleScrapper.extractConsequenceItems(rule))) {
            System.out.println("\t" + item);
        }

        System.out.println("\nNegated Items: ");
        for (String item : findNegatedItems(join(AssociationRuleScrapper.extractPremiseItems(rule)))) {
            System.out.println("\t" + item);
        }

        System.out.println("\nNormal Items: ");
        for (String item : findNormalItems(join(AssociationRuleScrapper.extractPremiseItems(rule)))) {
            System.out.println("\t" + item);
        }

        System.out.println("\nRemove Negation from: " + negativeItem);
        System.out.println("\t" + removeNegation(negativeItem, "~"));

        System.out.print("\nRemove Negation from: ");
        for (String item : findNegatedItems(join(AssociationRuleScrapper.extractPremiseItems(rule), AssociationRuleScrapper.extractConsequenceItems(rule)))) {
            System.out.print(item + " ");
        }
        System.out.println("");
        for (String item : removeNegation(
                findNegatedItems(
                        join(AssociationRuleScrapper.extractPremiseItems(rule),
                                AssociationRuleScrapper.extractConsequenceItems(rule))), "~")) {
            System.out.println("\t" + item);
        }
        
        System.out.println("\nSearch supporting transactions..");
        
        for (String transaction : AssociationRuleScrapper.searchSupportingTransactions(filePath, fileNameAndExtension, ruleToSearchForSupport)) {
            System.out.println("t" +transaction);
        }
    }
}
