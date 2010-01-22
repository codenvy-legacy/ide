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
package org.exoplatform.ideall.client.model.settings.marshal;

import java.util.ArrayList;

import org.exoplatform.gwt.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationContextUnmarshaller implements Const, Unmarshallable
{

   private ApplicationContext context;

   public ApplicationContextUnmarshaller(ApplicationContext context)
   {
      this.context = context;
   }

   private Node getChildNode(Node node, String name)
   {
      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node childNode = node.getChildNodes().item(i);
         if (name.equals(childNode.getNodeName()))
         {
            return childNode;
         }
      }

      return null;
   }

   public void unmarshal(String body)
   {
      try
      {
         Document dom = XMLParser.parse(body);

         Node configurationNode = dom.getElementsByTagName(SETTINGS).item(0);

         parseRepository(configurationNode);
         parseWorkspace(configurationNode);
         parseOpenedFiles(configurationNode);
         parseActiveFile(configurationNode);
         parseLineNumbers(configurationNode);
         parseToolbar(configurationNode);
      }
      catch (Exception exc)
      {
         Window.alert("Can't parse user settings!");
      }
   }

   private void parseRepository(Node configurationNode)
   {
      String repository = getChildNode(configurationNode, REPOSITORY).getChildNodes().item(0).getNodeValue();
      context.setRepository(repository);
   }

   private void parseWorkspace(Node configurationNode)
   {
      String workspace = getChildNode(configurationNode, WORKSPACE).getChildNodes().item(0).getNodeValue();
      context.setWorkspace(workspace);
   }

   private void parseOpenedFiles(Node configurationNode)
   {
      Node files = getChildNode(configurationNode, OPENED_FILES);

      context.getPreloadFiles().clear();

      for (int i = 0; i < files.getChildNodes().getLength(); i++)
      {
         Node fileNode = files.getChildNodes().item(i);
         String path = fileNode.getChildNodes().item(0).getNodeValue();
         System.out.println("restoring > " + path);
         File file = new File(path);
         context.getPreloadFiles().put(file.getPath(), file);
      }
   }

   private void parseActiveFile(Node configurationNode)
   {
      if (getChildNode(configurationNode, ACTIVE_FILE).getChildNodes().getLength() == 0)
      {
         return;
      }

      String activeFile = getChildNode(configurationNode, ACTIVE_FILE).getChildNodes().item(0).getNodeValue();

      if (context.getPreloadFiles().get(activeFile) != null)
      {
         File file = context.getPreloadFiles().get(activeFile);
         context.setActiveFile(file);
      }
   }

   private void parseLineNumbers(Node configurationNode) {
      if (getChildNode(configurationNode, LINE_NUMBERS).getChildNodes().getLength() == 0)
      {
         return;
      }

      String lineNumbers = getChildNode(configurationNode, LINE_NUMBERS).getChildNodes().item(0).getNodeValue();
      context.setShowLineNumbers(Boolean.parseBoolean(lineNumbers));
   }
   
   private void parseToolbar(Node configurationNode)
   {
      Node toolbar = getChildNode(configurationNode, TOOLBAR);

      ArrayList<String> toolbarItems = new ArrayList<String>();

      for (int i = 0; i < toolbar.getChildNodes().getLength(); i++)
      {
         Node toolbarItemNode = toolbar.getChildNodes().item(i);

         String item = "";
         if (toolbarItemNode.getChildNodes().getLength() != 0)
         {
            item = toolbarItemNode.getChildNodes().item(0).getNodeValue();
         }
         toolbarItems.add(item);
      }

      context.getToolBarItems().clear();
      context.getToolBarItems().addAll(toolbarItems);
   }

}
