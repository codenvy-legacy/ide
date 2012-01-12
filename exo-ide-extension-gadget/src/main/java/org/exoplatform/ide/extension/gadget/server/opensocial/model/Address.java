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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * The components of a physical mailing address.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public class Address
{
   /**
    * The country name component.
    */
   private String country;

   /**
    * The full mailing address, formatted for display or use with a mailing label. This field MAY contain newlines.
    */
   private String formatted;

   /**
    * Expresses the latitude of the location on a map.
    */
   private String latitude;

   /**
    * The city or locality component.
    */
   private String locality;

   /**
    * The longitude of the location on a map. 
    */
   private String longitude;

   /**
    * The zipcode or postal code component.
    */
   private String zipcode;

   /**
    * The state or region component.
    */
   private String region;

   /**
    * The full street address component.
    */
   private String streetAddress;

   /**
    * The address type or label. Examples include 'work', 'home'.
    */
   private String type;

   /**
    * @return the country
    */
   public String getCountry()
   {
      return country;
   }

   /**
    * @param country the country to set
    */
   public void setCountry(String country)
   {
      this.country = country;
   }

   /**
    * @return the formatted
    */
   public String getFormatted()
   {
      return formatted;
   }

   /**
    * @param formatted the formatted to set
    */
   public void setFormatted(String formatted)
   {
      this.formatted = formatted;
   }

   /**
    * @return the latitude
    */
   public String getLatitude()
   {
      return latitude;
   }

   /**
    * @param latitude the latitude to set
    */
   public void setLatitude(String latitude)
   {
      this.latitude = latitude;
   }

   /**
    * @return the locality
    */
   public String getLocality()
   {
      return locality;
   }

   /**
    * @param locality the locality to set
    */
   public void setLocality(String locality)
   {
      this.locality = locality;
   }

   /**
    * @return the longitude
    */
   public String getLongitude()
   {
      return longitude;
   }

   /**
    * @param longitude the longitude to set
    */
   public void setLongitude(String longitude)
   {
      this.longitude = longitude;
   }

   /**
    * @return the zipcode
    */
   public String getZipcode()
   {
      return zipcode;
   }

   /**
    * @param zipcode the zipcode to set
    */
   public void setZipcode(String zipcode)
   {
      this.zipcode = zipcode;
   }

   /**
    * @return the region
    */
   public String getRegion()
   {
      return region;
   }

   /**
    * @param region the region to set
    */
   public void setRegion(String region)
   {
      this.region = region;
   }

   /**
    * @return the streetAddress
    */
   public String getStreetAddress()
   {
      return streetAddress;
   }

   /**
    * @param streetAddress the streetAddress to set
    */
   public void setStreetAddress(String streetAddress)
   {
      this.streetAddress = streetAddress;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }
}
