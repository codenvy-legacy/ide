/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.netvibes.client.model;

import java.util.LinkedHashMap;

/**
 * List of available supported regions taken from http://dev.netvibes.com/doc/api/eco/region.
 * Using a non-supported code will result in a submission error.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 *
 */
public class Regions
{
   /**
    * Map with region code and value.
    */
   private static LinkedHashMap<String, String> regionsMap = new LinkedHashMap<String, String>();

   static
   {
      regionsMap.put("at", "Austria");
      regionsMap.put("be", "Belgium (dutch)");
      regionsMap.put("fr", "France");
      regionsMap.put("de", "Germany");
      regionsMap.put("lu", "Luxembourg");
      regionsMap.put("pt", "Portugal");
      regionsMap.put("es", "Spain");
      regionsMap.put("ch", "Switzerland");
      regionsMap.put("gb", "United Kingdom");
      regionsMap.put("ca", "Canada");
      
      regionsMap.put("us", "United States");
      regionsMap.put("mx", "Mexico");
      regionsMap.put("br", "Brazil");
      regionsMap.put("za", "South Africa");
      regionsMap.put("au", "Australia");
      regionsMap.put("nz", "New Zealand");
      regionsMap.put("cn", "China");
      regionsMap.put("il", "Israel");
      regionsMap.put("jp", "Japan");
      regionsMap.put("kr", "South Korea");
      
      regionsMap.put("sg", "Singapore");
      regionsMap.put("tw", "China (Taiwan)");
      regionsMap.put("hk", "China (Hong Kong)");
      regionsMap.put("ru", "Russia");
      regionsMap.put("se", "Sweden");
      regionsMap.put("it", "Italy");
      regionsMap.put("fi", "Finland");
      regionsMap.put("nl", "Netherlands");
      regionsMap.put("qc", "Canada (Quebec)");
      regionsMap.put("no", "Norway");
      
      regionsMap.put("tr", "Turkey");
      regionsMap.put("dk", "Denmark");
      regionsMap.put("cz", "Czech Republic");
      regionsMap.put("gr", "Greece");
      regionsMap.put("lt", "Lithuania");
      regionsMap.put("hu", "Hungary");
      regionsMap.put("ua", "Ukraina");
      regionsMap.put("pl", "Poland");
      regionsMap.put("lv", "Latvia");
      regionsMap.put("ma", "Morocco");
      
      regionsMap.put("th", "Thailand");
      regionsMap.put("in", "India");
      regionsMap.put("ro", "Romania");
      regionsMap.put("bg", "Bulgaria");
      regionsMap.put("pk", "Pakistan");
      regionsMap.put("vn", "Vietnam");
      regionsMap.put("lb", "Lebanon");
      regionsMap.put("al", "Albania");
      regionsMap.put("hr", "Croatia");
      regionsMap.put("ph", "Philippines");
      
      regionsMap.put("ba", "Bosnia and Herzegovina");
      regionsMap.put("ko", "Kosovo");
      regionsMap.put("ee", "Estonia");
      regionsMap.put("co", "Colombia");
      regionsMap.put("rs", "Serbia");
      regionsMap.put("ct", "Spain (Catalonia)");
      regionsMap.put("by", "Belarus");
      regionsMap.put("sy", "Syria");
      regionsMap.put("si", "Slovenia");
      regionsMap.put("eg", "Egypt");
      
      regionsMap.put("ga", "Spain (Galicia)");
      regionsMap.put("cm", "Cameroon");
      regionsMap.put("pe", "Peru");
      regionsMap.put("sk", "Slovakia");
      regionsMap.put("ar", "Argentina");
      regionsMap.put("jo", "Jordan");
      regionsMap.put("sa", "Saudi Arabia");
      regionsMap.put("bz", "Belize");
      regionsMap.put("cr", "Costa Rica");
      regionsMap.put("sv", "El Salvador");
      
      regionsMap.put("gt", "Guatemala");
      regionsMap.put("hn", "Honduras");
      regionsMap.put("ni", "Nicaragua");
      regionsMap.put("pa", "Panama");
      regionsMap.put("bo", "Bolivia");
      regionsMap.put("cl", "Chile");
      regionsMap.put("ec", "Ecuador");
      regionsMap.put("fk", "Falkland Islands");
      regionsMap.put("gy", "Guyana");
      regionsMap.put("py", "Paraguay");
      
      regionsMap.put("sr", "Suriname");
      regionsMap.put("uy", "Uruguay");
      regionsMap.put("ve", "Venezuela");
      regionsMap.put("nlf", "Netherlands (Friesland)");
      regionsMap.put("ae", "United Arab Emirates");
      regionsMap.put("mn", "Mongolia");
      regionsMap.put("dz", "Algeria");
      regionsMap.put("cy", "Cyprus");
      regionsMap.put("pv", "Basque Country");
      regionsMap.put("zz", "World");
      
      regionsMap.put("bef", "Belgium (french)");
      regionsMap.put("ie", "Ireland");
      regionsMap.put("my", "Malaysia");
      regionsMap.put("id", "Indonesia");
      regionsMap.put("kw", "Kuwait");
      regionsMap.put("ps", "Palestine");
      regionsMap.put("iq", "Iraq");
      regionsMap.put("bh", "Bahreïn");
      regionsMap.put("tn", "Tunisia");
      regionsMap.put("ci", "Ivory Coast");
      
      regionsMap.put("ke", "Kenya");
      regionsMap.put("mr", "Mauritania");
      regionsMap.put("sn", "Senegal");
      regionsMap.put("ng", "Nigeria");
      regionsMap.put("cd", "Congo");
      regionsMap.put("so", "Somalia");
      regionsMap.put("et", "Ethiopia");
      regionsMap.put("ly", "Lybia");
      regionsMap.put("is", "Iceland");
      regionsMap.put("gra", "Greece (Attica)");
      
      regionsMap.put("chf", "Switzerland (french)");
    }

   /**
    * Get map with available regions.
    * 
    * @return {@link LinkedHashMap}
    */
   public static LinkedHashMap<String, String> getRegionsMap()
   {
      return regionsMap;
   }
}
