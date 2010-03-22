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

import org.exoplatform.gwtframework.ui.client.component.command.Command;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.model.conversation.UserInfo;
import org.exoplatform.ideall.client.model.jcrservice.bean.RepositoryServiceConfiguration;
import org.exoplatform.ideall.client.model.template.TemplateList;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ApplicationContext
{

   /**
    * Logged user information
    */
   private UserInfo userInfo;

   /**
    * Selected items in browser
    */
   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * Current edited file
    */
   private File activeFile;

   private LinkedHashMap<String, File> preloadFiles = new LinkedHashMap<String, File>();

   /**
    * Opened files in editor
    */
   private LinkedHashMap<String, File> openedFiles = new LinkedHashMap<String, File>();

   private LinkedHashMap<String, String> openedEditors = new LinkedHashMap<String, String>();

   /**
    * List of available templates
    */
   private TemplateList templateList;

   /**
    * Selected Repository
    */
   private String repository;

   /**
    * Selected Workspace
    */
   private String workspace;

   /**
    * Configuration of Repository Service
    */
   private RepositoryServiceConfiguration repositoryServiceConfiguration;

   /*
    * Last entered value in Groovy script output form
    */
   private String testGroovyScriptURL;

   private String searchContent;

   private String searchFileName;

   private String searchContentType;

   /**
    * Registered components
    */
   private ArrayList<AbstractApplicationComponent> components = new ArrayList<AbstractApplicationComponent>();

   /**
    * Registered commands
    */
   private ArrayList<Command> commands = new ArrayList<Command>();

   /**
    * Uses for storing the current state of toolbar
    */
   private ArrayList<String> toolBarItems = new ArrayList<String>();

   /**
    * Uses for storing default state of toolbar
    */
   private ArrayList<String> toolBarDefaultItems = new ArrayList<String>();

   /**
    * Uses for storing the current state of defaults editors
    */
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

   private boolean showLineNumbers = true;

   private boolean initialized;
   
   private Item cached;

   public ApplicationContext()
   {
      toolBarItems.add("");
   }

   public UserInfo getUserInfo()
   {
      return userInfo;
   }

   public void setUserInfo(UserInfo userInfo)
   {
      this.userInfo = userInfo;
   }

   /**
    * @return the activeFile
    */
   public File getActiveFile()
   {
      return activeFile;
   }

   /**
    * @param activeFile the activeFile to set
    */
   public void setActiveFile(File activeFile)
   {
      this.activeFile = activeFile;
   }

   public LinkedHashMap<String, File> getPreloadFiles()
   {
      return preloadFiles;
   }

   /**
    * @return the openedFiles
    */
   public HashMap<String, File> getOpenedFiles()
   {
      return openedFiles;
   }

   public LinkedHashMap<String, String> getOpenedEditors()
   {
      return openedEditors;
   }

   /**
    * @return the selectedItems
    */
   public List<Item> getSelectedItems()
   {
      return selectedItems;
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
    * @return the repository
    */
   public String getRepository()
   {
      return repository;
   }

   /**
    * @param repository the repository to set
    */
   public void setRepository(String repository)
   {
      this.repository = repository;
   }

   /**
    * @return the workspace
    */
   public String getWorkspace()
   {
      return workspace;
   }

   /**
    * @param workspace the workspace to set
    */
   public void setWorkspace(String workspace)
   {
      this.workspace = workspace;
   }

   /**
    * @return the repositoryServiceConfiguration
    */
   public RepositoryServiceConfiguration getRepositoryServiceConfiguration()
   {
      return repositoryServiceConfiguration;
   }

   /**
    * @param repositoryServiceConfiguration the repositoryServiceConfiguration to set
    */
   public void setRepositoryServiceConfiguration(RepositoryServiceConfiguration repositoryServiceConfiguration)
   {
      this.repositoryServiceConfiguration = repositoryServiceConfiguration;
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

   public ArrayList<AbstractApplicationComponent> getComponents()
   {
      return components;
   }

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

   public ArrayList<Command> getCommands()
   {
      return commands;
   }

   public ArrayList<String> getToolBarItems()
   {
      return toolBarItems;
   }

   public ArrayList<String> getToolBarDefaultItems()
   {
      return toolBarDefaultItems;
   }

   public boolean isInitialized()
   {
      return initialized;
   }

   public void setInitialized(boolean initialized)
   {
      this.initialized = initialized;
   }

   public boolean isShowLineNumbers()
   {
      return showLineNumbers;
   }

   public void setShowLineNumbers(boolean showLineNumbers)
   {
      this.showLineNumbers = showLineNumbers;
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

   public void setSelectedEditorDescriptor(String selectedEditor)
   {
      this.selectedEditorDescription = selectedEditor;
   }

   public Item getCache()
   {
      return cached;
   }

   public void setCache(Item cache)
   {
      this.cached = cache;
   }   

}
