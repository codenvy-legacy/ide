/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.commons.xml;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class QName
{

   private String localName;

   private String namespaceURI;

   private String prefix;

   public QName(String fullname, String namespaceURI)
   {
      this.namespaceURI = namespaceURI;
      String[] tmp = fullname.split(":");
      if (tmp.length > 1)
      {
         this.localName = tmp[1];
         this.prefix = tmp[0];
      }
      else
      {
         this.localName = tmp[0];
      }
   }

   public final String getNamespaceURI()
   {
      return namespaceURI;
   }

   public final String getPrefix()
   {
      return prefix;
   }

   public final String getLocalName()
   {
      return localName;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((localName == null) ? 0 : localName.hashCode());
      result = prime * result + ((namespaceURI == null) ? 0 : namespaceURI.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final QName other = (QName)obj;
      if (localName == null)
      {
         if (other.localName != null)
            return false;
      }
      else if (!localName.equals(other.localName))
         return false;
      if (namespaceURI == null)
      {
         if (other.namespaceURI != null)
            return false;
      }
      else if (!namespaceURI.equals(other.namespaceURI))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "{\"prefix\":\"" + prefix + "\",\"localName\": \"" + localName + "\",\"namespaceURI\":\"" + namespaceURI
         + "\"}";
   }

}
