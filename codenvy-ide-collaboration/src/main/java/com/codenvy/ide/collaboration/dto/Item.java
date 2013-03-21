/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.collaboration.dto;

import org.exoplatform.ide.dtogen.shared.CompactJsonDto;
import org.exoplatform.ide.dtogen.shared.RoutableDto;
import org.exoplatform.ide.dtogen.shared.RoutingType;
import org.exoplatform.ide.dtogen.shared.SerializationIndex;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;
import org.exoplatform.ide.json.shared.JsonArray;
import org.exoplatform.ide.json.shared.JsonStringMap;

/**
 * Partial copy of {@link org.exoplatform.ide.vfs.shared.Item}
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = RoutableDto.NON_ROUTABLE_TYPE)
public interface Item extends ServerToClientDto, CompactJsonDto
{
   public enum ItemType
   {
      FILE("file"), FOLDER("folder"), PROJECT("project");

      private final String value;

      private ItemType(String value)
      {
         this.value = value;
      }

      /** @return value of Type */
      public String value()
      {
         return value;
      }

      /**
       * Get Type instance from string value.
       *
       * @param value string value
       * @return Type
       * @throws IllegalArgumentException if there is no corresponded Type for specified <code>value</code>
       */
      public static ItemType fromValue(String value)
      {
         String v = value.toLowerCase();
         for (ItemType e : ItemType.values())
         {
            if (e.value.equals(v))
            {
               return e;
            }
         }
         throw new IllegalArgumentException(value);
      }

      /** @see java.lang.Enum#toString() */
      @Override
      public String toString()
      {
         return value;
      }
   }

   /** @return id of object */
   @SerializationIndex(1)
   String getId();

   /** @return name of object */
   @SerializationIndex(2)
   String getName();

   /** @return type of item */
   @SerializationIndex(3)
   ItemType getItemType();

   /** @return path */
   @SerializationIndex(4)
   String getPath();

   /** @return id of parent folder and <code>null</code> if current item is root folder */
   @SerializationIndex(5)
   String getParentId();

   /** @return media type */
   @SerializationIndex(6)
   String getMimeType();

   /**
    * Other properties.
    *
    * @return properties. If there is no properties then empty list returned, never <code>null</code>
    */
   @SerializationIndex(7)
   JsonArray<Property> getProperties();


   /**
    * Links for retrieved or(and) manage item.
    *
    * @return links map. Never <code>null</code> but empty map instead
    */
   @SerializationIndex(8)
   JsonStringMap<Link> getLinks();

}
