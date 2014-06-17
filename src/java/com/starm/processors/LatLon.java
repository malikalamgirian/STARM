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
 * Provides functionality for handling latitude and longitude with ease
 * 
 * @author Wasif Altaf
 */
public class LatLon {
    private float lat;
    private float lon;

    /**
     * Construct LatLon using latitude and longitude
     * 
     * @param lat latitude
     * @param lon longitude
     */
    public LatLon(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return returns latitude,longitude
     */
    @Override
    public String toString() {
        return lat + "," + lon;
    }
    
    /**
     * 
     * @return returns longitude,latitude
     */
    public String toStringLonLat() {
        return lon + "," + lat;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
    
}
