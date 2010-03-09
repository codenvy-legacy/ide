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
import java.util.HashMap;
import java.util.Iterator;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;
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

   private HandlerManager eventBus;

   private ApplicationContext context;

   public ApplicationContextUnmarshaller(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;
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

   public void unmarshal(String body) throws UnmarshallerException
   {
      try
      {
         Document dom = XMLParser.parse(body);

         Node configurationNode = dom.getElementsByTagName(SETTINGS).item(0);

         parseLineNumbers(configurationNode);
         parseToolbar(configurationNode);
         parseEditors(configurationNode);
      }
      catch (Exception exc)
      {
         String message = "Can't parse user settings!";
         throw new UnmarshallerException(message);
      }
   }

   private void parseEditors(Node configurationNode)
   {
      Node editors = getChildNode(configurationNode, EDITORS);
      HashMap<String, String> editorsMap = new HashMap<String, String>();
      for (int i = 0; i < editors.getChildNodes().getLength(); i++)
      {
         Node editorItemNode = editors.getChildNodes().item(i);
         
         String itemKey = getChildNode(editorItemNode, MIME_TYPE).getChildNodes().item(0).getNodeValue();
         String itemValue = getChildNode(editorItemNode, EDITOR_DESCRIPTION).getChildNodes().item(0).getNodeValue();
         
         editorsMap.put(itemKey, itemValue);
      }
      context.getDefaultEditors().clear();
      context.getDefaultEditors().putAll(editorsMap);
   }

   private void parseLineNumbers(Node configurationNode)
   {
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
