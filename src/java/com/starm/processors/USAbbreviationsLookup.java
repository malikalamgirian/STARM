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

import java.util.HashMap;

/**
 * Provides mapping for United States state names and abbreviations
 * 
 * @author Wasif Altaf
 */
public class USAbbreviationsLookup {

    private HashMap<String, String> lookupMap = new HashMap();

    private String[] valuesTable;

    {
        valuesTable = new String[]{"USA,United States of America",
            "AL,Alabama",
            "AK,Alaska",
            "AZ,Arizona",
            "AR,Arkansas",
            "CA,California",
            "CO,Colorado",
            "CT,Connecticut",
            "DE,Delaware",
            "DC,District of Columbia",
            "FL,Florida",
            "GA,Georgia",
            "HI,Hawaii",
            "ID,Idaho",
            "IL,Illinois",
            "IN,Indiana",
            "IA,Iowa",
            "KS,Kansas",
            "KY,Kentucky",
            "LA,Louisiana",
            "ME,Maine",
            "MD,Maryland",
            "MA,Massachusetts",
            "MI,Michigan",
            "MN,Minnesota",
            "MS,Mississippi",
            "MO,Missouri",
            "MT,Montana",
            "NE,Nebraska",
            "NV,Nevada",
            "NH,New Hampshire",
            "NJ,New Jersey",
            "NM,New Mexico",
            "NY,New York",
            "NC,North Carolina",
            "ND,North Dakota",
            "OH,Ohio",
            "OK,Oklahoma",
            "OR,Oregon",
            "PA,Pennsylvania",
            "RI,Rhode Island",
            "SC,South Carolina",
            "SD,South Dakota",
            "TN,Tennessee",
            "TX,Texas",
            "UT,Utah",
            "VT,Vermont",
            "VA,Virginia",
            "WA,Washington",
            "WV,West Virginia",
            "WI,Wisconsin",
            "WY,Wyoming",
            "AS,American Samoa",
            "GU,Guam",
            "MP,Northern Mariana Islands",
            "PR,Puerto Rico",
            "VI,Virgin Islands"};

        for (String entity : valuesTable) {
            String[] entityNames = entity.split(",");

            lookupMap.put(entityNames[0], entityNames[1]);
        }

    }

    public USAbbreviationsLookup() {
    }

    /**
     * Looks-up state name for the stateCode
     * 
     * @param stateCode short state code such as SJ
     * @return complete state name such as San Jose
     */
    public String lookUp(String stateCode) {
        return lookupMap.get(stateCode);
    }

}
