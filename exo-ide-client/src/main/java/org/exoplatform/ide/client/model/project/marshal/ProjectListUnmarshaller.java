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

import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.project.Project;
import org.exoplatform.ide.client.framework.project.ProjectList;

/**
 * Unmarshaller for the list of projects.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class ProjectListUnmarshaller implements Unmarshallable, Const
{

   private ProjectList projects;

   public ProjectListUnmarshaller(ProjectList projects)
   {
      this.projects = projects;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         Document dom = XMLParser.parse(response.getText());
         NodeList nodes = dom.getElementsByTagName(PROJECTS);
         if (nodes == null || nodes.item(0) == null)
            return;
         NodeList projectNodes = nodes.item(0).getChildNodes();
         
         for (int i = 0; i < projectNodes.getLength(); i++)
         {
            Node projectNode = projectNodes.item(i);
            String baseLocation = "";
            String classpathLocation = "";
            for (int j = 0; j < projectNode.getFirstChild().getChildNodes().getLength(); j++)
            {
               Node node = projectNode.getFirstChild().getChildNodes().item(j);
               String value = (node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : "";
               value = javaScriptDecodeURIComponent(value);
               if (BASE_LOCATION.equals(node.getNodeName()))
               {
                  baseLocation = value;
               }
               else if (CLASSPATH_LOCATION.equals(node.getNodeName()))
               {
                  classpathLocation = value;
               }
            }
            projects.getProjects().add(new Project(baseLocation, classpathLocation, projectNode.getNodeName()));
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         String message = "Can't parse project list!";
         throw new UnmarshallerException(message);
      }
   }

   public static native String javaScriptDecodeURIComponent(String text) /*-{
                                                                         return decodeURIComponent(text);
                                                                         }-*/;

}
