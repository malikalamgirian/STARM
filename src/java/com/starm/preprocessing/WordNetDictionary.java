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

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides functionality for utilizing WordNet by using Java API for Wordnet Searching
 * 
 * @author Wasif Altaf
 */
public class WordNetDictionary {
    
    private String wnhome = null;
    private WordNetDatabase database = null;
    
    /**
     * Constructs WordNetDictionary by using wordNetHomeDirectoyPath
     * 
     * @param wordNetHomeDirectoryPath the folder path for WordNet 
     */
    public WordNetDictionary(String wordNetHomeDirectoryPath) {
        wnhome = wordNetHomeDirectoryPath + File.separatorChar + "dict\\";
        System.setProperty("wordnet.database.dir", wnhome);
        
        database = WordNetDatabase.getFileInstance();
    }
    
    /**
     * Finds synonyms for the given word and its POS tag. This method also finds
     * the synonyms of all the derivationally related forms of the given word
     * 
     * @param wordToFindSynonyms word for which to find synonyms
     * @param posTag POS tag of the word
     * @return array of synonyms 
     */
    public String[] findSynonyms(String wordToFindSynonyms, String posTag){
        List<String> synonymsList = new ArrayList<String>();
        String [] synonyms = {};
        Synset[] synsets;
        WordSense [] wordSenses;
        String[] wordForms;
        
        // get synsets containing the word form
        synsets = database.getSynsets(wordToFindSynonyms, convertPennTreeBankTagToWordNetSynsetType(posTag), true); 
        
        // for each of the synset, get all word forms
        for (Synset synset : synsets) {
            wordForms = synset.getWordForms();
            
            for (String wordForm : wordForms) {
                
                if (!synonymsList.contains(wordForm) && 
                        !wordForm.equalsIgnoreCase(wordToFindSynonyms)) 
                    synonymsList.add(wordForm);
                
                wordSenses = synset.getDerivationallyRelatedForms(wordForm);

                for (WordSense wordSense : wordSenses) {
                    if (!synonymsList.contains(wordSense.getWordForm()) && 
                            !wordSense.getWordForm().equalsIgnoreCase(wordToFindSynonyms)) {
                        synonymsList.add(wordSense.getWordForm());
                    }
                }                                        
            }
        }        
   
        return synonymsList.toArray(synonyms);
    }
    
    /**
     * Finds simple synonyms for the given word and its POS tag. This method does not
     * take into account the derivationally related forms of the word. 
     * 
     * @param wordToFindSynonyms word for which to find synonyms
     * @param posTag POS tag of the word
     * @return array of synonyms 
     */
    public String[] findSimpleSynonyms(String wordToFindSynonyms, String posTag){
        List<String> synonymsList = new ArrayList<String>();
        String [] synonyms = {};
        Synset[] synsets;
        String[] wordForms;
        
        // get synsets containing the word form
        synsets = database.getSynsets(wordToFindSynonyms, convertPennTreeBankTagToWordNetSynsetType(posTag), true); 
        
        // for each of the synset, get all word forms
        for (Synset synset : synsets) {
            wordForms = synset.getWordForms();
            
            for (String word : wordForms) {
                if (!synonymsList.contains(word) && 
                        !word.equalsIgnoreCase(wordToFindSynonyms)) {
                    synonymsList.add(word);
                }                        
            }
        }   
            
        return synonymsList.toArray(synonyms);
    }
    
