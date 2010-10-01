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

import com.google.gwt.xml.client.Element;

import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.xml.client.Document;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 24, 2010 $
 *
 */
public class ItemVersionsMarshaller implements Marshallable
{

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      Document doc = XMLParser.createDocument();
      
      Element versionTreeElement = doc.createElement("D:version-tree");
      versionTreeElement.setAttribute("xmlns:D", "DAV:");
      doc.appendChild(versionTreeElement);
      
      Element propElement = doc.createElement("D:prop");
      
     // Element allPropElement = doc.createElement("D:allprop");
      Element versionNameElement = doc.createElement("D:version-name");
//      Element creatorElement = doc.createElement("D:creator-displayname");
      Element successorElement = doc.createElement("D:successor-set");
      Element predecessorElement = doc.createElement("D:predecessor-set");
      
      Element creationElement = doc.createElement("D:creationdate");
      Element contentNodTypeElement = doc.createElement("D:getcontenttype");
      Element contentLengthElement = doc.createElement("D:getcontentlength");
      Element diplayNameElement = doc.createElement("D:displayname");
      
      propElement.appendChild(versionNameElement);
//      propElement.appendChild(creatorElement);
      propElement.appendChild(successorElement);
      propElement.appendChild(predecessorElement);
      propElement.appendChild(creationElement);
      propElement.appendChild(contentNodTypeElement);
      propElement.appendChild(contentLengthElement);
      propElement.appendChild(diplayNameElement);
            
      versionTreeElement.appendChild(propElement);
      
      return doc.toString();
   }

}
