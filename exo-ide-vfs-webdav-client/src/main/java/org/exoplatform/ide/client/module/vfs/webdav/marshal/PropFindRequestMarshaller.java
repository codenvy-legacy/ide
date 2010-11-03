/**
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
 *
 */

package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PropFindRequestMarshaller implements Marshallable
{

   private List<QName> properties;

   public PropFindRequestMarshaller(List<QName> properties)
   {
      this.properties = properties;
   }

   public String marshal()
   {
      String xml = "<?xml version='1.0' encoding='UTF-8' ?>";
      xml += "<D:propfind xmlns:D=\"DAV:\">";
      
      if(properties == null || properties.size() == 0)
      {
         xml += "<D:allprop />";
         xml += "</D:propfind>";
         return xml;
      }
      
      xml += "<D:prop>";
      for (QName property : properties)
      {
         if(ItemProperty.Namespace.DAV.equals(property.getNamespaceURI())){
            xml += "<D:" +  property.getLocalName() +" />";
         } else if (ItemProperty.Namespace.JCR.equals(property.getNamespaceURI())) {
            xml += "<jcr:" +  property.getLocalName() +" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\"/>";
         } 
      }
      xml += "</D:prop>";
      xml += "</D:propfind>";
      return xml;
//      xml += "<D:propfind xmlns:D=\"DAV:\">";
//      xml += "<D:prop>";
//      xml += "<D:acl />";
//      xml += "</D:prop>";
//      xml += "</D:propfind>";

//      return xml;
   }

}
