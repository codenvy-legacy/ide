/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.module.IDEModule;
import org.exoplatform.ideall.client.model.conversation.UserInfo;
import org.exoplatform.ideall.client.model.template.TemplateList;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ApplicationContext extends AbstractApplicationContext
{

   /**
    * Logged user information
    */
   private UserInfo userInfo;

   private String selectedNavigationPanel;

   /**
    * Selected items in all navigation panels
    */
   private HashMap<String, List<Item>> selectedItems = new HashMap<String, List<Item>>();

   /**
    * Current active text editor.
    */
   private TextEditor activeTextEditor;

   private LinkedHashMap<String, File> preloadFiles = new LinkedHashMap<String, File>();

   private LinkedHashMap<String, String> openedEditors = new LinkedHashMap<String, String>();

   /**
    * List of available templates
    */
   private TemplateList templateList;

   /*
    * Last entered value in Groovy script output form
    */
   private String testGroovyScriptURL;

   private String searchContent;

   private String searchFileName;

   private String searchContentType;

   private Map<String, String> hotKeys = new HashMap<String, String>();

   private Map<String, String> reservedHotkeys = new HashMap<String, String>();

//   /**
//    * Registered components
//    */
//   private ArrayList<AbstractApplicationComponent> components = new ArrayList<AbstractApplicationComponent>();

   private List<IDEModule> modules = new ArrayList<IDEModule>();

   public List<IDEModule> getModules()
   {
      return modules;
   }


   /**
    * Uses for storing the current state of defaults editors
    */
   //TODO
   private HashMap<String, String> defaultEditors = new HashMap<String, String>();

   /**
    * Uses for storing items to need copy
    */
   private List<Item> itemsToCopy = new ArrayList<Item>();

   /**
    * Uses to storing items to need cut
    */
   private List<Item> itemsToCut = new ArrayList<Item>();

   public List<Item> getItemsToCopy()
   {
      return itemsToCopy;
   }

   public List<Item> getItemsToCut()
   {
      return itemsToCut;
   }

   private String selectedEditorDescription;

   private boolean initialized;

   public UserInfo getUserInfo()
   {
      return userInfo;
   }

   public void setUserInfo(UserInfo userInfo)
   {
      this.userInfo = userInfo;
   }

   /**
    * @return the activeTextEditor
    */
   public TextEditor getActiveTextEditor()
   {
      return activeTextEditor;
   }

   /**
    * @param activeTextEditor the activeTextEditor to set
    */
   public void setActiveTextEditor(TextEditor activeTextEditor)
   {
      this.activeTextEditor = activeTextEditor;
   }

   public LinkedHashMap<String, File> getPreloadFiles()
   {
      return preloadFiles;
   }

   public LinkedHashMap<String, String> getOpenedEditors()
   {
      return openedEditors;
   }

   /**
    * @return the selectedItems
    */
   public List<Item> getSelectedItems(String navigationPanelName)
   {
      List<Item> items = selectedItems.get(navigationPanelName);
      if (items == null)
      {
         items = new ArrayList<Item>();
         selectedItems.put(navigationPanelName, items);
      }

      return items;
   }

   public String getSelectedNavigationPanel()
   {
      return selectedNavigationPanel;
   }

   public void setSelectedNavigationPanel(String selectedNavigationPanel)
   {
      this.selectedNavigationPanel = selectedNavigationPanel;
   }

   /**
    * @return the templateList
    */
   public TemplateList getTemplateList()
   {
      return templateList;
   }

   /**
    * @param templateList the templateList to set
    */
   public void setTemplateList(TemplateList templateList)
   {
      this.templateList = templateList;
   }

   /**
    * @return
    */
   public String getTestGroovyScriptURL()
   {
      return testGroovyScriptURL;
   }

   /**
    * @param testGroovyScriptURL
    */
   public void setTestGroovyScriptURL(String testGroovyScriptURL)
   {
      this.testGroovyScriptURL = testGroovyScriptURL;
   }

//   public ArrayList<AbstractApplicationComponent> getComponents()
//   {
//      return components;
//   }

   public String getSearchContent()
   {
      return searchContent;
   }

   public void setSearchContent(String searchContent)
   {
      this.searchContent = searchContent;
   }

   public String getSearchFileName()
   {
      return searchFileName;
   }

   public void setSearchFileName(String searchFileName)
   {
      this.searchFileName = searchFileName;
   }

   public String getSearchContentType()
   {
      return searchContentType;
   }

   public void setSearchContentType(String searchContentType)
   {
      this.searchContentType = searchContentType;
   }

   public boolean isInitialized()
   {
      return initialized;
   }

   public void setInitialized(boolean initialized)
   {
      this.initialized = initialized;
   }

   /**
    * 
    * @return Defaults editors
    */
   public HashMap<String, String> getDefaultEditors()
   {
      return defaultEditors;
   }

   public String getSelectedEditorDescription()
   {
      return selectedEditorDescription;
   }

   public void setSelectedEditorDescription(String selectedEditor)
   {
      this.selectedEditorDescription = selectedEditor;
   }


   public Map<String, String> getHotKeys()
   {
      return hotKeys;
   }

   public void setHotKeys(Map<String, String> hotKeys)
   {
      this.hotKeys = hotKeys;
   }

   public void setReservedHotkeys(Map<String, String> hotKeys)
   {
      this.reservedHotkeys = hotKeys;
   }

   public Map<String, String> getReservedHotkeys()
   {
      return reservedHotkeys;
   }

}
