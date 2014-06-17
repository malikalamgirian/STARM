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

import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;

/**
 * Convenience class uses BioLemmatizer to perform lemmatization tasks
 * 
 * @author Wasif Altaf
 */
public class Lemmatizer {

    private static BioLemmatizer bioLemmatizer = null;

    static {
        bioLemmatizer = new BioLemmatizer();
    }

    public Lemmatizer() {
        bioLemmatizer = new BioLemmatizer();
    }

    /**
     * Lemmatizes the wordToLemmatize using BioLemmatizer
     * 
     * @param wordToLemmatize the word to be lemmatized 
     * @param posTagOfWordToLemmatize the POS tag for the word to be lemmatized
     * @return Lemma or lemmas of the wordToLemmatize separated by
     * lemmaSeparator
     */
    public String lemmatizeWord(String wordToLemmatize, String posTagOfWordToLemmatize) {
        return bioLemmatizer.lemmatizeByLexiconAndRules(wordToLemmatize, posTagOfWordToLemmatize).lemmasToString();
    }

    /**
     * Lemmatizes the stringToLemmatize using BioLemmatizer
     * 
     * @param stringToLemmatize the string to be lemmatized 
     * @param posTagSeparatorChar the POS tag separator character
     * @return the lemmatized POS tagged string
     */
    public static String lemmatizeString(String stringToLemmatize, String posTagSeparatorChar) {
        String lemmatizedStringToReturn = "";
        String[] posTaggedTokens = null;
        String wordToLemmatize = "";
        String posTagOfWordToLemmatize = "";

        try {
            // tokenize
            posTaggedTokens = stringToLemmatize.split(" ");

            // lemmatize and join
            for (int i = 0; i < posTaggedTokens.length; i++) {
                wordToLemmatize = posTaggedTokens[i].substring(0, posTaggedTokens[i].indexOf(posTagSeparatorChar));
                posTagOfWordToLemmatize = posTaggedTokens[i].substring(posTaggedTokens[i].indexOf(posTagSeparatorChar) + 1, posTaggedTokens[i].length());

                lemmatizedStringToReturn += bioLemmatizer.lemmatizeByLexiconAndRules(wordToLemmatize, posTagOfWordToLemmatize).lemmasToString() + posTagSeparatorChar + posTagOfWordToLemmatize + " ";
            }

            // trim
            lemmatizedStringToReturn = lemmatizedStringToReturn.trim();

        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("Could not lemmatize: " + stringToLemmatize);
            return stringToLemmatize;
        }

        return lemmatizedStringToReturn;
    }

   
}