    /**
     * Tests whether the word is valid or not by finding it in WordNet 
     * 
     * @param word the word to check for validity
     * @param posTag POS tag of the word
     * @return true if word is a valid word, false if word is not a valid word
     */
    public boolean isValidWord(String word, String posTag) {
        try {
            Synset[] synsets = database.getSynsets(word, 
                    convertPennTreeBankTagToWordNetSynsetType(posTag), true);
            
            if (synsets != null && synsets.length > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
    
    /**
     * Finds derivationally related synonyms of the given word
     * 
     * @param wordToFindSynonyms word for which to find synonyms
     * @param posTag POS tag of the word
     * @return the array of derivationally related synonyms
     */
    public String[] findDerivationallyRelatedSynonyms(String wordToFindSynonyms, String posTag){
        List<String> synonymsList = new ArrayList<String>();
        String [] synonyms = {};
        Synset[] synsets;
        String[] wordForms;
        WordSense [] wordSenses;
        
        // get synsets containing the word form
        synsets = database.getSynsets(wordToFindSynonyms, convertPennTreeBankTagToWordNetSynsetType(posTag), true); 
        
        // for each of the synset, get all derivationally related word forms
        for (Synset synset : synsets) {
            wordForms = synset.getWordForms();

            for (String wordForm : wordForms) {
                wordSenses = synset.getDerivationallyRelatedForms(wordForm);

                for (WordSense wordSense : wordSenses) {
                    if (!synonymsList.contains(wordSense.getWordForm()) && 
                            !wordSense.getWordForm().equalsIgnoreCase(wordToFindSynonyms)) {
                        synonymsList.add(wordSense.getWordForm());
                    }
                }
            }
        }

        return synonymsList.toArray(synonyms);
    }
    
    /**
     * Finds base forms of the word. Some base forms may or may not be valid.
     * 
     * @param wordToFindBaseForm word for which to find base forms
     * @param posTag SynsetType of the word
     * @return array of base forms
     */
    public String[] getBaseFormCandidates(String wordToFindBaseForm, SynsetType posTag) {
        return database.getBaseFormCandidates(wordToFindBaseForm, posTag);
    }
    
    /**
     * Finds base forms of the word. Some base forms may or may not be valid.
     * 
     * @param wordToFindBaseForm word for which to find base forms
     * @param posTag POS tag of the word
     * @return array of base forms
     */
    public String[] getBaseFormCandidates(String wordToFindBaseForm, String posTag) {
        return database.getBaseFormCandidates(wordToFindBaseForm, this.convertPennTreeBankTagToWordNetSynsetType(posTag));
    }
    
    /**
     * Finds base forms of the word, without taking POS tag into account. 
     * Some base forms may or may not be valid.
     * 
     * @param wordToFindBaseForm word for which to find base forms
     * @return array of base forms
     */
    public String[] getBaseFormCandidates(String wordToFindBaseForm) {
        List<String> baseForms = new ArrayList<String>();
        String [] toReturn = {};
        
        for (SynsetType synsetType : SynsetType.ALL_TYPES) {
            baseForms.addAll(Arrays.asList(database.getBaseFormCandidates(wordToFindBaseForm, synsetType)));
        }
        
        return baseForms.toArray(toReturn);
    }
    
    /**
     * Finds smallest base form of the given word
     * 
     * @param wordToFindBaseForm word for which to find base form
     * @param posTag POS tag of the word
     * @return the smallest base form
     */
    public String getBaseForm(String wordToFindBaseForm, String posTag) {
        return WordNetDictionary.getSmallestWord(database.getBaseFormCandidates(wordToFindBaseForm, this.convertPennTreeBankTagToWordNetSynsetType(posTag)));
    }
    
    /**
     * Finds smallest base form of the given word
     * 
     * @param wordToFindBaseForm word for which to find base form
     * @param posTag SynsetType of the word
     * @return the smallest base form
     */
    public String getBaseForm(String wordToFindBaseForm, SynsetType posTag) {
        return WordNetDictionary.getSmallestWord(database.getBaseFormCandidates(wordToFindBaseForm, posTag));
    }
    
    /**
     * Finds the smallest word from list of words
     * 
     * @param words array of words
     * @return the smallest word
     */
    public static String getSmallestWord(String[] words){
        String smallestWord = null;
        
        if (words.length > 0 &&
                words[0] != null) {
            smallestWord = words[0];
        }        
        
        for (String word : words) {
            if (word.length() < smallestWord.length()) {
                smallestWord = word;
            }
        }
        
        return smallestWord;
    }

    /**
     * Converts Penn Tree Bank tag to Word Net SynsetType 
     * 
     * @param posTag the Penn Tree Bank tag to be converted into SynsetType
     * @return the synset type
     */
    public SynsetType convertPennTreeBankTagToWordNetSynsetType(String posTag){
        if(posTag == null){
            return null;
        } else if (posTag.startsWith("n") ||
                posTag.startsWith("N")) {
            return SynsetType.NOUN;
        } else if (posTag.startsWith("v") ||
                posTag.startsWith("V")) {
            return SynsetType.VERB;
        } else if (posTag.startsWith("j") ||
                posTag.startsWith("J")) {
            return SynsetType.ADJECTIVE;
        } else if (posTag.startsWith("r") ||
                posTag.startsWith("R")) {
            return SynsetType.ADVERB;
        } else {
            return null;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // construct the URL to the Wordnet dictionary directory
        String wnhome = "E:\\wasif\\myMSCS\\thesis\\implementation\\wordnet\\WordNet-3.0";        
        String word = "directioned";
        String [] synonyms;
        
        WordNetDictionary dictionary = new WordNetDictionary(wnhome);
        
        synonyms = dictionary.findSynonyms(word, null);
        
        System.out.println("\nSynonyms of " + word + " : ");
        
        for (String synonym : synonyms) {
            System.out.println(synonym);
        }
        System.out.flush();
        
        synonyms = dictionary.findSimpleSynonyms(word, null);
        
        System.out.println("\n-----------------------------------"
                + "\nSimple Synonyms of " + word + " : ");
        
        for (String synonym : synonyms) {
            System.out.println(synonym);
        }
        System.out.flush();
        
        synonyms = dictionary.findDerivationallyRelatedSynonyms(word, null);
        
        System.out.println("\n-----------------------------------"
                +"\nDerivationally Related Synonyms of " + word + " : ");
        
        for (String synonym : synonyms) {
            System.out.println(synonym);
        }
        System.out.flush();
        
        System.out.println("\n-----------------------------------"
                +"\nBase Forms of " + word + " : ");
        
        synonyms = dictionary.getBaseFormCandidates(word);
         
        for (String baseForm : synonyms) {
            System.out.println(baseForm);
        }
        System.out.flush();
        
        System.out.println("\n-----------------------------------"
                +"\nSmallest Base Form of " + word + " : ");
        
        System.out.println(dictionary.getBaseForm(word, "njj"));
        
        System.out.flush();
        
        System.exit(0);
        
    }
    
    public String getWnhome() {
        return wnhome;
    }

    public void setWnhome(String wnhome) {
        this.wnhome = wnhome;
    } 
    
    

}
