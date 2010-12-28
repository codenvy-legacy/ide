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
package org.exoplatform.ide.client.model.project.marshal;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.framework.project.Project;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class ProjectMarshaller implements Marshallable, Const
{
   private Project project;
   
   /**
    * 
    */
   public ProjectMarshaller(Project project)
   {
      this.project = project;
   }
   
   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   public String marshal()
   {
      Document doc = XMLParser.createDocument();
      
      Element projectElement = doc.createElement(PROJECT);
      doc.appendChild(projectElement);
      
      Element baseLocationElement = doc.createElement(BASE_LOCATION);
      baseLocationElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(project.getHref())));
      projectElement.appendChild(baseLocationElement);
      
      Element classpathLocationElement = doc.createElement(CLASSPATH_LOCATION);
      classpathLocationElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(project.getClassPathLocation())));
      projectElement.appendChild(classpathLocationElement);
      
      return doc.toString();
   }

   public static native String javaScriptEncodeURIComponent(String text) /*-{
   return encodeURIComponent(text);
   }-*/;
}
