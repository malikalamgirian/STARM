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

/**
 * Provides convenience methods for time stamp processing 
 * @author Wasif Altaf
 */
public class TimeStampProcessor {
    
    /**
     * Gets year from timestamp
     * 
     * @param timeStamp time stamp such as MM/DD/YYYY
     * @param separatorChar char(s) such as / separating time stamp units
     * @return YYYY
     */
    public static String getYear(String timeStamp, String separatorChar){
        return timeStamp.split(separatorChar)[2];    
    }
    
    /**
     * Gets day from timestamp
     * 
     * @param timeStamp time stamp such as MM/DD/YYYY
     * @param separatorChar char(s) such as / separating time stamp units
     * @return DD
     */
    public static String getDay(String timeStamp, String separatorChar){
        return timeStamp.split(separatorChar)[1];    
    }
    
    /**
     * Gets month from timestamp
     * 
     * @param timeStamp time stamp such as MM/DD/YYYY
     * @param separatorChar char(s) such as / separating time stamp units
     * @return MM
     */
    public static String getMonth(String timeStamp, String separatorChar){
        return timeStamp.split(separatorChar)[0];    
    }
    
}
