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
 * Represents a spatio-temporal transaction, and provides related functionality 
 * 
 * @author Wasif Altaf
 */
public class SpatioTemporalTransaction {
    
    private int id;
    private String timeStamp;
    private String location;
    private String transaction;
    private String topic;

    public SpatioTemporalTransaction() {
    }

    /**
     * Parses string transaction to SpatioTemporalTransaction
     * 
     * @param transaction transaction to be parsed
     * @return parsed transaction as SpatioTemporalTransaction or null if 
     * transaction could not be parsed
     */
    public static SpatioTemporalTransaction parse(String transaction) {
        SpatioTemporalTransaction transactionToReturn = null;
        String parts[];
        
        try {
            transactionToReturn = new SpatioTemporalTransaction();
            parts = transaction.split("\t");

            transactionToReturn.setId(Integer.parseInt(parts[0]));
            transactionToReturn.setTimeStamp(parts[1]);
            transactionToReturn.setLocation(parts[2]);
            transactionToReturn.setTransaction(parts[3]);
            transactionToReturn.setTopic(parts[4]);

        } catch (Exception e) {
            System.err.println("Exception in SpatioTemporalTransaction.parse()");
            System.err.println("Could not parse : " + transaction);
            e.printStackTrace();
            return null;
        }

        return transactionToReturn;
    }
   
    /**
     * Parses transactions 
     * 
     * @param transactions array of transactions to be parsed
     * @return parsed transactions as SpatioTemporalTransactions or null if 
     * transactions could not be parsed
     */
    public static SpatioTemporalTransaction[] parse(String[] transactions) {
        SpatioTemporalTransaction[] transactionsToReturn = null;

        try {
            transactionsToReturn = new SpatioTemporalTransaction[transactions.length];

            for (int i = 0; i < transactions.length; i++) {
                transactionsToReturn[i] = parse(transactions[i]);
                
                // print parsing error
                if (transactionsToReturn[i] == null) {
                    System.err.println("Could not parse transaction : " + transactions[i]);
                }
            }

        } catch (Exception e) {
            System.err.println("Exception in SpatioTemporalTransaction.parse()");
            e.printStackTrace();
            return null;
        }

        return transactionsToReturn;
    }
    
    @Override
    public String toString() {
        return id + "\t" + timeStamp + "\t" + location + "\t" + transaction + "\t" + topic;
    }
    
    /**
     * Returns string representation of the spatio-temporal transaction
     * 
     * @param provideDescription whether or not to provide description for the spatio-temporal transaction
     * @return returns string representation of the spatio-temporal transaction
     */
    public String toString(boolean provideDescription){
        if (!provideDescription) {
            return toString();
        }
        else{
            return "ID:\t"+ id + "\nTimestamp:\t" + timeStamp + "\nLocation:\t" 
                    + location + "\nTransaction:\t" + transaction + "\nTopic\t" + topic;
        }
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getTimeStampYear(){
        return TimeStampProcessor.getYear(this.timeStamp, "/");
    }    
    
}
