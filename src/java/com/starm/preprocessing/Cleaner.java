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


/**
 * Provides functionality to clean String(s)
 * 
 * @author Wasif Altaf
 */
public class Cleaner {

    /**
     * Cleans the POS tagged String 
     * 
     * @param stringToClean the string to be cleaned
     * @param posTagSeparatorChar char(s) with which the word and the POS tag have been separated
     * @return the cleaned String
     */
    public static String cleanString(String stringToClean, String posTagSeparatorChar) {
        String cleanedStringToReturn = "";
        String[] posTaggedTokens = null;
        String wordToClean = "";
        String posTagOfWordToClean = "";

        try {
            // tokenize
            posTaggedTokens = stringToClean.split(" ");

            // apply cleaning rules and join
            for (int i = 0; i < posTaggedTokens.length; i++) {
                wordToClean = posTaggedTokens[i].substring(0, posTaggedTokens[i].indexOf(posTagSeparatorChar));
                posTagOfWordToClean = posTaggedTokens[i].substring(posTaggedTokens[i].indexOf(posTagSeparatorChar) + 1, posTaggedTokens[i].length());

                wordToClean = applyCleaningRules(wordToClean);

                if (wordToClean != null) {
                    cleanedStringToReturn += wordToClean + posTagSeparatorChar + posTagOfWordToClean + " ";
                }
            }

            // trim
            cleanedStringToReturn = cleanedStringToReturn.trim();

        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
            return "";
        }

        return cleanedStringToReturn;
    }

    /**
     * Cleans the word using word cleaning rules
     * 
     * @param wordToClean word to be cleaned
     * @return null if the wordToClean can not be cleaned or is a dirty word and should be
     * discarded, otherwise returns the cleaned word
     */
    public static String applyCleaningRules(String wordToClean) {

        // cases where wordToClean cannot be cleaned hence NULL is returned
        if (wordToClean.matches(".*[0-9]+.*")
                || wordToClean.contains("!")
                || wordToClean.contains("?")
                || wordToClean.contains("%")
                || wordToClean.contains("#")
                || wordToClean.contains(">")
                || wordToClean.contains("<")
                || wordToClean.contains(",")
                || wordToClean.contains("+")
                || wordToClean.contains("*")
                || wordToClean.contains("=")
                || wordToClean.contains("&")
                || wordToClean.contains("$")
                || wordToClean.contains("@")
                || wordToClean.contains(";")
                || wordToClean.contains(":")
                || wordToClean.contains("\\")
                || wordToClean.contains("]")
                || wordToClean.contains("[")
                || wordToClean.contains("{")
                || wordToClean.contains("}")
                || wordToClean.contains("(")
                || wordToClean.contains(")")
                || wordToClean.contains("|")
                || wordToClean.contains("_")
                || wordToClean.contains(".")
                || wordToClean.contains("'")
                || wordToClean.contains("`")
                || wordToClean.contains("~")
                || wordToClean.contains("©")
                || wordToClean.contains("®")
                || wordToClean.contains("â")
                || wordToClean.contains("œ")
                || wordToClean.contains("‡")
                || wordToClean.contains("¡")
                || wordToClean.contains("¢")
                || wordToClean.contains("¥")
                || wordToClean.contains("£")
                || wordToClean.contains("¦")
                || wordToClean.contains("¼")
                || wordToClean.contains("½")
                || wordToClean.contains("Â")
                || wordToClean.contains("Ã")
                || wordToClean.contains("“")
                || wordToClean.contains("”")
                || wordToClean.contains("„")
                || wordToClean.contains("€")
                || wordToClean.contains("™")
                || wordToClean.contains("ª")
                || wordToClean.contains("¬")) {
            return null;
        }
        return wordToClean;
    }

}
