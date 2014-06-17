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

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Provides functionality for writing KML for spatio-temporal transactions.
 * 
 * @author Wasif Altaf
 */
public class KMLWriter {

    /**
     * Writes KML file for transactions input
     * 
     * @param transactions transactions for which to write KML file
     * @param outputFilePath folder where to save output KML file 
     * @param outputFileNameAndExtension file name and extension for output KML file
     * @param groupSpatially whether or not the transactions should be grouped by location
     * @return true if KML file was saved successfully, false if the KML file 
     * could not be saved
     */
    public static boolean write(SpatioTemporalTransaction[] transactions,
            String outputFilePath, String outputFileNameAndExtension, boolean groupSpatially) {
        boolean processSuccessful = false;
        CoordinatesLookup lookup;
        USAbbreviationsLookup usLookUp;

        try {
            // initialize 
            lookup = new CoordinatesLookup();
            usLookUp = new USAbbreviationsLookup();

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // create kml element
            Element kmlElement = doc.createElement("kml");
            kmlElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");

            // append kml to doc
            doc.appendChild(kmlElement);

            // create Document element, and add to kml element
            Element documentElement = doc.createElement("Document");
            kmlElement.appendChild(documentElement);

            // create Style element, and add to Document
            Element styleElement = doc.createElement("Style");
            styleElement.setAttribute("id", "customStyle");
            documentElement.appendChild(styleElement);

            // create IconStyle element, and add to Style
            Element iconStyleElement = doc.createElement("IconStyle");
            styleElement.appendChild(iconStyleElement);

            // create scale element, and add to IcosStyle
            Element scaleElement = doc.createElement("scale");
            iconStyleElement.appendChild(scaleElement);

            // create text node, and add to scale
            Text scaleValue = doc.createTextNode("3.0");
            scaleElement.appendChild(scaleValue);

            // create Icon element, and add to IconStyle
            Element iconElement = doc.createElement("Icon");
            iconStyleElement.appendChild(iconElement);

            // create href element, and add to Icon element
            Element hrefElement = doc.createElement("href");
            iconElement.appendChild(hrefElement);

            // create text node, and add to href 
            Text iconHref = doc.createTextNode("http://maps.google.com/mapfiles/kml/paddle/red-stars.png");
            hrefElement.appendChild(iconHref);

            // create baloonStyle element, and add to Style element
            Element balloonStyleElement = doc.createElement("BalloonStyle");
            styleElement.appendChild(balloonStyleElement);

            // create bgColor element, and add to BaloonStyle element
            Element bgColorElement = doc.createElement("bgColor");
            balloonStyleElement.appendChild(bgColorElement);

            // create text node, and add to bgColor element
            Text bgcolor = doc.createTextNode("#FF00FFE7");
            bgColorElement.appendChild(bgcolor);

            // create textColor element, and add to BaloonStyle element
            Element textColorElement = doc.createElement("textColor");
            balloonStyleElement.appendChild(textColorElement);

            // create text node, and add to textColor element
            Text textcolor = doc.createTextNode("#FF000000");
            textColorElement.appendChild(textcolor);

            // create text element, and add to BaloonStyle element
            Element baloonStyleTextElement = doc.createElement("text");
            balloonStyleElement.appendChild(baloonStyleTextElement);

            // create cdata section node, and add to text element
            Text textStyleCDATA = doc.createCDATASection("<span style=\"font-family:wf_segoe-ui_normal, "
                    + "'Segoe UI', Segoe, 'Segoe WP', Tahoma, Verdana, Arial, "
                    + "sans-serif; text-align: center; \"><H1>$[name]</H1> </span> "
                    + "<br/><span style=\"font-family:wf_segoe-ui_normal, "
                    + "'Segoe UI', Segoe, 'Segoe WP', Tahoma, Verdana, Arial,"
                    + " sans-serif;\">$[description]</span>");
            baloonStyleTextElement.appendChild(textStyleCDATA);

            if (groupSpatially) {
                // group transactions spatially
                Map<String, List<SpatioTemporalTransaction>> stTransactionsByLocation = Arrays.asList(transactions).stream().collect(Collectors.groupingBy(SpatioTemporalTransaction::getLocation));

                // generate Placemarks for each location
                for (Map.Entry<String, List<SpatioTemporalTransaction>> entry : stTransactionsByLocation.entrySet()) {
                    String location = entry.getKey();
                    List<SpatioTemporalTransaction> listOfTransactions = entry.getValue();

                    // create grouped placemark and add to document element
                    documentElement.appendChild(createPlacemark(doc, location,
                            listOfTransactions, lookup.getCoordinates(location), usLookUp));
                }

            } else {
                for (SpatioTemporalTransaction stTransaction : transactions) {
                    // create placemark and add to document element
                    documentElement.appendChild(createPlacemark(doc, stTransaction,
                            lookup.getCoordinates(stTransaction.getLocation()), usLookUp));
                }
            }

            // normalize and transform
            doc.normalizeDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer aTransformer = tf.newTransformer();

            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            aTransformer.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(FileSystems.getDefault().getPath(outputFilePath, outputFileNameAndExtension))));

