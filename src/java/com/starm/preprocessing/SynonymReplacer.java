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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides synonym replacement functionality 
 * 
 * @author Wasif Altaf
 */
public class SynonymReplacer {

    /**
     * Performs synonym replacement using words contained in synonym replacement map
     * 
     * @param synonymReplacementMap map containing words and their synonym replacements
     * @param textToReplaceSynonymsFrom text from which synonyms are to be replaced
     * @param splitCharacter tokenization character
     * @param posTagSeparatorChar char(s) separating token and pos tag
     * @return synonym replaced string
     */
    public static String applySynonymReplacement(Map<String, String> synonymReplacementMap,
            String textToReplaceSynonymsFrom,
            String splitCharacter,
            String posTagSeparatorChar) {
        String synonymsReplacedStringToReturn = "";
        
        String[] synonyms;
        String wordToSynonymize = "";
        String posTagOfWordToSynonymize = "";
        String baseForm = "";
        boolean synonymUsed = false;
       
        try {
            
            String[] tokens = textToReplaceSynonymsFrom.split(splitCharacter);
            
            for (String token : tokens) {
                wordToSynonymize = token.substring(0, token.indexOf(posTagSeparatorChar));
                posTagOfWordToSynonymize = token.substring(token.indexOf(posTagSeparatorChar) + 1, token.length());

                // find the replacement for wordToSynonymize and use it
                if (synonymReplacementMap.containsKey(wordToSynonymize)) {
                    synonymsReplacedStringToReturn += synonymReplacementMap.get(wordToSynonymize) + posTagSeparatorChar + posTagOfWordToSynonymize + splitCharacter;
                } else {
                    // replacement not found
                    // use the word as it is
                   synonymsReplacedStringToReturn += wordToSynonymize + posTagSeparatorChar + posTagOfWordToSynonymize + splitCharacter;                        
                }
            }

        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
            return "";
        }

        return synonymsReplacedStringToReturn.trim();
    }
    
    /**
     * Converts POS tagged text to Map <br /><br /> 
     * For output map, the key contains the word, value contains the POS tag for the word
     * 
     * @param transaction text to convert to map
     * @param splitCharacter tokenization char(s)
     * @param posTagSeparatorChar char(s) separating word and its POS tag
     * @return map containing the words as keys, and values as the POS tags for the respective words
     */
    public static Map stringToMap(String transaction, String splitCharacter, String posTagSeparatorChar) {
        Map mapToReturn = new HashMap();
        String[] tokens;
        String k, v;

        try {
            tokens = transaction.split(splitCharacter);
            for (String token : tokens) {
                k = token.substring(0, token.indexOf(posTagSeparatorChar));
                v = token.substring(token.indexOf(posTagSeparatorChar) + 1, token.length()); 
                
                mapToReturn.put(k, v);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not convert String to Map for : " + transaction);
        }
        return mapToReturn;
    }
  
    /**
     * Builds synonym replacement map by using unique terms index
     * 
     * @param uniqueTermsIndex unique terms index containing words as keys, 
     * and POS tags for respective words as values
     * @return the synonym replacement map containing words and keys, and their replacements as values 
     */
    public static HashMap buildSynonymReplacementMap(HashMap<String, String> uniqueTermsIndex) {
        HashMap<String, String> synonymReplacementMap = new HashMap<>();
        String word;
        String posTag;
        String wnhome = "E:\\wasif\\myMSCS\\thesis\\implementation\\wordnet\\WordNet-3.0";
        WordNetDictionary dictionary = new WordNetDictionary(wnhome);
        String[] baseForms;
        String[] synonyms;

        try {            
            for (String token : uniqueTermsIndex.keySet()) {
                word = token;
                posTag = uniqueTermsIndex.get(token);
                
                baseForms = dictionary.getBaseFormCandidates(word);
                
                // base forms analysis
                if (baseForms != null && baseForms.length > 0) {
                    for (String baseForm : baseForms) {
                        if (uniqueTermsIndex.containsKey(baseForm)) {
                            System.out.println(word + " replaced with " + baseForm + ". " + baseForm + " exists in terms index.");
                            synonymReplacementMap.put(word, baseForm);
                            break;
                        } 
//                        else if (dictionary.isValidWord(baseForm, null)) {
//                            System.out.println(word + " replaced with " + baseForm + ". " + baseForm + " doesn't exist in terms index. But is a valid word.");
//                            synonymReplacementMap.put(word, baseForm);
//                            //break;
//                        }                        
                    }
                } 
//                else {                
//                    // synonyms analysis                    
//                    synonyms = dictionary.findSynonyms(word, posTag);
//                    
//                    for (String synonym : synonyms) {
//                        if (uniqueTermsIndex.containsKey(synonym)) {
//                            synonymReplacementMap.put(word, synonym);
//                            break;
//                        }
//                    }
//                }
                
                // if a word is not a valid word
                // skip it
                // else if it is a valid word
                // process it further
                // find its synonyms and base forms
                // if it has no base forms and no synonyms
                // skip it
                // if it has base forms 
                // replace it with base form which exists in terms index
                // if it has no base forms which exist in terms index
                // then replace it with base form which is a valid word
                // if it has synonyms
                // replace it with synoym which exists in tems index but does not exists in the replacement index
                
//                if (dictionary.isValidWord(word, posTag)) {
//                    System.out.println(word +  " " + posTag +  " is a valid word.");
//                }
//                else{
//                    System.out.println(word + " " + posTag + " is an invalid word.");
//                }                    
//                
//                // synonyms analysis                    
//                synonyms = dictionary.findSynonyms(word, posTag);
//                
//                System.out.println("\nSynonyms for " + word + " " + posTag + " : ");
//            
//                for (String synonym : synonyms) {
//                    System.out.print(synonym + " : ");
//                    if (uniqueTermsIndex.containsKey(synonym)) {
//                        System.out.println("exists in terms index.");
//                        //synonymReplacementMap.put(word, synonym);
//                        //break;
//                    }
//                    else{
//                        System.out.println("doesn't exist in terms index.");
//                    }
//                }
//                
//                baseForms = dictionary.getBaseFormCandidates(word, posTag);
//                
//                System.out.println("\nBase forms for " + word + " " + posTag +  " : ");
//                
//                // base forms analysis
//                if (baseForms != null && baseForms.length > 0) {
//                    for (String baseForm : baseForms) {
//                        System.out.print(baseForm + " : ");
//                        if (uniqueTermsIndex.containsKey(baseForm)) {
//                            System.out.println("exists in terms index.");
//                            //synonymReplacementMap.put(word, baseForm);
//                            //break;
//                        } else if (dictionary.isValidWord(baseForm, null)) {
//                            System.out.print("doesn't exist in terms index.");
//                            System.out.println(" But is a valid word.");
//                            //synonymReplacementMap.put(word, baseForm);
//                            //break;
//                        }                        
//                    }
//                }
//                else{
//                    System.out.println("No baseforms.");
//                }
//                
//                System.out.println("----------------\n");
//                
//                

            }
        } catch (Exception e) {
            System.err.println("Exception in SynonymReplacer while building synonym replacement map.");
            e.printStackTrace();
        }
        
        System.out.println("uniqueTermsIndex size : " + uniqueTermsIndex.size());
        System.out.println("synonymReplacementMap size : " + synonymReplacementMap.size());
        
        // print the map
        for (Map.Entry<String, String> entry : synonymReplacementMap.entrySet()) {
            String term = entry.getKey();
            String replacement = entry.getValue();
            
            System.out.println(term + "\t" + replacement);
        }

        return synonymReplacementMap;
    }
}
