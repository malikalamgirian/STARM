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
 * Provides functionality for POS tags based filtration.
 * 
 * @author Wasif Altaf
 */
public class POSTagBasedFilter {

    /**
     * Performs POS tag based filtration 
     * 
     * @param posTaggedTextToApplyFilterTo POS tagged string to be filtered
     * @return the filtered string
     */
    public static String applyPOSBasedFilter(String posTaggedTextToApplyFilterTo) {
        String filteredTextToReturn = "";
        String[] posTaggedTokens = null;

        // split posTaggedTextToApplyFilterTo into tokens
        posTaggedTokens = posTaggedTextToApplyFilterTo.split(" ");

        // perform filtration
        for (int i = 0; i < posTaggedTokens.length; i++) {
            if (keepToken(posTaggedTokens[i]) == false) {
                posTaggedTokens[i] = null;
            }
        }

        // reconstruct string
        for (int i = 0; i < posTaggedTokens.length; i++) {
            if (posTaggedTokens[i] != null) {
                filteredTextToReturn += posTaggedTokens[i] + " ";
            }
        }

        // trim string, to remove spaces at end
        filteredTextToReturn = filteredTextToReturn.trim();

        return filteredTextToReturn;
    }

    /**
     * Tests whether the token should be kept (not filtered out)
     * 
     * @param posTaggedToken 
     * @return true if token is worth keeping for further processing,
     * false if token should be discarded
     */
    private static boolean keepToken(String posTaggedToken) {
        String pos;

        pos = posTaggedToken.substring(posTaggedToken.indexOf("_") + 1, posTaggedToken.length());

        if (pos.equalsIgnoreCase("$")
                || pos.equalsIgnoreCase("``")
                || pos.equalsIgnoreCase("''")
                || pos.equalsIgnoreCase("(")
                || pos.equalsIgnoreCase(")")
                || pos.equalsIgnoreCase(",")
                || pos.equalsIgnoreCase(".")
                || pos.equalsIgnoreCase(":")
                || pos.equalsIgnoreCase("to")
                || pos.equalsIgnoreCase("POS")
                || pos.equalsIgnoreCase("EX")
                || pos.equalsIgnoreCase("--")
                || pos.equalsIgnoreCase("-LRB-")
                || pos.equalsIgnoreCase("-RRB-")
                || pos.equalsIgnoreCase("SYM")
                || pos.equalsIgnoreCase("CC")
                || pos.equalsIgnoreCase("CD")                
                || pos.equalsIgnoreCase("IN")                
                || pos.equalsIgnoreCase("LS")                
                || pos.equalsIgnoreCase("PRP")                
                || pos.equalsIgnoreCase("PRP$")                
                || pos.equalsIgnoreCase("WDT")                
                || pos.equalsIgnoreCase("WP")                
                || pos.equalsIgnoreCase("WRB")
                || pos.equalsIgnoreCase("WP$")
                || pos.equalsIgnoreCase("FW")
                || pos.equalsIgnoreCase("DT")) {
            return false;
        }
        return true;
    }

}