            processSuccessful = true;
        } catch (Exception e) {
            System.err.println("Exception in KMLWriter.write() : " + e.getMessage());
            e.printStackTrace();
        }

        return processSuccessful;
    }

    /**
     * Creates placemark element for given transaction
     * 
     * @param doc XML document for which to create placemark element
     * @param transaction transaction for which to create placemark
     * @param coordinates coordinates for the transaction location 
     * @param usLookUp United States state names and abbreviations lookup
     * @return placemark element containing time span element, or null if element could not
     * be created successfully
     */
    private static Element createPlacemark(Document doc,
            SpatioTemporalTransaction transaction, LatLon coordinates, 
            final USAbbreviationsLookup usLookUp) {
        Element placemarkElement = null;
        String description;

        try {
            // create placemark element, and add to Document element
            placemarkElement = doc.createElement("Placemark");
            placemarkElement.setAttribute("id", Integer.toString(transaction.getId()));

            // create palacemark name element, and add to Placemark element
            Element placemarkNameElement = doc.createElement("name");
            placemarkElement.appendChild(placemarkNameElement);

            // create text node for placemark name, and add to name element 
            Text placemarkNameValue = doc.createTextNode(usLookUp.lookUp(transaction.getLocation()));
            placemarkNameElement.appendChild(placemarkNameValue);

            // create description element, and add to Placemark element
            Element descriptionElement = doc.createElement("description");
            placemarkElement.appendChild(descriptionElement);

            // create description text
            description = "<Table style=\"border-collapse:collapse; \" >";

            description += "<tr>";

                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00;  text-align: centre; padding: 4px;  \" >" + "ID" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 4px; \" >" + "Time Stamp" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 4px; \" >" + "Location" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 5px; \" >" + "Post" + "</th>";

            description += "</tr>";

            description += "<tr>";

                description += "<td style=\"border: 1px solid black; background-color: #feff9d;  text-align: center; padding: 4px;  \" >" + transaction.getId() + "</td>";
                description += "<td style=\"border: 1px solid black; background-color: #fdff00; text-align: center; padding: 4px; \" >" + transaction.getTimeStamp() + "</td>";
                description += "<td style=\"border: 1px solid black; background-color: #feff9d; text-align: center; padding: 4px; \" >" + transaction.getLocation() + "</td>";
                description += "<td style=\"border: 1px solid black; background-color: #fdff00; text-align: justify; padding: 5px; \" >" + transaction.getTransaction() + "</td>";

            description += "</tr>";

            description += "</Table>";

            // create cdata section for placemark description, and add to description element 
            Text placemarkDescriptionValue = doc.createCDATASection(description);
            descriptionElement.appendChild(placemarkDescriptionValue);
            
            // create time span element, and add to placemark
            Element timeSpanElement = doc.createElement("TimeSpan");
            placemarkElement.appendChild(timeSpanElement);
            
            // create begin element, and add to time span element
            Element beginElement = doc.createElement("begin");
            timeSpanElement.appendChild(beginElement);
            
            // create begin value text node, and add to begin element
            Text beginValue = doc.createTextNode(TimeStampProcessor.getYear(transaction.getTimeStamp(), "/") + "-01-01");
            beginElement.appendChild(beginValue);
            
            // create end element, and add to time span element
            Element endElement = doc.createElement("end");
            timeSpanElement.appendChild(endElement);
            
            // create end value text node, and add to end element
            Text endValue = doc.createTextNode(TimeStampProcessor.getYear(transaction.getTimeStamp(), "/") + "-12-31");
            endElement.appendChild(endValue);
                        
            // create visibility element, and add to Placemark element
            Element visibilityElelemt = doc.createElement("visibility");
            placemarkElement.appendChild(visibilityElelemt);

            // create text node for visibility element, and add to visibilityElement
            Text visibilityValue = doc.createTextNode("1");
            visibilityElelemt.appendChild(visibilityValue);

            // create styleUrl element, and add to Placemark element
            Element styleUrlElelemt = doc.createElement("styleUrl");
            placemarkElement.appendChild(styleUrlElelemt);

            // create text node for styleUrl element, and add to styleUrlElelemt
            Text styleUrlElementValue = doc.createTextNode("#customStyle");
            styleUrlElelemt.appendChild(styleUrlElementValue);

            // create Point element, and add to Placemark element
            Element pointElement = doc.createElement("Point");
            placemarkElement.appendChild(pointElement);

            // create coordinates element, and add to Point element
            Element coordinatesElement = doc.createElement("coordinates");
            pointElement.appendChild(coordinatesElement);

            // create text node for coordinates element, and add to coordinatesElement
            // LON, LAT
            Text coordinatesValue = doc.createTextNode(coordinates.toStringLonLat());
            coordinatesElement.appendChild(coordinatesValue);

        } catch (Exception e) {
            System.err.println("Exception in KMLWriter.createPlacemark() : " + e.getMessage());
            e.printStackTrace();
        }

        return placemarkElement;
    }

    /**
     * Creates Placemark element for list of transactions.
     * 
     * <br />
     * <br />
     * It is assumed that the listOfTransactions belong to same location 
     * 
     * @param doc XML document for which to create placemark element
     * @param location location for the list of transactions
     * @param listOfTransactions list of transactions for which to create placemark
     * @param coordinates coordinated for the location
     * @param usLookUp United States state names and abbreviations lookup
     * @return the placemark element for listOfTransactions grouped by location,
     * null if the placemark element could not be created
     */
    private static Element createPlacemark(Document doc, String location,
            List<SpatioTemporalTransaction> listOfTransactions,
            LatLon coordinates, final USAbbreviationsLookup usLookUp) {
        Element placemarkElement = null;
        String description;

        try {
            // create placemark element, and add to Document element
            placemarkElement = doc.createElement("Placemark");
            placemarkElement.setAttribute("id", location);

            // create palacemark name element, and add to Placemark element
            Element placemarkNameElement = doc.createElement("name");
            placemarkElement.appendChild(placemarkNameElement);

            // create text node for placemark name, and add to name element 
            Text placemarkNameValue = doc.createTextNode(usLookUp.lookUp(location));
            placemarkNameElement.appendChild(placemarkNameValue);

            // create description element, and add to Placemark element
            Element descriptionElement = doc.createElement("description");
            placemarkElement.appendChild(descriptionElement);

            // create description text
            description = "<Table style=\"border-collapse:collapse; \" >";

            description += "<tr>";

                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00;  text-align: centre; padding: 4px;  \" >" + "ID" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 4px; \" >" + "Time Stamp" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 4px; \" >" + "Location" + "</th>";
                description += "<th style=\"font-weight: bold; border: 1px solid black; background-color: #fdff00; text-align: centre; padding: 5px; \" >" + "Post" + "</th>";

            description += "</tr>";

            for (SpatioTemporalTransaction transaction : listOfTransactions) {
                description += "<tr>";

                    description += "<td style=\"border: 1px solid black; background-color: #feff9d; text-align: center; padding: 4px;  \">" + transaction.getId() + "</td>";
                    description += "<td style=\"border: 1px solid black; background-color: #fdff00; text-align: center; padding: 4px;  \">" + transaction.getTimeStamp() + "</td>";
                    description += "<td style=\"border: 1px solid black; background-color: #feff9d; text-align: center; padding: 4px;  \">" + transaction.getLocation() + "</td>";
                    description += "<td style=\"border: 1px solid black; background-color: #fdff00; text-align: justify; padding: 5px;  \">" + transaction.getTransaction() + "</td>";

                description += "</tr>";
            }

            description += "</Table>";

            // create cdata section for placemark description, and add to description element 
            Text placemarkDescriptionValue = doc.createCDATASection(description);
            descriptionElement.appendChild(placemarkDescriptionValue);

            // create visibility element, and add to Placemark element
            Element visibilityElelemt = doc.createElement("visibility");
            placemarkElement.appendChild(visibilityElelemt);

            // create text node for visibility element, and add to visibilityElement
            Text visibilityValue = doc.createTextNode("1");
            visibilityElelemt.appendChild(visibilityValue);

            // create styleUrl element, and add to Placemark element
            Element styleUrlElelemt = doc.createElement("styleUrl");
            placemarkElement.appendChild(styleUrlElelemt);

            // create text node for styleUrl element, and add to styleUrlElelemt
            Text styleUrlElementValue = doc.createTextNode("#customStyle");
            styleUrlElelemt.appendChild(styleUrlElementValue);

            // create Point element, and add to Placemark element
            Element pointElement = doc.createElement("Point");
            placemarkElement.appendChild(pointElement);

            // create coordinates element, and add to Point element
            Element coordinatesElement = doc.createElement("coordinates");
            pointElement.appendChild(coordinatesElement);

            // create text node for coordinates element, and add to coordinatesElement
            // LON, LAT
            Text coordinatesValue = doc.createTextNode(coordinates.toStringLonLat());
            coordinatesElement.appendChild(coordinatesValue);

        } catch (Exception e) {
            System.err.println("Exception in KMLWriter.createPlacemark() : " + e.getMessage());
            e.printStackTrace();
        }

        return placemarkElement;
    }
    
    
}
