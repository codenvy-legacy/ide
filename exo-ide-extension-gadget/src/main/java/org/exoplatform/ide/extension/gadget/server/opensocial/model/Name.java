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
 * The components of the person's real name.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public class Name
{
   /**
    * The family name of this Person, or "Last Name". 
    */
   private String familyName;
   
   /**
    * The full name, including all middle names, titles, and suffixes as appropriate, formatted for display (e.g. Mr. Joseph Robert Smarr, Esq.).
    */
   private String formatted;
   
   /**
    * The given name of this Person, or "First Name".
    */
   private String givenName;
   
   /**
    * The honorific prefix(es) of this Person.
    */
   private String honorificPrefix;
   
   /**
    * The honorifix suffix(es) of this Person.
    */
   private String honorificSuffix;
   
   /**
    * The middle name(s) of this Person.
    */
   private String middleName;

   /**
    * @return the familyName
    */
   public String getFamilyName()
   {
      return familyName;
   }

   /**
    * @param familyName the familyName to set
    */
   public void setFamilyName(String familyName)
   {
      this.familyName = familyName;
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
    * @return the givenName
    */
   public String getGivenName()
   {
      return givenName;
   }

   /**
    * @param givenName the givenName to set
    */
   public void setGivenName(String givenName)
   {
      this.givenName = givenName;
   }

   /**
    * @return the honorificPrefix
    */
   public String getHonorificPrefix()
   {
      return honorificPrefix;
   }

   /**
    * @param honorificPrefix the honorificPrefix to set
    */
   public void setHonorificPrefix(String honorificPrefix)
   {
      this.honorificPrefix = honorificPrefix;
   }

   /**
    * @return the honorificSuffix
    */
   public String getHonorificSuffix()
   {
      return honorificSuffix;
   }

   /**
    * @param honorificSuffix the honorificSuffix to set
    */
   public void setHonorificSuffix(String honorificSuffix)
   {
      this.honorificSuffix = honorificSuffix;
   }

   /**
    * @return the middleName
    */
   public String getMiddleName()
   {
      return middleName;
   }

   /**
    * @param middleName the middleName to set
    */
   public void setMiddleName(String middleName)
   {
      this.middleName = middleName;
   }
}

