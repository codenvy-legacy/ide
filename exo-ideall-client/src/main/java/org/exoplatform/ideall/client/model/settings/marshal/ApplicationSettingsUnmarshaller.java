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
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.settings.ApplicationSettings;

import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsUnmarshaller implements Const, Unmarshallable
{

   private ApplicationSettings applicationSettings;

   public final static String ERROR_MESSAGE = "Can't parse application settings!";

   public ApplicationSettingsUnmarshaller(ApplicationSettings applicationSettings)
   {
      this.applicationSettings = applicationSettings;
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

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         Document dom = XMLParser.parse(response.getText());

         Node configurationNode = dom.getElementsByTagName(SETTINGS).item(0);

         //parseLineNumbers(configurationNode);
         parseToolbar(configurationNode);
         parseEditors(configurationNode);
         parseHotKeys(configurationNode);
      }
      catch (Exception exc)
      {
         throw new UnmarshallerException(ERROR_MESSAGE);
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

      applicationSettings.getDefaultEditors().clear();
      applicationSettings.getDefaultEditors().putAll(editorsMap);
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

      applicationSettings.getToolbarItems().clear();
      applicationSettings.getToolbarItems().addAll(toolbarItems);
   }

   private void parseHotKeys(Node configurationNode)
   {
      Node hotKeys = getChildNode(configurationNode, HOT_KEYS);

      Map<String, String> hotKeysMap = new HashMap<String, String>();

      for (int i = 0; i < hotKeys.getChildNodes().getLength(); i++)
      {
         Node hotKeyItemNode = hotKeys.getChildNodes().item(i);

         String itemKey = getChildNode(hotKeyItemNode, SHORTCUT).getChildNodes().item(0).getNodeValue();
         String itemValue = getChildNode(hotKeyItemNode, CONTROL_ID).getChildNodes().item(0).getNodeValue();

         hotKeysMap.put(itemKey, itemValue);
      }

      applicationSettings.getHotKeys().clear();
      applicationSettings.getHotKeys().putAll(hotKeysMap);
   }

}
