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
package org.exoplatform.ide.client.model.template.marshal;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TemplateMarshaller implements Marshallable, Const
{

   private Template template;

   public TemplateMarshaller(Template template)
   {
      this.template = template;
   }

   public static native String javaScriptEncodeURIComponent(String text) /*-{
         return encodeURIComponent(text);
      }-*/;

   public String marshal()
   {
      Document doc = XMLParser.createDocument();
      
      Element templateElement = doc.createElement(TEMPLATE);
      doc.appendChild(templateElement);
      
      Element templateNameElement = doc.createElement(NAME);
      templateNameElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(template.getName())));
      templateElement.appendChild(templateNameElement);
      
      Element templateDescriptionElement = doc.createElement(DESCRIPTION);
      templateDescriptionElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(template.getDescription())));
      templateElement.appendChild(templateDescriptionElement);
      
      if (template instanceof FileTemplate)
      {
         buildFileElement(doc, templateElement, (FileTemplate)template);
      }
      else if (template instanceof ProjectTemplate)
      {
         Element typeElement = doc.createElement(TEMPLATE_TYPE);
         typeElement.appendChild(doc.createTextNode(TemplateType.PROJECT));
         templateElement.appendChild(typeElement);
         ProjectTemplate projectTemplate = (ProjectTemplate)template;
         if (projectTemplate.getClassPathLocation() != null && projectTemplate.getClassPathLocation().length() > 0)
         {
            Element classPathLocationElement = doc.createElement(CLASSPATH);
            classPathLocationElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(projectTemplate.getClassPathLocation())));
            templateElement.appendChild(classPathLocationElement);
         }
         buildProjectElement(doc, templateElement, projectTemplate.getChildren());
      }
      
      return doc.toString();
   }
   
   private void buildProjectElement(Document doc, Element parentElement, List<Template>templates)
   {
      if (templates == null)
      {
         return;
      }
      
      Element itemsListElement = doc.createElement(ITEMS);
      
      for (Template child : templates)
      {
         if (child instanceof FileTemplate)
         {
            Element fileElement = doc.createElement(FILE);
            
            Element nameElement = doc.createElement(TEMPLATE_FILE_NAME);
            nameElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(child.getName())));
            
            Element fileNameElement = doc.createElement(FILE_NAME);
            fileNameElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(((FileTemplate)child).getFileName())));
            
            fileElement.appendChild(nameElement);
            fileElement.appendChild(fileNameElement);
            
            itemsListElement.appendChild(fileElement);
         }
         else if (child instanceof FolderTemplate)
         {
            Element folderElement = doc.createElement(FOLDER);
            
            Element nameElement = doc.createElement(NAME);
            nameElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(child.getName())));
            folderElement.appendChild(nameElement);
            
            itemsListElement.appendChild(folderElement);
            
            buildProjectElement(doc, folderElement, ((FolderTemplate)child).getChildren());
         }
      }
      
      if (itemsListElement.getChildNodes() != null && itemsListElement.getChildNodes().getLength() > 0)
      {
         parentElement.appendChild(itemsListElement);
      }
   }
   
   private void buildFileElement(Document doc, Element templateElement, FileTemplate fileTemplate)
   {
      Element typeElement = doc.createElement(TEMPLATE_TYPE);
      typeElement.appendChild(doc.createTextNode(TemplateType.FILE));
      templateElement.appendChild(typeElement);
      
      Element mimeTypeElement = doc.createElement(MIME_TYPE);
      mimeTypeElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(fileTemplate.getMimeType())));
      templateElement.appendChild(mimeTypeElement);
      
      Element contentElement = doc.createElement(CONTENT);
      contentElement.appendChild(doc.createTextNode(javaScriptEncodeURIComponent(fileTemplate.getContent())));
      templateElement.appendChild(contentElement);
   }
   
}
