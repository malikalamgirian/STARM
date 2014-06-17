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
 * Provides text normalization functionality
 * 
 * @author Wasif Altaf
 */
public class Normalizer {

    /**
     * Normalizes case to lower case
     * 
     * @param textToNormalize the text to be normalized 
     * @return the case normalized text
     */
    public static String normalize(String textToNormalize) {
        String normalizedTextToReturn = null;

        // normalize to lower case if not null
        if (textToNormalize != null) {            
            normalizedTextToReturn = textToNormalize.toLowerCase();
        }        

        return normalizedTextToReturn;
    }

    /**
     * Normalizes slash(es) by replacing "/" with " / "
     * 
     * @param textToNormalize text to be normalized 
     * @return the slash(es) normalized text
     */
    public static String normalizeSlashes(String textToNormalize) {
        String normalizedTextToReturn = null;

        try {
            // normalize slashes
            normalizedTextToReturn = textToNormalize.replace("/", " / ");
        } catch (Exception e) {
            return textToNormalize;
        }

        return normalizedTextToReturn;
    }

}
