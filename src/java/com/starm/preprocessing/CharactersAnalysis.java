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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides methods for character level analysis 
 * 
 * @author Wasif Altaf
 */
public class CharactersAnalysis {
    
    private CharactersAnalysis(){
    }

    /**
     * Finds unique characters from String
     * 
     * @param stringFromWhichUniqueCharactersToFind string from which unique characters to find 
     * @return list of unique characters
     */
    public static List<Character> findUniqueCharacters(String stringFromWhichUniqueCharactersToFind) {
        List<Character> listOfUniqueCharacters = new ArrayList<>();

        for (int i = 0; i < stringFromWhichUniqueCharactersToFind.length(); i++) {
            if (!listOfUniqueCharacters.contains(stringFromWhichUniqueCharactersToFind.charAt(i))) {
                listOfUniqueCharacters.add(stringFromWhichUniqueCharactersToFind.charAt(i));
            }
        }
        Collections.sort(listOfUniqueCharacters);

        return listOfUniqueCharacters;
    }

    /**
     * Finds unique characters from List of String
     * 
     * @param listOfStringsFromWhichUniqueCharactersToFind list of Strings from which unique characters to find
     * @return list of unique characters 
     */
    public static List<Character> findUniqueCharacters(List<String> listOfStringsFromWhichUniqueCharactersToFind) {
        List<Character> listOfUniqueCharacters = new ArrayList<>();

        for (String stringFromWhichUniqueCharactersToFind : listOfStringsFromWhichUniqueCharactersToFind) {
            for (int i = 0; i < stringFromWhichUniqueCharactersToFind.length(); i++) {
                if (!listOfUniqueCharacters.contains(stringFromWhichUniqueCharactersToFind.charAt(i))) {
                    listOfUniqueCharacters.add(stringFromWhichUniqueCharactersToFind.charAt(i));
                }
            }
        }
        Collections.sort(listOfUniqueCharacters);

        return listOfUniqueCharacters;
    }
    
    /**
     * Prints unique characters to standard output
     * @param stringFromWhichUniqueCharactersToPrint  the string for which unique characters are to be printed
     */
    public static void printUniqueCharacters(String stringFromWhichUniqueCharactersToPrint){
        System.out.println(findUniqueCharacters(stringFromWhichUniqueCharactersToPrint));
    }
    
    /**
     * Prints unique characters to standard output
     * @param listOfStringsFromWhichUniqueCharactersToPrint the list of strings for which unique characters are to be printed
     */
    public static void printUniqueCharacters(List<String> listOfStringsFromWhichUniqueCharactersToPrint){
        System.out.println(findUniqueCharacters(listOfStringsFromWhichUniqueCharactersToPrint));
    }

}
