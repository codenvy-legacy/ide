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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.module.vfs.api.Version;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 28, 2010 $
 *
 */
public class UpdateItemMarshaller implements Marshallable
{

   private Version version;

   /**
    * @param version
    */
   public UpdateItemMarshaller(Version version)
   {
      this.version = version;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
      xml += "<D:update xmlns:D=\"DAV:\">";
      xml += "<D:version>" + "<D:href>" + version.getHref() + "</D:href></D:version>";
      xml += "</D:update>";
      
      return xml;
   }
}
