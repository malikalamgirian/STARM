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

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Provides functionality for POS tagging using the MaxentTagger (Stanford POS Tagger)
 * 
 * @author Wasif Altaf
 */
public class PosTagger {

    // MaxentTagger instance variable initialized as null
    private static MaxentTagger tagger = null;
    // path from which to load model
    private static String modelPath = null;
    
    // static initializations
    static {
        modelPath = "E:/wasif/myMSCS/thesis/implementation/posTagger/stanford-postagger-full-2014-01-04/models/english-bidirectional-distsim.tagger";
        tagger = new MaxentTagger(modelPath);
    }

    /**
     * No-argument constructor
     */
    public PosTagger() {        
        this.modelPath = "E:/wasif/myMSCS/thesis/implementation/posTagger/stanford-postagger-full-2014-01-04/models/english-bidirectional-distsim.tagger";
        this.tagger = new MaxentTagger(modelPath);
    }
    
    /**
     * Constructs PosTagger object based on the model path provided
     * @param modelPath the model path to construct MaxtentTagger, should not be null
     */
    public PosTagger(String modelPath){
        this.modelPath = modelPath;               
        this.tagger = new MaxentTagger(modelPath);
    }

    /**
     * POS tags the stringToTag
     * @param stringToTag String to be annotated with part of speech tags
     * @return Part of Speech Tagged String 
     */
    public String tagString(String stringToTag) {
        return tagger.tagString(stringToTag);
    }
 
    /**
     * POS tags the stringToTag
     * @param stringToTag string to be POS tagged
     * @return Part of Speech Tagged String 
     */
    public static String posTagString(String stringToTag) {
        return tagger.tagString(stringToTag);
    }
    
    /**
     * Removes POS tags from text
     * 
     * @param text the text from which to remove POS tags
     * @param splitCharater the char(s) with which to tokenize the String
     * @param posTagSeparatorChar the char(s) with which the token and POS tag are separated
     * @return the POS tags removed String
     */
    public static String removePosTags(String text, String splitCharater, String posTagSeparatorChar){
        String posTagRemovedString = "";
        String [] tokens;
        try {
            tokens = text.split(splitCharater);
            for (String token : tokens) {
                posTagRemovedString += token.substring(0, token.indexOf(posTagSeparatorChar)) + splitCharater;
            }
            
        } catch (Exception e) {
            System.err.println("Exception in removePosTags.");
            e.printStackTrace();            
        }  
        return posTagRemovedString;
    }

}